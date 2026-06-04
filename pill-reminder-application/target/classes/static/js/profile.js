const API_URL =
    "http://localhost:8080/api";

// =========================
// LOAD PROFILE
// =========================

let passwordOtpVerified = false;

async function loadProfile(){

    const token =
        localStorage.getItem("token");

    try{

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

        // =========================
        // BASIC INFO
        // =========================

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

        // =========================
        // PREFERENCES
        // =========================

        document.getElementById(
            "emailNotifications"
        ).checked =
            user.emailNotifications || false;

        document.getElementById(
            "pushNotifications"
        ).checked =
            user.pushNotifications || false;

        document.getElementById(
            "theme"
        ).value =
            localStorage.getItem("theme")
            || "dark";

        // =========================
        // PROFILE IMAGE
        // =========================

        if(

            user.profileImage &&

            user.profileImage !== "null" &&

            user.profileImage.trim() !== ""

        ){

            document.getElementById(
                "profilePreview"
            ).src =
                user.profileImage;

            // SAVE LOCAL BACKUP

            localStorage.setItem(

                "profileImage",

                user.profileImage
            );
        }

        else{

            // LOCAL FALLBACK

            const savedImage =

                localStorage.getItem(
                    "profileImage"
                );

            if(savedImage){

                document.getElementById(
                    "profilePreview"
                ).src =
                    savedImage;
            }
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

// =========================
// UPDATE PROFILE
// =========================

async function updateProfile(){

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

    const profileImage =
        document.getElementById(
            "profilePreview"
        ).src;

    try{

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

                    body: JSON.stringify({

                        fullName,

                        phone,

                        profileImage
                    })
                }
            );

        if(response.ok){

            localStorage.setItem(
                "profileImage",
                profileImage
            );

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

// =========================
// SAVE PREFERENCES
// =========================

async function savePreferences(){

    const token =
        localStorage.getItem("token");

    const emailNotifications =
        document.getElementById(
            "emailNotifications"
        ).checked;

    const pushNotifications =
        document.getElementById(
            "pushNotifications"
        ).checked;

    const theme =
        document.getElementById(
            "theme"
        ).value;

    try{

        const response =
            await fetch(

                API_URL + "/users/preferences",

                {
                    method: "PUT",

                    headers: {

                        "Content-Type":
                            "application/json",

                        Authorization:
                            "Bearer " + token
                    },

                    body: JSON.stringify({

                        emailNotifications,

                        pushNotifications,

                        theme
                    })
                }
            );

        if(response.ok){

            // SAVE THEME

            localStorage.setItem(
                "theme",
                theme
            );

            // APPLY THEME

            document.body.classList.remove(

                "dark-theme",

                "light-theme",

                "dim-theme"
            );

            document.body.classList.add(
                theme + "-theme"
            );

            showToast(
                "Preferences Saved",
                "success"
            );
        }

        else{

            showToast(
                "Failed To Save Preferences",
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

// =========================
// IMAGE MODAL
// =========================

function openImageOptions(){

    document.getElementById(
        "imageModal"
    ).style.display = "flex";
}

function closeImageModal(){

    // HIDE MODAL

    document.getElementById(
        "imageModal"
    ).style.display = "none";

    // STOP CAMERA

    if(cameraStream){

        cameraStream
        .getTracks()
        .forEach(track => {

            track.stop();
        });

        cameraStream = null;
    }

    // HIDE VIDEO

    document.getElementById(
        "camera"
    ).style.display = "none";

    // HIDE CAPTURE BUTTON

    document.getElementById(
        "captureBtn"
    ).style.display = "none";
}

// =========================
// OPEN GALLERY
// =========================

function openGallery(){

    closeImageModal();

    document.getElementById(
        "galleryInput"
    ).click();
}

// =========================
// CAMERA
// =========================

let cameraStream = null;

// =========================
// START CAMERA
// =========================

async function startCamera(){

    try{

        const video =
            document.getElementById(
                "camera"
            );

        cameraStream =
            await navigator.mediaDevices.getUserMedia({

                video: true
            });

        video.srcObject =
            cameraStream;

        video.style.display =
            "block";

        document.getElementById(
            "captureBtn"
        ).style.display =
            "inline-block";
    }

    catch(error){

        console.error(error);

        showToast(
            "Camera access denied",
            "error"
        );
    }
}

// =========================
// CAPTURE PHOTO
// =========================

function capturePhoto(){

    const video =
        document.getElementById(
            "camera"
        );

    const canvas =
        document.getElementById(
            "cameraCanvas"
        );

    const ctx =
        canvas.getContext("2d");

    canvas.width =
        video.videoWidth;

    canvas.height =
        video.videoHeight;

    ctx.drawImage(

        video,

        0,

        0,

        canvas.width,

        canvas.height
    );

    const image =
        canvas.toDataURL(
            "image/png"
        );

    // SHOW IMAGE

    document.getElementById(
        "profilePreview"
    ).src = image;

    // SAVE LOCAL

    localStorage.setItem(
        "profileImage",
        image
    );

    // AUTO SAVE TO DATABASE

    updateProfile();

    // STOP CAMERA

    if(cameraStream){

        cameraStream
        .getTracks()
        .forEach(track => {

            track.stop();
        });
    }

    video.style.display =
        "none";

    document.getElementById(
        "captureBtn"
    ).style.display =
        "none";

    closeImageModal();

    showToast(
        "Photo Captured",
        "success"
    );
}

// =========================
// PREVIEW IMAGE
// =========================

function previewProfileImage(event){

    const file =
        event.target.files[0];

    if(file){

        const reader =
            new FileReader();

        reader.onload = function(e){

            const image =
                e.target.result;

            // PREVIEW

            document.getElementById(
                "profilePreview"
            ).src = image;

            // SAVE LOCAL

            localStorage.setItem(
                "profileImage",
                image
            );

            // AUTO SAVE DATABASE

            updateProfile();
        };

        reader.readAsDataURL(file);
    }
}
// =========================
// LOGOUT
// =========================

function logout(){

    localStorage.removeItem(
        "token"
    );

    localStorage.removeItem(
        "role"
    );

    window.location.href =
        "index.html";
}



function openPasswordModal(){

    document
        .getElementById(
            "passwordModal"
        )
        .style.display =
        "flex";
}

function closePasswordModal(){

    document
        .getElementById(
            "passwordModal"
        )
        .style.display =
        "none";
}

async function sendPasswordOtp(){

    const token =
        localStorage.getItem(
            "token"
        );

    const response =
        await fetch(

            "http://localhost:8080/api/auth/send-password-otp",

            {

                method:"POST",

                headers:{

                    Authorization:
                    "Bearer " + token
                }
            }
        );

    if(response.ok){

        showToast(
            "OTP Sent",
            "success"
        );
    }
}


async function verifyPasswordOtp(){

    const token =
        localStorage.getItem(
            "token"
        );

    const otp =
        document.getElementById(
            "passwordOtp"
        ).value;

    const response =
        await fetch(

            `http://localhost:8080/api/auth/verify-password-otp?otp=${otp}`,

            {

                method:"POST",

                headers:{

                    Authorization:
                    "Bearer " + token
                }
            }
        );

    if(response.ok){

        passwordOtpVerified = true;

        showToast(
            "OTP Verified",
            "success"
        );
    }
}

async function changePassword(){

    if(!passwordOtpVerified){

        showToast(
            "Verify OTP First",
            "warning"
        );

        return;
    }

    const token =
        localStorage.getItem(
            "token"
        );

    const newPassword =
        document.getElementById(
            "newPassword"
        ).value;

    const response =
        await fetch(

            "http://localhost:8080/api/users/me/change-password-otp",

            {

                method:"POST",

                headers:{

                    "Content-Type":
                    "application/json",

                    Authorization:
                    "Bearer " + token
                },

                body:JSON.stringify({

                    newPassword
                })
            }
        );

		if(response.ok){

		    showToast(
		        "Password Changed Successfully",
		        "success"
		    );

		    // Reset values

		    document.getElementById(
		        "passwordOtp"
		    ).value = "";

		    document.getElementById(
		        "newPassword"
		    ).value = "";

		    passwordOtpVerified = false;

		    // Close popup

		    closePasswordModal();

		    return;
		}
}

// =========================
// LOAD PAGE
// =========================

window.onload = function(){

    loadProfile();
};