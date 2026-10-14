// Builds the sidebar + top bar once so every page shares them.
// Each page only needs: <body data-page="..." data-title="..."> and <main id="content">.
(function () {
  const NAV = [
    { id: "dashboard",    label: "Dashboard",    href: "index.html" },
    { id: "applications", label: "Applications", href: "applications.html" },
    { id: "scanners",     label: "Scanners",     href: "scanners.html" },
    { id: "scans",        label: "Scans",        href: "scans.html" },
    { id: "findings",     label: "Findings",     href: "findings.html" },
    { id: "reports",      label: "Reports",      href: "reports.html" }
  ];

  const page = document.body.dataset.page;
  const title = document.body.dataset.title || "ASPM";

  const navHtml = NAV.map(n =>
    `<a href="${n.href}" class="${n.id === page ? "active" : ""}">${n.label}</a>`
  ).join("");

  const shell = document.createElement("div");
  shell.className = "shell";
  shell.innerHTML = `
    <aside class="sidebar">
      <div class="logo"><span class="logo-icon">&#9921;</span> ASPM</div>
      <div class="workspace">
        <span class="avatar sq">AC</span>
        <div><strong>Acme Engineering</strong><small>Production workspace</small></div>
      </div>
      <div class="nav-label">WORKSPACE</div>
      <nav class="nav">${navHtml}</nav>
      <div class="sidebar-bottom">
        <div class="platform-status">
          <strong><span class="dot" id="api-dot"></span><span id="api-text">Checking...</span></strong>
          <small>ASPM v0.1</small>
        </div>
        <a href="#" class="help">Help &amp; documentation</a>
      </div>
    </aside>
    <div class="main">
      <header class="topbar">
        <div>Workspace &gt; <strong>${title}</strong></div>
        <div class="user">
          <span class="avatar">JD</span>
          <div><strong>John Doe</strong><small>Security administrator</small></div>
        </div>
      </header>
    </div>`;

  const content = document.getElementById("content");
  document.body.prepend(shell);
  shell.querySelector(".main").appendChild(content);

  // Test API call: drives the "Platform operational" card (Story 1.4: frontend talks to backend)
  const dot = document.getElementById("api-dot");
  const text = document.getElementById("api-text");
  api("/api/health")
    .then(() => { dot.className = "dot up"; text.textContent = "Platform operational"; })
    .catch(() => { dot.className = "dot down"; text.textContent = "Backend unreachable"; });
})();
