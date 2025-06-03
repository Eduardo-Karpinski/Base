# 🧱 Base

**Base** is a clean and robust Spring Boot starter project designed to kickstart your Java backend applications with best practices, production-ready configurations, and a solid structure out of the box.

---

## ✅ Features

This project includes the essential building blocks most applications need, already configured and ready to use:

- 🔐 **JWT-based Authentication via HttpOnly Cookie**
- 🚦 **Rate Limiting**
- 🧰 **Layered Architecture**
- 🧼 **Global Exception Handling**
- 📬 **Thymeleaf Email Template Engine**
- 📁 **Structured Package Organization**
- 🛡️ **Spring Security Configuration**
- 🧵 **Async Thread Pool for Background Tasks**
- 🧊 **Fully Native Build Compatible with GraalVM**

---

## 🗂️ Package Structure

```
br.com.base
├── config          # Global configuration classes (security, startup, etc.)
├── controller      # REST endpoints
├── domain          # Core domain models/entities
├── exception       # Global exception handling
├── filter          # Custom request filters (JWT, logging, rate limiting)
├── mappers         # Entity to DTO converters
├── records         # DTOs (Request/Response models)
├── repositories    # Spring Data JPA interfaces
├── security        # Roles, JWT, user authentication
├── services        # Business logic layer
├── utils           # Utility classes
```

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Eduardo-Karpinski/Base.git
cd Base
```

### 2. Configure environment variables

Create a `.env` file or export the following system environment variables:

```env
# Database
DB_URL=jdbc:mysql://localhost:3306/base?createDatabaseIfNotExist=true
DB_USER=root
DB_PASS=root

# JWT
JWT_SECRET=your-jwt-secret
JWT_EXPIRATION=60

# Email
MAIL_USERNAME=your.email@gmail.com
MAIL_PASSWORD=your-app-password
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

---

## 🔐 Authentication

This project uses **JWT for stateless authentication**, stored in a secure `HttpOnly` cookie instead of an `Authorization` header.

### How it works

- Login via `POST /login` with email and password.
- If authentication succeeds:
  - A JWT is generated and returned in a `Set-Cookie` header.
  - The cookie is marked as `HttpOnly` for security.
- For subsequent requests, the JWT is automatically sent via cookie — **no header manipulation required**.

> This approach enhances security by preventing JavaScript access to the token (XSS-safe).

---

## ⚙️ Rate Limiting

Rate limiting is applied at the filter level and adjusts dynamically based on request origin:

- For unauthenticated users, rate limiting is enforced by **IP address**.
- For authenticated users, rate limiting is applied **per user (via JWT)**.

This ensures fair resource usage while protecting the system from abuse.

---

## 📬 Email Support

The project includes an **asynchronous email service** using `JavaMailSender` and **Thymeleaf** for dynamic HTML templates.

To enable it, configure your SMTP credentials via environment variables.

---

## 🛠️ Build Instructions

This project is fully ready for both JAR packaging and native image compilation, with support for unit tests during the build process.
🔹 Standard JAR Build

You can build the project as a runnable .jar using Maven:

clean package

This will:

    Run your test suite

    Package the project into target/base.jar

🔹 Native Image Build (GraalVM)

If you have GraalVM installed and configured, you can compile the project into a native executable:

clean native:compile -Pnative

This will:

    Run your tests

    Compile the application into a lightweight, fast-startup native binary (e.g., target/base or target/base.exe on Windows)

    ✅ All native builds were tested and generated successfully using Eclipse IDE with the GraalVM plugin and the official Spring Native tooling.

---

## 🎯 Project Goals

- Provide a **reusable and extensible project skeleton**
- Promote **best practices** and clean architecture
- Speed up development by removing boilerplate setup
- Help developers focus on delivering **business value**

---

## 🤝 Contributing

Contributions are welcome!  
Feel free to fork, open issues, and submit pull requests.

---

## 🧠 Author

Made with 💻 & ☕ by [Eduardo Karpinski](https://github.com/Eduardo-Karpinski)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
