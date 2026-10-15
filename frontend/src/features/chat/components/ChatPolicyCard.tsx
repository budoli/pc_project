import type { ChatPolicySummary } from "../types/ChatMessage";
import { useState } from "react";
import { useFavoritePolicyStore } from "@/features/policy/store/favoritePolicyStore";

type Props = {
  policy: ChatPolicySummary;
  onClick: (policyId: number) => void;
};

export default function ChatPolicyCard({ policy, onClick }: Props) {
  const isFavorite = useFavoritePolicyStore((state) =>
    state.favorites.some((item) => item.id === policy.id),
  );
  const toggle = useFavoritePolicyStore((state) => state.toggle);
  const loadingFavorites = useFavoritePolicyStore((state) => state.loading);
  const isPending = useFavoritePolicyStore((state) => state.pendingIds.includes(policy.id));
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const handleToggle = async () => {
    if (saving) return;
    setSaving(true);
    setError("");
    try {
      await toggle({ id: policy.id, title: policy.title });
    } catch {
      setError("관심정책을 변경하지 못했습니다. 다시 시도해주세요.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="w-full rounded-xl border border-gray-200 bg-white p-4 text-left shadow-sm transition hover:border-blue-500 hover:shadow-md">
      <div className="mb-2 flex items-start justify-between gap-2">
        <span className="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-700">
          {policy.category || "정책"}
        </span>
        <button
          type="button"
          onClick={handleToggle}
          disabled={saving || loadingFavorites || isPending}
          aria-label={isFavorite ? "관심정책에서 제거" : "관심정책에 추가"}
          aria-pressed={isFavorite}
          title={isFavorite ? "관심정책에서 제거" : "관심정책에 추가"}
          className="rounded-full p-1 text-amber-500 transition hover:bg-amber-50 disabled:opacity-50"
        >
          <svg aria-hidden="true" viewBox="0 0 24 24" className="h-6 w-6" fill={isFavorite ? "currentColor" : "none"} stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round">
            <path d="m12 2.5 2.95 6.05 6.68.96-4.83 4.71 1.14 6.65L12 17.73l-5.94 3.14 1.14-6.65-4.83-4.71 6.68-.96L12 2.5Z" />
          </svg>
        </button>
      </div>
      <button type="button" onClick={() => onClick(policy.id)} className="w-full text-left">
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
      {error && <p role="alert" className="mt-2 text-xs text-red-600">{error}</p>}
    </div>
  );
}
