import { useEffect, useState } from "react";
import { reissue } from "@/features/auth/api/reissue";
import { getCurrentUser } from "@/features/user/api/getCurrentUser";
import { useAuthStore } from "@/features/auth/store/authStore";

type Props = {
    children: React.ReactNode;
};

const AuthInitializer = ({ children }: Props) => {
    const [isLoading, setIsLoading] = useState(true);
    const { setUser, clearAuth } = useAuthStore();

    useEffect(() => {
        const initializeAuth = async () => {
            try {
                await reissue();
                const user = await getCurrentUser();
                setUser(user);
            } catch {
                clearAuth();
            } finally {
                setIsLoading(false);
            }
        };

        initializeAuth();
    }, [setUser, clearAuth]);

    if (isLoading) {
        return null;
    }

    return children;
};

export default AuthInitializer;
