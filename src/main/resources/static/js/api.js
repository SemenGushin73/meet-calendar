async function handle(res) {
    if (res.status === 401) {
        const err = new Error("UNAUTHORIZED");
        err.status = 401;
        throw err;
    }

    if (res.ok) {
        const ct = res.headers.get("content-type") || "";
        return ct.includes("application/json") ? res.json() : null;
    }

    let payload = null;
    try {
        payload = await res.json();
    } catch {
    }

    const msg = payload?.message || payload?.error || `HTTP ${res.status}`;
    const err = new Error(msg);
    err.status = res.status;
    err.payload = payload;
    throw err;
}

export async function apiJson(url, method, body) {
    const res = await fetch(url, {
        method,
        credentials: "same-origin",
        headers: {"Accept": "application/json", "Content-Type": "application/json"},
        body: body ? JSON.stringify(body) : undefined,
    });
    return handle(res);
}

export async function apiGetWeek(week) {
    const url = week
        ? `/api/v1/calendar/week?week=${encodeURIComponent(week)}`
        : `/api/v1/calendar/week`;

    const res = await fetch(url, {credentials: "same-origin"});
    if (!res.ok) {
        const err = new Error("API error");
        err.status = res.status;
        throw err;
    }
    return res.json();
}

export async function apiGetRooms() {
    const res = await fetch("/api/v1/rooms", {
        method: "GET",
        credentials: "same-origin",
        headers: {"Accept": "application/json"},
    });

    if (res.status === 404) return [];

    return handle(res) || [];
}

export async function login(username, password) {
    const form = new URLSearchParams();
    form.set("username", username);
    form.set("password", password);

    const res = await fetch("/login", {
        method: "POST",
        credentials: "same-origin",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: form,
        redirect: "manual",
    });

    if (res.status === 0 || res.status === 200 || res.status === 302) return true;
    throw new Error("Invalid username or password");
}

export async function logout() {
    await fetch("/logout", {method: "POST", credentials: "same-origin"});
}