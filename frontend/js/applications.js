// Applications list page: loads the applications and shows them in the table.
(function () {
  const rows = document.getElementById("app-rows");
  const count = document.getElementById("app-count");
  const COLS = 5;

  let applications = [];

  // One message row inside the table (loading, empty, error)
  function showRow(text, retry) {
    const tr = document.createElement("tr");
    const td = document.createElement("td");
    td.colSpan = COLS;
    td.className = "empty";
    td.textContent = text;
    if (retry) {
      const btn = document.createElement("button");
      btn.type = "button";
      btn.className = "btn retry";
      btn.textContent = "Try again";
      btn.addEventListener("click", load);
      td.append(document.createElement("br"), btn);
    }
    tr.append(td);
    rows.replaceChildren(tr);
  }

  function updateCount() {
    const n = applications.length;
    count.textContent = n === 1 ? "1 application" : n + " applications";
  }

  // ---------- AP-77: display application information ----------
  // Text is always set with textContent (never innerHTML) so an application name
  // such as "<b>x</b>" is shown as text and can't inject markup.
  function cell(...children) {
    const td = document.createElement("td");
    td.append(...children);
    return td;
  }

  function nameCell(app) {
    const span = document.createElement("span");
    span.className = "app-name";
    span.textContent = app.name || "Unnamed application";
    return cell(span);
  }

  function repoCell(app) {
    const url = app.repositoryUrl;
    // Only link real web addresses (blocks things like javascript: URLs)
    if (url && /^https?:\/\//i.test(url)) {
      const a = document.createElement("a");
      a.className = "repo-link";
      a.href = url;
      a.target = "_blank";
      a.rel = "noopener noreferrer";
      a.textContent = url.replace(/^https?:\/\//i, "");
      return cell(a);
    }
    const none = document.createElement("span");
    none.className = "muted";
    none.textContent = url || "—";
    return cell(none);
  }

  function statusCell(app) {
    const badge = document.createElement("span");
    const archived = String(app.status).toUpperCase() === "ARCHIVED";
    badge.className = "badge " + (archived ? "queued" : "completed");   // grey vs green, from style.css
    badge.textContent = archived ? "Archived" : "Active";
    return cell(badge);
  }

  function createdCell(app) {
    const d = new Date(app.createdAt);
    const text = isNaN(d) ? "—" : d.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
    return cell(text);
  }

  function render() {
    if (applications.length === 0) {
      showRow("No applications registered yet.");
      return;
    }
    const trs = applications.map(app => {
      const tr = document.createElement("tr");
      tr.append(nameCell(app), repoCell(app), statusCell(app), createdCell(app), cell()); // last cell: actions, filled in AP-78
      return tr;
    });
    rows.replaceChildren(...trs);
  }

  async function load() {
    showRow("Loading applications…");
    try {
      const data = await api("/api/applications");
      applications = Array.isArray(data) ? data : [];
    } catch (e) {
      count.textContent = "Manage the applications that are scanned.";
      showRow("Couldn't load applications. Check that the backend is running.", true);
      return;
    }
    updateCount();
    render();
  }

  load();
})();
