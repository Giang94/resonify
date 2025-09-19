document.addEventListener("DOMContentLoaded", () => {
  // ===== Artist Tagging =====
  const container = document.querySelector('.tag-input-container');
  if (container) {
    const input = container.querySelector('#artist-input');
    const hiddenField = document.querySelector('#artists-hidden');
    if (!hiddenField) return;

    let tags = hiddenField.value ? hiddenField.value.split(',') : [];

    const renderTags = () => {
      container.querySelectorAll('.tag').forEach(t => t.remove());

      tags.forEach((tag, index) => {
        const tagEl = document.createElement('span');
        tagEl.className = 'tag';
        tagEl.style.cssText = 'background:#1976d2; color:white; padding:2px 6px; border-radius:4px; display:flex; align-items:center; gap:4px;';
        tagEl.textContent = tag;

        const removeBtn = document.createElement('button');
        removeBtn.type = 'button';
        removeBtn.textContent = 'x';
        removeBtn.style.cssText = 'background:none; border:none; color:white; cursor:pointer; font-weight:bold; padding:2px;';
        removeBtn.addEventListener('click', () => {
          tags.splice(index, 1);
          renderTags();
        });

        tagEl.appendChild(removeBtn);
        container.insertBefore(tagEl, input);
      });

      hiddenField.value = tags.join(',');
    };

    input.addEventListener('keydown', (e) => {
      if (e.key === ',') {
        e.preventDefault();
        const val = input.value.trim();
        if (val) {
          tags.push(val);
          input.value = '';
          renderTags();
        }
      }
    });

    renderTags();

    const form = container.closest('form');
    if (form) {
      form.addEventListener('submit', (e) => {
        const val = input.value.trim();
        if (val && !tags.includes(val)) {
          tags.push(val);
          input.value = '';
          hiddenField.value = tags.join(',');
        }
      });
    }
  }

});
