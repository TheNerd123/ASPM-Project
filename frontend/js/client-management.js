
"use strict";

// AP-161: Registered client listing and status display.
// AP-163 will connect the backend API to this renderer.

(function () {
  const rows = document.getElementById("client-rows");
  const count = document.getElementById("client-count");
  const notice = document.getElementById("client-notice");

  if (!rows || !count || !notice) return;

  function cell(row, value) {
    const td = document.createElement("td");
    td.textContent = value == null || value === ""
      ? "—"
      : String(value);
    row.appendChild(td);
    return td;
  }

  function showMessage(message) {
    rows.replaceChildren();

    const row = document.createElement("tr");
    const td = document.createElement("td");

    td.colSpan = 9;
    td.className = "client-empty";
    td.textContent = message;

    row.appendChild(td);
    rows.appendChild(row);
  }

  function renderClients(clients) {
    if (!Array.isArray(clients)) {
      count.textContent = "Client data unavailable";
      notice.textContent = "Unable to display client information.";
      showMessage("Client data could not be loaded.");
      return;
    }

    count.textContent =
      `${clients.length} registered client${clients.length === 1 ? "" : "s"}`;

    if (clients.length === 0) {
      showMessage("No registered scanning clients found.");
      notice.textContent = "No client connection alerts.";
      return;
    }

    rows.replaceChildren();

    let offlineCount = 0;

    clients.forEach((client) => {
      const row = document.createElement("tr");
      const status = String(client.status ?? "Unknown");
      const offline = status.toLowerCase() === "offline";

      if (offline) offlineCount++;

      cell(row, client.name);
      cell(row, client.id);
      cell(row, client.operatingSystem);

      const statusCell = cell(row, status);
      statusCell.style.fontWeight = "600";
      statusCell.style.color = offline
        ? "#dc3545"
        : status.toLowerCase() === "online"
          ? "#16845b"
          : "#64748b";

      cell(row, client.scanner);
      cell(row, client.jobsCapacity);
      cell(row, client.agentVersion);
      cell(row, client.lastHeartbeat);

      // Details and deactivation belong to AP-162.
      cell(row, "—");

      rows.appendChild(row);
    });

    notice.textContent = offlineCount
      ? `${offlineCount} scanning client${offlineCount === 1 ? " is" : "s are"} offline.`
      : "All listed clients are online or have an unknown status.";
  }

  // AP-163 will supply actual client records from the backend.
  window.ASPMClients = { renderClients };

  // Do not invent registered clients.
  renderClients([]);
})();
