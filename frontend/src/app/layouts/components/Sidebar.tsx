import { Logo } from "@/assets";
import { logout } from "@/features/auth/api/logout";
import UserMenu from "@/features/auth/components/UserMenu";
import { useAuthStore } from "@/features/auth/store/authStore";
import { deleteConversation, getConversations } from "@/features/chat/api/chatApi";
import type { ConversationResponse } from "@/features/chat/types/ConversationResponse";
import PolicyDetailModal from "@/features/policy/components/PolicyDetailModal";
import { useFavoritePolicyStore } from "@/features/policy/store/favoritePolicyStore";
import { useEffect, useState } from "react";
import { useMatch, useNavigate } from "react-router-dom";

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

const TrashIcon = () => (
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
    <path d="M3 6h18" />
    <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
    <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
  </svg>
);

const Sidebar = () => {
  const { user, clearAuth } = useAuthStore();
  const [conversations, setConversations] = useState<ConversationResponse[]>([]);
  const [selectedPolicyId, setSelectedPolicyId] = useState<number | null>(null);
  const [favoriteError, setFavoriteError] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [deleteError, setDeleteError] = useState(false);
  const currentChat = useMatch("/chat/:conversationId");
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

  const handleDeleteConversation = async (conversation: ConversationResponse) => {
    if (deletingId !== null) return;
    if (!window.confirm(`'${conversation.title}' 대화를 삭제할까요? 삭제한 대화는 되돌릴 수 없습니다.`)) return;

    setDeletingId(conversation.conversationId);
    setDeleteError(false);
    try {
      await deleteConversation(conversation.conversationId);
      setConversations((prev) => prev.filter((item) => item.conversationId !== conversation.conversationId));

      // 보고 있던 대화를 지우면 빈 화면이 남지 않도록 새 채팅으로 이동한다
      if (Number(currentChat?.params.conversationId) === conversation.conversationId) {
        navigate("/");
      }
    } catch {
      setDeleteError(true);
    } finally {
      setDeletingId(null);
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
                <li key={conversation.conversationId} className="group relative">
                  <button onClick={() => navigate(`/chat/${conversation.conversationId}`)}
                    className="sm w-full truncate rounded-lg py-2 pl-3 pr-9 text-left text-gray-700 group-hover:bg-gray-100"
                    title={conversation.title}
                  >
                    {conversation.title}
                  </button>
                  <button
                    type="button"
                    onClick={() => handleDeleteConversation(conversation)}
                    disabled={deletingId !== null}
                    aria-label={`'${conversation.title}' 대화 삭제`}
                    title="대화 삭제"
                    className="absolute right-1 top-1/2 -translate-y-1/2 rounded-md p-1.5 text-gray-400 opacity-0 transition hover:bg-gray-200 hover:text-red-600 focus-visible:opacity-100 group-hover:opacity-100 disabled:cursor-not-allowed"
                  >
                    <TrashIcon />
                  </button>
                </li>
              ))}
            </ul>
            {deleteError && <p role="alert" className="mt-2 text-sm text-red-600">대화를 삭제하지 못했습니다. 다시 시도해주세요.</p>}
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
