const API_BASE = "http://localhost:8080/api";

// AUTO LOAD

window.onload = function(){

    loadReminders();
	
	loadWaterReminders();
};

// LOAD REMINDERS

async function loadReminders(){

    const token =
        localStorage.getItem(
            "token"
        );

    console.log(
        "TOKEN : ",
        token
    );

    // CHECK LOGIN

    if(!token){

        alert(
            "Please Login"
        );

        window.location.href =
            "index.html";

        return;
    }

    try{

        const response =
            await fetch(

                `${API_BASE}/reminders/today`,

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        console.log(
            "RESPONSE : ",
            response
        );

        // ERROR

        if(!response.ok){

            document.getElementById(
                "reminderList"
            ).innerHTML = `

<div class="loading-card">

    <h3>
        Failed To Load Reminders
    </h3>

</div>

`;

            return;
        }

        const data =
            await response.json();

        console.log(
            "REMINDER DATA : ",
            data
        );

        let html = "";

        // NO DATA

        if(data.length === 0){

            html = `

<div class="loading-card">

    <h3>
        No Reminders Today
    </h3>

</div>

`;

        }else{

            data.forEach(item => {

                if(item.status !== "PENDING"){

                    return;
                }

                html += `

<div class="reminder-modern-card">

    <div class="reminder-header">

        <div>

            <h2 class="medicine-title">

                💊
                ${item.medicationName}

            </h2>

            <p class="reminder-time">

                ⏰
                ${
                    new Date(
                        item.scheduledTime
                    ).toLocaleString()
                }

            </p>

        </div>

        <span class="status-badge">

            ${item.status}

        </span>

    </div>

    <div class="reminder-buttons">

        <button
            class="taken-btn"
            onclick="markTaken(${item.id})">

             Taken

        </button>

        <button
            class="missed-btn"
            onclick="markMissed(${item.id})">

            Missed

        </button>

    </div>

</div>

`;
            });
        }

        document.getElementById(
            "reminderList"
        ).innerHTML = html;

    }

    catch(error){

        console.error(error);

        document.getElementById(
            "reminderList"
        ).innerHTML = `

<div class="loading-card">

    <h3>
        Server Error
    </h3>

</div>

`;
    }
}

// MARK TAKEN

async function markTaken(id){

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        const response =
            await fetch(

                `${API_BASE}/reminders/${id}/take`,

                {
                    method:"POST",

                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        if(response.ok){

            alert(
                " Reminder Marked As Taken"
            );

            loadReminders();

            return;
        }

        // GET BACKEND ERROR MESSAGE

        const errorMessage =
            await response.text();

        alert(
            errorMessage
        );

    }

    catch(error){

        console.error(error);

        alert(
            " Failed To Mark Reminder"
        );
    }
}

// MARK MISSED

async function markMissed(id){

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        const response =
            await fetch(

                `${API_BASE}/reminders/${id}/miss`,

                {
                    method:"POST",

                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        if(response.ok){

            alert(
                "❌ Reminder Marked As Missed"
            );

            loadReminders();

            return;
        }

        // GET BACKEND ERROR MESSAGE

        const errorMessage =
            await response.text();

        alert(
            errorMessage
        );
    }

    catch(error){

        console.error(error);

        alert(
            "Failed To Mark Reminder"
        );
    }
}

async function loadWaterReminders(){

    const token =
        localStorage.getItem("token");

    const waterList =
        document.getElementById(
            "waterReminderList"
        );

    try{

        console.log(
            "Loading Water Reminders..."
        );

        const response =
            await fetch(

                `${API_BASE}/water`,

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        if(!response.ok){

            throw new Error(
                "Failed to load water reminders"
            );
        }

		const reminders =
		    await response.json();

        console.log(
            "Water Reminders:",
            reminders
        );

        waterList.innerHTML = "";

        if(reminders.length === 0){

            waterList.innerHTML = `

                <div class="water-reminder-item">

                    No Water Reminders Found

                </div>

            `;

            return;
        }

        reminders.forEach(reminder => {

            const time =
                new Date(
                    reminder.scheduledTime
                )
                .toLocaleTimeString(
                    [],
                    {
                        hour:"2-digit",
                        minute:"2-digit"
                    }
                );

            let statusColor =
                "#fbbf24";

            if(
                reminder.status ===
                "TAKEN"
            ){
                statusColor =
                    "#22c55e";
            }

            if(
                reminder.status ===
                "MISSED"
            ){
                statusColor =
                    "#ef4444";
            }

            waterList.innerHTML += `

                <div class="water-reminder-item">

                    <h3>
                        💧 Drink Water
                    </h3>

                    <p>
                        ⏰ ${time}
                    </p>

                    <p
                        style="
                        color:${statusColor};
                        font-weight:bold;
                        "
                    >
                        ${reminder.status}
                    </p>

                    ${
                        reminder.status ===
                        "PENDING"

                        ?

                        `
                        <button
                            onclick="
                            markWaterTaken(
                                ${reminder.id}
                            )
                            "
                            class="otp-btn">

                             Taken

                        </button>

                        <button
                            onclick="
                            markWaterMissed(
                                ${reminder.id}
                            )
                            "
                            class="otp-btn">

                             Missed

                        </button>
                        `

                        :

                        ""
                    }

                </div>

            `;
        });

    }

    catch(error){

        console.error(error);

        waterList.innerHTML = `

            <div
                class="
                water-reminder-item
                "
            >

                Failed To Load
                Water Reminders

            </div>

        `;
    }
}

async function markWaterTaken(id){

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        const response =
            await fetch(

                `${API_BASE}/water/${id}/take`,

                {
                    method:"POST",

                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        const message =
            await response.text();

        alert(message);

        loadWaterReminders();

    }

    catch(error){

        console.error(error);
    }
}

async function markWaterMissed(id){

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        const response =
            await fetch(

                `${API_BASE}/water/${id}/miss`,

                {
                    method:"POST",

                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        const message =
            await response.text();

        alert(message);

        loadWaterReminders();

    }

    catch(error){

        console.error(error);
    }
}

document.addEventListener(

    "DOMContentLoaded",

    function(){

        loadWaterReminders();
    }
);