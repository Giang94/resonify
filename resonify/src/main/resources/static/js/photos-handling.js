document.addEventListener("DOMContentLoaded", () => {
  const photoFields = document.getElementById("photo-fields");
  const addBtn = document.getElementById("add-photo-btn");

  if (!photoFields || !addBtn) return;

  // ----- Helper: create new photo row (URL input) -----
  function createNewPhotoRow(urlValue = "") {
    const div = document.createElement("div");
    div.className = "photo-item new-photo";
    div.style.marginBottom = "12px";

    const topRow = document.createElement("div");
    topRow.style.display = "flex";
    topRow.style.alignItems = "center";

    const input = document.createElement("input");
    input.type = "text";
    input.name = "photoUrls";
    input.placeholder = "Photo URL";
    input.style.flex = "1";
    input.value = urlValue;

    const removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.textContent = "✖";
    removeBtn.addEventListener("click", () => div.remove());

    topRow.appendChild(input);
    topRow.appendChild(removeBtn);

    const preview = document.createElement("img");
    preview.style.maxWidth = "120px";
    preview.style.marginTop = "5px";
    preview.style.borderRadius = "4px";
    preview.style.objectFit = "cover";
    preview.style.display = urlValue ? "block" : "none";
    if (urlValue) preview.src = urlValue;

    input.addEventListener("input", () => {
      const val = input.value.trim();
      preview.src = val;
      preview.style.display = val ? "block" : "none";
    });

    div.appendChild(topRow);
    div.appendChild(preview);
    photoFields.appendChild(div);
  }

  // ----- Add new photo row on button click -----
  addBtn.addEventListener("click", () => createNewPhotoRow());

  // ----- Handle existing photos (Base64) -----
    photoFields.querySelectorAll(".existing-photo").forEach(item => {
      const btn = item.querySelector(".delete-photo");
      const idInput = item.querySelector(".photo-id");
      const base64Input = item.querySelector(".photo-base64");
      const img = item.querySelector("img");

      let deletedInput = null; // do not pre-create

      btn.addEventListener("click", () => {
        const isDeleted = !!deletedInput; // if input exists, it's marked deleted

        if (!isDeleted) {
          // Mark as deleted
          deletedInput = document.createElement("input");
          deletedInput.type = "hidden";
          deletedInput.name = "photoDeleted"; // Spring expects UUIDs here
          deletedInput.value = idInput.value; // the actual UUID
          item.appendChild(deletedInput);

          if (img) img.style.opacity = "0.4";
          btn.style.color = "green";
          btn.textContent = "⟳";

          // Disable inputs
          idInput.disabled = true;
          if (base64Input) base64Input.disabled = true;
        } else {
          // Undo deletion
          deletedInput.remove();
          deletedInput = null;

          if (img) img.style.opacity = "1";
          btn.style.color = "red";
          btn.textContent = "✖";

          // Re-enable inputs
          idInput.disabled = false;
          if (base64Input) base64Input.disabled = false;
        }
      });
    });


});
