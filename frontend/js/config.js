// Central place for settings. Change API_BASE if your backend runs elsewhere
// (e.g. inside Docker Compose, use the published port, usually still 8080).
window.ASPM_CONFIG = {
  API_BASE: "http://localhost:8080",

  // Login contract (Story 2.2, AuthController):
  //   POST /api/login  { email | username, password }
  //   200 -> { token, user: { id, name, email, role } }      401 -> wrong credentials
  LOGIN_PATH: "/api/login",
  LOGIN_FIELD: "email"   // the backend matches this value against the account email or name
};
