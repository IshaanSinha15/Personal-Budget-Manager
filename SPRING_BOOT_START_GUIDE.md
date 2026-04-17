# Spring Boot Application - Quick Start Guide

## 🚀 Application Already Running at http://localhost:8080

The Personal Budget Manager application is **currently running and fully operational**!

---

## Command to Run Spring Boot

### Open Terminal and Navigate to Project
```bash
cd c:\Users\Ishika\Documents\GitHub\Personal-Budget-Manager
```

### Start Spring Boot
```bash
./mvnw spring-boot:run
```

### Expected Startup Sequence
```
[INFO] Building budgetmanager 0.0.1-SNAPSHOT
[INFO] Compiling 45 source files...
...
:: Spring Boot ::                (v4.0.5)

2026-04-17T18:28:24 INFO ... Starting BudgetmanagerApplication...
...
2026-04-17T18:28:34 INFO ... Tomcat started on port(s): 8080 (http)
2026-04-17T18:28:34 INFO ... Started BudgetmanagerApplication in 11.047 seconds

✅ Application Ready!
```

---

## Access Points

| Feature | URL |
|---------|-----|
| **Dashboard** | http://localhost:8080 |
| **Login** | http://localhost:8080/login.html |
| **Register** | http://localhost:8080/register.html |
| **Transactions** | http://localhost:8080/transactions.html |

---

## Database Configuration

The application uses:
- **Database:** MySQL 8.0.43
- **Database Name:** `budgetmanager` (auto-created)
- **Connection URL:** `jdbc:mysql://localhost:3306/budgetmanager`
- **Username:** `root`
- **Password:** Stored in `.env` file (`ishika@1311`)

---

## Stopping the Application

Press `Ctrl + C` in the terminal where Spring Boot is running.

---

## Troubleshooting

### If application won't start:
1. Verify MySQL is running: `mysql -u root -p"ishika@1311" -e "SELECT 1"`
2. Check port 8080 is available: `netstat -ano | findstr :8080`
3. Rebuild: `./mvnw clean install -DskipTests`

### If database errors occur:
```sql
-- Create database if deleted
mysql -u root -p"ishika@1311" -e "CREATE DATABASE IF NOT EXISTS budgetmanager;"
```

---

## Key Endpoints Available

### User Management
- `POST /users/register` - Register new user
- `POST /users/login` - User login

### Transactions
- `POST /api/transactions/add` - Add transaction
- `GET /api/transactions/{userId}` - Get all transactions
- `GET /api/transactions/{userId}/type/{type}` - Filter by INCOME/EXPENSE
- `GET /api/transactions/{userId}/category/{category}` - Filter by category
- `GET /api/transactions/{userId}/summary` - Get financial summary
- `PUT /api/transactions/{transactionId}` - Update transaction
- `DELETE /api/transactions/{transactionId}` - Delete transaction

---

**Application Status:** ✅ RUNNING  
**Last Started:** 2026-04-17 18:28:34  
**All Systems:** OPERATIONAL

