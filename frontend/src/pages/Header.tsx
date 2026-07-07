import { useState } from "react";
import { useNavigate } from "react-router-dom";
import LoginModal from "@/features/auth/LoginModal";
import { useAuthStore } from "@/features/auth/store/authStore";

const Header = () => {
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const navigate = useNavigate();

  const {user} = useAuthStore();
  const isLogin = !!user;

  return (
    <>
      <header className="h-16 w-full px-8">
        <div className="flex h-full items-center justify-end">
          {!isLogin && (
            <div className="flex gap-3">
              <button
                onClick={() => navigate("/signup")}
                className="rounded-md bg-white px-4 py-2 hover:bg-gray-100"
              >
                회원가입
              </button>

              <button
                onClick={() => setIsLoginOpen(true)}
                className="rounded-md bg-black px-4 py-2 text-white hover:bg-gray-800"
              >
                로그인
              </button>
            </div>
          )}
        </div>
      </header>

      {isLoginOpen && !isLogin && (
        <LoginModal onClose={() => setIsLoginOpen(false)} />
      )}
    </>
  );
};

export default Header;