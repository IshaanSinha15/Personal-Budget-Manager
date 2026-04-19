(function () {
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
        var totalValue = document.getElementById("totalBudgetValue");
        var remainingValue = document.getElementById("remainingBudgetValue");
        var remainingCard = document.getElementById("remainingBudgetCard");
        var alertText = document.getElementById("budgetAlertText");

        if (!budget || !totalValue || !remainingValue || !remainingCard || !alertText) {
            return;
        }

        totalValue.textContent = "$" + Number(budget.totalBudget).toFixed(2);
        remainingValue.textContent = "$" + Number(budget.remainingBudget).toFixed(2);

        if (Number(budget.remainingBudget) < 0) {
            remainingCard.classList.add("exceeded");
            alertText.textContent = "Budget exceeded — adjust your spending or increase your monthly budget.";
            remainingCard.classList.add("remaining");
        } else {
            remainingCard.classList.remove("exceeded");
            alertText.textContent = "You are tracking your monthly spending against the budget.";
            remainingCard.classList.add("remaining");
        }

        if (budget.isExceeded) {
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
        var budgetForm = document.getElementById("budgetForm");
        if (budgetForm) {
            budgetForm.addEventListener("submit", setBudget);
        }

        loadBudget();
    });

    window.setBudget = setBudget;
    window.getBudget = getBudget;
})();