import { useState } from "react";
import type { ChatMessage } from "../types/ChatMessage";
import ChatPolicyCard from "./ChatPolicyCard";
import PolicyDetailModal from "@/features/policy/components/PolicyDetailModal";
import ReactMarkdown from "react-markdown"

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
                : "bg-gray-100 text-gray-800"
              }`}
          >
            <ReactMarkdown>
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
