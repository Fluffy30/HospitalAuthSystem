package com.hospital.auth.backend.config;

/**
 * DatabaseConfig.java
 * ---------------------------------------------------------------------
 * Centralised configuration for the persistence layer.
 *
 * HOW TO USE THIS FILE
 * ---------------------------------------------------------------------
 * 1. Set USE_DATABASE = true once you have a real database ready.
 *    (While it is false, the app keeps using the safe in-memory store,
 *    so the UI keeps working even before you've wired up a database.)
 * 2. Fill in JDBC_URL / DB_USERNAME / DB_PASSWORD / JDBC_DRIVER_CLASS
 *    for whichever database you choose (MySQL, PostgreSQL, SQL Server,
 *    Oracle, SQLite, etc.).
 * 3. Add the matching JDBC driver .jar to your project's classpath
 *    (or as a Maven/Gradle dependency) — see the commented examples
 *    below for the most common databases.
 *
 * In a real production deployment, NEVER hard-code credentials here.
 * Instead read them from environment variables, a `.env` file, or a
 * secrets manager. The constants below are placeholders for local
 * development only.
 * ---------------------------------------------------------------------
 */
public final class DatabaseConfig {

    private DatabaseConfig() {
        // Static-only configuration holder - never instantiated.
    }

    // ==================================================================
    // MASTER SWITCH
    // ==================================================================
    // false = use InMemoryUserDao (default, works immediately, no setup)
    // true  = use JdbcUserDao (requires everything below to be configured)
    public static final boolean USE_DATABASE = false;

    // ==================================================================
    // >>> TODO: PLUG IN YOUR DATABASE OF CHOICE HERE <<<
    // ==================================================================
    //
    // ---- Example: MySQL --------------------------------------------
    //   JDBC_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    //   JDBC_URL           = "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC";
    //   Maven dependency:   mysql:mysql-connector-j:8.x
    //
    // ---- Example: PostgreSQL ----------------------------------------
    //   JDBC_DRIVER_CLASS = "org.postgresql.Driver";
    //   JDBC_URL           = "jdbc:postgresql://localhost:5432/hospital_db";
    //   Maven dependency:   org.postgresql:postgresql:42.x
    //
    // ---- Example: Microsoft SQL Server -------------------------------
    //   JDBC_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //   JDBC_URL           = "jdbc:sqlserver://localhost:1433;databaseName=hospital_db";
    //   Maven dependency:   com.microsoft.sqlserver:mssql-jdbc:12.x
    //
    // ---- Example: SQLite (file-based, zero server setup) -------------
    //   JDBC_DRIVER_CLASS = "org.sqlite.JDBC";
    //   JDBC_URL           = "jdbc:sqlite:hospital.db";
    //   Maven dependency:   org.xerial:sqlite-jdbc:3.4x.x
    //
    // ---- Example: Oracle ----------------------------------------------
    //   JDBC_DRIVER_CLASS = "oracle.jdbc.OracleDriver";
    //   JDBC_URL           = "jdbc:oracle:thin:@localhost:1521:orcl";
    //   Maven dependency:   com.oracle.database.jdbc:ojdbc11
    //
    // Replace the placeholder values below with the ones matching your
    // chosen database, then flip USE_DATABASE to true above.

    /** Fully-qualified JDBC driver class name. TODO: set for your DB. */
    public static final String JDBC_DRIVER_CLASS = "TODO: e.g. com.mysql.cj.jdbc.Driver";

    /** JDBC connection URL. TODO: set for your DB. */
    public static final String JDBC_URL = "TODO: e.g. jdbc:mysql://localhost:3306/hospital_db";

    /** Database username. TODO: prefer an environment variable in production. */
    public static final String DB_USERNAME = "TODO: your_db_username";

    /** Database password. TODO: prefer an environment variable in production. */
    public static final String DB_PASSWORD = "TODO: your_db_password";

    // ==================================================================
    // Connection pool sizing (only relevant once you introduce a real
    // pooling library such as HikariCP - see DatabaseConnectionManager).
    // ==================================================================
    public static final int MAX_POOL_SIZE = 10;
    public static final int CONNECTION_TIMEOUT_MS = 5000;
}
