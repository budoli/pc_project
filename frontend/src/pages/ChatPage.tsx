import ChatInput from "@/shared/ui/Input/ChatInput";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import type { PolicyReference } from "@/features/recommendation/types";
import type { ChatMessageResponse } from "@/features/chat/types/ChatMessageResponse";
import { createConversation, getConversationMessages, sendConversationMessage } from "@/features/chat/api/chatApi";
import ChatMessageItem from "@/features/chat/components/ChatMessageItem";

interface ChatMessage {
    role: "user" | "assistant";
    content: string;
    references?: PolicyReference[];
}

const toChatMessage = (message: ChatMessageResponse): ChatMessage => ({
    role: message.role === "USER" ? "user" : "assistant",
    content: message.content,
    references: message.references,
});

const ChatPage = () => {
    const { conversationId } = useParams();
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const query = searchParams.get("q");

    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [loading, setLoading] = useState(false);
    const initializedRef = useRef(false);

    const currentConversationId = conversationId
        ? Number(conversationId)
        : null;

    const handleSend = async (message: string) => {
        const optimisticUserMessage: ChatMessage = {
            role: "user",
            content: message,
        };

        setMessages((prev) => [...prev, optimisticUserMessage]);
        setLoading(true);

        try {
            let activeConversationId = currentConversationId;

            if (!activeConversationId) {
                const conversation = await createConversation(message);
                activeConversationId = conversation.conversationId;
            }

            const result = await sendConversationMessage(
                activeConversationId,
                message
            );

            if (!currentConversationId) {
                navigate(`/chat/${activeConversationId}`, {
                    replace: true,
                });
            }

            setMessages((prev) => [
                ...prev,
                toChatMessage(result.assistantMessage),
            ]);
        } catch (error) {
            setMessages((prev) => [
                ...prev,
                {
                    role: "assistant",
                    content: "답변을 생성하지 못했습니다. 잠시 후 다시 시도해주세요.",
                },
            ]);
        } finally {
            setLoading(false);
        }
    };

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
    }, [query]);

    return (
        <div className="flex h-full justify-center px-6 py-10">
            <div className="flex h-full w-full max-w-[800px] flex-col">
                <div className="flex-1 space-y-4 overflow-y-auto pb-6">
                    {messages.map((message, index) => (
                        <ChatMessageItem
                            key={index}
                            message={{
                                id: String(index),
                                role: message.role,
                                content: message.content,
                                policies: message.references?.map((ref) => ({
                                    id: ref.policyId,
                                    title: ref.title,
                                    description: null,
                                    organization: null,
                                    category: null,
                                })),
                            }}
                        />
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
    )
}

export default ChatPage