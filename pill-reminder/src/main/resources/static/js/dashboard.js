async function loadDashboard() {

    const token =
        localStorage.getItem("token");

    // CHECK LOGIN
    if (!token) {

        window.location.href =
            "index.html";

        return;
    }

    const medicationResponse =
        await fetch(

            "/api/medications",

            {
                headers: {
                    Authorization:
                        `Bearer ${token}`
                }
            }
        );

    const reminderResponse =
        await fetch(

            "/api/reminders/today",

            {
                headers: {
                    Authorization:
                        `Bearer ${token}`
                }
            }
        );

    const medications =
        await medicationResponse.json();

    const reminders =
        await reminderResponse.json();

    document.getElementById(
        "medicationCount"
    ).innerText = medications.length;

    document.getElementById(
        "reminderCount"
    ).innerText = reminders.length;

    let pendingHtml = "";

    let takenHtml = "";

    reminders.forEach(item => {

        const card = `

            <div class="card">

                <h3>
                    ${item.medicationName}
                </h3>

                <p>
                    ${item.scheduledTime}
                </p>

            </div>
        `;

        if (item.status === "PENDING") {

            pendingHtml += card;
        }

        else if (item.status === "TAKEN") {

            takenHtml += card;
        }
    });

    // FINAL HTML
    const finalHtml = `

        <div
            style="
                display:flex;
                gap:20px;
                flex-wrap:wrap;
            "
        >

            <!-- PENDING -->

            <div
                style="
                    flex:1;
                    min-width:300px;
                "
            >

                <h2>
                    ⏳ Pending
                </h2>

                ${
                    pendingHtml ||

                    "<p>No pending reminders</p>"
                }

            </div>

            <!-- TAKEN -->

            <div
                style="
                    flex:1;
                    min-width:300px;
                "
            >

                <h2>
                    ✅ Taken
                </h2>

                ${
                    takenHtml ||

                    "<p>No taken reminders</p>"
                }

            </div>

        </div>
    `;

    // DISPLAY
    document.getElementById(
        "scheduleList"
    ).innerHTML = finalHtml;
}

function showAdminMenu() {

    const token = localStorage.getItem("token");

    if (!token) return;

    try {

        const payload = JSON.parse(
            atob(token.split(".")[1])
        );

        const role = payload.role;

        if (
            role === "ROLE_ADMIN" ||
            role === "ROLE_SUPER_ADMIN"
        ) {

            document.getElementById("adminLink")
                .style.display = "inline-block";
        }

    }
    catch (e) {

        console.error(e);
    }
}

function logout() {

    localStorage.clear();

    window.location.href =
        "index.html";
}
