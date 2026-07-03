import { RouterProvider } from "react-router-dom";
import { router } from "./router";
import AuthInitializer from "@/features/auth/components/AuthInitializer";

function App() {
  return (
    <AuthInitializer>
      <RouterProvider router={router} />
    </AuthInitializer>

  );
}

export default App;