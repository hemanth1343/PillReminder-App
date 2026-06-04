function loadProfileImage(){

    const image =
        localStorage.getItem(
            "profileImage"
        );

    if(image){

        document
            .querySelectorAll(
                "#profilePreview"
            )
            .forEach(img => {

                img.src =
                    image;
            });
    }
}

window.addEventListener(

    "load",

    loadProfileImage
);