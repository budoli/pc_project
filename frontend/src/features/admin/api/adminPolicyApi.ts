import { api } from "@/shared/api/axios";
import type { AdminPolicyStats } from "../types/AdminPolicyStats";
import type { PolicySyncResult } from "../types/PolicySyncResult";

export const getPolicyStats = async () => {
    const response = await api.get<AdminPolicyStats>("/api/v1/admin/policies/stats");
    return response.data;
};

// 동기화·재색인은 임베딩까지 끝나야 응답하므로 수십 분 걸릴 수 있다 (timeout 없음)
export const syncPolicies = async () => {
    const response = await api.post<PolicySyncResult>("/api/v1/admin/policies/sync", null, { timeout: 0 });
    return response.data;
};

export const reindexPolicies = async () => {
    const response = await api.post<number>("/api/v1/admin/policies/reindex", null, { timeout: 0 });
    return response.data;
};
