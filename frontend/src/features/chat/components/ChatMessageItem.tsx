import { useState } from "react";
import type { ChatMessage } from "../types/ChatMessage";
import ChatPolicyCard from "./ChatPolicyCard";
import PolicyDetailModal from "@/features/policy/components/PolicyDetailModal";
import ReactMarkdown, { type Components } from "react-markdown"
import remarkGfm from "remark-gfm";
import remarkCjkFriendly from "remark-cjk-friendly";

// cjkFriendly: "**'진입 장벽'**를"처럼 강조 뒤에 한국어 조사가 붙어도 굵게 인식되도록 한다
const remarkPlugins = [remarkGfm, remarkCjkFriendly];

// 열이 많은 표는 말풍선 안에서 가로 스크롤되도록 감싼다
const markdownComponents: Components = {
  table: ({ children }) => (
    <div className="overflow-x-auto rounded-lg">
      <table>{children}</table>
    </div>
  ),
};

type Props = {
  message: ChatMessage;
};

export default function ChatMessageItem({ message }: Props) {
  const [selectedPolicyId, setSelectedPolicyId] = useState<number | null>(null);

  const isUser = message.role === "user";

  return (
    <>
      <div className={`flex ${isUser ? "justify-end" : "justify-start"}`}>
        <div className="max-w-[82%] space-y-3">
          <div
            className={`sm rounded-2xl px-4 py-3 leading-6 ${isUser
                ? "bg-gray-600 text-white"
                : "chat-markdown bg-gray-100 text-gray-800"
              }`}
          >
            <ReactMarkdown remarkPlugins={remarkPlugins} components={markdownComponents}>
              {message.content}
            </ReactMarkdown>
          </div>

          {!isUser && message.policies && message.policies.length > 0 && (
            <div className="grid gap-3">
              {message.policies.map((policy) => (
                <ChatPolicyCard
                  key={policy.id}
                  policy={policy}
                  onClick={setSelectedPolicyId}
                />
              ))}
            </div>
          )}
        </div>
      </div>

      {selectedPolicyId && (
        <PolicyDetailModal
          policyId={selectedPolicyId}
          onClose={() => setSelectedPolicyId(null)}
        />
      )}
    </>
  );
}
