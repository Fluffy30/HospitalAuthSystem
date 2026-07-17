package com.hospital.auth.backend.dao;

import com.hospital.auth.model.User;

import java.util.List;
import java.util.Optional;

/**
 * UserDao.java
 * ---------------------------------------------------------------------
 * "Data Access Object" contract for everything the application needs
 * to do with user accounts in persistent storage.
 *
 * WHY THIS INTERFACE EXISTS
 * ---------------------------------------------------------------------
 * AuthService (the business-logic layer) talks ONLY to this interface -
 * it never knows or cares whether accounts live in a HashMap, a MySQL
 * table, a PostgreSQL table, or a NoSQL document store. That means:
 *
 *   - Today: {@link InMemoryUserDao} satisfies this interface with zero
 *     setup, so the whole Swing app runs immediately.
 *   - Later: implement {@link JdbcUserDao} (skeleton provided) against
 *     your database of choice, flip one config flag in
 *     {@link com.hospital.auth.backend.config.DatabaseConfig}, and the
 *     entire UI + AuthService layer keeps working unchanged.
 *
 * This is the standard DAO / Repository pattern used to decouple
 * business logic from storage technology.
 * ---------------------------------------------------------------------
 */
public interface UserDao {

    /**
     * Persists a brand-new user account.
     *
     * @param user the fully-populated User to insert (password must
     *             already be hashed by the caller - the DAO never
     *             hashes or validates, it only stores).
     * @throws DataAccessException if the insert fails (e.g. duplicate
     *         username/email, or a connectivity problem with the DB).
     */
    void insert(User user) throws DataAccessException;

    /**
     * Looks up a single user by their unique username (case-insensitive).
     *
     * @return an Optional containing the User if found, or empty otherwise.
     */
    Optional<User> findByUsername(String username) throws DataAccessException;

    /**
     * Looks up a single user by their e-mail address (case-insensitive).
     *
     * @return an Optional containing the User if found, or empty otherwise.
     */
    Optional<User> findByEmail(String email) throws DataAccessException;

    /**
     * Persists changes to an existing user (e.g. after a password reset).
     * Implementations should match the existing row by username.
     */
    void update(User user) throws DataAccessException;

    /**
     * @return true if a user with this username already exists.
     */
    boolean existsByUsername(String username) throws DataAccessException;

    /**
     * @return true if a user with this e-mail already exists.
     */
    boolean existsByEmail(String email) throws DataAccessException;

    /**
     * @return every registered user. Intended for admin-facing screens
     *         (e.g. AdminDashboard's future "Manage Staff Accounts").
     */
    List<User> findAll() throws DataAccessException;
}
