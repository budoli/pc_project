import { api } from "@/shared/api/axios";
import type { RecommendationHistory } from "../types/RecommendationHistory ";

export const getRecommendationHistory = async (): Promise<RecommendationHistory[]> => {
    const response = await api.get("/api/v1/recommendations");
    return response.data;
}