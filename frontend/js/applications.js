// Applications list page: loads the applications from the backend (AP-76).
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

    console.log("Applications from backend:", applications);
    updateCount();
    if (applications.length === 0) {
      showRow("No applications registered yet.");
    } else {
      // Placeholder until AP-77 draws the rows
      showRow(applications.length + " loaded. They will be listed here in the next step.");
    }
  }

  load();
})();
