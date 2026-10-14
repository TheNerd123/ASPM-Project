// Small fetch wrapper used by every page.
// Later sprints: login (2.3) stores a token in sessionStorage under "aspm_token",
// and this helper attaches it automatically.
async function api(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  let token = null;
  try { token = sessionStorage.getItem("aspm_token"); } catch (e) {}
  if (token) headers["Authorization"] = "Bearer " + token;

  const res = await fetch(window.ASPM_CONFIG.API_BASE + path, { ...options, headers });
  if (!res.ok) throw new Error("HTTP " + res.status);
  return res.status === 204 ? null : res.json();
}
