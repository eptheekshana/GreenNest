function showPreview(input) {
    const file = input.files && input.files[0];
    const previewContainer = document.getElementById("preview-container");
    const previewImg = document.getElementById("preview-img");
    const fileName = document.getElementById("file-name");

    if (!file) {
        previewContainer.style.display = "none";
        previewImg.src = "";
        fileName.textContent = "";
        return;
    }

    const reader = new FileReader();
    reader.onload = function (e) {
        previewImg.src = e.target.result;
        fileName.textContent = file.name;
        previewContainer.style.display = "flex";
    };
    reader.readAsDataURL(file);
}

// Add form submission handler
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('propertyForm');
    const submitBtn = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', function(e) {
        // Disable submit button to prevent double submission
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="ph-bold ph-spinner"></i> Uploading...';

        // Validate form
        const title = document.getElementById('title').value.trim();
        const location = document.getElementById('location').value.trim();
        const price = document.getElementById('price').value;
        const fileInput = document.getElementById('fileInput');

        if (!title || !location || !price || !fileInput.files[0]) {
            e.preventDefault();
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="ph-bold ph-check-circle"></i> Publish Listing';
            alert('Please fill all required fields and select an image');
            return false;
        }

        // Check file size (max 10MB)
        if (fileInput.files[0].size > 10 * 1024 * 1024) {
            e.preventDefault();
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="ph-bold ph-check-circle"></i> Publish Listing';
            alert('Image size must be less than 10MB');
            return false;
        }
    });
});
