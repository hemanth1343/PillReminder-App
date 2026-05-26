const API_BASE =
    "http://localhost:8080/api";

// ROLE PROTECTION

const role =
    localStorage.getItem(
        "role"
    );

if(

    role !== "ROLE_ADMIN" &&

    role !== "ROLE_SUPER_ADMIN"

){

    alert(
        "Access Denied"
    );

    window.location.href =
        "index.html";
}

// PAGE LOAD

window.onload = function(){

    loadAdminDashboard();
};

// LOAD DASHBOARD

async function loadAdminDashboard(){

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        // SUMMARY

        const summaryResponse =
            await fetch(

                `${API_BASE}/admin/stats/summary`,

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        const summary =
            await summaryResponse.json();

        document.getElementById(
            "totalUsers"
        ).innerText =
            summary.totalUsers || 0;

        document.getElementById(
            "totalMedications"
        ).innerText =
            summary.totalMedications || 0;

        document.getElementById(
            "totalLogs"
        ).innerText =
            summary.totalReminderLogs || 0;

        // USERS

        const usersResponse =
            await fetch(

                `${API_BASE}/admin/users`,

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        const users =
            await usersResponse.json();

        const tbody =
            document.getElementById(
                "userTableBody"
            );

        tbody.innerHTML = "";

        users.forEach(user => {

            tbody.innerHTML += `

<tr onclick="viewUserDetails(${user.id})">

    <td>${user.id}</td>

    <td>${user.fullName}</td>

    <td>${user.email}</td>

    <td>${user.role}</td>

	<td>

	    ${
	          user.blocked

	        ?

	        `<span class="status-blocked">
	            Blocked
	         </span>`

	        :

	        `<span class="status-active">
	            Active
	         </span>`
	    }

	</td>

    <td>

        <div class="action-buttons">

            <button class="block-btn"
                    onclick="blockUser(event,${user.id})">

                Block

            </button>

            <button class="unblock-btn"
                    onclick="unblockUser(event,${user.id})">

                Unblock

            </button>

            <button class="delete-btn"
                    onclick="deleteUser(event,${user.id})">

                Delete

            </button>

        </div>

    </td>

</tr>

`;
        });

    }

    catch(error){

        console.error(error);

        alert(
            "Error Loading Admin Dashboard"
        );
    }
}

// VIEW USER DETAILS

async function viewUserDetails(userId){

    const token =
        localStorage.getItem(
            "token"
        );

    document.getElementById(
        "userModal"
    ).style.display = "block";

    try{

        const response =
            await fetch(

                `${API_BASE}/admin/users/${userId}`,

                {
                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        const user =
            await response.json();

        console.log(
            "USER DETAILS : ",
            user
        );

        let medicationsHtml = "";

        let remindersHtml = "";

        // MEDICATIONS

        if(

            user.medications &&

            user.medications.length > 0

        ){

            user.medications.forEach(m => {

                medicationsHtml += `

<li>

    💊
    ${
        m.medicationName ||
        m.name ||
        "Medication"
    }

</li>

`;
            });

        }else{

            medicationsHtml = `

<li>
    No Medications Found
</li>

`;
        }

        // REMINDERS

		// REMINDERS

		if(

		    user.reminderLogs &&

		    user.reminderLogs.length > 0

		){

		    user.reminderLogs.forEach(r => {

		        remindersHtml += `

		<div class="reminder-card">

		    <div class="reminder-top">

		        ⏰
		        <strong>

		            ${
		                r.medicationName ||
		                "Reminder"
		            }

		        </strong>

		    </div>

		    <div class="reminder-info">

		        <p>

		            <strong>Status:</strong>

		            ${
		                r.status || "N/A"
		            }

		        </p>

		        <p>

		            <strong>Date & Time:</strong>

		            ${
		                r.scheduledTime

		                ?

		                new Date(
		                    r.scheduledTime
		                ).toLocaleString()

		                :

		                "N/A"
		            }

		        </p>

		        <p>

		            <strong>Notes:</strong>

		            ${
		                r.notes || "No Notes"
		            }

		        </p>

		    </div>

		</div>

		`;
		    });

		}else{

		    remindersHtml = `

		<li>
		    No Reminders Found
		</li>

		`;
		}

        document.getElementById(
            "userDetails"
        ).innerHTML = `

<div class="details-section">

    <h3>
        💊 Medications
    </h3>

    <ul>
        ${medicationsHtml}
    </ul>

</div>

<div class="details-section">

    <h3>
        ⏰ Reminders
    </h3>

    <ul>
        ${remindersHtml}
    </ul>

</div>

`;
    }

    catch(error){

        console.error(error);

        document.getElementById(
            "userDetails"
        ).innerHTML = `

<h3>
    Error Loading User Details
</h3>

<p>
    Unable To Load User Data
</p>

`;
    }
}

// CLOSE MODAL

function closeModal(){

    document.getElementById(
        "userModal"
    ).style.display = "none";
}

// BLOCK USER

async function blockUser(event,userId){

    event.stopPropagation();

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        const response =
            await fetch(

                `${API_BASE}/admin/users/${userId}/block`,

                {
                    method:"PUT",

                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        const message =
            await response.text();

        alert(message);

		if(response.ok){

		    setTimeout(() => {

		        loadAdminDashboard();

		    }, 500);
		}
    }

    catch(error){

        console.error(error);

        alert(
            "Server Error"
        );
    }
}

// UNBLOCK USER

async function unblockUser(event,userId){

    event.stopPropagation();

    const token =
        localStorage.getItem(
            "token"
        );

    try{

        const response =
            await fetch(

                `${API_BASE}/admin/users/${userId}/unblock`,

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
                "User Unblocked Successfully"
            );

            loadAdminDashboard();

        }else{

            alert(
                "Failed To Unblock User"
            );
        }

    }

    catch(error){

        console.error(error);

        alert(
            "Error Unblocking User"
        );
    }
}

// CREATE ADMIN

async function createAdmin(){

    const token =
        localStorage.getItem(
            "token"
        );

    const fullName =
        document.getElementById(
            "adminName"
        ).value;

    const email =
        document.getElementById(
            "adminEmail"
        ).value;

    const password =
        document.getElementById(
            "adminPassword"
        ).value;

    const phone =
        document.getElementById(
            "adminPhone"
        ).value;

    try{

        const response =
            await fetch(

                `${API_BASE}/admin/create-admin`,

                {
                    method:"POST",

                    headers:{
                        "Content-Type":
                            "application/json",

                        Authorization:
                            `Bearer ${token}`
                    },

                    body:JSON.stringify({

                        fullName:
                            fullName,

                        email:
                            email,

                        password:
                            password,

                        phone:
                            phone,

                        timezone:
                            "Asia/Kolkata"
                    })
                }
            );

        const result =
            await response.text();

        if(response.ok){

            alert(
                "Admin Created Successfully"
            );

            document.getElementById(
                "adminName"
            ).value = "";

            document.getElementById(
                "adminEmail"
            ).value = "";

            document.getElementById(
                "adminPassword"
            ).value = "";

            document.getElementById(
                "adminPhone"
            ).value = "";

            loadAdminDashboard();

        }else{

            alert(result);
        }

    }

    catch(error){

        console.error(error);

        alert(
            "Error Creating Admin"
        );
    }
}

// DELETE USER

async function deleteUser(event,userId){

    event.stopPropagation();

    const token =
        localStorage.getItem(
            "token"
        );

    const confirmDelete =
        confirm(
            "Are you sure you want to delete this user?"
        );

    if(!confirmDelete){

        return;
    }

    try{

        const response =
            await fetch(

                `${API_BASE}/admin/users/${userId}`,

                {
                    method:"DELETE",

                    headers:{
                        Authorization:
                            `Bearer ${token}`
                    }
                }
            );

        if(response.ok){

            alert(
                "User Deleted Successfully"
            );

            loadAdminDashboard();

        }else{

            alert(
                "Failed To Delete User"
            );
        }

    }

    catch(error){

        console.error(error);

        alert(
            "Error Deleting User"
        );
    }
}

// LOGOUT

function logout(){

    localStorage.clear();

    window.location.href =
        "admin-login.html";
}

// CLOSE MODAL WHEN CLICK OUTSIDE

window.onclick = function(event){

    const modal =
        document.getElementById(
            "userModal"
        );

    if(event.target === modal){

        modal.style.display = "none";
    }
};