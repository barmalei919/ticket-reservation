const API = '/api';

const auth = {
    getToken:   () => localStorage.getItem('token'),
    getUserId:  () => localStorage.getItem('userId'),
    getUserName:() => localStorage.getItem('userName'),
    getRole:    () => localStorage.getItem('role'),
    isLoggedIn: () => !!localStorage.getItem('token'),
    isAdmin:    () => localStorage.getItem('role') === 'ADMIN',

    set(token, refreshToken, userId, userName, role) {
        localStorage.setItem('token',        token);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('userId',       String(userId));
        localStorage.setItem('userName',     userName);
        localStorage.setItem('role',         role);
    },

    clear() {
        ['token', 'refreshToken', 'userId', 'userName', 'role'].forEach(k => localStorage.removeItem(k));
    }
};

async function request(method, url, body = null) {
    const headers = { 'Content-Type': 'application/json' };
    const token = auth.getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;

    const opts = { method, headers };
    if (body !== null) opts.body = JSON.stringify(body);

    const res = await fetch(API + url, opts);

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Ошибка ${res.status}`);
    }

    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) return res.json();
    return res.text();
}

const http = {
    get:    url        => request('GET',    url),
    post:   (url, b)   => request('POST',   url, b),
    patch:  url        => request('PATCH',  url),
    delete: url        => request('DELETE', url),
};

function requireAuth() {
    if (!auth.isLoggedIn()) {
        window.location.href = '/auth.html?redirect=' + encodeURIComponent(window.location.href);
        return false;
    }
    return true;
}

function renderNav() {
    const nav = document.getElementById('nav');
    if (!nav) return;

    if (auth.isLoggedIn()) {
        const adminLink = auth.isAdmin()
            ? `<a class="btn-nav" href="/admin.html">Админ</a>`
            : '';
        nav.innerHTML = `
            <span class="nav-user">Привет, ${auth.getUserName() || 'Пользователь'}</span>
            <a class="btn-nav" href="/profile.html">Мои билеты</a>
            ${adminLink}
            <button class="btn-nav" onclick="logout()">Выйти</button>
        `;
    } else {
        nav.innerHTML = `
            <a class="btn-nav" href="/auth.html">Войти</a>
        `;
    }
}

function logout() {
    auth.clear();
    window.location.href = '/';
}

function fmtDate(dt) {
    return new Date(dt).toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

function fmtTime(dt) {
    return new Date(dt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' });
}

function fmtDuration(start, end) {
    const ms = new Date(end) - new Date(start);
    const h  = Math.floor(ms / 3600000);
    const m  = Math.floor((ms % 3600000) / 60000);
    return h > 0 ? `${h} ч ${m} мин` : `${m} мин`;
}

function showAlert(containerId, message, type = 'error') {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
}

function clearAlert(containerId) {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = '';
}
