import { api } from "@/shared/api/axios";
import type { ConversationResponse } from "../types/ConversationResponse";
import type { ChatMessageResponse } from "../types/ChatMessageResponse";
import type { SendConversationMessageResponse } from "../types/SendConversationMessageResponse";
import type { CreateConversationResponse } from "../types/CreateConversationResponse";

// 대화 생성과 첫 메시지 전송을 한 번에 처리한다. AI가 실패하면 대화도 만들어지지 않는다
export const createConversation = async (firstMessage: string) => {
  const response = await api.post<CreateConversationResponse>(
    "/api/v1/conversations",
    { firstMessage }
  );

  return response.data;
};

export const getConversations = async () => {
  const response = await api.get<ConversationResponse[]>(
    "/api/v1/conversations"
  );

  return response.data;
};

export const deleteConversation = async (conversationId: number) => {
  await api.delete(`/api/v1/conversations/${conversationId}`);
};

export const getConversationMessages = async (conversationId: number) => {
  const response = await api.get<ChatMessageResponse[]>(
    `/api/v1/conversations/${conversationId}/messages`
  );

  return response.data;
};

export const sendConversationMessage = async (
  conversationId: number,
  message: string
) => {
  const response = await api.post<SendConversationMessageResponse>(
    `/api/v1/conversations/${conversationId}/messages`,
    { message }
  );

  return response.data;
};