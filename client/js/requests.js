function getRequestText(endpoint) {
    endpoint = "http://localhost:65432/" + endpoint;
    return fetch(endpoint, {method: "GET"})  // Replace with your URL
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();  // Parse text response
    })
    .catch(error => {
        Materialize.toast(error, 2000);
        console.error('There was a problem with the fetch operation:', error);
        throw error; // Re-throw error to allow further handling
    });
}

function getRequestData(endpoint, data = {}, method = "GET") {
    const url = new URL(`http://localhost:65432/${endpoint}`);

    if (method === "GET") {
        // Append query parameters for GET requests
        Object.keys(data).forEach(key => url.searchParams.append(key, data[key]));
    }

    return fetch(url.toString(), {
        method: method,
        headers: {
            "Content-Type": "application/json",
        },
        body: method !== "GET" ? JSON.stringify(data) : null, // Only send body for non-GET requests
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Network response was not ok. Status: ${response.status}`);
        }
        return response.text(); // Return raw text
    })
    .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
        // Replace or remove Materialize.toast depending on your environment
        Materialize.toast(error.message, 2000);
    
        throw error; // Re-throw the error for further handling
    });
}


function postRequest(endpoint) {
    endpoint = "http://localhost:65432/" + endpoint;
    return fetch(endpoint, {method: "POST"})  // Replace with your URL
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();  // Parse text response
    })
    .catch(error => {
        Materialize.toast(error, 2000);
        console.error('There was a problem with the fetch operation:', error);
        throw error; // Re-throw error to allow further handling
    });
}

function postRequestData(endpoint, jsonData) {
    const url = "http://localhost:65432/" + endpoint; // Construct the full endpoint URL

    return fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json", // Set content type to JSON
        },
        body: JSON.stringify(jsonData), // Convert JavaScript object to JSON string
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Network response was not ok: ${response.statusText}`);
        }
        return response.json(); // Parse response as JSON
    })
    .catch(error => {
        Materialize.toast(error, 2000);
        console.error("There was a problem with the fetch operation:", error);
        throw error; // Re-throw error to allow further handling
    });
}

