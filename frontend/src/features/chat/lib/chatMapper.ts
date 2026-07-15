import type { ChatMessageResponse } from "../types/ChatMessageResponse";
import type { ChatMessage } from "../types/ChatMessage";

export const toChatMessage = (
  message: ChatMessageResponse,
  id: string | number = message.messageId
): ChatMessage => ({
  id: String(id),
  role: message.role === "USER" ? "user" : "assistant",
  content: message.content,
  policies: message.references?.map((ref) => ({
    id: ref.policyId,
    title: ref.title,
    description: null,
    organization: null,
    category: null,
  })),
});
