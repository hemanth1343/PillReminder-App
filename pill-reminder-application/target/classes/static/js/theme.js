function applyTheme(){

    const savedTheme =

        localStorage.getItem("theme")

        || "dark";

    document.body.classList.remove(

        "dark-theme",

        "light-theme",

        "dim-theme"
    );

    document.body.classList.add(

        savedTheme + "-theme"
    );
}

document.addEventListener(

    "DOMContentLoaded",

    applyTheme
);