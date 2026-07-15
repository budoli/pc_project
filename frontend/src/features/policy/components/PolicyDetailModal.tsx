import { useEffect, useState } from "react";
import type { PolicyDetail } from "../types/PolicyDetail";
import { getPolicyDetail } from "../api/policyApi";
import { formatRegion } from "@/shared/constants/regions";

type Props = {
    policyId: number;
    onClose: () => void;
}

const PolicyDetailModal = ({ policyId, onClose }: Props) => {

    const [policy, setPolicy] = useState<PolicyDetail | null>(null);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        const fetchDetail = async () => {
            try {
                setLoading(true);
                setErrorMessage("");
                const data = await getPolicyDetail(policyId);
                setPolicy(data);
            } catch {
                setErrorMessage("정책 상세 정보를 불러오지 못했습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchDetail();
    }, [policyId]);


    return (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 px-4 py-6"
            onClick={onClose}
        >
            <article
                className="relative max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-2xl bg-white p-6 shadow-2xl"
                onClick={(event) => event.stopPropagation()}
            >
                <button
                    type="button"
                    onClick={onClose}
                    className="absolute right-4 top-4 flex h-9 w-9 items-center justify-center rounded-full text-gray-500 transition hover:bg-gray-100 hover:text-gray-900"
                    aria-label="닫기"
                >
                    x
                </button>

                {loading && (
                    <div className="sm py-16 text-center text-gray-500">
                        불러오는 중...
                    </div>
                )}

                {errorMessage && (
                    <div className="sm py-16 text-center text-red-500">
                        {errorMessage}
                    </div>
                )}

                {policy && (
                    <div className="space-y-6">
                        <header className="pr-10">
                            <div className="mb-3 flex flex-wrap gap-2">
                                <span className="rounded-full bg-blue-50 px-3 py-1 text-xs font-medium text-blue-700">
                                    {policy.category || "분류 없음"}
                                </span>
                                <span className="rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-600">
                                    {policy.status || "상태 없음"}
                                </span>
                            </div>

                            <h2 className="xl font-bold leading-snug text-gray-900">
                                {policy.title}
                            </h2>

                            <p className="sm mt-3 leading-6 text-gray-600">
                                {policy.description || "-"}
                            </p>
                        </header>

                        <section className="sm grid gap-3 rounded-xl bg-gray-50 p-4">
                            <InfoRow label="주관 기관" value={policy.organization} />
                            <InfoRow
                                label="신청 기간"
                                value={`${policy.applyStartDate || "-"} ~ ${policy.applyEndDate || "-"
                                    }`}
                            />
                            <InfoRow label="지역" value={formatRegion(policy.region)} />
                        </section>

                        {policy.condition && (
                            <section>
                                <h3 className="base mb-3 font-semibold text-gray-900">
                                    지원 조건
                                </h3>

                                <div className="sm grid gap-3 rounded-xl border border-gray-200 p-4">
                                    <InfoRow
                                        label="나이"
                                        value={`${policy.condition.minAge ?? "-"}세 ~ ${policy.condition.maxAge ?? "-"
                                            }세`}
                                    />
                                    <InfoRow
                                        label="소득 조건"
                                        value={policy.condition.incomeCondition}
                                    />
                                    <InfoRow
                                        label="취업 조건"
                                        value={policy.condition.jobCondition}
                                    />
                                    <InfoRow
                                        label="학력 조건"
                                        value={policy.condition.educationCondition}
                                    />
                                    <InfoRow
                                        label="추가 조건"
                                        value={policy.condition.additionalCondition}
                                    />
                                    <InfoRow
                                        label="참여 제한 대상"
                                        value={policy.condition.excludedTarget}
                                    />
                                </div>
                            </section>
                        )}

                        {policy.document?.summary && (
                            <section>
                                <h3 className="base mb-3 font-semibold text-gray-900">
                                    상세 설명
                                </h3>
                                <p className="sm rounded-xl border border-gray-200 p-4 leading-6 text-gray-700">
                                    {policy.document.summary}
                                </p>
                            </section>
                        )}

                        {policy.sourceUrl && (
                            <a
                                href={policy.sourceUrl}
                                target="_blank"
                                rel="noreferrer"
                                className="sm block rounded-xl bg-blue-600 px-4 py-3 text-center font-semibold text-white transition hover:bg-blue-700"
                            >
                                신청 페이지로 이동
                            </a>
                        )}
                    </div>
                )}
            </article>
        </div>
    );
}

function InfoRow({
    label,
    value,
}: {
    label: string;
    value?: string | number | null;
}) {
    return (
        <div className="grid grid-cols-[96px_1fr] gap-3">
            <dt className="font-medium text-gray-500">{label}</dt>
            <dd className="break-words text-gray-800">{value || "-"}</dd>
        </div>
    );
}

export default PolicyDetailModal
