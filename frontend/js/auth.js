// Login helpers shared by the login page (2.3) and, later, logout/guard work (2.5).
// The token is kept in sessionStorage (cleared when the tab closes), or in
// localStorage when the user ticks "Remember me".
const Auth = (() => {
  const TOKEN_KEY = "aspm_token";
  const USER_KEY = "aspm_user";

  function read(store, key) {
    try { return store.getItem(key); } catch (e) { return null; }
  }
  function write(store, key, value) {
    try { store.setItem(key, value); } catch (e) { /* storage blocked: login still works for this page view */ }
  }
  function remove(store, key) {
    try { store.removeItem(key); } catch (e) {}
  }

  function getToken() {
    return read(sessionStorage, TOKEN_KEY) || read(localStorage, TOKEN_KEY);
  }

  function getUser() {
    const raw = read(sessionStorage, USER_KEY) || read(localStorage, USER_KEY);
    try { return raw ? JSON.parse(raw) : null; } catch (e) { return null; }
  }

  function isLoggedIn() {
    return !!getToken();
  }

  function clear() {
    [sessionStorage, localStorage].forEach(s => { remove(s, TOKEN_KEY); remove(s, USER_KEY); });
  }

  function save(token, user, remember) {
    clear();
    const store = remember ? localStorage : sessionStorage;
    write(store, TOKEN_KEY, token);
    write(store, USER_KEY, JSON.stringify(user || {}));
  }

  // Returns { ok: true, token, user } or { ok: false, reason, message }.
  // reason: "invalid" | "network" | "server"
  async function login(identifier, password) {
    const cfg = window.ASPM_CONFIG;

    // Development only: lets the page be built before the login API (Story 2.2) exists.
    if (cfg.USE_MOCK_LOGIN) {
      await new Promise(r => setTimeout(r, 400));
      if (identifier === "admin@example.com" && password === "password123") {
        return { ok: true, token: "mock-token", user: { name: "Mock Admin", email: identifier, role: "ADMIN" } };
      }
      return { ok: false, reason: "invalid", message: "Incorrect username/email or password." };
    }

    let res;
    try {
      res = await fetch(cfg.API_BASE + cfg.LOGIN_PATH, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ [cfg.LOGIN_FIELD]: identifier, password })
      });
    } catch (e) {
      return { ok: false, reason: "network", message: "Can't reach the server. Check that the backend is running and try again." };
    }

    if (res.status === 401 || res.status === 403) {
      return { ok: false, reason: "invalid", message: "Incorrect username/email or password." };
    }
    if (!res.ok) {
      return { ok: false, reason: "server", message: "Sign-in failed (error " + res.status + "). Please try again or contact your administrator." };
    }

    let data = null;
    try { data = await res.json(); } catch (e) {}
    const token = data && (data.token || data.accessToken);
    if (!token) {
      return { ok: false, reason: "server", message: "The server accepted the sign-in but did not return a token. Please contact your administrator." };
    }
    const user = data.user || { name: data.name, email: data.email, username: data.username, role: data.role };
    return { ok: true, token, user };
  }

  return { getToken, getUser, isLoggedIn, clear, save, login };
})();
