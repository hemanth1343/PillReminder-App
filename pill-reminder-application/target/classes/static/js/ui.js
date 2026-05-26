const list =
    document.getElementById("list");

async function loadMedications() {

    try {

        const data =
            await apiRequest("/medications");

        if(!data) return;

        list.innerHTML = "";

        // No Data
        if(data.length === 0) {

            list.innerHTML =
                "<li>No Medications Found</li>";

            return;
        }

        // Display Data
        data.forEach(medication => {

            list.innerHTML += `

                <li class="card">

                    <h3>${medication.name}</h3>

                    <p>
                        <b>Dosage :</b>
                        ${medication.dosage}
                    </p>

                    <p>
                        <b>Description :</b>
                        ${medication.description || "N/A"}
                    </p>

                    <p>
                        <b>Frequency :</b>
                        ${medication.frequency}
                    </p>

                </li>
            `;
        });

    }
    catch(error) {

        console.log(error);

        alert("Error Loading Medications");
    }
}