import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [tailwindcss(),],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src")
    }
  },
  // 백엔드 CORS가 5173만 허용하므로, 포트가 사용 중이면 다른 포트로 넘어가지 않고 실패시킨다
  server: {
    port: 5173,
    strictPort: true,
  },
})
