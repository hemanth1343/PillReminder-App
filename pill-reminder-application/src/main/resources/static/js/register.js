const API_URL =
    "http://localhost:8080/api";

let otpVerified = false;


async function sendOtp(){

    const email =
        document.getElementById(
            "email"
        ).value;

    if(!email){

        showToast(
            "Please enter email",
            "warning"
        );

        return;
    }

    try{

        const response =
            await fetch(

                `${API_URL}/auth/send-otp?email=${encodeURIComponent(email)}`,

                {
                    method:"POST"
                }
            );

        const message =
            await response.text();

        if(response.ok){

            showToast(
                "OTP sent successfully",
                "success"
            );
        }

        else{

            showToast(
                message,
                "error"
            );
        }
    }

    catch(error){

        console.error(error);

        showToast(
            "Failed to send OTP",
            "error"
        );
    }
}


async function verifyOtp(){

    const email =
        document.getElementById(
            "email"
        ).value;

    const otp =
        document.getElementById(
            "otp"
        ).value;

    if(!otp){

        showToast(
            "Please enter OTP",
            "warning"
        );

        return;
    }

    try{

        const response =
            await fetch(

                `${API_URL}/auth/verify-otp?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}`,

                {
                    method:"POST"
                }
            );

        const message =
            await response.text();

        if(response.ok){

            otpVerified = true;

            showToast(
                "OTP Verified Successfully",
                "success"
            );
        }

        else{

            otpVerified = false;

            showToast(
                message,
                "error"
            );
        }
    }

    catch(error){

        console.error(error);

        showToast(
            "OTP Verification Failed",
            "error"
        );
    }
}

async function registerUser() {

    if(!otpVerified){

        showToast(
            "Please verify OTP first",
            "warning"
        );

        return;
    }

    const fullName =
        document.getElementById(
            "fullName"
        ).value;

    const email =
        document.getElementById(
            "email"
        ).value;

    const password =
        document.getElementById(
            "password"
        ).value;

    // VALIDATION

    if (

        !fullName ||

        !email ||

        !password

    ){

        showToast(

            "Please fill all fields",

            "warning"
        );

        return;
    }

    try {

        const response =
            await fetch(

                `${API_URL}/auth/register`,

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

        if(response.ok){

            showToast(

                "✓ Registration Successful",

                "success"
            );

            setTimeout(() => {

                window.location.href =
                    "index.html";

            }, 1500);

            return;
        }

        let errorMessage =
            "Registration Failed";

        try{

            const errorData =
                await response.json();

            if(errorData.message){

                errorMessage =
                    errorData.message;
            }

        }

        catch(e){

            console.log(
                "Error response parse failed"
            );
        }

        showToast(
            errorMessage,
            "error"
        );
    }

    catch(error){

        console.error(error);

        showToast(
            "Server Error",
            "error"
        );
    }
}