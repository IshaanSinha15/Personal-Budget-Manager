# Transaction Frontend - Quick Start Guide

## 🚀 Quick Access Guide

### Files Overview
```
src/main/resources/static/
├── index.html                 ← Dashboard hub
├── transactions.html          ← Transaction module main page
├── login.html                 ← Authentication
├── register.html              ← Registration
├── css/
│   ├── auth.css              ← Auth styling (existing)
│   └── transactions.css       ← Transaction styling (NEW)
└── js/
    ├── api.js                ← API client (UPDATED)
    ├── transaction.js        ← Transaction logic (NEW)
    └── user.js               ← Auth logic (existing)
```

### Documentation Files
```
├── TRANSACTION_FRONTEND_GUIDE.md           ← Full documentation
├── TRANSACTION_FRONTEND_VERIFICATION.md    ← Design verification
└── TRANSACTION_FRONTEND_QUICKSTART.md      ← This file
```

---

## 📋 How to Use the Transaction Module

### Step 1: Access the Module
1. Navigate to `http://localhost:8080/index.html`
2. Click on **"💰 Transactions"** card
3. Or directly go to `http://localhost:8080/transactions.html`

### Step 2: Add a Transaction
```
1. Select Type (Income / Expense)
2. Select Category (updates based on type)
3. Enter Amount (e.g., 5000.00)
4. Select Date (default is today)
5. Add Description (optional)
6. Click "Add Transaction"
```

**Categories Available:**
- **Income:** Salary, Freelance, Investment, Bonus, Gift, Other Income
- **Expense:** Food, Transportation, Entertainment, Utilities, Shopping, Healthcare, Education, Insurance, Rent, Other Expense

### Step 3: View Transactions
- **Table Display:** Shows all transactions with date, type, category, amount, description
- **Filter:** Select "Income Only" or "Expense Only" to filter
- **Summary:** Automatically shows Total Income, Total Expenses, Net Balance

### Step 4: Delete a Transaction
- Click **"Delete"** button on any transaction row
- Confirm the action in the popup
- Transaction removes immediately from table

---

## 🔌 API Endpoints Used

### Backend Connection Points

```javascript
// All endpoints require userId for security

// 1. Add Transaction
POST /api/transactions/add
{
    userId: 1,
    type: "INCOME",      // or "EXPENSE"
    category: "Salary",
    amount: 5000.00,
    date: "2024-06-15",
    description: "Optional"
}

// 2. Get All Transactions
GET /api/transactions/1

// 3. Filter by Type
GET /api/transactions/1/type/INCOME

// 4. Filter by Category
GET /api/transactions/1/category/Salary

// 5. Get Summary
GET /api/transactions/1/summary

// 6. Delete Transaction
DELETE /api/transactions/123
```

---

## 🧩 Adding a New Module (Budgets Example)

### Step 1: Create Files
```bash
# Create budgets page
touch src/main/resources/static/budgets.html

# Create budgets styling
touch src/main/resources/static/css/budgets.css

# Create budgets logic
touch src/main/resources/static/js/budget.js
```

### Step 2: Create budgets.html
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Budgets | Personal Budget Manager</title>
    <link rel="stylesheet" href="css/budgets.css">
</head>
<body>
    <div class="budgets-page">
        <section class="budgets-container">
            <header class="page-header">
                <h1>Manage Budgets</h1>
                <nav class="breadcrumb">
                    <a href="index.html">Dashboard</a> / <span>Budgets</span>
                </nav>
            </header>

            <!-- Add Budget Form -->
            <section class="form-section">
                <h2>Create Budget</h2>
                <form id="budgetForm">
                    <!-- Your form fields -->
                </form>
            </section>

            <!-- Display Budgets -->
            <section class="display-section">
                <h2>Your Budgets</h2>
                <!-- Your display content -->
            </section>

            <footer class="page-footer">
                <a href="index.html" class="btn btn-secondary">Back to Dashboard</a>
            </footer>
        </section>
    </div>

    <script src="js/api.js"></script>
    <script src="js/budget.js"></script>
</body>
</html>
```

### Step 3: Create budgets.css
```css
:root {
    --bg: #f4f6fb;
    --card-bg: #ffffff;
    --text: #1d2939;
    --primary: #1d4ed8;
    --border: #d0d5dd;
    --shadow: 0 10px 25px rgba(16, 24, 40, 0.08);
}

* {
    box-sizing: border-box;
}

body {
    margin: 0;
    font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
    background: radial-gradient(circle at top, #eef4ff 0%, var(--bg) 45%, #e9eef7 100%);
    color: var(--text);
}

.budgets-page {
    min-height: 100vh;
    padding: 2rem 1rem;
}

.budgets-container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    background-color: var(--card-bg);
    border: 1px solid var(--border);
    border-radius: 14px;
    box-shadow: var(--shadow);
    padding: 2rem;
}

/* Follow the same structure as transactions.css */
```

### Step 4: Create budget.js
```javascript
(function () {
    // Private state
    var API_BASE_URL = "http://localhost:8080";

    // Private functions
    function initializePage() {
        var userId = localStorage.getItem("userId");
        if (!userId) {
            window.location.href = "login.html";
            return;
        }

        var budgetForm = document.getElementById("budgetForm");
        if (budgetForm) {
            budgetForm.addEventListener("submit", submitBudget);
        }

        loadBudgets();
    }

    async function submitBudget(event) {
        event.preventDefault();
        
        var userId = localStorage.getItem("userId");
        // Your budget submission logic
        
        try {
            var response = await window.BudgetApi.addBudget({
                userId: parseInt(userId),
                // ... your fields
            });
            
            setBudgetMessage("Budget created!", "success");
            loadBudgets();
        } catch (error) {
            setBudgetMessage(error.message, "error");
        }
    }

    async function loadBudgets() {
        var userId = localStorage.getItem("userId");
        // Load and display budgets
    }

    function setBudgetMessage(text, type) {
        var messageElement = document.getElementById("budgetMessage");
        if (messageElement) {
            messageElement.textContent = text;
            messageElement.className = "form-message " + type;
        }
    }

    // Public API
    window.loadBudgets = loadBudgets;

    // Initialize
    document.addEventListener("DOMContentLoaded", initializePage);
})();
```

### Step 5: Extend api.js
```javascript
// In api.js, add after UserApi definition:

window.BudgetApi = {
    addBudget: function(data) {
        return postJson("/api/budgets/add", data);
    },
    getBudgets: function(userId) {
        return getJson("/api/budgets/" + userId);
    },
    updateBudget: function(budgetId, data) {
        return postJson("/api/budgets/" + budgetId, data);
    },
    deleteBudget: function(budgetId) {
        return deleteRequest("/api/budgets/" + budgetId);
    }
};
```

### Step 6: Update index.html
```html
<!-- In modules-grid, add -->
<a href="budgets.html" class="module-card">
    <h3>📊 Budgets</h3>
    <p>Create and track budgets by category</p>
</a>
```

---

## 🎨 Styling Guidelines

### Use CSS Variables (Consistency)
```css
/* ✅ DO - Use variables */
.my-element {
    color: var(--text);
    background: var(--card-bg);
    border: 1px solid var(--border);
}

/* ❌ DON'T - Hard-code colors */
.my-element {
    color: #1d2939;
    background: #ffffff;
    border: 1px solid #d0d5dd;
}
```

### Responsive Design Breakpoints
```css
/* Desktop: 1200px+ */
.container {
    grid-template-columns: repeat(3, 1fr);
}

/* Tablet: 768px - 1199px */
@media (max-width: 768px) {
    .container {
        grid-template-columns: repeat(2, 1fr);
    }
}

/* Mobile: < 768px */
@media (max-width: 480px) {
    .container {
        grid-template-columns: 1fr;
    }
}
```

### Naming Conventions
```css
/* Follow pattern from transactions.css */
.module-page              /* Main container */
.module-container         /* Content wrapper */
.page-header             /* Page title section */
.form-section            /* Form container */
.display-section         /* Content display */
.form-group              /* Individual form field */
.btn                     /* Button base */
.btn-primary             /* Action button */
.btn-secondary           /* Cancel/back button */
.form-message            /* Feedback message */
.form-message.success    /* Success state */
.form-message.error      /* Error state */
```

---

## 🔍 Debugging Tips

### Check Authentication
```javascript
// Open browser console (F12)
localStorage.getItem("userId");
// Should return a number, not null

// If null, user not logged in
window.location.href = "login.html";
```

### Check API Calls
```javascript
// Open Network tab in DevTools
// Look for requests to:
// - http://localhost:8080/api/transactions/add
// - http://localhost:8080/api/transactions/1

// Check Response:
// Should be JSON with transaction data
```

### Check Form Validation
```javascript
// In browser console:
// Set values manually
document.getElementById("type").value = "INCOME";
document.getElementById("amount").value = "100";

// Check if validation works
document.getElementById("transactionForm").dispatchEvent(
    new Event("submit")
);
```

### Common Issues

| Issue | Solution |
|-------|----------|
| "User not authenticated" | Clear localStorage, login again |
| Categories don't update | Check type is selected first |
| API returns 404 | Verify backend running on :8080 |
| Form doesn't clear | Check resetTransactionForm() called |
| Table empty | Check userId format (should be number) |

---

## 📚 Key JavaScript Functions

### transaction.js Public API
```javascript
window.resetTransactionForm()    // Clear form
window.deleteTransaction(id)     // Remove transaction
window.refreshTransactions()     // Reload from API
```

### api.js Public API
```javascript
window.TransactionApi.addTransaction(data)
window.TransactionApi.getTransactions(userId)
window.TransactionApi.getTransactionsByType(userId, type)
window.TransactionApi.getTransactionsByCategory(userId, category)
window.TransactionApi.deleteTransaction(id)

window.UserApi.login(email, password)
window.UserApi.register(name, email, password)
```

---

## 🚨 Important Notes

### Always Check User Authentication
```javascript
var userId = localStorage.getItem("userId");
if (!userId) {
    window.location.href = "login.html";
    return;
}
```

### Keep API Separate from Business Logic
```javascript
// ✅ GOOD - API logic in api.js
const response = await window.TransactionApi.addTransaction(data);

// ❌ BAD - API logic in transaction.js
const response = await fetch(API_URL + "/add", { ... });
```

### Always Include userId in API Calls
```javascript
// ✅ GOOD - User identified
userId: parseInt(userId)

// ❌ BAD - Could access other users' data
userId: undefined
```

### Use Module Pattern (IIFE)
```javascript
// ✅ GOOD - Encapsulated
(function () {
    var privateVar = ...;
    window.publicFunction = publicFunction;
})();

// ❌ BAD - Pollutes global scope
var CATEGORIES = ...;
function updateCategories() { ... }
```

---

## 🧪 Testing Checklist

### Before Committing Code
- [ ] Form submission works (add transaction)
- [ ] Table displays transactions
- [ ] Filter by type works (Income/Expense)
- [ ] Delete removes transaction
- [ ] Summary calculates correctly
- [ ] Page responds on mobile (< 480px)
- [ ] No console errors (F12)
- [ ] localStorage data persists
- [ ] Logout works and redirects to login

### Responsive Design
- [ ] Desktop (1200px+) - 3+ columns
- [ ] Tablet (768px) - 2 columns  
- [ ] Mobile (480px) - 1 column, readable

---

## 📞 When to Update Files

### Update api.js when:
- Adding new API endpoint
- Changing endpoint path
- Adding new module API
- Changing error handling

### Update transaction.js when:
- Adding new form fields
- Changing validation logic
- Adding new display feature
- Changing event handlers

### Update transactions.html when:
- Adding form fields
- Changing table columns
- Adding new sections

### Update transactions.css when:
- Changing colors (update :root)
- Adding responsive breakpoints
- Changing layout

---

## 🎯 Next Steps

### To Add Budgets Module:
1. Follow "Adding a New Module" section above
2. Create budgets-related API endpoints in backend
3. Update index.html with budgets link
4. Follow same module pattern

### To Add Reporting Module:
1. Create reports.html, report.js, reports.css
2. Create report API endpoints
3. Add charts/visualizations
4. Link from index.html

### To Enhance Transactions:
1. Add date range filtering (datepicker)
2. Add bulk operations (select multiple)
3. Add export to CSV
4. Add search functionality

---

## 📖 Full Documentation

For complete details, see:
- **Architecture & Patterns:** TRANSACTION_FRONTEND_GUIDE.md
- **Design Verification:** TRANSACTION_FRONTEND_VERIFICATION.md
- **Implementation Details:** Source code comments

---

**Quick Summary:**
- ✅ Transactions module fully implemented
- ✅ Easy to add new modules (follow template)
- ✅ Consistent with existing code
- ✅ Production-ready
- ✅ Well-documented

**Ready to extend! Follow the patterns and best practices outlined above.**
