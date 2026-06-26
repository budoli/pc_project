import { Link } from "react-router-dom";
import { Logo } from "../assets";
import { Input, DateInput, Button, Select } from "@/shared/ui"
import { useState } from "react";
import { signup } from "@/features/auth/api/signup";

const SignupPage = () => {
  const [form, setForm] = useState({
    email: "",
    password: "",
    passwordConfirm: "",
    name: "",
    birthDate: "",
    gender: "",
    region: "",
    jobStatus: "",
  });

  const handlerSignup = async () => {
    if (form.password !== form.passwordConfirm) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    const { passwordConfirm, ...signupRequest } = form;


    try {
      await signup(signupRequest);
      alert("회원가입이 완료되었습니다.");
    } catch (error) {
      console.log(error);
      alert("회원가입에 실패했습니다.");
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-10">
      <div className="w-full max-w-[520px] rounded-2xl border border-gray-200 bg-white px-10 py-9 shadow-lg">
        <div className="mb-8 flex flex-col items-center text-center">
          <img src={Logo} alt="로고" className="mb-4 h-20 w-20" />

          <h1 className="text-2xl font-bold leading-[140%] tracking-[-0.025em]">
            회원가입
          </h1>

          <p className="mt-3 text-sm leading-[140%] tracking-[-0.025em] text-gray-500">
            정확한 정보를 입력하시면 더 정확한 추천이 가능합니다.
          </p>
        </div>

        <div className="flex flex-col gap-4">
          <Input
            label="이메일"
            type="email"
            placeholder="이메일을 입력하세요"
            icon="@"
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />

          <Input
            label="비밀번호"
            type="password"
            placeholder="비밀번호를 입력하세요"
            icon="="
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />

          <Input
            label="비밀번호 확인"
            type="password"
            placeholder="비밀번호를 다시 입력해주세요"
            icon="="
            onChange={(e) => setForm({ ...form, passwordConfirm: e.target.value })}
          />

          <Input
            label="이름"
            type="text"
            placeholder="이름을 입력해주세요"
            icon="👤"
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />

          <div className="grid grid-cols-2 gap-4">
            <DateInput
              label="생년월일"
              value={form.birthDate}
              onChange={(e) => setForm({ ...form, birthDate: e.target.value })}
            />

            <Select
              label="성별"
              options={[
                { label: "선택해주세요", value: "" },
                { label: "남성", value: "MALE" },
                { label: "여성", value: "FEMALE" },
              ]}
              onChange={(e) => setForm({ ...form, gender: e.target.value })}
            />

            <Select
              label="거주 지역"
              options={[
                { label: "선택해주세요", value: "" },
                { label: "서울", value: "SEOUL" },
                { label: "경기", value: "GYEONGGI" },
                { label: "인청", value: "INCHEON" },
              ]}
              onChange={(e) => setForm({ ...form, region: e.target.value })}
            />

            <Select
              label="직업"
              options={[
                { label: "선택해주세요", value: "" },
                { label: "학생", value: "STUDENT" },
                { label: "취업준비생", value: "JOB_SEEKER" },
                { label: "직장인", value: "WORKER" },
                { label: "무직", value: "NONE" },
              ]}
              onChange={(e) => setForm({ ...form, jobStatus: e.target.value })}
            />
          </div>

          <Button className="mt-3" variant="primary" onClick={handlerSignup}>
            회원가입
          </Button>
        </div>

        <div className="my-4 flex items-center gap-4">
          <div className="h-px flex-1 bg-gray-300" />
          <span className="sm text-gray-400">또는</span>
          <div className="h-px flex-1 bg-gray-300" />
        </div>

        <div className="text-center sm text-gray-500">
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