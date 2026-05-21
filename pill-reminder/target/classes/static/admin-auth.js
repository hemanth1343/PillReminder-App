const BASE_URL =
    "http://localhost:8080/api";

async function adminLogin(){

    const email =
        document.getElementById(
            "email"
        ).value;

    const password =
        document.getElementById(
            "password"
        ).value;

    if(!email || !password){

        alert(
            "Please enter credentials"
        );

        return;
    }

    try{

        const response =
            await fetch(

                `${BASE_URL}/auth/login`,

                {
                    method:"POST",

                    headers:{
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        email,
                        password
                    })
                }
            );

        if(!response.ok){

            alert(
                "Invalid Credentials"
            );

            return;
        }

        const data =
            await response.json();

        console.log(data);

        const token =
            data.accessToken;

        const role =
            data.user.role;

        // ADMIN CHECK
        if(

            role !== "ROLE_ADMIN" &&

            role !== "ROLE_SUPER_ADMIN"

        ){

            alert(
                "Access Denied"
            );

            return;
        }

        // SAVE AUTH
        localStorage.setItem(
            "token",
            token
        );

        localStorage.setItem(
            "role",
            role
        );

        // REDIRECT
        window.location.href =
            "admin.html";
    }

    catch(error){

        console.error(error);

        alert(
            "Server Error"
        );
    }
}