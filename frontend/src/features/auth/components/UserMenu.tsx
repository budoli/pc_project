import { Button } from "@/shared/ui";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";

type UserMenuProps = {
    name: string;
    role: "USER" | "ADMIN";
    onLogout: () => void;
};

const UserMenu = ({ name, role, onLogout }: UserMenuProps) => {
    const [open, setOpen] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
                setOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    return (
        <div className="relative" ref={menuRef}>
            {open && (
                <div className="absolute bottom-16 left-0 w-full rounded-xl border border-gray-200 bg-white p-2 shadow-xl">
                    <div className="border-b px-3 py-3">
                        <p className="font-semibold">{name}</p>
                        <p className="sm text-gray-500">
                            {role === "ADMIN" ? "관리자" : "일반 사용자"}
                        </p>
                    </div>

                    {role === "ADMIN" && (
                        <button
                            onClick={() => {
                                navigate("/admin");
                                setOpen(false);
                            }}
                            className="sm mt-2 w-full rounded-lg px-3 py-3 text-left hover:bg-gray-100"
                        >
                            관리자 페이지
                        </button>
                    )}

                    <button
                        onClick={() => {
                            navigate("/mypage");
                            setOpen(false);
                        }}
                        className="sm w-full rounded-lg px-3 py-3 text-left hover:bg-gray-100"
                    >
                        내 정보
                    </button>

                    <button
                        onClick={() => {
                            onLogout();
                            setOpen(false);
                        }}
                        className="sm w-full rounded-lg px-3 py-3 text-left text-red-500 hover:bg-red-50"
                    >
                        로그아웃
                    </button>
                </div>
            )}

            <Button
                variant="ghost"
                size="menu"
                onClick={() => setOpen((prev) => !prev)}
                className="flex items-center justify-between"
            >
                <span className="font-medium">{name}</span>
                <span className="text-gray-400">⌄</span>
            </Button>
        </div>
    );
};

export default UserMenu;
