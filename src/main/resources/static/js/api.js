(function () {
    var API_BASE_URL = (window.location && window.location.origin && window.location.origin !== "null")
        ? window.location.origin
        : "http://localhost:8080";

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
            var message = data && (data.message || data.error) ? (data.message || data.error) : "Request failed";
            throw new Error(message);
        }

        return data;
    }

    async function getJson(path) {
        var response = await fetch(API_BASE_URL + path, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
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

    async function deleteRequest(path) {
        var response = await fetch(API_BASE_URL + path, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            }
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

    // Transaction API - Following LSP and low coupling principles
    // UI triggers creation via API (GRASP Creator pattern)
    window.TransactionApi = {
        addTransaction: function (transactionData) {
            return postJson("/api/transactions/add", transactionData);
        },
        getTransactions: function (userId) {
            return getJson("/api/transactions/" + userId);
        },
        getTransactionsByType: function (userId, type) {
            return getJson("/api/transactions/" + userId + "/type/" + type);
        },
        getTransactionsByCategory: function (userId, category) {
            return getJson("/api/transactions/" + userId + "/category/" + category);
        },
        getTransactionSummary: function (userId) {
            return getJson("/api/transactions/" + userId + "/summary");
        },
        deleteTransaction: function (transactionId) {
            return deleteRequest("/api/transactions/" + transactionId);
        }
    };

    window.BudgetApi = {
        setBudget: function (userId, amount) {
            return postJson("/budget/set", {
                userId: Number(userId),
                amount: Number(amount),
                period: "monthly"
            });
        },
        getBudget: function (userId) {
            return getJson("/budget/" + encodeURIComponent(userId));
        }
    };
})();