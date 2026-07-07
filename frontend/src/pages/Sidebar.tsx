import { Logo } from "@/assets";
import UserMenu from "@/features/auth/components/UserMenu";
import { useAuthStore } from "@/features/auth/store/authStore";
import { getRecommendationHistory } from "@/features/recommendation/api/getRecommendationHistory";
import type { RecommendationHistory } from "@/features/recommendation/types/RecommendationHistory ";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const Sidebar = () => {
  const { user, clearAuth } = useAuthStore();
  const [history, setHistory] = useState<RecommendationHistory[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchHistory = async () => {
      const result = await getRecommendationHistory();
      setHistory(result);
    };

    fetchHistory();
  }, []);

  return (
    <aside className="fixed left-0 top-0 flex h-screen w-[260px] flex-col bg-white px-6 py-6">
      <div className="mb-12 flex items-center">
        <img src={Logo} alt="로고" onClick={() => navigate(`/`)} className="h-10 w-10 object-contain" />
      </div>

      <nav className="space-y-5 text-[15px]">
        <button
          onClick={() => navigate(`/`)}
          className="block">
          □ 새 채팅
        </button>

        {user && (
          <>
            <button
              onClick={() => navigate(`/`)}
              className="block">
              □ 북마크
            </button>
            <button
              onClick={() => navigate(`/`)}
              className="block">
              □ 나중에 추가
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
              {history.map((item) => (
                <li key={item.recommendationId}>
                  <button onClick={() => navigate(`/chat/${item.recommendationId}`)}
                    className="w-full truncate rounded-lg px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                  >
                    {item.question}
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
            onLogout={clearAuth}
          />
        </div>
      )}
    </aside>
  );
};

export default Sidebar;