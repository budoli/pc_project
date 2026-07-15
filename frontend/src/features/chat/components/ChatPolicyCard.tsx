import type { ChatPolicySummary } from "../types/ChatMessage";

type Props = {
  policy: ChatPolicySummary;
  onClick: (policyId: number) => void;
};

export default function ChatPolicyCard({ policy, onClick }: Props) {
  return (
    <button
      type="button"
      onClick={() => onClick(policy.id)}
      className="w-full rounded-xl border border-gray-200 bg-white p-4 text-left shadow-sm transition hover:border-blue-500 hover:shadow-md"
    >
      <div className="mb-2 flex flex-wrap gap-2">
        <span className="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-700">
          {policy.category || "정책"}
        </span>
      </div>

      <h3 className="sm font-semibold leading-5 text-gray-900">
        {policy.title}
      </h3>

      {policy.description && (
        <p className="sm mt-2 line-clamp-2 leading-5 text-gray-600">
          {policy.description}
        </p>
      )}

      {policy.organization && (
        <p className="mt-3 text-xs text-gray-500">{policy.organization}</p>
      )}

      <p className="mt-3 text-xs font-medium text-blue-600">자세히 보기</p>
    </button>
  );
}
