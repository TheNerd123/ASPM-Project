// Wires up login.html: validation, show/hide password, submit, errors, redirect.
(function () {
  const form = document.getElementById("login-form");
  const identifier = document.getElementById("identifier");
  const password = document.getElementById("password");
  const remember = document.getElementById("remember");
  const submit = document.getElementById("signin");
  const message = document.getElementById("form-message");
  const toggle = document.getElementById("toggle-password");

  function showMessage(text, kind) {
    message.textContent = text;
    message.className = "message " + kind;
    message.hidden = false;
  }
  function clearMessage() { message.hidden = true; message.textContent = ""; }

  function setFieldError(input, text) {
    const err = document.getElementById(input.id + "-error");
    input.classList.toggle("invalid", !!text);
    input.setAttribute("aria-invalid", text ? "true" : "false");
    err.textContent = text || "";
    err.hidden = !text;
  }

  // Show / hide password
  toggle.addEventListener("click", () => {
    const show = password.type === "password";
    password.type = show ? "text" : "password";
    toggle.setAttribute("aria-pressed", String(show));
    toggle.setAttribute("aria-label", show ? "Hide password" : "Show password");
    document.getElementById("eye-on").hidden = !show;
    document.getElementById("eye-off").hidden = show;
    password.focus();
  });

  // Clear a field's error as soon as the user types again
  [identifier, password].forEach(input =>
    input.addEventListener("input", () => { setFieldError(input, ""); clearMessage(); })
  );

  // Features that are not part of the MVP: say so instead of a dead link
  document.getElementById("forgot").addEventListener("click", e => {
    e.preventDefault();
    showMessage("Password reset isn't available yet. Please contact your administrator.", "info");
  });
  document.getElementById("contact-admin").addEventListener("click", e => {
    e.preventDefault();
    showMessage("Ask your ASPM administrator to create an account for you.", "info");
  });

  form.addEventListener("submit", async e => {
    e.preventDefault();
    clearMessage();

    const id = identifier.value.trim();
    const pw = password.value;
    
    if (!id) setFieldError(identifier, "Enter your username or email.");
    if (!pw) setFieldError(password, "Enter your password.");
    if (!id || !pw) { (id ? password : identifier).focus(); return; }

    submit.disabled = true;
    submit.textContent = "Signing in…";

    const result = await Auth.login(id, pw);

    if (result.ok) {
      Auth.save(result.token, result.user, remember.checked);
      window.location.href = "index.html";
      return;
    }

    submit.disabled = false;
    submit.textContent = "Sign In";
    showMessage(result.message, "error");
    if (result.reason === "invalid") { password.value = ""; password.focus(); }
  });
})();
