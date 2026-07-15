export interface PolicyReference {
    policyId: number;
    title: string;
    score: number;
}

export interface RecommendationResponse {
    recommendationId: number;
    answer: string;
    reference: PolicyReference[];
}
