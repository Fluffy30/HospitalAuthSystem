package com.hospital.auth.model;

import java.util.Objects;

/**
 * User.java
 * ---------------------------------------------------------------------
 * Plain data model representing a single registered account inside the
 * Hospital Authentication System. An instance of this class is created
 * whenever a new account is registered (Sign Up) and is retrieved
 * whenever a user signs in or resets a password.
 *
 * NOTE ON PASSWORD STORAGE:
 * For demonstration purposes only, the password is stored as a SHA-256
 * hash (see AuthService#hashPassword). In a real production hospital
 * system you would NEVER keep users in memory - you would use a proper
 * database with salted password hashing (e.g. BCrypt/Argon2) and a
 * secure backend service.
 * ---------------------------------------------------------------------
 */
public class User {

    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private String hashedPassword;
    private Role role;

    /**
     * Full constructor used by AuthService when registering a new account.
     */
    public User(String fullName, String username, String email,
                String phoneNumber, String hashedPassword, Role role) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    // ----------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Two users are considered equal if they share the same username,
     * since the username is the unique key used throughout AuthService.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(username.toLowerCase(), user.username.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase());
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
