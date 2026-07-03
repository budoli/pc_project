import type { LoginResponse } from "../types/LoginResponse";

export const reissue = async (): Promise<LoginResponse> => {
    const response = await fetch("http://localhost:8080/api/v1/auth/reissue", {
      method: "POST",
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("토큰 재발급 실패했습니다.");
    }

    return response.json();

};