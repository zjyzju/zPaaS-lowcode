import { fileURLToPath, URL } from 'url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import fs from 'fs'

// https://vitejs.dev/config/
export default defineConfig({
    define: {
        // enable hydration mismatch details in production build
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: 'true'
    },
    plugins: [
        vue({
            template: {
                compilerOptions: {
                    isCustomElement: tag => /^micro-app/.test(tag)
                }
            }
        })
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        //host: '0.0.0.0', 
        headers: {
            'Access-Control-Allow-Origin': '*'
        },
        port: '13043',
        proxy: {
            "/lcdp-proxy": {
                target: "https://localhost:18043",
                //target: "https://192.168.0.100:18043",
                changeOrigin: true,
                secure: false,
                rewrite: (path) => path.replace(/^\/lcdp-proxy/, ""),
                cookiePathRewrite: {//用于解决跨域cookie的问题，如JSESSIONID的传递
                    "/lowcode": "/lcdp-proxy/lowcode",
                }
            },
        },
        https: {
            key: fs.readFileSync('cert/ca-key.pem'),
            cert: fs.readFileSync('cert/ca.cer')
        }
    },
    build: {
        rollupOptions: {
            input: {
                index: path.resolve(__dirname, 'index.html'),
                workbench: path.resolve(__dirname, 'workbench.html'),
                businessSystemMgr: path.resolve(__dirname, 'businessSystemMgr.html'),
                businessSystemGrant: path.resolve(__dirname, 'businessSystemGrant.html'),
                login: path.resolve(__dirname, 'login.html'),
                moreMgrFunc: path.resolve(__dirname, 'moreMgrFunc.html'),
                simpleMgrFunc: path.resolve(__dirname, 'simpleMgrFunc.html'),
                tabbedComboFunc: path.resolve(__dirname, 'tabbedComboFunc.html'),
                designer: path.resolve(__dirname, 'designer.html'),
                customizedFunc: path.resolve(__dirname, 'customizedFunc.html'),
                reportFunc: path.resolve(__dirname, 'reportFunc.html'),
                chartFunc: path.resolve(__dirname, 'chartFunc.html'),
                systemFramework: path.resolve(__dirname, 'systemFramework.html')
            },
            output: {
                chunkFileNames: 'static/js/[name]-[hash].js',
                entryFileNames: "static/js/[name]-[hash].js",
                assetFileNames: "static/[ext]/name-[hash].[ext]"
            }
        }
    }
})
