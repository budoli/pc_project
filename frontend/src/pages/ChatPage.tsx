import { requestRecommendation } from "@/features/recommendation/api/recommendationApi";
import ChatInput from "@/shared/ui/Input/ChatInput";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";


interface ChatMessage {
    role: "user" | "assistant";
    content: string
}

const ChatPage = () => {
    const [searchParams] = useSearchParams();

    const query = searchParams.get("q");

    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [loading, setLoading] = useState(false);

    const handleSend = async (message: string) => {
        setMessages((prev) => [
            ...prev,
            {
                role: "user",
                content: message
            },
        ]);

        setLoading(true);

        try {
            const result = await requestRecommendation(message);

            setMessages((prev) => [
                ...prev,
                { role: "assistant", content: result.answer },
            ])
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (query) {
            handleSend(query);
        }
    }, []);

    return (
        <div className="flex h-full justify-center px-6 py-10">
            <div className="flex h-full w-full max-w-[800px] flex-col">
                <div className="flex-1 space-y-4 overflow-y-auto pb-6">
                    {messages.map((message, index) => (
                        <div
                            key={index}
                            className={
                                message.role === "user"
                                    ? "ml-auto max-w-[70%] rounded-2xl bg-black px-4 py-3 text-white"
                                    : "mr-auto max-w-[70%] rounded-2xl bg-gray-100 px-4 py-3 text-gray-900"
                            }
                        >
                            {message.content}
                        </div>
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