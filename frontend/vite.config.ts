import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import * as path from 'path';
import { defineConfig } from 'vite';
import eslint from 'vite-plugin-eslint2';

// https://vite.dev/config/
export default defineConfig({
  plugins: [eslint({ cache: false }), react(), tailwindcss()],
  resolve: {
    alias: {
      assets: path.resolve(__dirname, './src/assets'),
      modules: path.resolve(__dirname, './src/modules'),
      shared: path.resolve(__dirname, './src/shared'),
      styles: path.resolve(__dirname, './src/styles'),
      types: path.resolve(__dirname, './src/types'),
    },
  },
  build: {
    target: 'esnext',
  },
});
