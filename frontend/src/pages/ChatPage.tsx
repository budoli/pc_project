import ChatInput from "@/shared/ui/Input/ChatInput";
import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import {
  createConversation,
  getConversationMessages,
  sendConversationMessage,
} from "@/features/chat/api/chatApi";
import ChatMessageItem from "@/features/chat/components/ChatMessageItem";
import type { ChatMessage } from "@/features/chat/types/ChatMessage";
import { toChatMessage } from "@/features/chat/lib/chatMapper";

const ChatPage = () => {
  const { conversationId } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const query = searchParams.get("q");

  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState(false);
  const initializedRef = useRef(false);

  const currentConversationId = conversationId ? Number(conversationId) : null;

  const handleSend = useCallback(async (message: string) => {
    const optimisticUserMessage: ChatMessage = {
      id: `user-${Date.now()}`,
      role: "user",
      content: message,
    };

    setMessages((prev) => [...prev, optimisticUserMessage]);
    setLoading(true);

    try {
      if (!currentConversationId) {
        const result = await createConversation(message);

        navigate(`/chat/${result.conversation.conversationId}`, {
          replace: true,
        });
        setMessages((prev) => [...prev, toChatMessage(result.assistantMessage)]);
        return;
      }

      const result = await sendConversationMessage(
        currentConversationId,
        message,
      );

      setMessages((prev) => [...prev, toChatMessage(result.assistantMessage)]);
    } catch {
      setMessages((prev) => [
        ...prev,
        {
          id: `assistant-error-${Date.now()}`,
          role: "assistant",
          content: "답변을 생성하지 못했습니다. 잠시 후 다시 시도해주세요.",
        },
      ]);
    } finally {
      setLoading(false);
    }
  }, [currentConversationId, navigate]);

  useEffect(() => {
    if (!currentConversationId) return;

    const fetchMessages = async () => {
      setLoading(true);

      try {
        const result = await getConversationMessages(currentConversationId);
        setMessages(result.map(toChatMessage));
      } finally {
        setLoading(false);
      }
    };

    fetchMessages();
  }, [currentConversationId]);

  useEffect(() => {
    if (!query || initializedRef.current) return;

    initializedRef.current = true;
    handleSend(query);
  }, [query, handleSend]);

  return (
    <div className="flex h-full justify-center px-6 py-10">
      <div className="flex h-full w-full max-w-[800px] flex-col">
        <div className="flex-1 space-y-4 overflow-y-auto pb-6">
          {messages.map((message) => (
            <ChatMessageItem key={message.id} message={message} />
          ))}

          {loading && (
            <div className="mr-auto max-w-[70%] rounded-2xl bg-gray-100 px-4 py-3 text-gray-500">
              답변을 생성하고 있습니다...
            </div>
          )}
        </div>

        <ChatInput onSend={handleSend} />
      </div>
    </div>
  );
};

export default ChatPage;
