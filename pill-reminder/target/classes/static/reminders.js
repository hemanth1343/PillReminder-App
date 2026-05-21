const API_BASE =
    "http://localhost:8080/api";

window.onload = function(){

    loadReminders();
};

async function loadReminders(){

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        const response =
            await fetch(

                `${API_BASE}/reminders`,

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        if(!response.ok){

            throw new Error(
                "Failed to load reminders"
            );
        }

        const reminders =
            await response.json();

        console.log(reminders);

        const list =
            document.getElementById(
                "reminderList"
            );

        list.innerHTML = "";

        if(reminders.length === 0){

            list.innerHTML = `

                <p>
                    No reminders available
                </p>
            `;

            return;
        }

        reminders.forEach(reminder => {

            let statusClass =
                "pending";

            if(
                reminder.status === "TAKEN"
            ){

                statusClass =
                    "taken";
            }

            if(
                reminder.status === "MISSED"
            ){

                statusClass =
                    "missed";
            }

            list.innerHTML += `

<div class="medication-item">

    <h3>
        💊 ${reminder.medicationName}
    </h3>

    <p>

        ⏰ Scheduled:
        ${reminder.scheduledTime}

    </p>

    <span class="status-badge ${statusClass}">

        ${reminder.status}

    </span>

    <div style="
        display:flex;
        gap:12px;
        margin-top:20px;
    ">

        <button class="btn"

            onclick="
                markAsTaken(
                    ${reminder.id}
                )
            ">

            ✓ Taken

        </button>

        <button class="btn"

            style="
                background:
                    linear-gradient(
                        135deg,
                        #f59e0b,
                        #d97706
                    );
            "

            onclick="
                snoozeReminder(
                    ${reminder.id}
                )
            ">

            ⏰ Snooze

        </button>

    </div>

</div>

`;
        });
    }

    catch(error){

        console.error(error);

        alert(
            "Error loading reminders"
        );
    }
}

async function markAsTaken(id){

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        const response =
            await fetch(

                `${API_BASE}/reminders/${id}/taken`,

                {
                    method:"PUT",

                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        if(response.ok){

            alert(
                "Reminder marked as taken"
            );

            loadReminders();
        }
    }

    catch(error){

        console.error(error);
    }
}

async function snoozeReminder(id){

    alert(
        "Snooze feature coming next"
    );
}

function logout(){

    localStorage.clear();

    window.location.href =
        "index.html";
}