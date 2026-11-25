// Scanner management page: loads scanners from the backend and shows their status.
(function () {
  const rows = document.getElementById("scanner-rows");
  const count = document.getElementById("scanner-count");
  const COLS = 4;

  let scanners = [];

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
    const n = scanners.length;
    count.textContent = n === 1 ? "1 scanner" : n + " scanners";
  }

  function cell(...children) {
    const td = document.createElement("td");
    td.append(...children);
    return td;
  }

  // ---------- AP-153: display scanner information ----------
  function nameCell(s) {
    const span = document.createElement("span");
    span.className = "scanner-name";
    span.textContent = s.name || "Unnamed scanner";
    return cell(span);
  }

  function typeCell(s) {
    return cell(document.createTextNode(s.type || "—"));
  }

  function statusBadge(enabled) {
    const badge = document.createElement("span");
    badge.className = "badge " + (enabled ? "completed" : "queued"); // green vs grey, from style.css
    badge.textContent = enabled ? "Enabled" : "Disabled";
    return badge;
  }

  function statusCell(s) {
    return cell(statusBadge(s.enabled));
  }

  function render() {
    if (scanners.length === 0) {
      showRow("No scanners registered yet.");
      return;
    }
    const trs = scanners.map(s => {
      const tr = document.createElement("tr");
      tr.append(nameCell(s), typeCell(s), statusCell(s), cell()); // last cell: controls, added in AP-154
      return tr;
    });
    rows.replaceChildren(...trs);
  }

  async function load() {
    showRow("Loading scanners…");
    try {
      const data = await api("/api/scanners");
      scanners = Array.isArray(data) ? data : [];
    } catch (e) {
      count.textContent = "View and control the scanners available to ASPM.";
      showRow("Couldn't load scanners. Check that the backend is running.", true);
      return;
    }
    updateCount();
    render();
  }

  load();
})();
