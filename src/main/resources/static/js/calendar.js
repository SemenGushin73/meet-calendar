const HOUR_HEIGHT = 60;
const DAY_START_HOUR = 0;
const DAY_END_HOUR = 24;

export function renderCalendar(container, model, onEmptyClick, onEventClick) {
    container.innerHTML = "";

    const header = buildHeader(model?.weekStart);
    container.appendChild(header);

    const grid = document.createElement("div");
    grid.className = "grid";

    const timeCol = document.createElement("div");
    timeCol.className = "time-col";
    for (let h = DAY_START_HOUR; h < DAY_END_HOUR; h++) {
        const row = document.createElement("div");
        row.className = "time-row";
        row.style.height = `${HOUR_HEIGHT}px`;
        row.textContent = `${String(h).padStart(2, "0")}:00`;
        timeCol.appendChild(row);
    }
    grid.appendChild(timeCol);

    const days = getWeekDays(model?.weekStart);
    const columnsWrap = document.createElement("div");
    columnsWrap.className = "days-wrap";

    const bookings = (model?.bookings || []).map(normalizeBooking);

    for (const day of days) {
        const col = document.createElement("div");
        col.className = "day-col";
        col.dataset.dayKey = day.key;
        col.style.height = `${(DAY_END_HOUR - DAY_START_HOUR) * HOUR_HEIGHT}px`;

        for (let h = DAY_START_HOUR; h < DAY_END_HOUR; h++) {
            const slot = document.createElement("div");
            slot.className = "slot";
            slot.style.height = `${HOUR_HEIGHT}px`;
            col.appendChild(slot);
        }

        col.addEventListener("click", (e) => {
            if (e.target.closest(".event")) return;

            const rect = col.getBoundingClientRect();
            const y = e.clientY - rect.top;
            const minutes = snapTo30(Math.round((y / HOUR_HEIGHT) * 60));

            const start = dayKeyToLocalDateTime(day.key, minutes);
            const end = addMinutes(start, 60);

            onEmptyClick({dayKey: day.key, start, end});
        });

        const events = bookings.filter(b => b.dayKey === day.key);
        for (const b of events) {
            const ev = document.createElement("div");
            ev.className = `event status-${(b.status || "UNKNOWN").toLowerCase()}`;
            ev.style.top = `${b.topPx}px`;
            ev.style.height = `${Math.max(18, b.heightPx)}px`;

            ev.innerHTML = `
        <div class="event-title">${escapeHtml(b.title || "(no title)")}</div>
        <div class="event-room">${escapeHtml(b.roomName || "")}</div>
      `;

            ev.addEventListener("click", (e) => {
                e.stopPropagation();
                onEventClick(b);
            });

            col.appendChild(ev);
        }

        columnsWrap.appendChild(col);
    }

    grid.appendChild(columnsWrap);
    container.appendChild(grid);
}

export function formatWeekLabel(model) {
    const s = String(model?.weekStart || "").slice(0, 10).split("-");
    const e = String(model?.weekEnd || "").slice(0, 10).split("-");
    if (s.length === 3 && e.length === 3) return `${s[2]}.${s[1]} — ${e[2]}.${e[1]}`;
    return "";
}

export function toDateTimeLocalValue(date) {
    const y = date.getFullYear();
    const m = pad2(date.getMonth() + 1);
    const d = pad2(date.getDate());
    const hh = pad2(date.getHours());
    const mm = pad2(date.getMinutes());
    return `${y}-${m}-${d}T${hh}:${mm}`;
}

export function toApiLocalDateTime(date) {
    const y = date.getFullYear();
    const m = pad2(date.getMonth() + 1);
    const d = pad2(date.getDate());
    const hh = pad2(date.getHours());
    const mm = pad2(date.getMinutes());
    return `${y}-${m}-${d}T${hh}:${mm}:00`;
}

function buildHeader(weekStartIso) {
    const header = document.createElement("div");
    header.className = "cal-header";

    const left = document.createElement("div");
    left.className = "cal-header-left";

    const daysRow = document.createElement("div");
    daysRow.className = "days-header";

    const days = getWeekDays(weekStartIso);
    for (const d of days) {
        const cell = document.createElement("div");
        cell.className = "day-head";
        cell.innerHTML = `<div class="dow">${d.dow}</div><div class="dom">${d.dom}</div>`;
        daysRow.appendChild(cell);
    }

    header.appendChild(left);
    header.appendChild(daysRow);
    return header;
}

function getWeekDays(weekStartIso) {
    const baseStr = weekStartIso ? String(weekStartIso).slice(0, 10) : todayKey();
    const [y, m, d] = baseStr.split("-").map(Number);
    const base = new Date(y, m - 1, d, 12, 0, 0, 0);

    const names = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
    const out = [];
    for (let i = 0; i < 7; i++) {
        const dd = new Date(base);
        dd.setDate(base.getDate() + i);
        const key = `${dd.getFullYear()}-${pad2(dd.getMonth() + 1)}-${pad2(dd.getDate())}`;
        out.push({key, dow: names[i], dom: String(dd.getDate())});
    }
    return out;
}

function normalizeBooking(b) {
    const startRaw = b.startAt || b.start || b.start_time || b.startDateTime;
    const endRaw = b.endAt || b.end || b.end_time || b.endDateTime;

    const start = parseLocalIso(startRaw);
    const end = parseLocalIso(endRaw);

    const dayKey =
        b.dayKey ||
        (start?.dateKey) ||
        (typeof startRaw === "string" ? startRaw.slice(0, 10) : null);

    let topPx = b.topPx;
    let heightPx = b.heightPx;

    if ((topPx == null || heightPx == null) && start && end) {
        const startMin = start.hh * 60 + start.mm;
        const endMin = end.hh * 60 + end.mm;
        topPx = Math.round((startMin / 60) * HOUR_HEIGHT);
        heightPx = Math.max(18, Math.round(((endMin - startMin) / 60) * HOUR_HEIGHT));
    }

    return {
        ...b,
        dayKey,
        topPx: topPx ?? 0,
        heightPx: heightPx ?? 18,
        startAt: b.startAt || startRaw,
        endAt: b.endAt || endRaw,
    };
}

function parseLocalIso(v) {
    if (!v || typeof v !== "string") return null;
    const m = v.match(/^(\d{4}-\d{2}-\d{2})T(\d{2}):(\d{2})/);
    if (!m) return null;
    return {dateKey: m[1], hh: Number(m[2]), mm: Number(m[3])};
}

function snapTo30(min) {
    return Math.round(min / 30) * 30;
}

function dayKeyToLocalDateTime(dayKey, minutesFromMidnight) {
    const [y, m, d] = dayKey.split("-").map(Number);
    const hh = Math.floor(minutesFromMidnight / 60);
    const mm = minutesFromMidnight % 60;
    return new Date(y, m - 1, d, hh, mm, 0, 0);
}

function addMinutes(date, minutes) {
    const d = new Date(date);
    d.setMinutes(d.getMinutes() + minutes);
    return d;
}

function todayKey() {
    const t = new Date();
    return `${t.getFullYear()}-${pad2(t.getMonth() + 1)}-${pad2(t.getDate())}`;
}

function pad2(n) {
    return String(n).padStart(2, "0");
}

function escapeHtml(s) {
    return String(s)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}