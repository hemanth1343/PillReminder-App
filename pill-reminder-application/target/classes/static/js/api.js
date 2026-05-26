const BASE_URL =
    "http://localhost:8080/api";

function getToken() {

    return localStorage.getItem("token");
}

async function apiRequest(
    endpoint,
    method = "GET",
    body = null
) {

    const token = getToken();

    console.log("TOKEN => ", token);

    const options = {

        method: method,

        headers: {
            "Content-Type": "application/json"
        }
    };

    // ADD TOKEN
    if(token) {

        options.headers.Authorization =
            `Bearer ${token}`;
    }

    // ADD BODY
    if(body) {

        options.body =
            JSON.stringify(body);
    }

    try {

        const response = await fetch(
            `${BASE_URL}${endpoint}`,
            options
        );

        console.log(response);

        if(
            response.status === 401 ||
            response.status === 403
        ) {

            alert("Session Expired");

            localStorage.clear();

            window.location.href =
                "index.html";

            return null;
        }

        return await response.json();

    }
    catch(error) {

        console.log(error);

        alert("Server Error");

        return null;
    }
}