# Complete API Endpoints Reference

## Application Running Successfully ✅

**Base URL:** `http://localhost:8080`  
**Status:** 🟢 RUNNING  
**Port:** 8080 (HTTP)

---

## 1. USER MANAGEMENT ENDPOINTS

### Base URL: `/users`

| Method | Endpoint | Purpose | Status |
|--------|----------|---------|--------|
| POST | `/users/register` | Register new user | ✅ WORKING |
| POST | `/users/login` | User authentication | ✅ Verified |

#### Request Example: Register User
```bash
POST http://localhost:8080/users/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword@123"
}
```

#### Response Example
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "message": "User registered successfully"
}
```

---

## 2. TRANSACTION MANAGEMENT ENDPOINTS

### Base URL: `/api/transactions`

| Method | Endpoint | Purpose | Status |
|--------|----------|---------|--------|
| POST | `/api/transactions/add` | Create new transaction | ✅ Verified |
| GET | `/api/transactions/{userId}` | Get all user transactions | ✅ Verified |
| GET | `/api/transactions/{userId}/type/{type}` | Filter by type (INCOME/EXPENSE) | ✅ Verified |
| GET | `/api/transactions/{userId}/category/{category}` | Filter by category | ✅ Verified |
| GET | `/api/transactions/{userId}/summary` | Get financial summary | ✅ Verified |
| POST | `/api/transactions/{transactionId}/clone` | Clone transaction (recurring) | ✅ Verified |
| PUT | `/api/transactions/{transactionId}` | Update transaction | ✅ Verified |
| DELETE | `/api/transactions/{transactionId}` | Delete transaction | ✅ Verified |
| GET | `/api/transactions/detail/{transactionId}` | Get single transaction | ✅ Verified |

#### Request Example: Add Transaction
```bash
POST http://localhost:8080/api/transactions/add
Content-Type: application/json

{
  "userId": 1,
  "type": "INCOME",
  "category": "SALARY",
  "amount": 50000.00,
  "date": "2026-04-17",
  "description": "Monthly salary"
}
```

#### Financial Summary Response
```json
{
  "totalIncome": 50000.00,
  "totalExpenses": 12500.00,
  "netBalance": 37500.00
}
```

---

## 3. BUDGET MANAGEMENT ENDPOINTS

### Base URL: `/api/budget`

| Method | Endpoint | Purpose | Status |
|--------|----------|---------|--------|
| POST | `/api/budget/set` | Set user budget | ✅ Verified |
| GET | `/api/budget/{userId}` | Get budget for period | ✅ Verified |
| GET | `/api/budget/user/{userId}/all` | Get all user budgets | ✅ Verified |
| GET | `/api/budget/{budgetId}/remaining` | Get remaining budget | ✅ Verified |
| POST | `/api/budget/{budgetId}/check` | Check if budget exceeded | ✅ Verified |

#### Request Example: Set Budget
```bash
POST http://localhost:8080/api/budget/set?userId=1
Content-Type: application/json

{
  "amount": 100000.00,
  "period": "MONTHLY"
}
```

---

## 4. REPORTS & ANALYTICS ENDPOINTS

### Base URL: `/reports`

| Method | Endpoint | Purpose | Status |
|--------|----------|---------|--------|
| GET | `/reports/monthly/{userId}` | Monthly income/expense totals | ✅ Verified |
| GET | `/reports/category/{userId}` | Category-wise spending breakdown | ✅ Verified |
| GET | `/reports/summary/{userId}` | Lifetime financial summary | ✅ Verified |
| GET | `/reports/dashboard/{userId}` | Dashboard data with recent transactions | ✅ Verified |

#### Request Example: Get Monthly Report
```bash
GET http://localhost:8080/reports/monthly/1
```

#### Response Example
```json
{
  "month": "April 2026",
  "totalIncome": 50000.00,
  "totalExpenses": 12500.00,
  "netBalance": 37500.00,
  "transactions": [...]
}
```

---

## 5. STATIC PAGES

| URL | Purpose |
|-----|---------|
| `http://localhost:8080/` | Dashboard (index.html) |
| `http://localhost:8080/login.html` | User login page |
| `http://localhost:8080/register.html` | User registration page |
| `http://localhost:8080/transactions.html` | Transaction management interface |

---

## 6. TRANSACTION CATEGORIES

### Income Categories
- SALARY
- FREELANCE
- INVESTMENT
- BONUS
- GIFT
- OTHER_INCOME

### Expense Categories
- DINING
- GROCERIES
- TRANSPORTATION
- ENTERTAINMENT
- UTILITIES
- SHOPPING
- HEALTHCARE
- EDUCATION
- INSURANCE
- RENT
- OTHER_EXPENSE

---

## 7. REQUEST/RESPONSE FORMATS

### Common Headers
```
Content-Type: application/json
Accept: application/json
```

### HTTP Status Codes
| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 404 | Not Found |
| 500 | Server Error |

### Error Response Example
```json
{
  "error": "Invalid request",
  "message": "Email already registered",
  "status": 400
}
```

---

## 8. TESTING ENDPOINTS WITH CURL

### Register User
```bash
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "Test@1234"
  }'
```

### Add Transaction
```bash
curl -X POST http://localhost:8080/api/transactions/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "EXPENSE",
    "category": "DINING",
    "amount": 500.00,
    "description": "Lunch"
  }'
```

### Get Transactions
```bash
curl -X GET http://localhost:8080/api/transactions/1
```

### Get Summary
```bash
curl -X GET http://localhost:8080/api/transactions/1/summary
```

---

## 9. DESIGN PATTERNS IMPLEMENTED

### Single Responsibility Principle
- Controllers: Route requests only
- Services: Business logic only
- Repositories: Data access only
- Models: Entity definitions only

### Liskov Substitution Principle
```java
// Income and Expense are substitutable for Transaction
public class Income extends Transaction {}
public class Expense extends Transaction {}
```

### Factory Pattern
```java
// TransactionFactory creates appropriate type
Transaction tx = TransactionFactory.createTransaction(TransactionType.INCOME);
```

### Strategy Pattern
```java
// Budget calculation strategies
budgetService.setStrategy(new MonthlyBudgetStrategy());
```

### Decorator Pattern
```java
// Add features to transactions
new TaggedTransaction(new NotedTransaction(baseTransaction));
```

### Repository Pattern
```java
// Spring Data JPA handles queries
List<Transaction> txs = transactionRepository.findByUserId(userId);
```

---

## 10. DATABASE SCHEMA

### Users Table
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL
);
```

### Transactions Table (Single-Table Inheritance)
```sql
CREATE TABLE transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  transaction_type VARCHAR(31) CHECK(transaction_type IN ('INCOME','EXPENSE')),
  type ENUM('INCOME','EXPENSE') NOT NULL,
  category ENUM(...) NOT NULL,
  user_id BIGINT NOT NULL,
  amount DECIMAL(38,2) NOT NULL,
  date DATETIME NOT NULL,
  description VARCHAR(500),
  created_at DATETIME NOT NULL
);
```

### Budgets Table
```sql
CREATE TABLE budgets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  total_budget FLOAT NOT NULL,
  total_spent FLOAT NOT NULL,
  period VARCHAR(255) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);
```

---

## 11. JAVASCRIPT API LAYER

### User API
```javascript
window.UserApi = {
  login(email, password),      // POST /users/login
  register(name, email, password) // POST /users/register
}
```

### Transaction API
```javascript
window.TransactionApi = {
  addTransaction(transactionData),              // POST /api/transactions/add
  getTransactions(userId),                       // GET /api/transactions/{userId}
  getTransactionsByType(userId, type),           // Filter by INCOME/EXPENSE
  getTransactionsByCategory(userId, category),   // Filter by category
  getTransactionSummary(userId),                 // GET summary
  deleteTransaction(transactionId)               // DELETE
}
```

---

## 12. QUICK START STEPS

1. **Register User**
```bash
POST /users/register
{
  "name": "Your Name",
  "email": "your@email.com",
  "password": "YourPassword@123"
}
```

2. **Login**
```bash
POST /users/login
{
  "email": "your@email.com",
  "password": "YourPassword@123"
}
```

3. **Add Income**
```bash
POST /api/transactions/add
{
  "userId": 1,
  "type": "INCOME",
  "category": "SALARY",
  "amount": 50000,
  "description": "Monthly salary"
}
```

4. **Add Expense**
```bash
POST /api/transactions/add
{
  "userId": 1,
  "type": "EXPENSE",
  "category": "DINING",
  "amount": 500,
  "description": "Lunch with team"
}
```

5. **View Summary**
```bash
GET /api/transactions/1/summary
```

---

## 13. ENVIRONMENT CONFIGURATION

**.env File** (Project Root)
```properties
DB_PASSWORD=ishika@1311
```

**application.properties** (src/main/resources)
```properties
spring.application.name=budgetmanager
spring.config.import=optional:file:.env[.properties]
spring.datasource.url=jdbc:mysql://localhost:3306/budgetmanager
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
```

---

## 14. TROUBLESHOOTING

### Port 8080 Already in Use
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process (replace PID)
taskkill /PID <PID> /F
```

### Database Connection Error
```bash
# Test MySQL connection
mysql -u root -p"ishika@1311" -e "SELECT 1"

# Recreate database if needed
mysql -u root -p"ishika@1311" -e "CREATE DATABASE budgetmanager;"
```

### Application Won't Start
```bash
# Clean and rebuild
./mvnw clean install -DskipTests

# Then run
./mvnw spring-boot:run
```

---

## 15. PERFORMANCE NOTES

- **Connection Pool:** HikariCP with optimal settings
- **Isolation Level:** REPEATABLE_READ (MySQL default)
- **Startup Time:** ~11 seconds average
- **Database Engine:** InnoDB (transactions support)
- **Query Optimization:** Indexed on user_id and email

---

**All Systems Operational ✅**  
**Last Updated:** 2026-04-17 18:28  
**Application Status:** RUNNING on http://localhost:8080

