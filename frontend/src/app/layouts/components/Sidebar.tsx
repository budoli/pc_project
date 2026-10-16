import { Logo } from "@/assets";
import { logout } from "@/features/auth/api/logout";
import UserMenu from "@/features/auth/components/UserMenu";
import { useAuthStore } from "@/features/auth/store/authStore";
import { getConversations } from "@/features/chat/api/chatApi";
import type { ConversationResponse } from "@/features/chat/types/ConversationResponse";
import PolicyDetailModal from "@/features/policy/components/PolicyDetailModal";
import { useFavoritePolicyStore } from "@/features/policy/store/favoritePolicyStore";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const iconClassName = "h-4 w-4 shrink-0";

const NewChatIcon = () => (
  <svg
    aria-hidden="true"
    className={iconClassName}
    fill="none"
    viewBox="0 0 24 24"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="M21 15a4 4 0 0 1-4 4H8l-5 3V7a4 4 0 0 1 4-4h6" />
    <path d="M19 3v6" />
    <path d="M16 6h6" />
  </svg>
);

const BookmarkIcon = () => (
  <svg
    aria-hidden="true"
    className={iconClassName}
    fill="none"
    viewBox="0 0 24 24"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="M19 21l-7-4-7 4V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
  </svg>
);

const PlusIcon = () => (
  <svg
    aria-hidden="true"
    className={iconClassName}
    fill="none"
    viewBox="0 0 24 24"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="M12 5v14" />
    <path d="M5 12h14" />
  </svg>
);

const Sidebar = () => {
  const { user, clearAuth } = useAuthStore();
  const [conversations, setConversations] = useState<ConversationResponse[]>([]);
  const [selectedPolicyId, setSelectedPolicyId] = useState<number | null>(null);
  const [favoriteError, setFavoriteError] = useState(false);
  const favorites = useFavoritePolicyStore((state) => state.favorites);
  const loadingFavorites = useFavoritePolicyStore((state) => state.loading);
  const loadFavorites = useFavoritePolicyStore((state) => state.load);
  const clearFavorites = useFavoritePolicyStore((state) => state.clear);
  const userId = user?.id;
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
    } finally {
      clearAuth();
      clearFavorites();
      navigate("/");
    }
  };

  useEffect(() => {
    if (!user) return;

    const fetchConversations = async () => {
      const result = await getConversations();
      setConversations(result);
    };

    fetchConversations();
  }, [user]);

  useEffect(() => {
    if (userId === undefined) {
      clearFavorites();
      return;
    }
    loadFavorites(userId)
      .then(() => setFavoriteError(false))
      .catch(() => setFavoriteError(true));
  }, [userId, loadFavorites, clearFavorites]);

  return (
    <aside className="fixed left-0 top-0 flex h-screen w-[260px] flex-col bg-white px-6 py-6">
      <div className="mb-12 flex items-center">
        <img src={Logo} alt="로고" onClick={() => navigate("/")} className="h-10 w-10 object-contain" />
      </div>

      <nav className="space-y-5 text-[15px]">
        <button
          onClick={() => navigate("/")}
          className="flex items-center gap-2 text-gray-700 transition-colors hover:text-gray-950">
          <NewChatIcon />
          <span>새 채팅</span>
        </button>

        {user && (
          <>
            <button
              onClick={() => document.getElementById("favorite-policies")?.scrollIntoView({ behavior: "smooth" })}
              className="flex items-center gap-2 text-gray-700 transition-colors hover:text-gray-950">
              <BookmarkIcon />
              <span>북마크</span>
            </button>
            <button
              onClick={() => navigate("/")}
              className="flex items-center gap-2 text-gray-700 transition-colors hover:text-gray-950">
              <PlusIcon />
              <span>나중에 추가</span>
            </button>
          </>
        )}
      </nav>

      {user && (
        <div className="mt-16 flex-1 overflow-y-auto">
          <section id="favorite-policies">
            <h3 className="mb-5 font-bold">관심정책</h3>
            <ul className="space-y-4">
              {favorites.map((policy) => (
                <li key={policy.id}>
                  <button type="button" onClick={() => setSelectedPolicyId(policy.id)}
                    className="w-full truncate text-left text-sm text-gray-700 hover:text-blue-600"
                    title={policy.title}>
                    {policy.title}
                  </button>
                </li>
              ))}
              {loadingFavorites && <li className="text-sm text-gray-500">불러오는 중...</li>}
              {!loadingFavorites && !favoriteError && favorites.length === 0 && <li className="text-sm text-gray-500">관심정책이 없습니다.</li>}
              {favoriteError && <li className="text-sm text-red-600">관심정책을 불러오지 못했습니다.</li>}
            </ul>
          </section>

          <section className="mt-16">
            <h3 className="mb-5 font-bold">이전 대화</h3>
            <ul className="space-y-1">
              {conversations.map((conversation) => (
                <li key={conversation.conversationId}>
                  <button onClick={() => navigate(`/chat/${conversation.conversationId}`)}
                    className="sm w-full truncate rounded-lg px-3 py-2 text-left text-gray-700 hover:bg-gray-100"
                  >
                    {conversation.title}
                  </button>
                </li>
              ))}
            </ul>
          </section>
        </div>
      )}

      {user && (
        <div className="mt-auto border-t pt-4">
          <UserMenu
            name={user.name}
            role={user.role}
            onLogout={handleLogout}
          />
        </div>
      )}
      {selectedPolicyId !== null && (
        <PolicyDetailModal policyId={selectedPolicyId} onClose={() => setSelectedPolicyId(null)} />
      )}
    </aside>
  );
};

export default Sidebar;
