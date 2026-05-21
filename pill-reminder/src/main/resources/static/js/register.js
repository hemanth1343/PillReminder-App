async function registerUser() {

    const fullName =
        document.getElementById("fullName").value;

    const email =
        document.getElementById("email").value;

    const password =
        document.getElementById("password").value;

    /*
        VALIDATION
    */

    if (!fullName || !email || !password) {

        showToast(
            "Please fill all fields",
            "warning"
        );

        return;
    }

    try {

        /*
            REGISTER API CALL
        */

        const response = await fetch(
            "http://localhost:8080/api/auth/register",
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({
                    fullName,
                    email,
                    password
                })
            }
        );

        console.log(
            "REGISTER STATUS => ",
            response.status
        );

        /*
            SUCCESS
        */

        if (response.ok) {

            showToast(
                "✓ Registration Successful",
                "success"
            );

            window.location.href =
                "index.html";

            return;
        }

        /*
            BACKEND ERROR RESPONSE
        */

        let errorMessage =
            "Registration Failed";

        try {

            const errorData =
                await response.json();

            console.log(
                "REGISTER ERROR => ",
                errorData
            );

            if (errorData.message) {

                errorMessage =
                    errorData.message;
            }

        } catch(e) {

            console.log(
                "Could not parse error response"
            );
        }

        showToast(
            errorMessage,
            "error"
        );

    }
    catch(error) {

        console.log(
            "REGISTER ERROR => ",
            error
        );

        alert(
            "Server Error"
        );
    }
}