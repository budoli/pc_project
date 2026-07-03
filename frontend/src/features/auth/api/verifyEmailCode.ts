type VerifyEmailCodeRequest = {
  email: string;
  code: string;
};

export const verifyEmailCode = async (
  body: VerifyEmailCodeRequest
): Promise<void> => {
  const response = await fetch(
    "http://localhost:8080/api/v1/auth/email/verify",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    }
  );

  if (!response.ok) {
    throw new Error("이메일 인증에 실패했습니다.");
  }
};