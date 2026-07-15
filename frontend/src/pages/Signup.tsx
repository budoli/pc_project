import { Link, useNavigate } from "react-router-dom";
import { Logo } from "../assets";
import { Input, Button } from "@/shared/ui";
import { useEffect, useState } from "react";
import InputGroup from "@/shared/ui/Input/InputGroup";
import { sendEmailVerification } from "@/features/auth/api/sendEmailVerification";
import { useSignupStore } from "@/features/auth/store/signupStore";
import { verifyEmailCode } from "@/features/auth/api/verifyEmailCode";

const SignupPage = () => {
    const [form, setForm] = useState({
        email: "",
        password: "",
        passwordConfirm: "",
        name: "",
    });

    const [emailCode, setEmailCode] = useState("");
    const [isEmailSent, setIsEmailSent] = useState(false);
    const [isEmailVerified, setIsEmailVerified] = useState(false);
    const [remainingTime, setRemainingTime] = useState(0);

    const navigate = useNavigate();
    const setSignupForm = useSignupStore((state) => state.setSignupForm);

    useEffect(() => {
        if (!isEmailSent || isEmailVerified || remainingTime <= 0) return;

        const timer = setInterval(() => {
            setRemainingTime((prev) => prev - 1);
        }, 1000);

        return () => clearInterval(timer);
    }, [isEmailSent, isEmailVerified, remainingTime]);

    const formatTime = (seconds: number) => {
        const min = Math.floor(seconds / 60);
        const sec = seconds % 60;

        return `${min}:${String(sec).padStart(2, "0")}`;
    }


    const handleEmailVerify = async () => {
        if (!form.email) {
            alert("이메일을 입력해주세요.");
            return;
        }

        try {
            await sendEmailVerification({
                email: form.email,
            });

            setIsEmailSent(true);
            setIsEmailVerified(false);
            setRemainingTime(180);

            alert("인증번호가 발송되었습니다.");
        } catch (error) {
            console.log(error);
            alert("이메일 인증 요청에 실패했습니다.");
        }
    };

    const handleVerifyEmailCode = async () => {
        if (!emailCode.trim()) {
            alert("인증번호를 입력해주세요");
            return;
        }

        if (remainingTime <= 0) {
            alert("인증 시간이 만료되었습니다.");
            return;
        }

        try {
            await verifyEmailCode({
                email: form.email.trim(),
                code: emailCode.trim(),
            })
            
            setIsEmailVerified(true);
            setRemainingTime(0);

            alert("이메일 인증이 완료되었습니다.");
        } catch (error) {
            console.log(error);
            alert("인증번호가 올바르지 않습니다.");
        }
    }

    const handlerSignup = () => {
        if (!isEmailVerified) {
            alert("이메일 인증을 완료해주세요.");
            return;
        }

        if (form.password !== form.passwordConfirm) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }

        setSignupForm({
            email: form.email,
            password: form.password,
            name: form.name,
        });

        navigate("/signup/profile");
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-10">
            <div className="w-full max-w-[520px] rounded-2xl border border-gray-200 bg-white px-10 py-9 shadow-lg">
                <div className="mb-8 flex flex-col items-center text-center">
                    <img src={Logo} alt="로고" className="mb-4 h-20 w-20" />

                    <h1 className="xl2">
                        회원가입
                    </h1>

                    <p className="sm mt-3 text-gray-500">
                        정확한 정보를 입력하시면 더 정확한 추천이 가능합니다.
                    </p>
                </div>

                <div className="flex flex-col gap-4">
                    <InputGroup
                        label="이메일"
                        type="email"
                        placeholder="이메일을 입력하세요"
                        icon="@"
                        value={form.email}
                        onChange={(e) =>
                            setForm({ ...form, email: e.target.value })
                        }
                        button={
                            <Button
                                type="button"
                                size="input"
                                variant="primary"
                                onClick={handleEmailVerify}
                            >
                                인증하기
                            </Button>
                        }
                    />
                    {isEmailSent && (
                        <InputGroup
                            label="인증번호"
                            placeholder="인증번호 6자리를 입력하세요"
                            value={emailCode}
                            onChange={(e) => setEmailCode(e.target.value)}
                            button={
                                <Button
                                    type="button"
                                    size="input"
                                    onClick={handleVerifyEmailCode}
                                    disabled={isEmailVerified || remainingTime <= 0}
                                >
                                    확인
                                </Button>
                            }
                            helperText={
                                isEmailVerified ? (
                                    <span className="text-green-600">
                                        이메일 인증이 완료되었습니다.
                                    </span>
                                ) : remainingTime > 0 ? (
                                    <>남은 시간 {formatTime(remainingTime)}</>
                                ) : (
                                    <span className="text-red-500">
                                        인증 시간이 만료되었습니다.
                                    </span>
                                )
                            }
                        />
                    )}
                    <Input
                        label="비밀번호"
                        type="password"
                        placeholder="비밀번호를 입력하세요"
                        icon="="
                        onChange={(e) =>
                            setForm({ ...form, password: e.target.value })
                        }
                    />

                    <Input
                        label="비밀번호 확인"
                        type="password"
                        placeholder="비밀번호를 다시 입력해주세요"
                        icon="="
                        onChange={(e) =>
                            setForm({
                                ...form,
                                passwordConfirm: e.target.value,
                            })
                        }
                    />

                    <Input
                        label="이름"
                        type="text"
                        placeholder="이름을 입력해주세요"
                        icon="👤"
                        onChange={(e) =>
                            setForm({ ...form, name: e.target.value })
                        }
                    />

                    <Button onClick={handlerSignup} className="w-full">
                        다음
                    </Button>
                </div>

                <div className="my-4 flex items-center gap-4">
                    <div className="h-px flex-1 bg-gray-300" />
                    <span className="sm text-gray-400">또는</span>
                    <div className="h-px flex-1 bg-gray-300" />
                </div>

                <div className="sm text-center text-gray-500">
                    이미 계정이 있으신가요?{" "}
                    <Link
                        to="/"
                        className="font-medium text-gray-900 transition-colors hover:text-blue-600"
                    >
                        로그인
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default SignupPage;
