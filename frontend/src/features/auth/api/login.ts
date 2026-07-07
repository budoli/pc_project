import { api } from "@/shared/api/axios";
import { type LoginRequest } from "../types/loginRequest";

export const login = async (request: LoginRequest) => {
  await api.post("/api/v1/auth/login", request);

};