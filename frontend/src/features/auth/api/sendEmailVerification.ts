export interface sendEmailVerificationRequest  {
  email: string;
}

export const sendEmailVerification = async (body : sendEmailVerificationRequest): Promise<void> => {
    const response = await fetch("http://localhost:8080/api/v1/auth/email/send", {
      method: "POST",
      headers: {
        "Content-Type" : "application/json"
      },
      credentials: "include",
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      throw new Error("이메일 이능 요청에 실패했습니다.");
    }

};