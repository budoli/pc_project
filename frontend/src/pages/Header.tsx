import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Logo } from "@/assets";
import LoginModal from "@/features/auth/LoginModal";

const Header = () => {
    const [isLoginOpen, setIsLoginOpen] = useState(false);
    const navigate = useNavigate();

    return (
        <>
            <header className="h-16 w-full px-8"> {/*border-b border-gray-200 bg-white/80*/}
                <div className="flex h-full w-full items-center justify-between">
                    <div className="flex h-9 w-9 shrink-0 items-center justify-center overflow-hidden">
                        <img
                            src={Logo}
                            alt="로고"
                            className="block h-9 w-9 object-contain"
                        />
                    </div>

                    <div className="flex gap-3">
                        <button 
                        onClick={() => navigate("/signup")}
                        className="rounded-md bg-white px-4 py-2 sm hover:bg-gray-100">
                            회원가입
                        </button>
                        <button
                            onClick={() => setIsLoginOpen(true)}
                            className="rounded-md bg-black px-4 py-2 sm text-white hover:bg-gray-800"
                        >
                            로그인
                        </button>
                    </div>
                </div>
            </header>

            {isLoginOpen && (
                <LoginModal onClose={() => setIsLoginOpen(false)} />
            )}
        </>
    );
};

export default Header;
