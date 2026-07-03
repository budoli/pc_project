import Sidebar from "@/pages/Sidebar";
import Header from "@/pages/Header";
import { Outlet } from "react-router-dom";

const AppLayout = () => {
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