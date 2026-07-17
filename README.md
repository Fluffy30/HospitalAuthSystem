# City Hospital Secure Portal — Java Swing Authentication System

A complete, single-window authentication system for a hospital application,
supporting **Patients**, **Doctors**, and **Administrators** with Sign In,
Sign Up, and Forgot Password flows.

## Features

- Single resizable `JFrame` (`LoginFrame`) switching between forms via `CardLayout` — no absolute positioning anywhere, layout is 100% `GridBagLayout` / `BorderLayout` / `FlowLayout`.
- **Sign In**: username-or-email + password, role selector, "Remember Me" (persisted with `java.util.prefs`), password show/hide toggle, inline validation errors.
- **Sign Up**: full name, username, email, phone number, password, confirm password, role selection — fully validated.
- **Forgot Password**: two-step identity verification → new password reset flow, all inside one panel.
- Role-based redirection to `PatientDashboard`, `DoctorDashboard`, or `AdminDashboard` after login.
- Clean hospital-themed visual design (teal/mint palette, rounded cards, soft borders) defined centrally in `UITheme`.
- SHA-256 password hashing via `AuthService` (see in-code notes on why a real system needs salted hashing like BCrypt/Argon2 instead).
- Every class, method, and non-trivial block is documented with comments.

## Project Structure

```
src/com/hospital/auth/
├── Main.java                     # Entry point
├── model/
│   ├── Role.java                 # PATIENT / DOCTOR / ADMINISTRATOR enum
│   └── User.java                 # Account data model
├── util/
│   └── ValidationUtils.java      # Shared field validation logic
├── service/
│   └── AuthService.java          # Business logic: register/authenticate/reset
├── backend/                       # <-- Persistence layer (this is where the "database" lives)
│   ├── config/
│   │   └── DatabaseConfig.java    # >>> Your DB connection settings go here <<<
│   ├── db/
│   │   └── DatabaseConnectionManager.java  # Opens JDBC connections
│   └── dao/
│       ├── UserDao.java           # Storage-agnostic contract used by AuthService
│       ├── InMemoryUserDao.java   # Default, zero-setup implementation
│       ├── JdbcUserDao.java       # >>> SQL skeleton ready for your database <<<
│       ├── DaoFactory.java        # Chooses which DAO implementation is active
│       └── DataAccessException.java
└── ui/
    ├── UITheme.java               # Colors, fonts, reusable widget factories
    ├── PasswordFieldWithToggle.java
    ├── LoginFrame.java            # Top-level window + CardLayout host
    ├── SignInPanel.java
    ├── SignUpPanel.java
    ├── ForgotPasswordPanel.java
    └── dashboard/
        ├── BaseDashboard.java     # Shared header/logout shell
        ├── PatientDashboard.java
        ├── DoctorDashboard.java
        └── AdminDashboard.java

resources/sql/
└── schema.sql                     # Reference `users` table schema for JdbcUserDao
```

## Connecting Your Own Database

The backend is built around the **DAO (Data Access Object) pattern**, so
`AuthService` never talks to storage directly — it only calls the
`UserDao` interface. Right now that interface is satisfied by
`InMemoryUserDao` (no setup needed), and a ready-to-fill `JdbcUserDao`
skeleton sits right next to it. To plug in your database:

1. **Pick a database** (MySQL, PostgreSQL, SQL Server, SQLite, Oracle, etc.) and add its JDBC driver to your classpath/build file. Commented examples for each are in `DatabaseConfig.java`.
2. **Create the table** by running `resources/sql/schema.sql` against your database (tweak column types if your DB's SQL dialect differs).
3. **Fill in `backend/config/DatabaseConfig.java`**: set `JDBC_DRIVER_CLASS`, `JDBC_URL`, `DB_USERNAME`, `DB_PASSWORD`.
4. **Flip the switch**: set `DatabaseConfig.USE_DATABASE = true`.

That's it — `DaoFactory` will now hand `AuthService` a `JdbcUserDao`
instead of the in-memory one, and every Sign Up / Sign In / Forgot
Password flow in the UI keeps working unchanged, now backed by your
real database. `JdbcUserDao` already contains working SQL for insert,
find-by-username, find-by-email, update, exists-checks, and find-all —
look for `>>> TODO <<<` comments if you need to adapt column/table names.

If you'd rather use an ORM (e.g. Hibernate/JPA) or a NoSQL store instead
of raw JDBC, just write a new class that `implements UserDao` and point
`DaoFactory.getUserDao()` at it — nothing else in the app needs to change.

## Demo Account

A demo administrator account is seeded automatically on startup:

```
Username: admin
Password: Admin123
Role:     Administrator
```

You can also register brand-new Patient/Doctor/Administrator accounts from the Sign Up screen.

## Running the App

### Option A — IntelliJ IDEA
1. Open the `HospitalAuthSystem` folder as a project (`File → Open`).
2. Mark `src` as the **Sources Root** if IntelliJ doesn't detect it automatically (right-click `src` → `Mark Directory as → Sources Root`).
3. Run `com.Main`.

### Option B — Visual Studio Code
1. Open the `HospitalAuthSystem` folder.
2. Install the "Extension Pack for Java" if you don't already have it.
3. Open `src/com/hospital/auth/Main.java` and click **Run**.

### Option C — Command Line
```bash
cd HospitalAuthSystem
javac -d out $(find src -name "*.java")
java -cp out com.Main
```

### Option D — Prebuilt JAR
If `HospitalAuthSystem.jar` is included alongside this project:
```bash
java -jar HospitalAuthSystem.jar
```

## Notes for Production Use

This project is a self-contained teaching/demo implementation:
- Accounts are stored **in memory only** (a `ConcurrentHashMap`) and are lost when the app closes.
- Password hashing uses plain SHA-256 without per-user salt, which is **not sufficient for production**. Replace `AuthService.hashPassword` with a proper library (e.g. BCrypt/Argon2) and back `AuthService` with a real database before deploying.
