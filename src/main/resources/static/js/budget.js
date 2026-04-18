(function () {
    function formatMoney(value) {
        var numericValue = Number(value || 0);
        return "$" + numericValue.toFixed(2);
    }

    function setMessage(text, type) {
        var messageElement = document.getElementById("budgetMessage");
        if (!messageElement) {
            return;
        }
        messageElement.textContent = text;
        messageElement.classList.remove("success", "error");
        if (type) {
            messageElement.classList.add(type);
        }
    }

    function renderDashboardSummary(budget) {
        var totalValue = document.getElementById("totalBudgetValue");
        var remainingValue = document.getElementById("remainingBudgetValue");
        var remainingCard = document.getElementById("remainingBudgetCard");
        var alertText = document.getElementById("budgetAlertText");

        if (!totalValue || !remainingValue || !remainingCard || !alertText) {
            return;
        }

        if (!budget) {
            totalValue.textContent = formatMoney(0);
            remainingValue.textContent = formatMoney(0);
            remainingCard.classList.remove("exceeded");
            alertText.textContent = "No monthly budget found. Set it in Budget Details.";
            return;
        }

        totalValue.textContent = formatMoney(budget.totalBudget);
        remainingValue.textContent = formatMoney(budget.remainingBudget);

        if (Number(budget.remainingBudget) < 0) {
            remainingCard.classList.add("exceeded");
            alertText.textContent = "Budget exceeded. Visit Budget Details for a full breakdown.";
        } else {
            remainingCard.classList.remove("exceeded");
            alertText.textContent = "Dashboard shows total and remaining monthly budget.";
        }
    }

    function renderBudgetDetails(budget) {
        if (!budget) {
            return;
        }

        var fields = {
            budgetId: budget.id,
            budgetPeriod: budget.period || "monthly",
            totalBudgetDetail: formatMoney(budget.totalBudget),
            totalSpentDetail: formatMoney(budget.totalSpent),
            remainingBudgetDetail: formatMoney(budget.remainingBudget),
            percentageUsedDetail: Number(budget.percentageUsed || 0).toFixed(1) + "%",
            budgetStatusDetail: budget.isExceeded ? "Exceeded" : "Within Budget",
            budgetCreatedDetail: budget.createdAt ? new Date(budget.createdAt).toLocaleString() : "-",
            budgetUpdatedDetail: budget.updatedAt ? new Date(budget.updatedAt).toLocaleString() : "-"
        };

        Object.keys(fields).forEach(function (id) {
            var element = document.getElementById(id);
            if (element) {
                element.textContent = fields[id];
            }
        });

        var statusEl = document.getElementById("budgetStatusDetail");
        if (statusEl) {
            statusEl.classList.remove("status-ok", "status-error");
            statusEl.classList.add(budget.isExceeded ? "status-error" : "status-ok");
        }
    }

    function currentUserId() {
        var userId = localStorage.getItem("userId");
        return userId ? Number(userId) : null;
    }

    function clearSession() {
        localStorage.removeItem("userId");
        localStorage.removeItem("userName");
        localStorage.removeItem("userEmail");
    }

    function bindCommonLogout() {
        var logoutBtn = document.getElementById("logoutBtn");
        if (logoutBtn) {
            logoutBtn.addEventListener("click", function () {
                clearSession();
                window.location.href = "login.html";
            });
        }
    }

    async function loadBudgetForDashboard() {
        var dashboardMarker = document.querySelector(".dashboard-summary");
        if (!dashboardMarker) {
            return;
        }

        var userId = currentUserId();
        if (!userId) {
            renderDashboardSummary(null);
            setMessage("Login to sync dashboard budget values.", "error");
            return;
        }

        try {
            var budget = await window.BudgetApi.getBudget(userId);
            renderDashboardSummary(budget);
            setMessage("Budget summary loaded.", "success");
        } catch (error) {
            renderDashboardSummary(null);
            setMessage("No budget found yet. Set it in Budget Details.", "error");
        }
    }

    async function setBudget(event) {
        if (event) {
            event.preventDefault();
        }

        var amountInput = document.getElementById("budgetAmount");
        var amount = amountInput ? parseFloat(amountInput.value) : NaN;
        var userId = currentUserId();

        if (!userId) {
            setMessage("Please login to set your budget.", "error");
            return;
        }

        if (isNaN(amount) || amount <= 0) {
            setMessage("Enter a valid monthly budget amount greater than zero.", "error");
            return;
        }

        setMessage("Saving budget...", "");

        try {
            var budget = await window.BudgetApi.setBudget(userId, amount);
            renderBudgetDetails(budget);
            setMessage("Monthly budget set successfully.", "success");
        } catch (error) {
            setMessage(error.message || "Unable to save budget.", "error");
        }
    }

    async function loadBudgetModule() {
        var budgetModule = document.getElementById("budgetModulePage");
        if (!budgetModule) {
            return;
        }

        var userId = currentUserId();
        if (!userId) {
            window.location.href = "login.html";
            return;
        }

        var userNameEl = document.getElementById("budgetUserName");
        if (userNameEl) {
            userNameEl.textContent = localStorage.getItem("userName") || "User";
        }

        try {
            var budget = await window.BudgetApi.getBudget(userId);
            renderBudgetDetails(budget);
            setMessage("Budget details loaded.", "success");
        } catch (error) {
            setMessage("No active budget found. Create one using the form.", "error");
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        bindCommonLogout();

        var budgetForm = document.getElementById("budgetForm");
        if (budgetForm) {
            budgetForm.addEventListener("submit", setBudget);
        }

        loadBudgetForDashboard();
        loadBudgetModule();
    });

    window.setBudget = setBudget;
})();