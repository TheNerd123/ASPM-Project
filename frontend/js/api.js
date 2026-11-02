// Small fetch wrapper used by every page.
// The login page stores the token in sessionStorage, or in localStorage when
// "Remember me" is ticked, under "aspm_token". This helper attaches it automatically.
function getStoredToken() {
  try {
    return sessionStorage.getItem("aspm_token") || localStorage.getItem("aspm_token");
  } catch (e) {
    return null;
  }
}

async function api(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  const token = getStoredToken();
  if (token) headers["Authorization"] = "Bearer " + token;

  const res = await fetch(window.ASPM_CONFIG.API_BASE + path, { ...options, headers });
  if (!res.ok) throw new Error("HTTP " + res.status);
  return res.status === 204 ? null : res.json();
}
