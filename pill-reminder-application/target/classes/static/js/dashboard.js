window.onload = function(){

    loadDashboard();

    showAdminMenu();

    loadCharts();
};

// =========================
// LOAD DASHBOARD
// =========================

async function loadDashboard(){

    const token =
        localStorage.getItem("token");

    // CHECK LOGIN

    if(!token){

        window.location.href =
            "index.html";

        return;
    }

    try{

        // =========================
        // FETCH MEDICATIONS
        // =========================

        const medicationResponse =
            await fetch(

                "http://localhost:8080/api/medications",

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        // =========================
        // FETCH REMINDERS
        // =========================

        const reminderResponse =
            await fetch(

                "http://localhost:8080/api/reminders/today",

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        if(

            !medicationResponse.ok ||

            !reminderResponse.ok

        ){

            console.error(
                "Failed To Load Dashboard"
            );

            return;
        }

        const medications =
            await medicationResponse.json();

        const reminders =
            await reminderResponse.json();

        // =========================
        // COUNTS
        // =========================

        document.getElementById(
            "activeMedications"
        ).innerText =
            medications.length;

        document.getElementById(
            "todayReminders"
        ).innerText =
            reminders.length;

        // =========================
        // CALCULATE ADHERENCE
        // =========================

        const taken =
            reminders.filter(

                r => r.status === "TAKEN"

            ).length;

        const missed =
            reminders.filter(

                r => r.status === "MISSED"

            ).length;

        const adherence =
            reminders.length > 0

            ?

            Math.round(
                (taken / reminders.length) * 100
            )

            :

            0;

        document.getElementById(
            "adherence"
        ).innerText =
            adherence + "%";

        document.getElementById(
            "missedDoses"
        ).innerText =
            missed;

        // =========================
        // UPCOMING LIST
        // =========================

        let pendingHtml = "";

        let takenHtml = "";

        reminders.forEach(item => {

            const card = `

                <div class="card">

                    <h3>
                        ${item.medicationName}
                    </h3>

                    <p>
                        ⏰
                        ${
                            new Date(
                                item.scheduledTime
                            ).toLocaleString()
                        }
                    </p>

                    <p>
                        Status:
                        ${item.status}
                    </p>

                </div>
            `;

            if(item.status === "PENDING"){

                pendingHtml += card;
            }

            else if(item.status === "TAKEN"){

                takenHtml += card;
            }
        });

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

        const scheduleList =
            document.getElementById(
                "scheduleList"
            );

        if(scheduleList){

            scheduleList.innerHTML =
                finalHtml;
        }

    }

    catch(error){

        console.error(
            "Dashboard Error",
            error
        );
    }
}

// =========================
// CHARTS
// =========================

async function loadCharts(){

    const token =
        localStorage.getItem("token");

    try{

        const response =
            await fetch(

                "http://localhost:8080/api/dashboard/analytics",

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        const data =
            await response.json();

        console.log(
            "Analytics:",
            data
        );

        // =========================
        // SAFE VALUES
        // =========================

        const taken =
            Number(data.taken || 0);

        const missed =
            Number(data.missed || 0);

        const pending =
            Number(data.pending || 0);

        const weekly =
            data.weeklyActivity ||

            [0,0,0,0,0,0,0];

        const labels =
            data.labels ||

            ["Mon","Tue","Wed","Thu","Fri","Sat","Sun"];

        // =========================
        // DESTROY OLD CHARTS
        // =========================

        if(window.adherenceChartInstance){

            window.adherenceChartInstance.destroy();
        }

        if(window.activityChartInstance){

            window.activityChartInstance.destroy();
        }

        // =========================
        // ADHERENCE CHART
        // =========================

        const adherenceCanvas =
            document.getElementById(
                "adherenceChart"
            );

        const adherenceCtx =
            adherenceCanvas.getContext("2d");

        window.adherenceChartInstance =
            new Chart(adherenceCtx, {

                type: "doughnut",

                data: {

                    labels: [

                        "Taken",

                        "Missed",

                        "Pending"
                    ],

                    datasets: [{

                        data: [

                            taken,

                            missed,

                            pending
                        ],

                        backgroundColor: [

                            "#22c55e",

                            "#ef4444",

                            "#f59e0b"
                        ],

                        borderWidth: 2,

                        hoverOffset: 15
                    }]
                },

                options: {

                    responsive: true,

                    maintainAspectRatio: false,

                    plugins: {

                        legend: {

                            position: "top",

                            labels: {

                                color: "white"
                            }
                        }
                    }
                }
            });

        // =========================
        // WEEKLY ACTIVITY
        // =========================

        const activityCanvas =
            document.getElementById(
                "activityChart"
            );

        const activityCtx =
            activityCanvas.getContext("2d");

        window.activityChartInstance =
            new Chart(activityCtx, {

                type: "bar",

                data: {

                    labels:
                        labels,

                    datasets: [{

                        label:
                            "Medications Taken",

                        data:
                            weekly,

                        backgroundColor:
                            "#6366f1",

                        borderRadius: 10
                    }]
                },

                options: {

                    responsive: true,

                    maintainAspectRatio: false,

                    plugins: {

                        legend: {

                            labels: {

                                color: "white"
                            }
                        }
                    },

                    scales: {

                        y: {

                            beginAtZero: true,

                            ticks: {

                                color: "white",

                                stepSize: 1
                            },

                            grid: {

                                color:
                                    "rgba(255,255,255,0.08)"
                            }
                        },

                        x: {

                            ticks: {

                                color: "white"
                            },

                            grid: {

                                color:
                                    "rgba(255,255,255,0.05)"
                            }
                        }
                    }
                }
            });

    }

    catch(error){

        console.error(
            "Chart Error:",
            error
        );
    }
}

// =========================
// ADMIN MENU
// =========================

function showAdminMenu(){

    const token =
        localStorage.getItem("token");

    if(!token) return;

    try{

        const payload =
            JSON.parse(

                atob(
                    token.split(".")[1]
                )
            );

        const role =
            payload.role;

        if(

            role === "ROLE_ADMIN" ||

            role === "ROLE_SUPER_ADMIN"

        ){

            const adminLink =
                document.getElementById(
                    "adminLink"
                );

            if(adminLink){

                adminLink.style.display =
                    "inline-block";
            }
        }

    }

    catch(e){

        console.error(e);
    }
}

// =========================
// LOGOUT
// =========================

function logout(){

    localStorage.clear();

    window.location.href =
        "index.html";
}