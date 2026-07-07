import { api } from "@/shared/api/axios";

export const requestRecommendation = async (query: string) => {
    const response = await api.post(
        "/api/v1/recommendations",
        { query }
    );

    return response.data;

};