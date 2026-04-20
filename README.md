# FinTrack - Personal Finance Manager

A full-stack web application for managing personal finances with budget tracking, transaction management, and detailed financial reports. FinTrack helps users control spending, monitor budgets in real-time, and gain insights into their financial habits.

## Features

✅ **Budget Management**
- Set and track monthly budgets
- Real-time spending monitoring
- Automatic alerts when budget exceeds 80% or is exceeded
- Support for multiple budget periods

✅ **Transaction Tracking**
- Record income and expenses
- Categorize transactions
- Track by transaction type (Income/Expense)
- View transaction history with filters

✅ **Financial Reports**
- Generate detailed spending reports
- View spending patterns by category
- Monthly summaries and analytics
- Visualize budget vs. actual spending

✅ **User Management**
- Secure user authentication
- Email-based login/registration
- Personal dashboard
- Session management

✅ **Responsive UI**
- Mobile-friendly interface
- Clean, modern design
- Intuitive navigation
- Real-time data updates

## Tech Stack

**Backend:**
- Java 17+
- Spring Boot 3.x
- Spring Security (Authentication)
- JPA/Hibernate (ORM)
- MySQL Database
- Maven

**Frontend:**
- HTML5
- CSS3 (Grid & Flexbox)
- Vanilla JavaScript (ES6+)
- Fetch API
- LocalStorage for session management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL Server 8.0+
- Git
- Web browser (Chrome, Firefox, Safari, Edge)

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/fintrack.git
cd FinTrack
```

### 2. Database Setup
```sql
CREATE DATABASE fintrack;
```

### 3. Configure Database Connection
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fintrack
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
# Using Maven
mvn spring-boot:run

# Or use the provided script
./run.sh          # Linux/Mac
run.bat           # Windows
```

The application will start at `http://localhost:8080`

## Quick Start

1. **Register**: Create a new account with email and password
2. **Login**: Access your personal dashboard
3. **Set Budget**: Define your monthly budget limit
4. **Add Transactions**: Record income and expenses
5. **Monitor**: Check budget status and spending patterns
6. **View Reports**: Generate detailed financial reports

## Project Structure

```
FinTrack/
├── src/main/java/com/budgetmanager/
│   ├── controller/          # REST API endpoints
│   ├── service/             # Business logic
│   ├── model/               # JPA entities
│   ├── repository/          # Database access
│   ├── decorator/           # Design patterns
│   ├── factory/             # Object creation
│   └── config/              # Security & app configuration
│
├── src/main/resources/
│   ├── static/              # Frontend files
│   │   ├── css/             # Stylesheets
│   │   ├── js/              # JavaScript files
│   │   └── *.html           # Page templates
│   └── application.properties
│
├── src/test/                # Unit tests
├── pom.xml                  # Maven dependencies
└── README.md
```

## Key Endpoints

### Authentication
- `POST /users/register` - Create new account
- `POST /users/login` - User login

### Budget
- `POST /budget/set` - Set monthly budget
- `GET /budget/{userId}` - Get budget details

### Transactions
- `POST /api/transactions/add` - Add transaction
- `GET /api/transactions/{userId}` - Get all transactions
- `GET /api/transactions/{userId}/type/{type}` - Filter by type

### Reports
- `GET /api/reports/{userId}` - Generate report
- `GET /api/reports/{userId}/summary` - Get summary

For detailed API documentation, see [API_ENDPOINTS_REFERENCE.md](./API_ENDPOINTS_REFERENCE.md)

## Usage Examples

### Setting a Budget
```javascript
const response = await fetch('/budget/set', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userId: 1,
    amount: 5000.00,
    period: 'monthly'
  })
});
```

### Adding a Transaction
```javascript
const response = await fetch('/api/transactions/add', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userId: 1,
    description: 'Grocery Shopping',
    amount: 150.00,
    category: 'Food',
    type: 'EXPENSE',
    date: '2024-04-20'
  })
});
```

## Database Schema

The application uses the following main tables:
- **users** - User accounts and authentication
- **budgets** - User budget allocations
- **budget_categories** - Category-wise budget breakdown
- **transactions** - Income and expense records
- **categories** - Transaction categories

See database schema in the documentation files for complete details.

## Design Patterns

- **Strategy Pattern** - Multiple budget calculation strategies
- **Composite Pattern** - Budget hierarchy with categories
- **Observer Pattern** - Notification system
- **Repository Pattern** - Data access abstraction
- **DTO Pattern** - Request/response objects
- **Module Pattern** - Frontend JavaScript organization

## Performance Notes

- Lazy loading enabled for JPA relationships
- Efficient query caching for frequently accessed data
- Optimized database indexes on userId and period
- Lightweight frontend with no heavy frameworks

## Security Features

- Password encryption with Spring Security
- CSRF protection
- SQL injection prevention via parameterized queries
- Session-based authentication with localStorage
- Input validation on both client and server

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Troubleshooting

**Port 8080 already in use:**
```bash
# Change port in application.properties
server.port=8081
```

**Database connection failed:**
- Verify MySQL is running
- Check database credentials
- Ensure database exists

**Budget not saving:**
- Clear browser cache
- Check browser console for errors
- Verify user is logged in

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/YourFeature`
3. Commit changes: `git commit -m 'Add YourFeature'`
4. Push to branch: `git push origin feature/YourFeature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Documentation

- [Budget System Documentation](./BUDGET_SYSTEM_DOCUMENTATION.md) - Detailed budget module guide
- [API Endpoints Reference](./API_ENDPOINTS_REFERENCE.md) - Complete API documentation
- [Spring Boot Start Guide](./SPRING_BOOT_START_GUIDE.md) - Backend setup guide
- [Transaction Frontend Guide](./TRANSACTION_FRONTEND_GUIDE.md) - Frontend transaction handling

## Support

For issues, questions, or suggestions:
1. Check existing documentation
2. Review error logs in console
3. Create an issue on GitHub
4. Contact the development team

## Roadmap

- [ ] Mobile app version
- [ ] Advanced analytics dashboard
- [ ] Budget forecasting
- [ ] Multi-currency support
- [ ] Export to PDF/CSV
- [ ] Collaborative budgets
- [ ] API rate limiting
- [ ] Two-factor authentication

---

**Version:** 1.0  
**Last Updated:** April 2026  
**Author:** FinTrack Development Team
