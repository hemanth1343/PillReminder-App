function showToast(message, type = "success") {

    const toast =
        document.createElement("div");

    toast.className =
        `toast toast-${type}`;

    toast.innerHTML =
        message;

    document
        .getElementById("toastContainer")
        .appendChild(toast);

    setTimeout(() => {

        toast.remove();

    }, 4000);
}