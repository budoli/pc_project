import { createBrowserRouter } from "react-router-dom";

import AppLayout from "./layouts/AppLayout";
import AuthLayout from "./layouts/AuthLayout";

import HomePage from "@/pages/HomePage";
import Signup from "@/pages/Signup";
import ChatPage from "@/pages/ChatPage";
import SignupProfilePage from "@/pages/SignupProfile";
import AdminPage from "@/pages/AdminPage";


export const router = createBrowserRouter([
    {
        path: "/",
        element: <AppLayout />,
        children: [
            {
                index: true,
                element: <HomePage />,
            },
            {
                path: "chat",
                element: <ChatPage />,
            },
            {
                path: "chat/:conversationId",
                element: <ChatPage />,
            },
            {
                path: "admin",
                element: <AdminPage />,
            }
        ],
    },
    {
        element: <AuthLayout />,
        children: [
            {
                path: "signup",
                element: <Signup />,
            },
            {
                path: "signup/profile",
                element: <SignupProfilePage />
            }
        ],
    },

]);