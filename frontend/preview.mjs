import {createServer, request} from 'node:http';
import {readFile} from 'node:fs/promises';
import {resolve, extname} from 'node:path';

const root = resolve('dist');
const backendPort = Number(process.env.BACKEND_PORT || 8083);
const port = Number(process.env.FRONTEND_PORT || 4178);
const mime = {
    '.html': 'text/html; charset=utf-8',
    '.js': 'text/javascript; charset=utf-8',
    '.css': 'text/css; charset=utf-8'
};

createServer(async (req, res) => {
    const url = new URL(req.url, 'http://localhost');
    if (/^\/api\/posts(?:\/(?:mine|\d+))?$/.test(url.pathname)) {
        const upstream = request({
            hostname: '127.0.0.1', port: backendPort, path: url.pathname,
            method: req.method, headers: {...req.headers, host: '127.0.0.1:' + backendPort}
        }, response => {
            res.writeHead(response.statusCode, response.headers);
            response.pipe(res);
        });
        upstream.on('error', () => {
            res.writeHead(502);
            res.end();
        });
        req.pipe(upstream);
        return;
    }

    try {
        const name = decodeURIComponent(url.pathname);
        const path = resolve(root, '.' + (name === '/' ? '/index.html' : name));
        if (!path.startsWith(root + '/')) throw Error('bad path');
        let file = await readFile(path);
        if (path === resolve(root, 'index.html') && !url.searchParams.has('demo')) {
            file = file.toString().replace('<head>', '<head><meta name="maeum-post-api" content="enabled">');
        }
        res.writeHead(200, {'Content-Type': mime[extname(path)] || 'application/octet-stream'});
        res.end(file);
    } catch {
        res.writeHead(404);
        res.end('Not found');
    }
}).listen(port, '127.0.0.1', () => console.log('Local: http://127.0.0.1:' + port));
