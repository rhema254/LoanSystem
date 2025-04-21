# 💼 Loan Management System

A Simple Loan Management System built with **Spring Boot** and **PostgreSQL**, designed to manage users, assign loans, auto-generate repayment schedules, and offer simple loan analytics. This system supports role-based access control with **JWT authentication** and serves different stakeholders including Loan Officers, Admins, and Super Admins.

---

## 🚀 Features

- 👥 **User Roles**: Super Admin, Admin, Loan Officer  
- 🔐 **JWT Authentication** for secure access to protected resources  
- 👤 **Customer Management**: Create and manage customer profiles  
- 💳 **Loan Assignment**: Loan Officers can assign loans to customers  
- 📅 **Repayment Schedule**: Automatically generated upon loan creation  
- 📊 **Analytics Dashboard**: Super Admin-only access to business insights like:
  - Loan Officer Portfolio
  - Portfolio performance

---

## 🛠 Tech Stack

| Layer           | Technology         |
|----------------|--------------------|
| Backend         | Java, Spring Boot  |
| Database        | PostgreSQL         |
| Authentication  | JWT                |
| ORM             | Spring Data JPA    |
| Build Tool      | Maven              |

---

## 📁 Project Structure (by Feature)

```bash
src/main/java/com/LoanManagementApp/LoansApp/
│
├── Controllers         # REST Controllers for different resources
├── Enums               # Enum definitions (LoanStatus, etc.)
├── Exceptions          # Centralized exception handling
├── Models              # Entity classes (User, Loan, Customer, etc.)
├── Repositories        # Spring Data JPA Repositories
├── Requests            # Request DTOs for incoming API payloads
├── Responses           # Response DTOs for API outputs
├── security            # JWT security and filter configuration
├── Services            # Business logic and service layer
└── LoansAppApplication.java
```

---

## 🔑 Sample Credentials

Use the following credentials to log in as an admin during testing:

```json
{
  "username": "Your username",
  "password": "your password",
  "email": "your email",
  "role": "'ADMIN' / 'SUPER_ADMIN' / 'LOAN_OFFICER'" 
}
```

---

## ⚙️ Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/loans-app.git
cd loans-app
```

### 2. Setup PostgreSQL

Ensure a PostgreSQL instance is running and configure `application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/loansystem
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run the App

Before the first run, **uncomment the role creator block** inside `LoansAppApplication.java` to seed the roles (`ROLE_ADMIN`, `ROLE_SUPERADMIN`, `ROLE_LOAN_OFFICER`).

After successful seeding, **comment the block again** to avoid duplicate role creation.

Then:

```bash
./mvnw spring-boot:run
```

---
## 📬 Postman Collection

[Postman Collection](https://www.postman.com/tesh254/workspace/learning-springboot/collection/30157107-0e774764-892c-4171-83d9-eeac25888946?action=share&creator=30157107)

You can import the provided Postman collection for quick API testing. It includes:
- Auth flow (login/register)
- Customer and loan endpoints
- Payment handling
- Superadmin analytics

---

## 👨🏽‍💻 Author

**Rhema Mutethia** – • [GitHub](https://github.com/rhema254)
