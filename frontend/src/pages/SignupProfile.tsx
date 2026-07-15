import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, DateInput, Select } from "@/shared/ui";
import { signup } from "@/features/auth/api/signup";
import { useSignupStore } from "@/features/auth/store/signupStore";
import {
  GENDER_OPTIONS,
  HOUSEHOLD_TYPE_OPTIONS,
  INCOME_LEVEL_OPTIONS,
  INTEREST_OPTIONS,
  JOB_STATUS_OPTIONS,
} from "@/features/auth/constants/signupOptions";
import { REGION_OPTIONS } from "@/shared/constants/regions";

const SignupProfilePage = () => {
  const signupForm = useSignupStore((state) => state.signupForm);
  const clearSignupForm = useSignupStore((state) => state.clearSignupForm);

  const navigate = useNavigate();

  const [form, setForm] = useState({
    birthDate: "",
    gender: "",
    region: "",
    jobStatus: "",
    incomeLevel: "",
    householdType: "",
    interests: [] as string[],
  });

  useEffect(() => {
    if (!signupForm.email || !signupForm.password || !signupForm.name) {
      alert("회원가입 정보를 먼저 입력해주세요.");
      navigate("/signup");
    }
  }, [signupForm, navigate]);

  const handleChange = (key: keyof typeof form, value: string) => {
    setForm((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  const toggleInterest = (interest: string) => {
    setForm((prev) => ({
      ...prev,
      interests: prev.interests.includes(interest)
        ? prev.interests.filter((item) => item !== interest)
        : [...prev.interests, interest],
    }));
  };

  const handleSubmit = async () => {
    const requestBody = {
      ...signupForm,
      ...form,
      interests: form.interests.join(","),
    };

    try {
      await signup(requestBody);
      alert("회원가입이 완료되었습니다.");
      clearSignupForm();
      navigate("/");
    } catch (error) {
      console.log(error);
      alert("회원가입에 실패했습니다.");
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-10">
      <div className="w-full max-w-xl rounded-2xl bg-white p-8 shadow-sm">
        <div className="mb-8">
          <h1 className="xl2">추가 정보 입력</h1>
          <p className="sm mt-2 text-gray-500">
            더 정확한 정책 추천을 위해 정보를 입력해주세요.
          </p>
        </div>

        <div className="space-y-5">
          <DateInput
            label="생년월일"
            value={form.birthDate}
            onChange={(e) => handleChange("birthDate", e.target.value)}
          />

          <Select
            label="성별"
            value={form.gender}
            onChange={(e) => handleChange("gender", e.target.value)}
            options={GENDER_OPTIONS}
          />

          <Select
            label="지역"
            value={form.region}
            onChange={(e) => handleChange("region", e.target.value)}
            options={REGION_OPTIONS}
          />

          <Select
            label="직업상태"
            value={form.jobStatus}
            onChange={(e) => handleChange("jobStatus", e.target.value)}
            options={JOB_STATUS_OPTIONS}
          />

          <Select
            label="소득"
            value={form.incomeLevel}
            onChange={(e) => handleChange("incomeLevel", e.target.value)}
            options={INCOME_LEVEL_OPTIONS}
          />

          <Select
            label="거주"
            value={form.householdType}
            onChange={(e) => handleChange("householdType", e.target.value)}
            options={HOUSEHOLD_TYPE_OPTIONS}
          />

          <div>
            <p className="sm mb-2 font-medium text-gray-700">관심 분야</p>
            <div className="flex flex-wrap gap-2">
              {INTEREST_OPTIONS.map((interest) => {
                const selected = form.interests.includes(interest);

                return (
                  <button
                    key={interest}
                    type="button"
                    onClick={() => toggleInterest(interest)}
                    className={`sm rounded-full border px-4 py-2 ${
                      selected
                        ? "border-black bg-black text-white"
                        : "border-gray-300 bg-white text-gray-700"
                    }`}
                  >
                    {interest}
                  </button>
                );
              })}
            </div>
          </div>

          <Button onClick={handleSubmit} className="w-full">
            시작하기
          </Button>

          <button
            type="button"
            onClick={() => navigate("/")}
            className="sm w-full text-gray-500 hover:text-black"
          >
            나중에 입력하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default SignupProfilePage;
