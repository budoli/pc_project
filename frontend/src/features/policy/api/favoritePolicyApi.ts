import { api } from "@/shared/api/axios";

export type FavoritePolicy = { id: number; title: string };

export const getFavoritePolicies = async (): Promise<FavoritePolicy[]> => {
  const response = await api.get<FavoritePolicy[]>("/api/v1/policies/favorites");
  return response.data;
};

export const addFavoritePolicy = (policyId: number) =>
  api.post(`/api/v1/policies/favorites/${policyId}`);

export const removeFavoritePolicy = (policyId: number) =>
  api.delete(`/api/v1/policies/favorites/${policyId}`);
