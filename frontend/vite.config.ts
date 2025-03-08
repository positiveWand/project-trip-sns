import path from "path"
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs'

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
    }})(),
    (() => {return {
      name: 'exclude-msw',
      renderStart(outputOptions) {
        const msw = path.resolve(outputOptions.dir!, 'mockServiceWorker.js');
        fs.rm(msw, () => console.log(`vite plugin(exclude-msw): msw 제거 완료(${msw})`))
      }
    }})()
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src")
    }
  },
  build: {
    rollupOptions: {
      input: {
        index: path.resolve(__dirname, 'index.html'),
        login: path.resolve(__dirname, 'login/index.html'),
        signup: path.resolve(__dirname, 'signup/index.html'),
        map: path.resolve(__dirname, 'map/index.html'),
        social: path.resolve(__dirname, 'social/index.html'),
      }
    }
  }
})
