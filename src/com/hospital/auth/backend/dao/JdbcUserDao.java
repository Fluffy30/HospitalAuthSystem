package com.hospital.auth.backend.dao;

import com.hospital.auth.backend.db.DatabaseConnectionManager;
import com.hospital.auth.model.Role;
import com.hospital.auth.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JdbcUserDao.java
 * ---------------------------------------------------------------------
 * Real-database implementation of {@link UserDao}, built on plain JDBC
 * so it works with ANY relational database (MySQL, PostgreSQL, SQL
 * Server, Oracle, SQLite, etc.) - you only need to change the driver
 * and connection URL in {@link com.hospital.auth.backend.config.DatabaseConfig}.
 *
 * >>> THIS IS A SKELETON <<<
 * The SQL statements below assume a `users` table matching the schema
 * in `resources/sql/schema.sql`. Every method is fully wired with
 * try-with-resources JDBC boilerplate already written for you - the
 * only thing left to do is:
 *
 *   1. Create the `users` table in your chosen database (run
 *      resources/sql/schema.sql, or adapt it to your DB's SQL dialect).
 *   2. Configure DatabaseConfig (driver, URL, username, password).
 *   3. Set DatabaseConfig.USE_DATABASE = true.
 *   4. Point DaoFactory at this class (already done for you - see
 *      DaoFactory.getUserDao()).
 *
 * Each method below is marked with a `>>> TODO <<<` comment only where
 * you might want to customise behaviour (e.g. column names, table name,
 * or swapping in a stored procedure) - otherwise the code is ready to
 * run as-is against a standard SQL schema.
 * ---------------------------------------------------------------------
 */
public class JdbcUserDao implements UserDao {

    // ------------------------------------------------------------------
    // >>> TODO: adjust table/column names here if your schema differs <<<
    // ------------------------------------------------------------------
    private static final String TABLE = "users";

    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE +
                    " (full_name, username, email, phone_number, hashed_password, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_USERNAME =
            "SELECT full_name, username, email, phone_number, hashed_password, role " +
                    "FROM " + TABLE + " WHERE LOWER(username) = LOWER(?)";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT full_name, username, email, phone_number, hashed_password, role " +
                    "FROM " + TABLE + " WHERE LOWER(email) = LOWER(?)";

    private static final String SQL_UPDATE =
            "UPDATE " + TABLE +
                    " SET full_name = ?, email = ?, phone_number = ?, hashed_password = ?, role = ? " +
                    "WHERE LOWER(username) = LOWER(?)";

    private static final String SQL_EXISTS_BY_USERNAME =
            "SELECT 1 FROM " + TABLE + " WHERE LOWER(username) = LOWER(?)";

    private static final String SQL_EXISTS_BY_EMAIL =
            "SELECT 1 FROM " + TABLE + " WHERE LOWER(email) = LOWER(?)";

    private static final String SQL_FIND_ALL =
            "SELECT full_name, username, email, phone_number, hashed_password, role FROM " + TABLE;

    // ==================================================================
    // INSERT
    // ==================================================================
    @Override
    public void insert(User user) throws DataAccessException {
        // ------------------------------------------------------------
        // >>> TODO: this method is ready to run as-is once your `users`
        // table exists and DatabaseConfig is configured. <<<
        // ------------------------------------------------------------
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getHashedPassword());
            stmt.setString(6, user.getRole().name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert user: " + user.getUsername(), e);
        }
    }

    // ==================================================================
    // FIND BY USERNAME
    // ==================================================================
    @Override
    public Optional<User> findByUsername(String username) throws DataAccessException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_USERNAME)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to find user by username: " + username, e);
        }
    }

    // ==================================================================
    // FIND BY EMAIL
    // ==================================================================
    @Override
    public Optional<User> findByEmail(String email) throws DataAccessException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to find user by email: " + email, e);
        }
    }

    // ==================================================================
    // UPDATE (used by "Forgot Password" reset, and future profile edits)
    // ==================================================================
    @Override
    public void update(User user) throws DataAccessException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getHashedPassword());
            stmt.setString(5, user.getRole().name());
            stmt.setString(6, user.getUsername());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("No user found to update: " + user.getUsername());
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to update user: " + user.getUsername(), e);
        }
    }

    // ==================================================================
    // EXISTS CHECKS (used during Sign Up to reject duplicates)
    // ==================================================================
    @Override
    public boolean existsByUsername(String username) throws DataAccessException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_BY_USERNAME)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to check username existence: " + username, e);
        }
    }

    @Override
    public boolean existsByEmail(String email) throws DataAccessException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EXISTS_BY_EMAIL)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to check email existence: " + email, e);
        }
    }

    // ==================================================================
    // FIND ALL (for future admin "Manage Staff Accounts" screen)
    // ==================================================================
    @Override
    public List<User> findAll() throws DataAccessException {
        List<User> results = new ArrayList<>();

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to load all users", e);
        }
        return results;
    }

    // ==================================================================
    // Helper: maps one JDBC ResultSet row into a User object.
    // ==================================================================
    private User mapRowToUser(ResultSet rs) throws SQLException {
        // ------------------------------------------------------------
        // >>> TODO: if you rename columns in your schema, update the
        // rs.getString(...) keys below to match. <<<
        // ------------------------------------------------------------
        return new User(
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("hashed_password"),
                Role.valueOf(rs.getString("role"))
        );
    }
}
