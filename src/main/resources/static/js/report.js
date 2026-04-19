(function () {
    function formatCurrency(value) {
        var number = Number(value || 0);
        if (!Number.isFinite(number)) {
            number = 0;
        }

        return new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD",
            minimumFractionDigits: 2
        }).format(number);
    }

    function escapeHtml(text) {
        if (text == null) {
            return "";
        }
        return String(text)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/\"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function setMessage(text, isError) {
        var messageEl = document.getElementById("reportMessage");
        if (!messageEl) {
            return;
        }
        messageEl.textContent = text || "";
        messageEl.style.color = isError ? "#b42318" : "#065f46";
    }

    function setText(id, value) {
        var element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
    }

    function renderCategoryTable(categoryData) {
        var table = document.getElementById("categoryTable");
        if (!table || !table.tBodies || !table.tBodies[0]) {
            return;
        }

        var tbody = table.tBodies[0];
        var keys = Object.keys(categoryData || {}).filter(function (key) {
            return key !== "message";
        });

        if (keys.length === 0) {
            tbody.innerHTML = "<tr><td colspan=\"2\">No category data available.</td></tr>";
            return;
        }

        tbody.innerHTML = keys
            .map(function (key) {
                return "<tr><td>" + escapeHtml(key) + "</td><td>" + escapeHtml(formatCurrency(categoryData[key])) + "</td></tr>";
            })
            .join("");
    }

    function normalizeDate(dateValue) {
        if (!dateValue) {
            return "-";
        }

        var normalized = new Date(dateValue);
        if (Number.isNaN(normalized.getTime())) {
            return String(dateValue);
        }

        return normalized.toLocaleDateString();
    }

    function renderRecentTransactions(items) {
        var table = document.getElementById("recentTransactionsTable");
        if (!table || !table.tBodies || !table.tBodies[0]) {
            return;
        }

        var tbody = table.tBodies[0];
        var rows = Array.isArray(items) ? items : [];

        if (rows.length === 0) {
            tbody.innerHTML = "<tr><td colspan=\"5\">No recent transactions found.</td></tr>";
            return;
        }

        tbody.innerHTML = rows
            .map(function (item) {
                return "<tr>"
                    + "<td>" + escapeHtml(normalizeDate(item.date)) + "</td>"
                    + "<td>" + escapeHtml(item.type || "-") + "</td>"
                    + "<td>" + escapeHtml(item.category || "-") + "</td>"
                    + "<td>" + escapeHtml(formatCurrency(item.amount)) + "</td>"
                    + "<td>" + escapeHtml(item.description || "-") + "</td>"
                    + "</tr>";
            })
            .join("");
    }

    function clearSession() {
        localStorage.removeItem("userId");
        localStorage.removeItem("userName");
        localStorage.removeItem("userEmail");
    }

    function initializeHeaderActions() {
        var logoutBtn = document.getElementById("logoutBtn");
        var printBtn = document.getElementById("printReportBtn");

        if (logoutBtn) {
            logoutBtn.addEventListener("click", function () {
                clearSession();
                window.location.href = "login.html";
            });
        }

        if (printBtn) {
            printBtn.addEventListener("click", function () {
                window.print();
            });
        }
    }

    async function loadReportData() {
        if (!window.ReportApi) {
            setMessage("Report API is not available.", true);
            return;
        }

        var userId = localStorage.getItem("userId");
        if (!userId) {
            setMessage("Please login to view reports.", true);
            setTimeout(function () {
                window.location.href = "login.html";
            }, 900);
            return;
        }

        try {
            setMessage("Loading report data...", false);

            var responses = await Promise.all([
                window.ReportApi.getSummary(userId),
                window.ReportApi.getMonthlyReport(userId),
                window.ReportApi.getCategoryReport(userId),
                window.ReportApi.getDashboardReport(userId)
            ]);

            var summary = responses[0] || {};
            var monthly = responses[1] || {};
            var category = responses[2] || {};
            var dashboard = responses[3] || {};

            setText("totalIncomeValue", formatCurrency(summary.totalIncome));
            setText("totalExpenseValue", formatCurrency(summary.totalExpense));
            setText("balanceValue", formatCurrency(summary.balance));
            setText("transactionCountValue", String(monthly.transactionCount || 0));

            setText("monthValue", monthly.month || "-");
            setText("yearValue", monthly.year || "-");
            setText("monthlyIncomeValue", formatCurrency(monthly.totalIncome));
            setText("monthlyExpenseValue", formatCurrency(monthly.totalExpense));

            renderCategoryTable(category);
            renderRecentTransactions(dashboard.recentTransactions);

            setMessage("Report updated.", false);
        } catch (error) {
            setMessage(error && error.message ? error.message : "Could not load report data.", true);
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        initializeHeaderActions();
        loadReportData();
    });
})();
