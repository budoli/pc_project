import type { PolicyReference } from "@/features/recommendation/types";

export interface ChatMessageResponse {
  messageId: number;
  role: "USER" | "ASSISTANT";
  content: string;
  references: PolicyReference[];
  createdAt: string;
}