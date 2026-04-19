(function () {
    function toCurrency(value) {
        var numeric = Number(value);
        if (Number.isNaN(numeric)) {
            numeric = 0;
        }
        return "$" + numeric.toFixed(2);
    }

    function toPercent(value) {
        var numeric = Number(value);
        if (Number.isNaN(numeric)) {
            numeric = 0;
        }
        return numeric.toFixed(1) + "%";
    }

    function formatDateTime(value) {
        if (!value) {
            return "-";
        }

        var date = new Date(value);
        if (Number.isNaN(date.getTime())) {
            return "-";
        }

        return date.toLocaleString();
    }

    function setText(id, value) {
        var element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
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

    function renderBudget(budget) {
        if (!budget) {
            return;
        }

        var totalValue = document.getElementById("totalBudgetValue");
        var remainingValue = document.getElementById("remainingBudgetValue");
        var remainingCard = document.getElementById("remainingBudgetCard");
        var alertText = document.getElementById("budgetAlertText");
        var isExceeded = Boolean(budget.isExceeded);
        var period = budget.period || "monthly";

        // Dashboard cards (index.html)
        if (totalValue && remainingValue) {
            totalValue.textContent = toCurrency(budget.totalBudget);
            remainingValue.textContent = toCurrency(budget.remainingBudget);
        }

        if (remainingCard && alertText) {
            if (Number(budget.remainingBudget) < 0) {
                remainingCard.classList.add("exceeded");
                alertText.textContent = "Budget exceeded — adjust your spending or increase your monthly budget.";
                remainingCard.classList.add("remaining");
            } else {
                remainingCard.classList.remove("exceeded");
                alertText.textContent = "You are tracking your monthly spending against the budget.";
                remainingCard.classList.add("remaining");
            }
        }

        // Budget details page (budget.html)
        setText("totalBudgetDetail", toCurrency(budget.totalBudget));
        setText("totalSpentDetail", toCurrency(budget.totalSpent));
        setText("remainingBudgetDetail", toCurrency(budget.remainingBudget));
        setText("percentageUsedDetail", toPercent(budget.percentageUsed));
        setText("budgetStatusDetail", isExceeded ? "Exceeded" : "Within Budget");
        setText("budgetPeriod", period);
        setText("budgetId", budget.id != null ? String(budget.id) : "-");
        setText("budgetCreatedDetail", formatDateTime(budget.createdAt));
        setText("budgetUpdatedDetail", formatDateTime(budget.updatedAt));

        if (isExceeded) {
            setMessage("Warning: your budget has been exceeded.", "error");
        } else {
            setMessage("Budget loaded successfully.", "success");
        }
    }

    async function setBudget(event) {
        if (event) {
            event.preventDefault();
        }

        var amountInput = document.getElementById("budgetAmount");
        var amount = amountInput ? parseFloat(amountInput.value) : NaN;
        var userId = localStorage.getItem("userId");

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
            renderBudget(budget);
            setMessage("Monthly budget set successfully.", "success");
        } catch (error) {
            setMessage(error.message || "Unable to save budget.", "error");
        }
    }

    function getBudget(userId) {
        if (!userId) {
            return Promise.reject(new Error("Missing userId"));
        }
        return window.BudgetApi.getBudget(userId);
    }

    async function loadBudget() {
        var userId = localStorage.getItem("userId");
        var userName = localStorage.getItem("userName");
        var userNameElement = document.getElementById("budgetUserName");
        if (userNameElement) {
            userNameElement.textContent = userName || "User";
        }

        if (!userId) {
            setMessage("Login first to manage your budget.", "error");
            return;
        }

        try {
            var budget = await getBudget(userId);
            renderBudget(budget);
        } catch (error) {
            setMessage("No active budget found. Create one using the form above.", "error");
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        var logoutBtn = document.getElementById("logoutBtn");
        if (logoutBtn) {
            logoutBtn.addEventListener("click", function () {
                localStorage.removeItem("userId");
                localStorage.removeItem("userName");
                localStorage.removeItem("userEmail");
                window.location.href = "login.html";
            });
        }

        var budgetForm = document.getElementById("budgetForm");
        if (budgetForm) {
            budgetForm.addEventListener("submit", setBudget);
        }

        loadBudget();
    });

    window.setBudget = setBudget;
    window.getBudget = getBudget;
})();