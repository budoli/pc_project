export type PolicySyncResult = {
    total: number;
    created: number;
    updated: number;
    // 원본 응답이 같아 갱신을 건너뛴 정책
    unchanged: number;
    embedded: number;
    // 다음 동기화 때 다시 임베딩된다
    embeddingFailed: number;
    elapsedMs: number;
};
