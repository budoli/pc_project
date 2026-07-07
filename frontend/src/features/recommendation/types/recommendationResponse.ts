export interface PolicyReference {
    policyId: number;
    title: string;
    score: number;
}

export interface recommendationResponse {
    recommendationId: number;
    answer: string;
    reference: PolicyReference[];
}
