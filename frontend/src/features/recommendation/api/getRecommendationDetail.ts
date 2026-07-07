import { api } from "@/shared/api/axios";

export const getRecommendationDetail = async (id:number) => {
    const response = await api.get(`/api/v1/recommendations/${id}`);
    return response.data;
}