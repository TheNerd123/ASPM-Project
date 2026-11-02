
const searchInput = document.getElementById("user-search");
const userTable = document.querySelector("tbody");

const addUserButton = document.getElementById("add-user-btn");
const cancelUserButton = document.getElementById("cancel-user-btn");
const addUserForm = document.getElementById("add-user-form");

const nameInput = document.getElementById("new-user-name");
const emailInput = document.getElementById("new-user-email");
const roleInput = document.getElementById("new-user-role");
const formError = document.getElementById("user-form-error");

// Search the table by name or email.
function filterUsers() {
  const searchTerm = searchInput.value.toLowerCase().trim();

  const userRows = userTable.querySelectorAll("tr");

  userRows.forEach(function (row) {
    const name = row.cells[0].textContent.toLowerCase();
    const email = row.cells[1].textContent.toLowerCase();

    const matches =
      name.includes(searchTerm) ||
      email.includes(searchTerm);

    row.hidden = !matches;
  });
}

searchInput.addEventListener("input", filterUsers);

// Show the form when Add User is clicked.
addUserButton.addEventListener("click", function () {
  addUserForm.hidden = false;
  formError.textContent = "";
  nameInput.focus();
});

// Hide the form when Cancel is clicked.
cancelUserButton.addEventListener("click", function () {
  addUserForm.reset();
  addUserForm.hidden = true;
  formError.textContent = "";
});

// Add a sample user when the form is submitted.
addUserForm.addEventListener("submit", function (event) {
  event.preventDefault();

  const name = nameInput.value.trim();
  const email = emailInput.value.trim();
  const role = roleInput.value;

  if (!name || !email) {
    formError.textContent = "Name and email are required.";
    return;
  }

  // Prevent duplicate email addresses.
  const existingRows = userTable.querySelectorAll("tr");

  for (const row of existingRows) {
    const existingEmail = row.cells[1].textContent.trim();

    if (existingEmail.toLowerCase() === email.toLowerCase()) {
      formError.textContent = "This email address already exists.";
      return;
    }
  }

  // Create a new table row.
  const newRow = document.createElement("tr");

  const nameCell = newRow.insertCell();
  nameCell.textContent = name;

  const emailCell = newRow.insertCell();
  emailCell.textContent = email;

  const roleCell = newRow.insertCell();
  roleCell.textContent = role;

  const statusCell = newRow.insertCell();
  statusCell.textContent = "Active";

  userTable.appendChild(newRow);

  // Clear and close the form.
  addUserForm.reset();
  addUserForm.hidden = true;
  formError.textContent = "";

  // Reapply any active search.
  filterUsers();
});
