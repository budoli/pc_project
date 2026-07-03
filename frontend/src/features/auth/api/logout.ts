export const logout = async (): Promise<void> => {
  const response = await fetch("http://localhost:8080/api/v1/auth/logout", {
    method: "POST",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("로그아웃 실패");
  }
}