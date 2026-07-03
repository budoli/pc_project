import { create } from "zustand";

type SignupForm = {
  email: string;
  password: string;
  name: string;
};

type SignupStore = {
  signupForm: SignupForm;
  setSignupForm: (form: SignupForm) => void;
  clearSignupForm: () => void;
};

export const useSignupStore = create<SignupStore>((set) => ({
  signupForm: {
    email: "",
    password: "",
    name: "",
  },

  setSignupForm: (form) => set({ signupForm: form }),

  clearSignupForm: () =>
    set({
      signupForm: {
        email: "",
        password: "",
        name: "",
      },
    }),
}));