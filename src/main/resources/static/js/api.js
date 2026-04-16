(function () {
    var API_BASE_URL = "http://localhost:8080";

    async function postJson(path, payload) {
        var response = await fetch(API_BASE_URL + path, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        var data = null;
        try {
            data = await response.json();
        } catch (error) {
            data = null;
        }

        if (!response.ok) {
            var message = data && data.message ? data.message : "Request failed";
            throw new Error(message);
        }

        return data;
    }

    window.UserApi = {
        login: function (email, password) {
            return postJson("/users/login", {
                email: email,
                password: password
            });
        },
        register: function (name, email, password) {
            return postJson("/users/register", {
                name: name,
                email: email,
                password: password
            });
        }
    };
})();