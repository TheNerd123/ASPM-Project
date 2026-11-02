
const searchInput = document.getElementById("user-search");
const userRows = document.querySelectorAll("tbody tr");

searchInput.addEventListener("input", function () {
  const searchTerm = searchInput.value.toLowerCase().trim();

  userRows.forEach(function (row) {
    const name = row.cells[0].textContent.toLowerCase();
    const email = row.cells[1].textContent.toLowerCase();

    const matches =
      name.includes(searchTerm) ||
      email.includes(searchTerm);

    row.hidden = !matches;
  });
});
