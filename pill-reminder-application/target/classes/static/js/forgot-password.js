const API =
    "http://localhost:8080/api/auth";

let otpVerified = false;

async function sendOtp(){

    const email =
        document.getElementById(
            "email"
        ).value;

    const response =
        await fetch(

            `${API}/forgot-password/send-otp?email=${email}`,

            {
                method:"POST"
            }
        );

    const message =
        await response.text();

    showToast(
        message,
        response.ok
            ? "success"
            : "error"
    );
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

    const response =
        await fetch(

            `${API}/forgot-password/verify-otp?email=${email}&otp=${otp}`,

            {
                method:"POST"
            }
        );

    const message =
        await response.text();

    if(response.ok){

        otpVerified = true;
    }

    showToast(
        message,
        response.ok
            ? "success"
            : "error"
    );
}

async function resetPassword(){

    if(!otpVerified){

        showToast(
            "Verify OTP First",
            "warning"
        );

        return;
    }

    const email =
        document.getElementById(
            "email"
        ).value;

    const newPassword =
        document.getElementById(
            "newPassword"
        ).value;

    const response =
        await fetch(

            `${API}/forgot-password/reset?email=${email}&newPassword=${newPassword}`,

            {
                method:"POST"
            }
        );

    const message =
        await response.text();

    showToast(
        message,
        response.ok
            ? "success"
            : "error"
    );

    if(response.ok){

        setTimeout(() => {

            window.location.href =
                "index.html";

        }, 1500);
    }
}