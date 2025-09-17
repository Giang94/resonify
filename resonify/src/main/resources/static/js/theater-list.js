document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".photo-editor").forEach(editor => {
        let img = editor.querySelector(".photo-thumb");
        const deleteBtn = editor.querySelector(".photo-delete-btn");
        const changeBtn = editor.querySelector(".photo-change-btn");
        const undoBtn = editor.querySelector(".photo-undo-btn");
        const input = editor.querySelector(".photo-url-input");
        const actionInput = editor.querySelector(".photo-action-input");
        const hiddenUrl = editor.querySelector(".photo-url-hidden");

        const originalPhoto = hiddenUrl.value; // could be empty

        const showInput = (show) => {
            input.style.display = show ? "block" : "none";
        };
        const showThumbnail = (show) => {
            if (img) img.style.display = show ? "block" : "none";
        };
        const showBtns = (show) => {
            if (deleteBtn) deleteBtn.style.display = show ? "inline-block" : "none";
            if (changeBtn) changeBtn.style.display = show ? "inline-block" : "none";
            if (undoBtn) undoBtn.style.display = show ? "inline-block" : "none";
        };

        // INITIAL STATE
        // INITIAL STATE
        undoBtn.style.display = "none"; // always hide undo at start
        if (!originalPhoto) {
            showInput(true);
            showThumbnail(false);
            deleteBtn.style.display = "none";
            changeBtn.style.display = "none";
            actionInput.value = "NEW";
        } else {
            showInput(false);
            showThumbnail(true);
            deleteBtn.style.display = "inline-block";
            changeBtn.style.display = "inline-block";
            actionInput.value = "KEEP";
        }

        // Delete behavior
        deleteBtn?.addEventListener("click", () => {
            showThumbnail(false);
            showInput(true);
            showBtns(false);
            undoBtn.style.display = "inline-block";
            actionInput.value = "DELETE";
            input.value = "";
            hiddenUrl.value = ""; // sync hidden
        });

        // Change behavior
        changeBtn?.addEventListener("click", () => {
            showInput(true);
            undoBtn.style.display = "inline-block";
            actionInput.value = "CHANGE";
            input.value = hiddenUrl.value;
        });

        // Undo behavior
        undoBtn?.addEventListener("click", () => {
            if (originalPhoto) {
                showThumbnail(true);
                showBtns(true);
                showInput(false);
                actionInput.value = "KEEP";
                input.value = "";
                hiddenUrl.value = originalPhoto; // restore hidden
            } else {
                showThumbnail(false);
                showBtns(false);
                showInput(true);
                actionInput.value = "NEW";
                input.value = "";
                hiddenUrl.value = "";
            }
            undoBtn.style.display = "none";
        });

        // Update thumbnail on input change if REPLACE
        input.addEventListener("input", () => {
            if (actionInput.value === "CHANGE" || actionInput.value === "NEW") {
                if (!img) {
                    const newImg = document.createElement("img");
                    newImg.className = "photo-thumb";
                    newImg.style.width = "80px";
                    newImg.style.borderRadius = "4px";
                    editor.insertBefore(newImg, input);
                    img = newImg;
                }
                img.src = input.value;
                img.style.display = input.value ? "block" : "none";

                // Update hidden input
                hiddenUrl.value = input.value;
            }
        });
    });
});
