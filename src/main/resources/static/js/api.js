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
            var message = data && (data.message || data.error) ? (data.message || data.error) : "Request failed";
            throw new Error(message);
        }

        return data;
    }

    async function putJson(path, payload) {
        var response = await fetch(API_BASE_URL + path, {
            method: "PUT",
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
            var message = data && (data.message || data.error) ? (data.message || data.error) : "Request failed";
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
            var message = data && (data.message || data.error) ? (data.message || data.error) : "Request failed";
            throw new Error(message);
        }

        return data;
    }

    function resolveUserId(userId) {
        var resolved = userId != null && userId !== "" ? Number(userId) : Number(localStorage.getItem("userId"));
        if (!resolved || resolved <= 0 || Number.isNaN(resolved)) {
            throw new Error("User session not found. Please login again.");
        }
        return resolved;
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
            var resolvedUserId = resolveUserId(transactionData && transactionData.userId);
            var payload = Object.assign({}, transactionData, { userId: resolvedUserId });
            return postJson("/api/transactions/add?userId=" + encodeURIComponent(resolvedUserId), payload)
                .then(function (response) {
                    return response && response.data ? response.data : response;
                });
        },
        getTransactions: function (userId) {
            var resolvedUserId = resolveUserId(userId);
            return getJson("/api/transactions/" + resolvedUserId)
                .then(function (response) {
                    return response && response.data ? response.data : [];
                });
        },
        getTransactionsByType: function (userId, type) {
            var resolvedUserId = resolveUserId(userId);
            return getJson("/api/transactions/" + resolvedUserId + "/type/" + type)
                .then(function (response) {
                    return response && response.data ? response.data : [];
                });
        },
        getTransactionsByCategory: function (userId, category) {
            var resolvedUserId = resolveUserId(userId);
            return getJson("/api/transactions/" + resolvedUserId + "/category/" + category)
                .then(function (response) {
                    return response && response.data ? response.data : [];
                });
        },
        getTransactionSummary: function (userId) {
            var resolvedUserId = resolveUserId(userId);
            return getJson("/api/transactions/" + resolvedUserId + "/summary")
                .then(function (response) {
                    return response && response.data ? response.data : {};
                });
        },
        updateTransaction: function (transactionId, payload) {
            return putJson("/api/transactions/" + transactionId, payload)
                .then(function (response) {
                    return response && response.data ? response.data : response;
                });
        },
        deleteTransaction: function (transactionId) {
            return deleteRequest("/api/transactions/" + transactionId);
        }
    };

    window.BudgetApi = {
        setBudget: function (userId, amount) {
            return postJson("/api/budget/set?userId=" + encodeURIComponent(Number(userId)), {
                userId: Number(userId),
                amount: Number(amount),
                period: "monthly"
            });
        },
        getBudget: function (userId) {
            return getJson("/api/budget/" + encodeURIComponent(userId) + "?period=monthly");
        }
    };

    window.ReportApi = {
        getSummary: function (userId) {
            var resolvedUserId = resolveUserId(userId);
            return getJson("/reports/summary/" + encodeURIComponent(resolvedUserId));
        },
        getMonthlyReport: function (userId) {
            var resolvedUserId = resolveUserId(userId);
            return getJson("/reports/monthly/" + encodeURIComponent(resolvedUserId));
        },
        getCategoryReport: function (userId) {
            var resolvedUserId = resolveUserId(userId);
            return getJson("/reports/category/" + encodeURIComponent(resolvedUserId));
        },
        getDashboardReport: function (userId) {
            var resolvedUserId = resolveUserId(userId);
            return getJson("/reports/dashboard/" + encodeURIComponent(resolvedUserId));
        }
    };
})();