function showPreview(input) {
    const files = Array.from(input.files || []);
    const previewContainer = document.getElementById("preview-container");
    const previewGrid = document.getElementById("preview-grid");
    const fileCount = document.getElementById("file-count");

    if (!files.length) {
        previewContainer.style.display = "none";
        previewGrid.innerHTML = "";
        fileCount.textContent = "0 files";
        return;
    }

    previewGrid.innerHTML = "";
    fileCount.textContent = `${files.length} file${files.length === 1 ? '' : 's'}`;
    previewContainer.style.display = "flex";

    files.forEach((file) => {
        const reader = new FileReader();

        reader.onload = function (e) {
            const card = document.createElement("div");
            card.className = "preview-card";
            card.innerHTML = `
                <img src="${e.target.result}" alt="Preview of ${file.name}">
                <div class="preview-caption">${file.name}</div>
            `;
            previewGrid.appendChild(card);
        };

        reader.readAsDataURL(file);
    });
}

// Add form submission handler
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('propertyForm');
    const submitBtn = form.querySelector('button[type="submit"]');
    const isEdit = form.dataset.isEdit === 'true';
    const originalSubmitLabel = submitBtn.innerHTML;

    form.addEventListener('submit', function(e) {
        // Disable submit button to prevent double submission
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="ph-bold ph-spinner"></i> Uploading...';

        // Validate form
        const title = document.getElementById('title').value.trim();
        const location = document.getElementById('location').value.trim();
        const price = document.getElementById('price').value;
        const fileInput = document.getElementById('fileInput');
        const selectedFiles = Array.from(fileInput.files || []);

        if (!title || !location || !price) {
            e.preventDefault();
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalSubmitLabel;
            alert('Please fill all required fields');
            return false;
        }

        if (!isEdit && selectedFiles.length === 0) {
            e.preventDefault();
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalSubmitLabel;
            alert('Please select at least one property photo');
            return false;
        }

        const invalidFile = selectedFiles.find((file) => !file.type.startsWith('image/'));
        if (invalidFile) {
            e.preventDefault();
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalSubmitLabel;
            alert(`Invalid file type: ${invalidFile.name}. Please select image files only.`);
            return false;
        }

        const oversizedFile = selectedFiles.find((file) => file.size > 10 * 1024 * 1024);
        if (oversizedFile) {
            e.preventDefault();
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalSubmitLabel;
            alert(`${oversizedFile.name} is larger than 10MB.`);
            return false;
        }
    });
});
