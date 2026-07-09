import { api } from "@/shared/api/axios";
import type { PolicyDetail } from "../types/PolicyDetail";

export const getPolicyDetail = async (policyId: number) => {
  const response = await api.get<PolicyDetail>(`/api/v1/policies/${policyId}`);
  return response.data;
};