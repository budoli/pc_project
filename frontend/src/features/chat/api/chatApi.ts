import { api } from "@/shared/api/axios";
import type { ConversationResponse } from "../types/ConversationResponse";
import type { ChatMessageResponse } from "../types/ChatMessageResponse";
import type { SendConversationMessageResponse } from "../types/SendConversationMessageResponse";

export const createConversation = async (firstMessage?: string) => {
  const response = await api.post<ConversationResponse>(
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