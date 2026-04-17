(function () {
    // Category mapping for Income and Expense
    var CATEGORIES = {
        INCOME: [
            "Salary",
            "Freelance",
            "Investment",
            "Bonus",
            "Gift",
            "Other Income"
        ],
        EXPENSE: [
            "Food",
            "Transportation",
            "Entertainment",
            "Utilities",
            "Shopping",
            "Healthcare",
            "Education",
            "Insurance",
            "Rent",
            "Other Expense"
        ]
    };

    // Initialize page
    function initializePage() {
        // Check if user is logged in
        var userId = localStorage.getItem("userId");
        if (!userId) {
            window.location.href = "login.html";
            return;
        }

        // Set today's date as default
        var dateInput = document.getElementById("date");
        if (dateInput) {
            var today = new Date().toISOString().split('T')[0];
            dateInput.value = today;
        }

        // Setup event listeners
        var transactionForm = document.getElementById("transactionForm");
        if (transactionForm) {
            transactionForm.addEventListener("submit", submitTransaction);
        }

        var typeSelect = document.getElementById("type");
        if (typeSelect) {
            typeSelect.addEventListener("change", updateCategories);
        }

        var filterType = document.getElementById("filterType");
        if (filterType) {
            filterType.addEventListener("change", function () {
                loadTransactions();
            });
        }

        // Load initial data
        updateCategories();
        loadTransactions();
    }

    // Update category dropdown based on transaction type
    function updateCategories() {
        var typeSelect = document.getElementById("type");
        var categorySelect = document.getElementById("category");

        if (!typeSelect || !categorySelect) return;

        var selectedType = typeSelect.value;
        var categories = CATEGORIES[selectedType] || [];

        // Clear existing options
        var placeholder = categorySelect.querySelector('option[value=""]');
        categorySelect.innerHTML = '';

        if (placeholder) {
            categorySelect.appendChild(placeholder);
        }

        // Add categories
        categories.forEach(function (category) {
            var option = document.createElement("option");
            option.value = category;
            option.textContent = category;
            categorySelect.appendChild(option);
        });

        categorySelect.disabled = !selectedType;
    }

    // Submit transaction form
    async function submitTransaction(event) {
        event.preventDefault();

        var userId = localStorage.getItem("userId");
        if (!userId) {
            setTransactionMessage("User not authenticated", "error");
            return;
        }

        var type = document.getElementById("type").value;
        var category = document.getElementById("category").value;
        var amount = parseFloat(document.getElementById("amount").value);
        var date = document.getElementById("date").value;
        var description = document.getElementById("description").value;

        // Validation
        if (!type || !category || !amount || !date) {
            setTransactionMessage("Please fill in all required fields", "error");
            return;
        }

        if (amount <= 0) {
            setTransactionMessage("Amount must be greater than 0", "error");
            return;
        }

        setTransactionMessage("Adding transaction...", "");

        try {
            var response = await window.TransactionApi.addTransaction({
                userId: parseInt(userId),
                type: type,
                category: category,
                amount: amount,
                date: date + "T00:00:00",
                description: description || null
            });

            setTransactionMessage("Transaction added successfully!", "success");
            resetTransactionForm();
            loadTransactions();
        } catch (error) {
            setTransactionMessage(error.message || "Failed to add transaction", "error");
        }
    }

    // Reset form
    function resetTransactionForm() {
        var form = document.getElementById("transactionForm");
        if (form) {
            form.reset();

            // Reset date to today
            var dateInput = document.getElementById("date");
            if (dateInput) {
                var today = new Date().toISOString().split('T')[0];
                dateInput.value = today;
            }

            // Reset category dropdown
            updateCategories();
            setTransactionMessage("", "");
        }
    }

    // Load and display transactions
    async function loadTransactions() {
        var userId = localStorage.getItem("userId");
        if (!userId) {
            return;
        }

        var filterType = document.getElementById("filterType").value;

        try {
            var transactions;
            if (filterType) {
                transactions = await window.TransactionApi.getTransactionsByType(
                    userId,
                    filterType
                );
            } else {
                transactions = await window.TransactionApi.getTransactions(userId);
            }

            displayTransactions(transactions);
            calculateSummary(transactions);
        } catch (error) {
            var tbody = document.getElementById("transactionsBody");
            if (tbody) {
                tbody.innerHTML =
                    '<tr class="empty-row"><td colspan="6">No transactions found</td></tr>';
            }
        }
    }

    // Display transactions in table
    function displayTransactions(transactions) {
        var tbody = document.getElementById("transactionsBody");
        if (!tbody) return;

        if (!transactions || transactions.length === 0) {
            tbody.innerHTML =
                '<tr class="empty-row"><td colspan="6">No transactions found. Start by adding one!</td></tr>';
            return;
        }

        tbody.innerHTML = "";

        transactions.forEach(function (transaction) {
            var row = createTransactionRow(transaction);
            tbody.appendChild(row);
        });
    }

    // Create transaction table row
    function createTransactionRow(transaction) {
        var row = document.createElement("tr");

        var dateStr = formatDate(transaction.date);
        var typeClass = transaction.type.toLowerCase();
        var amountClass =
            transaction.type === "INCOME" ? "income" : "expense";
        var amountSign =
            transaction.type === "INCOME" ? "+" : "-";

        row.innerHTML =
            '<td>' + dateStr + "</td>" +
            '<td><span class="type-badge ' + typeClass + '">' +
            transaction.type +
            "</span></td>" +
            "<td>" + (transaction.category || "N/A") + "</td>" +
            '<td class="amount ' + amountClass + '">' +
            amountSign +
            "$" +
            parseFloat(transaction.amount).toFixed(2) +
            "</td>" +
            "<td>" + (transaction.description || "-") + "</td>" +
            '<td class="actions">' +
            '<button class="action-btn action-delete" onclick="deleteTransaction(' +
            transaction.id +
            ')">Delete</button>' +
            "</td>";

        return row;
    }

    // Format date helper
    function formatDate(dateStr) {
        var options = {
            year: "numeric",
            month: "short",
            day: "numeric"
        };
        var date = new Date(dateStr);
        return date.toLocaleDateString("en-US", options);
    }

    // Calculate and display summary
    function calculateSummary(transactions) {
        var totalIncome = 0;
        var totalExpense = 0;

        if (transactions && transactions.length > 0) {
            transactions.forEach(function (transaction) {
                var amount = parseFloat(transaction.amount);
                if (transaction.type === "INCOME") {
                    totalIncome += amount;
                } else {
                    totalExpense += amount;
                }
            });
        }

        var netBalance = totalIncome - totalExpense;

        // Update summary display
        var summarySection = document.getElementById("summarySection");
        if (summarySection) {
            summarySection.style.display =
                transactions && transactions.length > 0 ? "grid" : "none";
        }

        var totalIncomeEl = document.getElementById("totalIncome");
        if (totalIncomeEl) {
            totalIncomeEl.textContent = "$" + totalIncome.toFixed(2);
        }

        var totalExpenseEl = document.getElementById("totalExpenses");
        if (totalExpenseEl) {
            totalExpenseEl.textContent = "$" + totalExpense.toFixed(2);
        }

        var balanceEl = document.getElementById("netBalance");
        if (balanceEl) {
            balanceEl.textContent = "$" + netBalance.toFixed(2);
        }
    }

    // Delete transaction
    async function deleteTransaction(transactionId) {
        var confirmed = confirm(
            "Are you sure you want to delete this transaction?"
        );
        if (!confirmed) {
            return;
        }

        try {
            await window.TransactionApi.deleteTransaction(transactionId);
            loadTransactions();
        } catch (error) {
            alert(error.message || "Failed to delete transaction");
        }
    }

    // Set message display
    function setTransactionMessage(text, type) {
        var messageElement = document.getElementById("transactionMessage");
        if (!messageElement) {
            return;
        }

        messageElement.textContent = text;
        messageElement.classList.remove("error", "success");
        if (type) {
            messageElement.classList.add(type);
        }
    }

    // Refresh transactions
    function refreshTransactions() {
        loadTransactions();
    }

    // Expose functions to global scope
    window.resetTransactionForm = resetTransactionForm;
    window.deleteTransaction = deleteTransaction;
    window.refreshTransactions = refreshTransactions;

    // Initialize on DOM ready
    document.addEventListener("DOMContentLoaded", initializePage);
})();
