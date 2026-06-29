import { Outlet } from "react-router-dom";

const AuthLayout = () => {
    return (
        <main className="min-h-screen bg-[#f7f7fb]">
            <Outlet />
        </main>
    );
};

export default AuthLayout;