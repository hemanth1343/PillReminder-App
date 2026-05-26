function showToast(

    message,

    type = "success"

){

    const toast =
        document.createElement(
            "div"
        );

    toast.className =
        `toast toast-${type}`;

    toast.innerHTML = `

<div class="toast-content">

    <span class="toast-icon">

        ${
            type === "success"

            ? "✅"

            :

            type === "error"

            ? "❌"

            :

            type === "warning"

            ? "⚠️"

            :

            "🚨"
        }

    </span>

    <span>

        ${message}

    </span>

</div>

`;

    document
        .getElementById(
            "toastContainer"
        )
        .appendChild(toast);

    // SHOW ANIMATION

    setTimeout(() => {

        toast.classList.add(
            "show"
        );

    }, 100);

    // REMOVE

    setTimeout(() => {

        toast.classList.remove(
            "show"
        );

        setTimeout(() => {

            toast.remove();

        }, 500);

    }, 4000);
}