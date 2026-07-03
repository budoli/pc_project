import type { User } from "@/features/user/types/User";

export const getCurrentUser = async (accessToken : string): Promise<User> => {
    const response = await fetch("http://localhost:8080/api/v1/user/me", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
    });

    if (!response.ok) {
      throw new Error("사용자 정보 조회 실패.");
    }

    return response.json();

};