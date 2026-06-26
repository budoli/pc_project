export interface SignupRequest  {
  email: string;
  password :string;
  name:string;
  birthDate:string;
  gender:string;
  region:string;
  jobStatus:string;
}

export const signup = async (body : SignupRequest): Promise<void> => {
    const response = await fetch("http://localhost:8080/api/v1/auth/signup", {
      method: "POST",
      headers: {
        "Content-Type" : "application/json"
      },
      credentials: "include",
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      throw new Error("회원가입에 실패했습니다.");
    }

};