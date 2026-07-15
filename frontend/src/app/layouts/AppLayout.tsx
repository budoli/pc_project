import Sidebar from "./components/Sidebar";
import Header from "./components/Header";
import { Outlet } from "react-router-dom";
import { useAuthStore } from "@/features/auth/store/authStore";
import { useEffect } from "react";
import { getCurrentUser } from "@/features/user/api/getCurrentUser";

const AppLayout = () => {
  const { setUser, clearAuth } = useAuthStore();

  useEffect(() => {
    const restoreUser = async () => {
      try {
        const user = await getCurrentUser();
        setUser(user);
      } catch {
        clearAuth();
      }
    };

    restoreUser();

  }, [setUser, clearAuth]);

  return (
    <div className="min-h-screen bg-[#f7f7fb]">
      <Sidebar />

      <div className="ml-[260px] min-h-screen">
        <Header />
        <main>
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AppLayout;
