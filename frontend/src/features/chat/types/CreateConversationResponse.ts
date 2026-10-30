import type { ChatMessageResponse } from "./ChatMessageResponse";
import type { ConversationResponse } from "./ConversationResponse";

export interface CreateConversationResponse {
  conversation: ConversationResponse;
  userMessage: ChatMessageResponse;
  assistantMessage: ChatMessageResponse;
}
