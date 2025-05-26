import { defineConfig } from 'vite';
import laravel from 'laravel-vite-plugin';

export default defineConfig({
    server: {
        host: '0.0.0.0', // Acepta conexiones desde cualquier IP
        port: 5173, // o el que uses
        hmr: {
            host: '192.168.1.37' // PON AQUÍ LA IP de tu servidor Ubuntu
        }
    },
    plugins: [
        laravel({
            input: [
                'resources/css/app.css',
                'resources/js/app.js',
            ],
            refresh: true,
        }),
    ],
});
