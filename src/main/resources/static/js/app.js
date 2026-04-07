import {apiGetWeek, login, logout, apiJson, apiGetRooms} from "./api.js";
import {renderCalendar, toDateTimeLocalValue, toApiLocalDateTime, formatWeekLabel} from "./calendar.js";
import {renderAvatar} from "./avatar.js";

const els = {
    prev: document.getElementById("prevWeekBtn"),
    next: document.getElementById("nextWeekBtn"),
    today: document.getElementById("todayBtn"),
    weekLabel: document.getElementById("weekLabel"),
    calendar: document.getElementById("calendar"),

    roomSelect: document.getElementById("roomSelect"),
    roomId: document.getElementById("roomIdInput"),
    title: document.getElementById("titleInput"),
    globalError: document.getElementById("globalError"),

    loginOverlay: document.getElementById("loginOverlay"),
    loginBtn: document.getElementById("loginBtn"),
    loginUser: document.getElementById("loginUsername"),
    loginPass: document.getElementById("loginPassword"),
    loginError: document.getElementById("loginError"),

    avatar: document.getElementById("avatar"),
    username: document.getElementById("username"),
    logoutBtn: document.getElementById("logoutBtn"),

    createModal: document.getElementById("createModal"),
    startInput: document.getElementById("startInput"),
    endInput: document.getElementById("endInput"),
    createError: document.getElementById("createError"),
    createCancelBtn: document.getElementById("createCancelBtn"),
    createSaveBtn: document.getElementById("createSaveBtn"),

    eventModal: document.getElementById("eventModal"),
    eventInfo: document.getElementById("eventInfo"),
    eventCloseBtn: document.getElementById("eventCloseBtn"),
};

let currentWeek = null;

function show(el) {
    el.classList.remove("hidden");
}

function hide(el) {
    el.classList.add("hidden");
}

function showGlobalError(msg) {
    els.globalError.textContent = msg;
    show(els.globalError);
}

function clearGlobalError() {
    els.globalError.textContent = "";
    hide(els.globalError);
}

function showLogin(msg) {
    if (msg) {
        els.loginError.textContent = msg;
        show(els.loginError);
    } else {
        hide(els.loginError);
    }
    show(els.loginOverlay);
    els.loginOverlay.setAttribute("aria-hidden", "false");
    setTimeout(() => els.loginUser?.focus(), 0);
}

function hideLogin() {
    hide(els.loginOverlay);
    els.loginOverlay.setAttribute("aria-hidden", "true");
    hide(els.loginError);
}

async function loadRooms() {
    try {
        const rooms = await apiGetRooms();

        if (!rooms || rooms.length === 0) {
            els.roomSelect.innerHTML = `<option value="">Rooms API is not available</option>`;
            els.roomSelect.value = "";
            return;
        }

        els.roomSelect.innerHTML =
            `<option value="">Select a room…</option>` +
            rooms.map(r => {
                const id = r.id ?? r.roomId ?? r.value;
                const name = r.name ?? r.title ?? r.label ?? `Room ${id}`;
                const extra = r.floor ? ` (floor ${r.floor})` : (r.location ? ` (${r.location})` : "");
                return `<option value="${String(id)}">${escapeHtml(name)}${escapeHtml(extra)}</option>`;
            }).join("");

        const typed = String(els.roomId.value || "").trim();
        if (typed) els.roomSelect.value = typed;

        els.roomSelect.addEventListener("change", () => {
            if (els.roomSelect.value) els.roomId.value = els.roomSelect.value;
        });
    } catch (e) {
        els.roomSelect.innerHTML = `<option value="">Failed to load rooms</option>`;
    }
}

async function loadWeek(weekStr) {
    clearGlobalError();
    try {
        const data = await apiGetWeek(weekStr);
        currentWeek = data;

        els.weekLabel.textContent = formatWeekLabel(data) || "";

        const u = (els.loginUser.value || "").trim() || "User";
        els.username.textContent = u;
        els.username.classList.remove("muted");
        renderAvatar(els.avatar, u);

        hideLogin();

        console.log("Week loaded:", {
            weekStart: data.weekStart,
            bookingsCount: (data.bookings || []).length,
            sampleBooking: (data.bookings || [])[0]
        });

        renderCalendar(els.calendar, data, onEmptySlotClick, onEventClick);
    } catch (e) {
        if (e && e.status === 401) {
            renderCalendar(els.calendar, {
                weekStart: currentWeek?.weekStart,
                bookings: []
            }, onEmptySlotClick, onEventClick);
            showLogin();
            return;
        }
        console.error("loadWeek failed", e);
        showGlobalError(e.message || "Failed to load week");
    }
}

function onEmptySlotClick({start, end}) {
    if (!currentWeek) {
        showLogin("Please login first");
        return;
    }
    els.startInput.value = toDateTimeLocalValue(start);
    els.endInput.value = toDateTimeLocalValue(end);
    hide(els.createError);
    show(els.createModal);
}

function onEventClick(b) {
    const lines = [
        `<b>${escapeHtml(b.title || "(no title)")}</b>`,
        b.roomName ? escapeHtml(b.roomName) : "",
        b.startAt ? `Start: ${escapeHtml(String(b.startAt))}` : "",
        b.endAt ? `End: ${escapeHtml(String(b.endAt))}` : "",
        b.status ? `Status: ${escapeHtml(String(b.status))}` : ""
    ].filter(Boolean);

    els.eventInfo.innerHTML = lines.join("<br>");
    show(els.eventModal);
}

async function doLogin() {
    const u = (els.loginUser.value || "").trim();
    const p = els.loginPass.value || "";
    if (!u || !p) {
        showLogin("Enter username and password");
        return;
    }
    try {
        await login(u, p);
        await loadWeek(null);
    } catch (e) {
        showLogin("Login failed. Check credentials.");
    }
}

async function doLogout() {
    try {
        await logout();
    } finally {
        currentWeek = null;
        els.username.textContent = "—";
        els.username.classList.add("muted");
        renderAvatar(els.avatar, "User");
        showLogin();
    }
}

async function createBooking() {
    try {
        hide(els.createError);

        const selected = String(els.roomSelect.value || "").trim();
        const roomId = Number(selected || els.roomId.value);

        const title = (els.title.value || "").trim() || "Meeting";

        if (!Number.isFinite(roomId) || roomId <= 0) throw new Error("Room ID must be > 0");

        const start = new Date(els.startInput.value);
        const end = new Date(els.endInput.value);

        if (isNaN(start.getTime())) throw new Error("Invalid start datetime");
        if (isNaN(end.getTime())) throw new Error("Invalid end datetime");
        if (end <= start) throw new Error("End must be after start");

        const body = {
            roomId,
            title,
            startAt: toApiLocalDateTime(start),
            endAt: toApiLocalDateTime(end),
        };

        await apiJson("/api/v1/bookings", "POST", body);

        hide(els.createModal);

        const weekKey = String(currentWeek?.weekStart || "").slice(0, 10) || null;
        await loadWeek(weekKey);
    } catch (e) {
        els.createError.textContent = e.message || "Failed to create booking";
        show(els.createError);
    }
}

function wire() {
    els.prev.addEventListener("click", () => currentWeek && loadWeek(currentWeek.previousWeek));
    els.next.addEventListener("click", () => currentWeek && loadWeek(currentWeek.nextWeek));
    els.today.addEventListener("click", () => loadWeek(null));

    els.loginBtn.addEventListener("click", doLogin);
    els.logoutBtn.addEventListener("click", doLogout);

    els.createCancelBtn.addEventListener("click", () => hide(els.createModal));
    els.createSaveBtn.addEventListener("click", createBooking);

    els.eventCloseBtn.addEventListener("click", () => hide(els.eventModal));

    els.loginPass.addEventListener("keydown", (e) => {
        if (e.key === "Enter") doLogin();
    });

    renderAvatar(els.avatar, "User");

    loadRooms();

    loadWeek(null);
}

function escapeHtml(s) {
    return String(s)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

wire();