
const searchInput = document.getElementById("user-search");
const userTable = document.querySelector("tbody");

const addUserButton = document.getElementById("add-user-btn");
const cancelUserButton = document.getElementById("cancel-user-btn");
const userForm = document.getElementById("add-user-form");

const nameInput = document.getElementById("new-user-name");
const emailInput = document.getElementById("new-user-email");
const roleInput = document.getElementById("new-user-role");
const formError = document.getElementById("user-form-error");

// These elements already exist inside our form.
const formTitle = userForm.querySelector("h2");
const saveButton = userForm.querySelector('button[type="submit"]');

// null means we're adding a user.
// Otherwise, this holds the row we're editing.
let editingRow = null;

// Search users by name or email.
function filterUsers() {
  const searchTerm = searchInput.value.toLowerCase().trim();

  userTable.querySelectorAll("tr").forEach(function (row) {
    const name = row.cells[0].textContent.toLowerCase();
    const email = row.cells[1].textContent.toLowerCase();

    const matches =
      name.includes(searchTerm) ||
      email.includes(searchTerm);

    row.hidden = !matches;
  });
}

searchInput.addEventListener("input", filterUsers);

// Reset and close the form.
function closeForm() {
  userForm.reset();
  userForm.hidden = true;
  formError.textContent = "";
  editingRow = null;
}

// Open the form in Add mode.
addUserButton.addEventListener("click", function () {
  editingRow = null;

  userForm.reset();
  formError.textContent = "";

  formTitle.textContent = "Add New User";
  saveButton.textContent = "Save User";

  userForm.hidden = false;
  nameInput.focus();
});

// Close the form when Cancel is clicked.
cancelUserButton.addEventListener("click", closeForm);

// Handle clicks on Edit buttons.
userTable.addEventListener("click", function (event) {
  const editButton = event.target.closest(".edit-user-btn");

  if (!editButton) {
    return;
  }

  editingRow = editButton.closest("tr");

  // Read the existing values from the selected row.
  nameInput.value = editingRow.cells[0].textContent.trim();
  emailInput.value = editingRow.cells[1].textContent.trim();
  roleInput.value = editingRow.cells[2].textContent.trim();

  formTitle.textContent = "Edit User";
  saveButton.textContent = "Save Changes";
  formError.textContent = "";

  userForm.hidden = false;
  nameInput.focus();
});

// Save a new user or update an existing one.
userForm.addEventListener("submit", function (event) {
  event.preventDefault();

  const name = nameInput.value.trim();
  const email = emailInput.value.trim();
  const role = roleInput.value;

  if (!name || !email) {
    formError.textContent = "Name and email are required.";
    return;
  }

  // Check for duplicate emails, excluding the row being edited.
  const existingRows = userTable.querySelectorAll("tr");

  for (const row of existingRows) {
    if (row === editingRow) {
      continue;
    }

    const existingEmail = row.cells[1].textContent.trim();

    if (existingEmail.toLowerCase() === email.toLowerCase()) {
      formError.textContent = "This email address already exists.";
      return;
    }
  }

  if (editingRow !== null) {
    // EDIT: Update the existing row.
    editingRow.cells[0].textContent = name;
    editingRow.cells[1].textContent = email;
    editingRow.cells[2].textContent = role;

  } else {
    // ADD: Create a completely new row.
    const newRow = document.createElement("tr");

    newRow.insertCell().textContent = name;
    newRow.insertCell().textContent = email;
    newRow.insertCell().textContent = role;
    newRow.insertCell().textContent = "Active";

    // Give the new user an Edit button.
    const actionsCell = newRow.insertCell();

    const editButton = document.createElement("button");
    editButton.type = "button";
    editButton.className = "btn edit-user-btn";
    editButton.textContent = "Edit";

    actionsCell.appendChild(editButton);
    userTable.appendChild(newRow);
  }

  closeForm();
  filterUsers();
});
