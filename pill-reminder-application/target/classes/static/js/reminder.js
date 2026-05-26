const API_BASE =
    "http://localhost:8080/api";

// AUTO LOAD

window.onload = function(){

    loadReminders();
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

            ✔ Taken

        </button>

        <button
            class="missed-btn"
            onclick="markMissed(${item.id})">

            ✖ Missed

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
                "Reminder Marked As Taken"
            );

            loadReminders();
        }

    }

    catch(error){

        console.error(error);
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
                "Reminder Marked As Missed"
            );

            loadReminders();
        }

    }

    catch(error){

        console.error(error);
    }
}

// LOGOUT

function logout(){

    localStorage.clear();

    window.location.href =
        "index.html";
}