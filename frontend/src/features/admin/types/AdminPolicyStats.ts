export type AdminPolicyStats = {
    policyCount: number;
    documentCount: number;
    // AI 서버에 연결할 수 없으면 null
    vectorCount: number | null;
};
