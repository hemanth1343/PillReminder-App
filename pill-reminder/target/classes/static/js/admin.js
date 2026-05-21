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

window.onload = function(){

    loadAdminDashboard();
};

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

<tr>

    <td>${user.id}</td>

    <td>${user.fullName}</td>

    <td>${user.email}</td>

    <td>${user.role}</td>

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

function logout(){

    localStorage.clear();

    window.location.href =
        "admin-login.html";
}
