export function renderAvatar(el, username) {
    const initials = getInitials(username);
    el.style.background = colorFromString(username || "user");
    el.textContent = initials;
}

function getInitials(s) {
    if (!s) return "?";
    const parts = s.trim().split(/[\s._-]+/).filter(Boolean);
    const a = (parts[0]?.[0] || "?").toUpperCase();
    const b = (parts[1]?.[0] || parts[0]?.[1] || "").toUpperCase();
    return (a + b).slice(0, 2);
}

function colorFromString(s) {
    let h = 0;
    for (let i = 0; i < s.length; i++) h = (h * 31 + s.charCodeAt(i)) >>> 0;
    const hue = h % 360;
    return `hsl(${hue} 72% 42%)`;
}