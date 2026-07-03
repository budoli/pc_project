export interface LoginRequest  {
  email: string;
  password :string;
}

export interface LoginResponse  {
  accessToken: string;
  refreshToken: string;
}

export const login = async (body : LoginRequest): Promise<LoginResponse> => {
    const response = await fetch("http://localhost:8080/api/v1/auth/login", {
      method: "POST",
      headers: {
        "Content-Type" : "application/json"
      },
      credentials: "include",
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      throw new Error("로그인에 실패했습니다.");
    }

    return response.json();

};