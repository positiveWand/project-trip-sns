import path from "path"
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    (() => {return {
      name: 'configure-server',
      configureServer(server) {
        server.middlewares.use((req, res, next) => {
          if (req.url?.startsWith('/map/')) {
            req.url = '/map/index.html';
          } else if(req.url?.startsWith('/social/')) {
            req.url = '/social/index.html';
          }
          next();
        })
      }
    }})()
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src")
    }
  }
})
