import type { ChatMessageResponse } from "./ChatMessageResponse";

export interface SendConversationMessageResponse {
  userMessage: ChatMessageResponse;
  assistantMessage: ChatMessageResponse;
}