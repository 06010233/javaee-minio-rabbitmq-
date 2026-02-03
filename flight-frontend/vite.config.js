// 文件路径: vite.config.js

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue2'
import path from 'path'

export default defineConfig({
  plugins: [vue()],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      'util': 'util',
    }
  },
  
  // ★★★ 核心修复 1：定义 global 变量，解决 sockjs 报错 ★★★
  define: {
    'global': 'window',
    'process': {
      'env': {}
    }
  },
  
  server: {
    port: 5173,
    proxy: {
      // 代理 API 请求
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      // ★★★ 核心修复 2：代理 WebSocket 连接 ★★★
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true,
        changeOrigin: true,
      }
    }
  },
  
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "@/assets/variables.scss";`
      }
    }
  },
  
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          vue: ['vue', 'vue-router', 'vuex'],
          element: ['element-ui'],
          axios: ['axios'],
          moment: ['moment']
        }
      }
    }
  }
})