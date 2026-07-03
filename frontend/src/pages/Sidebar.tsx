import { Logo } from "@/assets";
import { useAuthStore } from "@/features/auth/store/authStore";

const Sidebar = () => {
  const accessToken = useAuthStore((state) => state.accessToken);

  return (
    <aside className="fixed left-0 top-0 h-screen w-[260px] bg-white px-8 py-6">
      <div className="mb-12 flex items-center">
        <img src={Logo} alt="로고" className="h-10 w-10 object-contain" />
      </div>
    
      <nav className="space-y-5 text-[15px]">
        <button className="block">□ 새 채팅</button>

        {accessToken && (
          <>
            <button className="block">□ 북마크</button>
            <button className="block">□ 나중에 추가</button>
          </>
        )}
      </nav>

      {accessToken && (
        <>
          <section className="mt-20">
            <h3 className="mb-5 font-bold">관심정책</h3>
            <ul className="space-y-4">
              <li>policy 예시 예시</li>
              <li>policy 예시 예시</li>
              <li>policy 예시 예시</li>
              <li>policy 예시 예시</li>
            </ul>
          </section>

          <section className="mt-20">
            <h3 className="mb-5 font-bold">이전 대화</h3>
            <ul className="space-y-4">
              <li>history 예시 예시</li>
              <li>history 예시 예시</li>
              <li>history 예시 예시</li>
              <li>history 예시 예시</li>
            </ul>
          </section>
        </>
      )}
    </aside>
  );
};

export default Sidebar;