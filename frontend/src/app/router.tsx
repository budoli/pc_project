import { createBrowserRouter } from "react-router-dom";

import AppLayout from "./layouts/AppLayout";
import AuthLayout from "./layouts/AuthLayout";

import HomePage from "@/pages/HomePage";
import Signup from "@/pages/Signup";


export const router = createBrowserRouter([
    {
        path: "/",
        element: <AppLayout />,
        children: [
            {
                index: true,
                element: <HomePage />,
            },
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