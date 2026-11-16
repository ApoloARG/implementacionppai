import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// El backend (Spring Boot) corre en el 8081 porque el mock del GPS de la catedra usa el 8080.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
