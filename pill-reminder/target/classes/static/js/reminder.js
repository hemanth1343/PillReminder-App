async function loadReminders() {

    const token =
        localStorage.getItem("token");

    // CHECK LOGIN
    if (!token) {

        alert("Please Login");

        window.location.href =
            "index.html";

        return;
    }

    try {

        const response = await fetch(

            "/api/reminders/today",

            {
                headers: {

                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

        console.log(response);

        // UNAUTHORIZED
        if (response.status === 403) {

            alert("Session Expired. Login Again");

            localStorage.clear();

            window.location.href =
                "index.html";

            return;
        }

        // OTHER ERROR
        if (!response.ok) {

            alert("Failed to load reminders");

            return;
        }

        const data =
            await response.json();

        console.log(data);

        let html = "";

        // NO REMINDERS
        if (data.length === 0) {

            html = `
                <div class="card">
                    <h3>
                        No reminders today
                    </h3>
                </div>
            `;

        } else {

			data.forEach(item => {

			    // SHOW ONLY PENDING
			    if (item.status !== "PENDING") {
			        return;
			    }

			    html += `

			        <div class="card">

			            <h3>
			                ${item.medicationName}
			            </h3>

			            <p>
			                Time:
			                ${item.scheduledTime}
			            </p>

			            <p>
			                Status:
			                ${item.status}
			            </p>

			            <button
			                onclick="markTaken(${item.id})">

			                Taken
			            </button>

			            <button
			                onclick="markMissed(${item.id})">

			                Missed
			            </button>

			        </div>
			    `;
			});
        }

        document.getElementById(
            "reminderList"
        ).innerHTML = html;

    } catch (error) {

        console.error(error);

        alert("Server Error");
    }
}

// MARK TAKEN
async function markTaken(id) {

    const token =
        localStorage.getItem("token");

    try {

        const response = await fetch(

            `/api/reminders/${id}/take`,

            {
                method: "POST",

                headers: {

                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

        if (response.ok) {

            alert("Reminder marked as taken");

            loadReminders();

        } else {

            alert("Failed");
        }

    } catch (error) {

        console.error(error);
    }
}

// MARK MISSED
async function markMissed(id) {

    const token =
        localStorage.getItem("token");

    try {

        const response = await fetch(

            `/api/reminders/${id}/miss`,

            {
                method: "POST",

                headers: {

                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

        if (response.ok) {

            alert("Reminder marked as missed");

            loadReminders();

        } else {

            alert("Failed");
        }

    } catch (error) {

        console.error(error);
    }
}