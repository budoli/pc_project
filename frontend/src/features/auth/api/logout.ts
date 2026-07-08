import { api } from "@/shared/api/axios";

export const logout = async (): Promise<void> => {
  await api.post("/api/v1/auth/logout");
}