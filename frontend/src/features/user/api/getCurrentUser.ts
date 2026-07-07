import { type User } from "@/features/user/types/User";
import { api } from "@/shared/api/axios";

export const getCurrentUser = async (): Promise<User> => {
    const response = await api.get("/api/v1/user/me");
    return response.data;
};