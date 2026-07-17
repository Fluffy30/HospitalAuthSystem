package com.hospital.auth.backend.db;

import com.hospital.auth.backend.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnectionManager.java
 * ---------------------------------------------------------------------
 * Thin wrapper responsible for opening (and, later, pooling) JDBC
 * connections to whichever real database you plug in.
 *
 * This class is intentionally simple - a single `DriverManager`
 * connection per call - so it is easy to read and modify. For a
 * production hospital system, swap the body of {@link #getConnection()}
 * for a real connection pool (see the commented HikariCP example below);
 * opening a brand-new TCP connection per query does not scale.
 *
 * >>> This class is only used when DatabaseConfig.USE_DATABASE = true <<<
 * While that flag is false, JdbcUserDao (and this class) are never
 * touched, and the application safely uses InMemoryUserDao instead.
 * ---------------------------------------------------------------------
 */
public final class DatabaseConnectionManager {

    private static boolean driverLoaded = false;

    private DatabaseConnectionManager() {
        // Static-only helper - never instantiated.
    }

    /**
     * Opens and returns a new JDBC {@link Connection} using the settings
     * in {@link DatabaseConfig}.
     *
     * TODO (once you choose a database):
     *   1. Add the corresponding JDBC driver dependency to your build
     *      (pom.xml / build.gradle / manual classpath jar).
     *   2. Fill in DatabaseConfig.JDBC_DRIVER_CLASS / JDBC_URL /
     *      DB_USERNAME / DB_PASSWORD.
     *   3. Set DatabaseConfig.USE_DATABASE = true.
     *
     * Callers MUST close the returned Connection when finished
     * (a try-with-resources block is the simplest way) so connections
     * are not leaked.
     */
    public static Connection getConnection() throws SQLException {
        loadDriverIfNeeded();

        // ------------------------------------------------------------
        // >>> TODO: REPLACE WITH YOUR REAL CONNECTION LOGIC HERE <<<
        // ------------------------------------------------------------
        // Simple, unpooled connection (fine for small apps / demos):
        return DriverManager.getConnection(
                DatabaseConfig.JDBC_URL,
                DatabaseConfig.DB_USERNAME,
                DatabaseConfig.DB_PASSWORD
        );

        // ------------------------------------------------------------
        // OPTIONAL - PRODUCTION UPGRADE: connection pooling with HikariCP
        // ------------------------------------------------------------
        // For real hospital workloads, prefer a pooled DataSource so you
        // are not opening/closing a raw TCP connection on every request.
        // Add the HikariCP dependency (com.zaxxer:HikariCP), then
        // replace the body of this method with something like:
        //
        //   private static HikariDataSource dataSource;
        //
        //   static {
        //       HikariConfig config = new HikariConfig();
        //       config.setJdbcUrl(DatabaseConfig.JDBC_URL);
        //       config.setUsername(DatabaseConfig.DB_USERNAME);
        //       config.setPassword(DatabaseConfig.DB_PASSWORD);
        //       config.setMaximumPoolSize(DatabaseConfig.MAX_POOL_SIZE);
        //       config.setConnectionTimeout(DatabaseConfig.CONNECTION_TIMEOUT_MS);
        //       dataSource = new HikariDataSource(config);
        //   }
        //
        //   public static Connection getConnection() throws SQLException {
        //       return dataSource.getConnection();
        //   }
    }

    /**
     * Loads the JDBC driver class once, so `Class.forName(...)` isn't
     * repeated on every connection request. Most modern JDBC 4+ drivers
     * auto-register themselves and don't strictly need this, but it is
     * kept here for maximum compatibility with older drivers.
     */
    private static synchronized void loadDriverIfNeeded() throws SQLException {
        if (driverLoaded) {
            return;
        }
        try {
            Class.forName(DatabaseConfig.JDBC_DRIVER_CLASS);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "JDBC driver class not found: " + DatabaseConfig.JDBC_DRIVER_CLASS +
                            ". Did you add the driver dependency and set DatabaseConfig.JDBC_DRIVER_CLASS?", e);
        }
    }
}
