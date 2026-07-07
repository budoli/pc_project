import { useState } from "react";

interface ChatInputProps {
    onSend: (message: string) => void;
}

const ChatInput = ({ onSend }: ChatInputProps) => {
    const [message, setMessage] = useState("");

    const handleSubmit = () => {
        if (!message.trim()) return;

        onSend(message);
        setMessage("");
    };

    return (
        <div className="flex h-16 items-center rounded-2xl bg-white px-5 shadow">
            <button className="mr-3 text-lg">
                📎
            </button>

            <input
                className="flex-1 bg-transparent outline-none"
                placeholder="궁금한 내용을 입력하세요."
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                onKeyDown={(e) => {
                    if (e.key === "Enter") handleSubmit();
                }}
            />

            <button
                onClick={handleSubmit}
                className="text-lg"
            >
                ➤
            </button>
        </div>
    );
};

export default ChatInput;