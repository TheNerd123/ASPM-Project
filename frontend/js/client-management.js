
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

    // AP-162: Client details and deactivation controls.
const actionsCell = document.createElement("td");

const detailsButton = document.createElement("button");
detailsButton.type = "button";
detailsButton.textContent = "View Details";
detailsButton.className = "btn btn-secondary";

detailsButton.addEventListener("click", () => {
  const details = [
    `Client Name: ${client.name ?? "N/A"}`,
    `Client ID: ${client.id ?? "N/A"}`,
    `Operating System: ${client.operatingSystem ?? "N/A"}`,
    `Status: ${client.status ?? "Unknown"}`,
    `Scanner: ${client.scanner ?? "N/A"}`,
    `Jobs / Capacity: ${client.jobsCapacity ?? "N/A"}`,
    `Agent Version: ${client.agentVersion ?? "N/A"}`,
    `Last Heartbeat: ${client.lastHeartbeat ?? "N/A"}`
  ];

  window.alert(details.join("\n"));
});

actionsCell.appendChild(detailsButton);

// AP-162: Client deactivation control.
const deactivateButton = document.createElement("button");
deactivateButton.type = "button";
deactivateButton.textContent = "Deactivate";
deactivateButton.className = "btn btn-secondary";
deactivateButton.style.marginLeft = "8px";

deactivateButton.addEventListener("click", () => {
  const confirmed = window.confirm(
    `Are you sure you want to deactivate ${client.name ?? "this client"}?`
  );

  if (!confirmed) return;

  window.alert(
    "Client deactivation is not available yet. " +
    "No changes have been made. Backend integration is pending."
  );
});

actionsCell.appendChild(deactivateButton);
row.appendChild(actionsCell);

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
