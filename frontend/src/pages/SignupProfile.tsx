import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, DateInput, Select } from "@/shared/ui";
import { signup } from "@/features/auth/api/signup";
import { useSignupStore } from "@/features/auth/store/signupStore";

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

  const interests = ["취업", "주거", "창업", "교육", "복지", "금융"];

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-10">
      <div className="w-full max-w-xl rounded-2xl bg-white p-8 shadow-sm">
        <div className="mb-8">
          <h1 className="text-2xl font-bold">추가 정보 입력</h1>
          <p className="mt-2 text-sm text-gray-500">
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
            options={[
              { label: "선택", value: "" },
              { label: "남성", value: "M" },
              { label: "여성", value: "F" },
            ]}
          />

          <Select
            label="지역"
            value={form.region}
            onChange={(e) => handleChange("region", e.target.value)}
            options={[
              { label: "선택", value: "" },
              { label: "서울", value: "서울" },
              { label: "경기", value: "경기" },
              { label: "부산", value: "부산" },
              { label: "대구", value: "대구" },
              { label: "인천", value: "인천" },
            ]}
          />

          <Select
            label="직업상태"
            value={form.jobStatus}
            onChange={(e) => handleChange("jobStatus", e.target.value)}
            options={[
              { label: "선택", value: "" },
              { label: "학생", value: "STUDENT" },
              { label: "취업준비생", value: "JOB_SEEKER" },
              { label: "직장인", value: "EMPLOYED" },
              { label: "자영업자", value: "SELF_EMPLOYED" },
              { label: "프리랜서", value: "FREELANCER" },
              { label: "미취업", value: "UNEMPLOYED" },
              { label: "퇴직", value: "RETIRED" },
              { label: "기타", value: "ETC" },
            ]}
          />

          <Select
            label="소득"
            value={form.incomeLevel}
            onChange={(e) => handleChange("incomeLevel", e.target.value)}
            options={[
              { label: "선택", value: "" },
              { label: "저소득", value: "LOW" },
              { label: "중간", value: "MIDDLE" },
              { label: "고소득", value: "HIGH" },
              { label: "모름", value: "UNKNOWN" },
            ]}
          />

          <Select
            label="거주"
            value={form.householdType}
            onChange={(e) => handleChange("householdType", e.target.value)}
            options={[
              { label: "선택", value: "" },
              { label: "1인가구", value: "SINGLE" },
              { label: "부부", value: "COUPLE " },
              { label: "자녀 있는 가구", value: "FAMILY_WITH_CHILDREN"},
              { label: "한부모 가구", value: "SINGLE_PARENT " },
              { label: "다인 가구", value: "MULTI_CHILD" },
              { label: "기타", value: "ETC" },
            ]}
          />

          <div>
            <p className="mb-2 text-sm font-medium text-gray-700">관심 분야</p>
            <div className="flex flex-wrap gap-2">
              {interests.map((interest) => {
                const selected = form.interests.includes(interest);

                return (
                  <button
                    key={interest}
                    type="button"
                    onClick={() => toggleInterest(interest)}
                    className={`rounded-full border px-4 py-2 text-sm ${
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
            className="w-full text-sm text-gray-500 hover:text-black"
          >
            나중에 입력하기
          </button>
        </div>
      </div>
    </div>
  );
};

export default SignupProfilePage;