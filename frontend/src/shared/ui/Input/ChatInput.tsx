import { useState } from "react";

interface ChatInputProps {
    onSend: (message: string) => void;
}

const ChatInput = ({ onSend }: ChatInputProps) => {
    const [message, setMessage] = useState("");

    const trimmedMessage = message.trim();

    const handleSubmit = () => {
        if (!trimmedMessage) return;

        onSend(trimmedMessage);
        setMessage("");
    };

    return (
        <div className="flex h-16 items-center gap-3 rounded-2xl border border-gray-100 bg-white px-4 shadow-sm">
            <button
                type="button"
                className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl text-gray-400 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-60"
                aria-label="파일 첨부"
                title="파일 첨부 기능은 준비 중입니다"
                disabled
            >
                <svg
                    aria-hidden="true"
                    className="h-5 w-5"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth="2"
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 1 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"
                    />
                </svg>
            </button>

            <input
                className="min-w-0 flex-1 bg-transparent text-sm text-gray-900 outline-none placeholder:text-gray-400"
                placeholder="궁금한 정책을 입력해 주세요"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                onKeyDown={(e) => {
                    if (e.key === "Enter") handleSubmit();
                }}
            />

            <button
                type="button"
                onClick={handleSubmit}
                className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-gray-900 text-white transition-colors hover:bg-gray-700 disabled:cursor-not-allowed disabled:bg-gray-200 disabled:text-gray-400"
                aria-label="메시지 보내기"
                disabled={!trimmedMessage}
            >
                <svg
                    aria-hidden="true"
                    className="h-5 w-5"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth="2"
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M5 12h14m0 0-6-6m6 6-6 6"
                    />
                </svg>
            </button>
        </div>
    );
};

export default ChatInput;
