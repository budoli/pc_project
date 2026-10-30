import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import { getPolicyStats, reindexPolicies, syncPolicies } from "@/features/admin/api/adminPolicyApi";
import type { AdminPolicyStats } from "@/features/admin/types/AdminPolicyStats";
import type { PolicySyncResult } from "@/features/admin/types/PolicySyncResult";
import { useAuthStore } from "@/features/auth/store/authStore";

type Job = "sync" | "reindex";

type JobResult = {
    type: "success" | "error";
    message: string;
    finishedAt: string;
};

const JOB_LABEL: Record<Job, string> = {
    sync: "정책 동기화",
    reindex: "재색인",
};

// 작업이 도는 동안 벡터 수 증가를 보여주기 위한 현황 갱신 주기
const STATS_POLL_MS = 5000;

const formatElapsed = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    return `${String(minutes).padStart(2, "0")}:${String(seconds % 60).padStart(2, "0")}`;
};

const formatSyncResult = (sync: PolicySyncResult) => {
    const summary = `정책 동기화 완료 (${(sync.elapsedMs / 1000).toFixed(1)}초): 전체 ${sync.total.toLocaleString()}건 · `
        + `신규 ${sync.created.toLocaleString()} · 변경 ${sync.updated.toLocaleString()} · `
        + `변경 없음 ${sync.unchanged.toLocaleString()} · 임베딩 ${sync.embedded.toLocaleString()}건`;

    if (sync.embeddingFailed > 0) {
        return `${summary}. 임베딩 실패 ${sync.embeddingFailed.toLocaleString()}건: AI 서버를 확인한 뒤 다시 동기화하면 실패한 정책만 다시 임베딩합니다.`;
    }
    return summary;
};

const toErrorMessage = (error: unknown, job: Job) => {
    if (axios.isAxiosError(error)) {
        if (error.response?.status === 403) {
            return "관리자 권한이 필요합니다. 관리자 계정으로 다시 로그인해 주세요.";
        }
        const serverMessage = error.response?.data?.message;
        if (serverMessage) {
            return serverMessage;
        }
        if (!error.response) {
            return "백엔드 서버에 연결할 수 없습니다.";
        }
    }
    return `${JOB_LABEL[job]}에 실패했습니다.`;
};

const AdminPage = () => {
    const userId = useAuthStore((state) => state.user?.id);
    const [stats, setStats] = useState<AdminPolicyStats | null>(null);
    const [statsError, setStatsError] = useState("");
    const [runningJob, setRunningJob] = useState<Job | null>(null);
    const [elapsed, setElapsed] = useState(0);
    const [result, setResult] = useState<JobResult | null>(null);

    const loadStats = useCallback(async () => {
        try {
            setStats(await getPolicyStats());
            setStatsError("");
        } catch (error) {
            setStatsError(
                axios.isAxiosError(error) && error.response?.status === 403
                    ? "관리자 권한이 필요합니다. 관리자 계정으로 로그인해 주세요."
                    : "현황을 불러오지 못했습니다."
            );
        }
    }, []);

    // 로그인 사용자가 바뀌면(로그인 직후, 페이지 새로고침 후 사용자 복원) 다시 불러온다
    useEffect(() => {
        const fetchStats = async () => {
            await loadStats();
        };

        fetchStats();
    }, [loadStats, userId]);

    useEffect(() => {
        if (!runningJob) {
            return;
        }

        const startedAt = Date.now();
        const timer = setInterval(() => setElapsed(Math.floor((Date.now() - startedAt) / 1000)), 1000);
        const poller = setInterval(loadStats, STATS_POLL_MS);

        return () => {
            clearInterval(timer);
            clearInterval(poller);
        };
    }, [runningJob, loadStats]);

    const runJob = async (job: Job) => {
        const confirmMessage = job === "sync"
            ? "온통청년 API에서 전체 정책을 가져와 저장하고 임베딩합니다. 시간이 오래 걸릴 수 있습니다. 진행할까요?"
            : "DB의 전체 정책을 다시 임베딩합니다. 진행할까요?";
        if (!window.confirm(confirmMessage)) {
            return;
        }

        setRunningJob(job);
        setElapsed(0);
        setResult(null);

        try {
            if (job === "sync") {
                const sync = await syncPolicies();
                setResult({
                    type: sync.embeddingFailed > 0 ? "error" : "success",
                    message: formatSyncResult(sync),
                    finishedAt: new Date().toLocaleTimeString(),
                });
            } else {
                const count = await reindexPolicies();
                setResult({
                    type: "success",
                    message: `${JOB_LABEL[job]} 완료: ${count.toLocaleString()}건`,
                    finishedAt: new Date().toLocaleTimeString(),
                });
            }
        } catch (error) {
            setResult({
                type: "error",
                message: toErrorMessage(error, job),
                finishedAt: new Date().toLocaleTimeString(),
            });
        } finally {
            setRunningJob(null);
            loadStats();
        }
    };

    return (
        <div className="mx-auto w-full max-w-[880px] px-8 pb-16 pt-6">
            <h1 className="mb-2 text-3xl font-bold">관리자</h1>
            <p className="sm mb-8 text-gray-500">정책 데이터 수집과 검색 인덱스를 관리합니다.</p>

            <section className="mb-8">
                <div className="mb-3 flex items-center justify-between">
                    <h2 className="lg font-semibold">색인 현황</h2>
                    <button
                        type="button"
                        onClick={loadStats}
                        className="sm rounded-md px-3 py-1.5 text-gray-600 hover:bg-gray-100"
                    >
                        새로고침
                    </button>
                </div>

                {statsError ? (
                    <p className="sm rounded-xl border border-red-200 bg-red-50 px-5 py-4 text-red-600">{statsError}</p>
                ) : (
                    <>
                        <div className="grid grid-cols-3 gap-4">
                            <StatCard label="DB 정책" value={stats?.policyCount} />
                            <StatCard label="정책 문서" value={stats?.documentCount} />
                            <StatCard
                                label="검색 벡터 (Qdrant)"
                                value={stats?.vectorCount}
                                unavailable={stats !== null && stats.vectorCount === null}
                            />
                        </div>
                        {stats && <IndexStatus stats={stats} />}
                    </>
                )}
            </section>

            <section>
                <h2 className="lg mb-3 font-semibold">작업</h2>
                <div className="divide-y divide-gray-200 rounded-xl border border-gray-200 bg-white">
                    <JobRow
                        title="정책 동기화"
                        description="온통청년 API에서 정책을 가져와 DB에 저장하고, 저장이 끝나면 임베딩합니다."
                        buttonLabel="동기화 실행"
                        primary
                        running={runningJob === "sync"}
                        disabled={runningJob !== null}
                        elapsed={elapsed}
                        onRun={() => runJob("sync")}
                    />
                    <JobRow
                        title="재색인"
                        description="DB에 저장된 정책 전체를 다시 임베딩합니다. 동기화 후 임베딩이 실패했을 때 사용합니다."
                        buttonLabel="재색인 실행"
                        running={runningJob === "reindex"}
                        disabled={runningJob !== null}
                        elapsed={elapsed}
                        onRun={() => runJob("reindex")}
                    />
                </div>

                {runningJob && (
                    <p className="sm mt-3 text-gray-500">
                        작업이 끝날 때까지 이 페이지를 닫지 마세요. 벡터 수는 {STATS_POLL_MS / 1000}초마다 갱신됩니다.
                    </p>
                )}

                {result && (
                    <p
                        className={`sm mt-4 rounded-xl border px-5 py-4 ${
                            result.type === "success"
                                ? "border-green-200 bg-green-50 text-green-700"
                                : "border-red-200 bg-red-50 text-red-600"
                        }`}
                    >
                        <span className="mr-2 text-gray-400">{result.finishedAt}</span>
                        {result.message}
                    </p>
                )}
            </section>
        </div>
    );
};

type StatCardProps = {
    label: string;
    value: number | null | undefined;
    unavailable?: boolean;
};

const StatCard = ({ label, value, unavailable = false }: StatCardProps) => (
    <div className="rounded-xl border border-gray-200 bg-white px-5 py-4">
        <p className="sm mb-1 text-gray-500">{label}</p>
        <p className="text-2xl font-bold tabular-nums">
            {unavailable ? <span className="text-base font-medium text-gray-400">연결 안 됨</span>
                : value === undefined || value === null ? "-" : value.toLocaleString()}
        </p>
    </div>
);

const IndexStatus = ({ stats }: { stats: AdminPolicyStats }) => {
    if (stats.vectorCount === null) {
        return <p className="sm mt-3 text-red-600">AI 서버에 연결할 수 없습니다. 벡터 수를 확인하려면 AI 서버를 실행해 주세요.</p>;
    }
    if (stats.policyCount === 0) {
        return <p className="sm mt-3 text-gray-500">저장된 정책이 없습니다. 정책 동기화를 실행해 주세요.</p>;
    }
    if (stats.vectorCount === stats.policyCount) {
        return <p className="sm mt-3 text-green-700">DB 정책과 검색 벡터 수가 일치합니다.</p>;
    }
    return (
        <p className="sm mt-3 text-amber-700">
            DB 정책과 검색 벡터 수가 다릅니다 ({(stats.policyCount - stats.vectorCount).toLocaleString()}건 차이).
            작업 중이 아니라면 재색인이 필요합니다.
        </p>
    );
};

type JobRowProps = {
    title: string;
    description: string;
    buttonLabel: string;
    primary?: boolean;
    running: boolean;
    disabled: boolean;
    elapsed: number;
    onRun: () => void;
};

const JobRow = ({ title, description, buttonLabel, primary = false, running, disabled, elapsed, onRun }: JobRowProps) => (
    <div className="flex items-center justify-between gap-6 px-5 py-5">
        <div>
            <p className="font-semibold">{title}</p>
            <p className="sm mt-1 text-gray-500">{description}</p>
        </div>
        <button
            type="button"
            onClick={onRun}
            disabled={disabled}
            className={`sm shrink-0 rounded-md px-4 py-2 font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50 ${
                primary
                    ? "bg-black text-white hover:bg-gray-800 disabled:hover:bg-black"
                    : "border border-gray-300 bg-white hover:bg-gray-100 disabled:hover:bg-white"
            }`}
        >
            {running ? `진행 중 ${formatElapsed(elapsed)}` : buttonLabel}
        </button>
    </div>
);

export default AdminPage;
