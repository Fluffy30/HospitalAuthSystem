package com.hospital.auth.service;

import com.hospital.auth.backend.dao.DaoFactory;
import com.hospital.auth.backend.dao.DataAccessException;
import com.hospital.auth.backend.dao.UserDao;
import com.hospital.auth.model.Role;
import com.hospital.auth.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * AuthService.java
 * ---------------------------------------------------------------------
 * Business-logic layer sitting between the Swing UI and persistence.
 * It exposes the operations the UI needs:
 *
 *   - register(...)         -> Sign Up
 *   - authenticate(...)     -> Sign In
 *   - resetPassword(...)    -> Forgot Password
 *   - findByUsernameOrEmail -> lookups shared by several forms
 *
 * BACKEND WIRING
 * ---------------------------------------------------------------------
 * AuthService no longer stores users itself. Instead it delegates all
 * reads/writes to a {@link UserDao} obtained from
 * {@link com.hospital.auth.backend.dao.DaoFactory}. This means:
 *
 *   - Right now (default): DaoFactory hands back an InMemoryUserDao,
 *     so the whole app runs immediately with zero database setup.
 *   - Once you plug in a real database: implement the SQL in
 *     JdbcUserDao (skeleton already provided), flip
 *     DatabaseConfig.USE_DATABASE to true, and AuthService - and every
 *     UI panel that calls it - keeps working completely unchanged.
 *
 * See package com.hospital.auth.backend for every piece of the
 * persistence layer (config, connection manager, DAO interface/impls).
 * ---------------------------------------------------------------------
 */
public class AuthService {

    /** Single shared instance (classic lazy-initialised singleton). */
    private static AuthService instance;

    /** The active persistence implementation, chosen by DaoFactory. */
    private final UserDao userDao;

    /**
     * Private constructor. Obtains the DAO from DaoFactory (rather than
     * "new InMemoryUserDao()" directly) so swapping in a real database
     * later requires no change here at all.
     */
    private AuthService() {
        this.userDao = DaoFactory.getUserDao();
        seedDemoAccountIfNeeded();
    }

    /** @return the single shared AuthService instance. */
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Seeds one ready-to-use administrator account (if it doesn't
     * already exist) so the login screen can be demonstrated
     * immediately without first registering a user.
     *      Username: admin   Password: Admin123
     *
     * NOTE: once you switch to a real database, you may prefer to seed
     * this account via resources/sql/schema.sql instead (see the
     * commented INSERT statement there) and remove this method.
     */
    private void seedDemoAccountIfNeeded() {
        try {
            if (userDao.existsByUsername("admin")) {
                return;
            }
            User demoAdmin = new User(
                    "System Administrator",
                    "admin",
                    "admin@cityhospital.org",
                    "+10000000000",
                    hashPassword("Admin123".toCharArray()),
                    Role.ADMINISTRATOR
            );
            userDao.insert(demoAdmin);
        } catch (DataAccessException e) {
            // Non-fatal: if seeding fails (e.g. DB not reachable yet),
            // the app still starts - Sign Up remains available.
            System.err.println("Warning: could not seed demo account - " + e.getMessage());
        }
    }

    /**
     * Registers a brand new account.
     *
     * @return null on success, or a human-readable error message if the
     *         username/e-mail is already taken.
     */
    public String register(User newUser) {
        try {
            if (userDao.existsByUsername(newUser.getUsername())) {
                return "That username is already taken. Please choose another.";
            }
            if (userDao.existsByEmail(newUser.getEmail())) {
                return "An account with that e-mail address already exists.";
            }
            userDao.insert(newUser);
            return null; // null == "no error"
        } catch (DataAccessException e) {
            return "We couldn't create your account right now. Please try again later.";
        }
    }

    /**
     * Attempts to authenticate a user for Sign In.
     *
     * @param usernameOrEmail the text entered in the "Username or Email" field
     * @param password        the plaintext password entered by the user
     * @return the matching User on success, or null if credentials are invalid
     */
    public User authenticate(String usernameOrEmail, char[] password) {
        User user = findByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            return null;
        }
        String attemptHash = hashPassword(password);
        return attemptHash.equals(user.getHashedPassword()) ? user : null;
    }

    /**
     * Looks up a user by either their username OR their e-mail address,
     * since many hospital users find it more natural to sign in with
     * their e-mail.
     */
    public User findByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail == null) return null;
        String needle = usernameOrEmail.trim();

        try {
            Optional<User> byUsername = userDao.findByUsername(needle);
            if (byUsername.isPresent()) {
                return byUsername.get();
            }
            return userDao.findByEmail(needle).orElse(null);
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * Resets a password after the "Forgot Password" flow has verified
     * that the supplied username and e-mail belong to the same account.
     *
     * @return null on success, or an error message on failure.
     */
    public String resetPassword(String username, String email, char[] newPassword) {
        try {
            Optional<User> found = userDao.findByUsername(username.trim());
            if (found.isEmpty() || !found.get().getEmail().equalsIgnoreCase(email.trim())) {
                return "We couldn't find an account matching that username and e-mail.";
            }
            User user = found.get();
            user.setHashedPassword(hashPassword(newPassword));
            userDao.update(user);
            return null;
        } catch (DataAccessException e) {
            return "We couldn't reset your password right now. Please try again later.";
        }
    }

    /**
     * Hashes a password with SHA-256.
     *
     * IMPORTANT: SHA-256 without a per-user salt is used here purely to
     * keep this self-contained demo simple. A real system must use a
     * dedicated password-hashing algorithm such as BCrypt, SCrypt, or
     * Argon2 with a unique salt per user.
     */
    public String hashPassword(char[] password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = new String(password).getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to exist on every standard JVM, so this
            // branch should never actually execute.
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
