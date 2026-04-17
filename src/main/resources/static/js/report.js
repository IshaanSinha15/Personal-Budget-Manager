/**
 * report.js  –  Reports & Dashboard module (Tahir)
 *
 * Responsibilities (SRP):
 *   - Fetch dashboard data from /reports/dashboard/{userId}
 *   - Render summary cards: income, expense, balance
 *   - Render recent-transactions table
 *
 * Design patterns:
 *   - Facade  : single loadDashboard() call hides all fetch/render steps
 *   - Template Method : fetchData → processData → renderUI
 *
 * Does NOT touch budget logic – that lives in budget.js (Low Coupling).
 */

(function () {

    // ── Helpers ────────────────────────────────────────────────────────────

    function formatMoney(value) {
        var num = parseFloat(value);
        if (isNaN(num)) { return "$0.00"; }
        return "$" + num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function formatDate(dateStr) {
        if (!dateStr) { return "—"; }
        var d = new Date(dateStr);
        if (isNaN(d.getTime())) { return dateStr; }
        return d.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
    }

    // ── Step 1 : fetch ─────────────────────────────────────────────────────

    async function fetchDashboard(userId) {
        var response = await fetch("http://localhost:8080/reports/dashboard/" + encodeURIComponent(userId));
        if (!response.ok) {
            throw new Error("Dashboard API returned " + response.status);
        }
        return response.json();
    }

    // ── Step 2 : process (validate / normalise) ────────────────────────────

    function processDashboard(data) {
        return {
            totalIncome:          parseFloat(data.totalIncome)  || 0,
            totalExpense:         parseFloat(data.totalExpense) || 0,
            balance:              parseFloat(data.balance)      || 0,
            recentTransactions:   Array.isArray(data.recentTransactions) ? data.recentTransactions : []
        };
    }

    // ── Step 3 : render ────────────────────────────────────────────────────

    function renderSummaryCards(data) {
        var incomeEl  = document.getElementById("dashIncome");
        var expenseEl = document.getElementById("dashExpense");
        var balanceEl = document.getElementById("dashBalance");

        if (incomeEl)  { incomeEl.textContent  = formatMoney(data.totalIncome);  }
        if (expenseEl) { expenseEl.textContent = formatMoney(data.totalExpense); }
        if (balanceEl) { balanceEl.textContent = formatMoney(data.balance);      }
    }

    function renderTransactionsTable(transactions) {
        var container = document.getElementById("recentTransactionsContainer");
        if (!container) { return; }

        if (transactions.length === 0) {
            container.innerHTML = "<p class=\"no-data\">No transactions found. Add your first transaction to see it here.</p>";
            return;
        }

        var rows = transactions.map(function (t) {
            var type    = (t.type || "").toUpperCase();
            var cat     = t.category || "—";
            var desc    = t.description || "—";
            var sign    = type === "INCOME" ? "+" : "-";
            var amount  = formatMoney(t.amount);
            var dateStr = formatDate(t.date || t.createdAt);

            return "<tr>" +
                "<td>" + dateStr + "</td>" +
                "<td><span class=\"badge " + type + "\">" + type + "</span></td>" +
                "<td>" + cat + "</td>" +
                "<td>" + desc + "</td>" +
                "<td class=\"txn-amount " + type + "\">" + sign + amount + "</td>" +
            "</tr>";
        }).join("");

        container.innerHTML =
            "<table class=\"txn-table\">" +
                "<thead><tr>" +
                    "<th>Date</th>" +
                    "<th>Type</th>" +
                    "<th>Category</th>" +
                    "<th>Description</th>" +
                    "<th>Amount</th>" +
                "</tr></thead>" +
                "<tbody>" + rows + "</tbody>" +
            "</table>";
    }

    // ── Main entry point ───────────────────────────────────────────────────

    async function loadDashboard(userId) {
        if (!userId) {
            // Not logged in — show placeholder text, budget.js handles its own message
            var container = document.getElementById("recentTransactionsContainer");
            if (container) {
                container.innerHTML = "<p class=\"no-data\">Login first to see your dashboard data.</p>";
            }
            return;
        }

        try {
            // Template Method: fetch → process → render
            var raw       = await fetchDashboard(userId);
            var data      = processDashboard(raw);
            renderSummaryCards(data);
            renderTransactionsTable(data.recentTransactions);
        } catch (error) {
            console.error("Dashboard load error:", error);

            // Show graceful fallback — don't crash the whole page
            var incomeEl  = document.getElementById("dashIncome");
            var expenseEl = document.getElementById("dashExpense");
            var balanceEl = document.getElementById("dashBalance");
            if (incomeEl)  { incomeEl.textContent  = "$0.00"; }
            if (expenseEl) { expenseEl.textContent = "$0.00"; }
            if (balanceEl) { balanceEl.textContent = "$0.00"; }

            var container = document.getElementById("recentTransactionsContainer");
            if (container) {
                container.innerHTML = "<p class=\"no-data\">Could not load transactions. Make sure the server is running.</p>";
            }
        }
    }

    // ── Auto-run on DOMContentLoaded ───────────────────────────────────────

    document.addEventListener("DOMContentLoaded", function () {
        var userId = localStorage.getItem("userId");
        loadDashboard(userId);
    });

    // Expose globally so other scripts can call it if needed
    window.loadDashboard = loadDashboard;

})();
