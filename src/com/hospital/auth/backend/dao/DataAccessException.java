package com.hospital.auth.backend.dao;

/**
 * DataAccessException.java
 * ---------------------------------------------------------------------
 * Unchecked exception wrapping any failure that occurs while reading or
 * writing user data (e.g. a SQLException from the JDBC layer, or a
 * constraint violation). Wrapping storage-specific exceptions
 * (SQLException, etc.) in this single application-level exception keeps
 * the rest of the codebase (AuthService, the UI panels) from needing to
 * know about `java.sql.*` at all - they only ever catch
 * DataAccessException.
 * ---------------------------------------------------------------------
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
