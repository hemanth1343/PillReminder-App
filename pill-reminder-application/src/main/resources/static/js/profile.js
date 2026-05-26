const API_URL =
    "http://localhost:8080/api";

async function loadProfile() {

    const token =
        localStorage.getItem("token");

    try {

        const response =
            await fetch(
                API_URL + "/users/profile",
                {
                    headers: {
                        Authorization:
                            "Bearer " + token
                    }
                }
            );

        if(!response.ok){

            showToast(
                "Failed to load profile",
                "error"
            );

            return;
        }

        const user =
            await response.json();

        document.getElementById(
            "fullName"
        ).value =
            user.fullName || "";

        document.getElementById(
            "email"
        ).value =
            user.email || "";

        document.getElementById(
            "phone"
        ).value =
            user.phone || "";

    }

    catch(error){

        console.error(error);

        showToast(
            "Server Error",
            "error"
        );
    }
}

async function updateProfile() {

    const token =
        localStorage.getItem("token");

    const fullName =
        document.getElementById(
            "fullName"
        ).value;

    const phone =
        document.getElementById(
            "phone"
        ).value;

    try {

        const response =
            await fetch(
                API_URL + "/users/profile",
                {
                    method:"PUT",

                    headers:{
                        "Content-Type":
                            "application/json",

                        Authorization:
                            "Bearer " + token
                    },

                    body:JSON.stringify({

                        fullName,
                        phone
                    })
                }
            );

        if(response.ok){

            showToast(
                "✓ Profile Updated",
                "success"
            );
        }

        else{

            showToast(
                "Update Failed",
                "error"
            );
        }
    }

    catch(error){

        console.error(error);

        showToast(
            "Server Error",
            "error"
        );
    }
}

window.onload = function(){

    loadProfile();
};