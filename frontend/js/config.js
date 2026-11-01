// Central place for settings. Change API_BASE if your backend runs elsewhere
// (e.g. inside Docker Compose, use the published port, usually still 8080).
window.ASPM_CONFIG = {
  API_BASE: "http://localhost:8080",

  // Login contract (Story 2.2). Match these to what the backend login endpoint expects.
  LOGIN_PATH: "/api/login",      // docs/DATABASE_DESIGN.md lists POST /api/login
  LOGIN_FIELD: "email",          // JSON field name for the "Username/email" box: "email" or "username"

  // DEVELOPMENT ONLY. Set to true to try the login page before the login API exists
  // (accepts admin@example.com / password123). Set back to false and delete once 2.2 is merged.
  USE_MOCK_LOGIN: true
};
