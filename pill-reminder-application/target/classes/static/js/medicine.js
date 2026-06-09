const API_BASE =
    "http://localhost:8080/api";

// Search when Enter key pressed

document
    .getElementById("medicineName")
    .addEventListener("keypress", function(event){

        if(event.key === "Enter"){

            searchMedicine();
        }
    });

async function searchMedicine(){

    const medicineName =
        document
            .getElementById("medicineName")
            .value
            .trim();

    const loader =
        document.getElementById(
            "loader"
        );

    const medicineCard =
        document.getElementById(
            "medicineCard"
        );

    if(!medicineName){

        alert(
            "💊 Please enter a medicine name"
        );

        return;
    }

    loader.style.display = "block";

    loader.innerHTML =
        "🔍 Searching medicine information...";

    medicineCard.innerHTML = "";

    try{

        const token =
            localStorage.getItem(
                "token"
            );

        const response =
            await fetch(

                `${API_BASE}/medicines/${encodeURIComponent(medicineName)}`,

                {
                    method:"GET",

                    headers:{

                        Authorization:
                            `Bearer ${token}`,

                        "Content-Type":
                            "application/json"
                    }
                }
            );

        if(!response.ok){

            const errorText =
                await response.text();

            throw new Error(

                errorText ||

                "Medicine not found"
            );
        }

        const data =
            await response.json();

        medicineCard.innerHTML = `

        <div class="card">

            <h2 style="
                color:#7c4dff;
                text-align:center;
                margin-bottom:20px;
            ">
                💊 ${data.medicineName || "N/A"}
            </h2>

            <p>
                <strong>Generic Name:</strong>
                ${data.genericName || "Not Available"}
            </p>

            <p>
                <strong>Uses:</strong>
                ${data.uses || "Not Available"}
            </p>

            <p>
                <strong>Side Effects:</strong>
                ${data.sideEffects || "Not Available"}
            </p>

            <p>
                <strong>Dosage Information:</strong>
                ${data.dosage || "Not Available"}
            </p>

            <p>
                <strong>Who Can Take:</strong>
                ${data.whoCanTake || "Not Available"}
            </p>

            <p>
                <strong>Who Should Avoid:</strong>
                ${data.whoShouldAvoid || "Not Available"}
            </p>

            <p>
                <strong>Drug Interactions:</strong>
                ${data.interactions || "Not Available"}
            </p>

            <p>
                <strong>Warnings:</strong>
                ${data.warnings || "Not Available"}
            </p>

            <p>
                <strong>Manufacturer:</strong>
                ${data.manufacturer || "Not Available"}
            </p>

            <p>
                <strong>Storage Instructions:</strong>
                ${data.storage || "Not Available"}
            </p>

            <hr style="
                margin:20px 0;
            ">

            <p style="
                color:red;
                font-size:15px;
                font-weight:bold;
                text-align:center;
            ">
                ⚠️ This information is for educational purposes only and should not replace professional medical advice.
            </p>

        </div>
        `;

    }

    catch(error){

        console.error(error);

        medicineCard.innerHTML = `

        <div class="card">

            <h3 style="
                color:red;
                text-align:center;
            ">
                ❌ Medicine Not Found
            </h3>

            <p style="
                text-align:center;
            ">
                ${error.message}
            </p>

        </div>
        `;
    }

    finally{

        loader.style.display =
            "none";
    }
}