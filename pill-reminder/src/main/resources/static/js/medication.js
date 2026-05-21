async function loadMedications() {

    const token =
        localStorage.getItem("token");

    try {

        const response = await fetch(
            "http://localhost:8080/api/medications",
            {
                method: "GET",

                headers: {

                    "Content-Type":
                        "application/json",

                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

        let data = [];

        try {

            data =
                await response.json();

        }
        catch(e){

            console.log(
                "No JSON Response"
            );
        }

        const list =
            document.getElementById(
                "list"
            );

        list.innerHTML = "";

        if(!Array.isArray(data)){

            list.innerHTML =
                "<li>No Medications Found</li>";

            return;
        }

        if(data.length === 0){

            list.innerHTML =
                "<li>No Medications Found</li>";

            return;
        }

        data.forEach(medication => {

            list.innerHTML += `

<li class="medication-item">

    <h3>
        ${medication.name}
    </h3>

    <p>

        💊 Dosage:
        ${medication.dosage}<br>

        🔁 Frequency:
        ${medication.frequency}<br>

        ⏰ Times:
        ${medication.scheduledTimes}<br>

        📅 Start:
        ${medication.startDate || "-"}

    </p>

    <span class="status-badge">

        Active

    </span>

    <div style="
        display:flex;
        gap:12px;
        margin-top:20px;
    ">

        <button class="btn"

            style="
                padding:12px;
                font-size:14px;
            "

            onclick="editMedication(
                ${medication.id},
                '${medication.name}',
                '${medication.dosage}',
                '${medication.frequency}',
                '${medication.scheduledTimes}',
                '${medication.startDate || ""}'
            )">

            ✏️ Edit

        </button>

        <button class="btn"

            style="
                padding:12px;
                font-size:14px;

                background:
                    linear-gradient(
                        135deg,
                        #ef4444,
                        #dc2626
                    );
            "

            onclick="deleteMedication(${medication.id})">

            🗑 Delete

        </button>

    </div>

</li>

`;
        });
    }

    catch(error){

        console.error(error);

        showToast(
            "Server Error",
            "error"
        );
    }
}

async function addMedication(){

    const token =
        localStorage.getItem("token");

    const name =
        document.getElementById(
            "name"
        ).value;

    const dosage =
        document.getElementById(
            "dosage"
        ).value;

    const frequency =
        document.getElementById(
            "frequency"
        ).value;

    const startDate =
        document.getElementById(
            "startDate"
        ).value;

    const scheduledTimes =
        document.getElementById(
            "scheduledTimes"
        )
            .value
            .split(",")
            .map(time => time.trim());

    const medicationId =
        document.getElementById(
            "medicationId"
        ).value;

    // VALIDATION
    if(
        name === "" ||
        dosage === "" ||
        frequency === "" ||
        startDate === "" ||
        scheduledTimes.length === 0
    ){

        showToast(
            "Please Fill Required Fields",
            "error"
        );

        return;
    }

    try{

        const url = medicationId

            ? `http://localhost:8080/api/medications/${medicationId}`

            : "http://localhost:8080/api/medications";

        const method =
            medicationId ? "PUT" : "POST";

        const response =
            await fetch(url, {

                method: method,

                headers: {

                    "Content-Type":
                        "application/json",

                    Authorization:
                        "Bearer " + token
                },

                body: JSON.stringify({

                    name,
                    dosage,
                    frequency,
                    startDate,
                    scheduledTimes
                })
            });

        if(response.ok){

            showToast(

                medicationId

                    ? "✓ Medication Updated"

                    : "✓ Medication Added",

                "success"
            );

            // RESET FORM
            document.getElementById(
                "medicationId"
            ).value = "";

            document.getElementById(
                "submitBtn"
            ).innerText =
                "Add Medication";

            document.getElementById(
                "name"
            ).value = "";

            document.getElementById(
                "dosage"
            ).value = "";

            document.getElementById(
                "frequency"
            ).value = "";

            document.getElementById(
                "startDate"
            ).value = "";

            document.getElementById(
                "scheduledTimes"
            ).value = "";

            loadMedications();
        }

        else{

            const errorData =
                await response.text();

            console.error(errorData);

            showToast(
                errorData,
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

async function deleteMedication(id){

    const token =
        localStorage.getItem("token");

    const confirmDelete =
        confirm(
            "Delete this medication?"
        );

    if(!confirmDelete){

        return;
    }

    try{

        const response =
            await fetch(

                `http://localhost:8080/api/medications/${id}`,

                {
                    method:"DELETE",

                    headers:{
                        Authorization:
                            "Bearer " + token
                    }
                }
            );

        if(response.ok){

            showToast(
                "✓ Medication Deleted",
                "success"
            );

            loadMedications();
        }

        else{

            showToast(
                "Delete Failed",
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

function editMedication(

    id,
    name,
    dosage,
    frequency,
    scheduledTimes,
    startDate

){

    document.getElementById(
        "medicationId"
    ).value = id;

    document.getElementById(
        "name"
    ).value = name;

    document.getElementById(
        "dosage"
    ).value = dosage;

    document.getElementById(
        "frequency"
    ).value = frequency;

    document.getElementById(
        "startDate"
    ).value = startDate;

    document.getElementById(
        "scheduledTimes"
    ).value = scheduledTimes;

    document.getElementById(
        "submitBtn"
    ).innerText =
        "Update Medication";

    window.scrollTo({

        top:0,

        behavior:"smooth"
    });

    showToast(
        "Edit Mode Enabled",
        "warning"
    );
}