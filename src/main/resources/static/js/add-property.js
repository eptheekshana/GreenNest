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

