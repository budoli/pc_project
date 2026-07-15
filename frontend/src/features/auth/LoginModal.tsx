import { Logo } from "@/assets";
import { Button, Input } from "@/shared/ui"
import { Link } from "react-router-dom";
import { useState } from "react";
import { login } from "@/features/auth/api/login";
import { useAuthStore } from "@/features/auth/store/authStore";
import { getCurrentUser } from "../user/api/getCurrentUser";

type Props = {
  onClose: () => void;
}


const LoginModal = ({ onClose }: Props) => {
  const [email, setEmail] = useState("user1@user.com");
  const [password, setPassword] = useState("user1234");
  const { setUser } = useAuthStore();

  const handleLogin = async () => {
    await login({
      email,
      password,
    });

    const user = await getCurrentUser();

    setUser(user);

    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="relative flex h-[486px] w-[698px] rounded-lg bg-white shadow-xl">
        <button
          onClick={onClose}
          className="absolute right-5 top-5 text-2xl"
        >
          x
        </button>

        <div className="flex w-1/2 flex-col items-center justify-center border-r">
          <div className="flex flex-col items-center">
            <div>
              <img src={Logo} alt="로고" className="h-24 w-24"></img>
            </div>
            <div className="mt-8 flex flex-col items-center text-center">
              <div className="lg font-semibold">로그인하여 맞춤 정책을 확인하세요</div>
              <p className="base text-gray-500">AI가 나에게 맞는 청년 지원 정책을 추천해드립니다.</p>
            </div>
          </div>
        </div>

        <div className="flex w-1/2 items-center justify-center px-8">
          <div className="flex w-full max-w-[300px] flex-col gap-3">
            <Input
              label="이메일"
              type="email"
              placeholder="이메일을 입력하세요"
              icon="@"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <Input
              label="비밀번호"
              type="password"
              placeholder="비밀번호를 입력하세요"
              icon="="
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />


            <Button variant="primary" onClick={handleLogin}>로그인</Button>
            <div className="flex items-center gap-4">
              <div className="h-px flex-1 bg-gray-300" />
              <span className="sm text-gray-400">또는</span>
              <div className="h-px flex-1 bg-gray-300" />
            </div>
            <Button variant="google">Google로 계속하기</Button>
            <Button variant="kakao">카카오로 시작하기</Button>
            <div className="sm mt-1 text-center text-gray-500">
              계정이 없으신가요? {" "}
              <Link
                to="/signup"
                className="sm text-gray-900 transition-colors hover:text-blue-600"
              >
                회원가입
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LoginModal
