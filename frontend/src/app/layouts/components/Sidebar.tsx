import { Logo } from "@/assets";
import { logout } from "@/features/auth/api/logout";
import UserMenu from "@/features/auth/components/UserMenu";
import { useAuthStore } from "@/features/auth/store/authStore";
import { getConversations } from "@/features/chat/api/chatApi";
import type { ConversationResponse } from "@/features/chat/types/ConversationResponse";
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
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
    } finally {
      clearAuth();
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
              onClick={() => navigate("/")}
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
          <section>
            <h3 className="mb-5 font-bold">관심정책</h3>
            <ul className="space-y-4">
              <li>policy 예시 예시</li>
              <li>policy 예시 예시</li>
              <li>policy 예시 예시</li>
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
    </aside>
  );
};

export default Sidebar;
