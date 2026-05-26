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

    // VALIDATION

    if(!email || !password){

        alert(
            "Please enter email and password"
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

        // LOGIN FAILED

        if(!response.ok){

            const error =
                await response.text();

            alert(error);

            return;
        }

        // SUCCESS RESPONSE

        const data =
            await response.json();

        console.log(data);

        // TOKEN

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

        // ROLE

        let role = "ROLE_USER";

        if(

            data.user &&

            data.user.role

        ){

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

        // REDIRECT

        if(

            role === "ROLE_ADMIN" ||

            role === "ROLE_SUPER_ADMIN"

        ){

            window.location.href =
                "admin.html";
        }

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