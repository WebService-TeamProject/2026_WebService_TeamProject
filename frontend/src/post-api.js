export const sharedPosts = document.querySelector('meta[name="maeum-post-api"]')?.content === 'enabled';

export function postAuthorization(email, password) {
    const bytes = new TextEncoder().encode(email + ':' + password);
    return 'Basic ' + btoa(String.fromCharCode(...bytes));
}

export async function postRequest(path = '', {method = 'GET', authorization, data} = {}) {
    const headers = {'X-Requested-With': 'XMLHttpRequest'};
    if (authorization) headers.Authorization = authorization;
    if (data) headers['Content-Type'] = 'application/json';
    if (method !== 'GET') {
        const csrf = document.cookie.split('; ').find(cookie => cookie.startsWith('XSRF-TOKEN='));
        if (csrf) headers['X-XSRF-TOKEN'] = csrf.slice('XSRF-TOKEN='.length);
    }
    const response = await fetch('/api/posts' + path, {
        method, headers, body: data ? JSON.stringify(data) : undefined
    });
    if (response.status === 401) throw Error('게시글 계정의 이메일과 비밀번호를 확인해주세요.');
    if (response.status === 403) throw Error('게시글을 변경할 권한이 없어요. 다시 로그인해주세요.');
    if (response.status === 404) throw Error('게시글을 찾을 수 없어요.');
    if (response.status === 400) throw Error('카테고리, 제목과 본문을 확인해주세요.');
    if (!response.ok) throw Error('게시글 서버에 연결하지 못했어요.');
    return response.status === 204 ? null : response.json();
}

export function sharedPost(post, ownerId) {
    return {...post, id: 'db-' + post.id, userId: ownerId};
}
