import { api } from "@/shared/api/axios";

export const reissue = async (): Promise<void> => {
    await api.post("/api/v1/auth/reissue");
};
