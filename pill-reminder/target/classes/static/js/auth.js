const BASE_URL =
    "http://localhost:8080/api";

/*
    LOGIN FUNCTION
*/

async function login() {

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
            "Please enter email and password"
        );

        return;
    }

    try{

        const response =
            await fetch(

                "http://localhost:8080/api/auth/login",

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
                "Invalid email or password"
            );

            return;
        }

        const data =
            await response.json();

        console.log(data);

        // SAFE TOKEN EXTRACTION
        const token =

            data.accessToken ||

            data.token ||

            data.jwt;

        if(!token){

            alert(
                "Token missing from response"
            );

            return;
        }

        localStorage.setItem(
            "token",
            token
        );

        // SAFE ROLE EXTRACTION
        let role = "ROLE_USER";

        if(data.user && data.user.role){

            role =
                data.user.role;
        }

        else if(data.role){

            role =
                data.role;
        }

        localStorage.setItem(
            "role",
            role
        );

        console.log(
            "ROLE:",
            role
        );

        // ADMIN REDIRECT
        if(

            role === "ROLE_ADMIN" ||

            role === "ROLE_SUPER_ADMIN"

        ){

            window.location.href =
                "admin.html";
        }

        // USER REDIRECT
        else{

            window.location.href =
                "medications.html";
        }
    }

    catch(error){

        console.error(error);

        alert(
            "Server Error"
        );
    }
}