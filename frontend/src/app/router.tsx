import { createBrowserRouter } from "react-router-dom";

import AppLayout from "./layouts/AppLayout";
import AuthLayout from "./layouts/AuthLayout";

import HomePage from "@/pages/HomePage";
import Signup from "@/pages/Signup";
import ChatPage from "@/pages/ChatPage";


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
        ],
    },

]);