// Application details page. Reached from the list via application-details.html?id=<application id>.
// The backend has no "get one application" endpoint yet, so this reads the list and picks the matching id.
(function () {
  const title = document.getElementById("detail-title");
  const sub = document.getElementById("detail-sub");
  const card = document.getElementById("detail-card");
  const list = document.getElementById("detail-list");
  const message = document.getElementById("detail-message");

  function showMessage(text) {
    sub.textContent = "";
    card.hidden = true;
    message.hidden = false;
    message.textContent = text;
  }

  function addRow(label, value, href) {
    const dt = document.createElement("dt");
    dt.textContent = label;
    const dd = document.createElement("dd");
    if (href) {
      const a = document.createElement("a");
      a.className = "repo-link";
      a.href = href;
      a.target = "_blank";
      a.rel = "noopener noreferrer";
      a.textContent = value;
      dd.append(a);
    } else {
      dd.textContent = value;
    }
    list.append(dt, dd);
  }

  async function load() {
    const id = new URLSearchParams(window.location.search).get("id");
    if (!id) { showMessage("No application selected. Go back and choose one from the list."); return; }

    let apps;
    try {
      apps = await api("/api/applications");
    } catch (e) {
      showMessage("Couldn't load this application. Check that the backend is running.");
      return;
    }

    const app = (Array.isArray(apps) ? apps : []).find(a => a.id === id);
    if (!app) { showMessage("Application not found. It may have been removed."); return; }

    const url = app.repositoryUrl;
    const created = new Date(app.createdAt);
    title.textContent = app.name || "Unnamed application";
    sub.textContent = "Application details";
    document.title = (app.name || "Application") + " | ASPM";

    addRow("Name", app.name || "—");
    addRow("Repository", url || "—", url && /^https?:\/\//i.test(url) ? url : null);
    addRow("Status", String(app.status).toUpperCase() === "ARCHIVED" ? "Archived" : "Active");
    addRow("Created", isNaN(created) ? "—" : created.toLocaleString());
    addRow("ID", app.id);
    card.hidden = false;
  }

  load();
})();
