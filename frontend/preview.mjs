import {createServer} from 'node:http';
import {readFile} from 'node:fs/promises';
import {resolve,extname} from 'node:path';
const root=resolve('dist');
const mime={'.html':'text/html; charset=utf-8','.js':'text/javascript; charset=utf-8','.css':'text/css; charset=utf-8'};
createServer(async(req,res)=>{try{const name=decodeURIComponent(new URL(req.url,'http://localhost').pathname);const p=resolve(root,'.'+(name==='/'?'/index.html':name));if(!p.startsWith(root+'/'))throw Error('bad path');res.writeHead(200,{'Content-Type':mime[extname(p)]||'application/octet-stream'});res.end(await readFile(p));}catch{res.writeHead(404);res.end('Not found');}}).listen(4178,'127.0.0.1',()=>console.log('Local: http://127.0.0.1:4178'));
