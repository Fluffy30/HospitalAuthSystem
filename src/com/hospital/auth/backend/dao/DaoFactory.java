package com.hospital.auth.backend.dao;

import com.hospital.auth.backend.config.DatabaseConfig;

/**
 * DaoFactory.java
 * ---------------------------------------------------------------------
 * Single point of control for which {@link UserDao} implementation the
 * rest of the application uses. AuthService asks this factory for a
 * UserDao instead of constructing one directly, so switching from the
 * in-memory store to a real database is a ONE-LINE config change
 * ({@link DatabaseConfig#USE_DATABASE}) rather than a code change
 * scattered across the app.
 * ---------------------------------------------------------------------
 */
public final class DaoFactory {

    /** Lazily-created singleton DAO instance shared by the whole app. */
    private static UserDao userDaoInstance;

    private DaoFactory() {
        // Static-only factory - never instantiated.
    }

    /**
     * @return the active {@link UserDao} implementation, chosen based on
     *         {@link DatabaseConfig#USE_DATABASE}:
     *         <ul>
     *             <li>false (default) -> {@link InMemoryUserDao}</li>
     *             <li>true             -> {@link JdbcUserDao} (your database)</li>
     *         </ul>
     */
    public static synchronized UserDao getUserDao() {
        if (userDaoInstance == null) {
            userDaoInstance = DatabaseConfig.USE_DATABASE
                    ? new JdbcUserDao()      // >>> your database, once configured <<<
                    : new InMemoryUserDao(); // safe default, zero setup required
        }
        return userDaoInstance;
    }
}
