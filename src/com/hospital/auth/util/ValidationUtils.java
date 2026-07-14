package com.hospital.auth.util;

import java.util.regex.Pattern;

/**
 * ValidationUtils.java
 * ---------------------------------------------------------------------
 * Centralised, reusable static validation helpers used by every form in
 * the application (Sign In, Sign Up, Forgot Password).
 *
 * Keeping validation logic in one utility class (rather than duplicating
 * regex/length checks inside every panel) means:
 *   1. Rules stay consistent across the whole application.
 *   2. Rules can be changed/tightened in a single place.
 *   3. Panels stay focused on UI wiring, not business rules.
 * ---------------------------------------------------------------------
 */
public final class ValidationUtils {

    // Standard, widely-used email pattern (covers the vast majority of
    // real-world addresses without being overly strict).
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    // Accepts optional leading '+', then 7-15 digits (E.164-ish range).
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{7,15}$");

    // Username: letters, numbers, underscores, 4-20 characters.
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    /** Prevent instantiation - this class only exposes static helpers. */
    private ValidationUtils() {
    }

    /**
     * @return true if the given text is non-null and contains at least
     *         one non-whitespace character.
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validates an e-mail address against a standard pattern.
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a phone number: digits only, optional leading '+',
     * length between 7 and 15 (covers most international formats).
     */
    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates a username: 4-20 characters, alphanumeric/underscore only.
     */
    public static boolean isValidUsername(String username) {
        return isNotEmpty(username) && USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * A "strong enough" password for this demo application requires:
     *   - at least 8 characters
     *   - at least one letter
     *   - at least one digit
     */
    public static boolean isStrongPassword(char[] password) {
        if (password == null || password.length < 8) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    /**
     * Compares two char[] passwords for equality (used to confirm that
     * "Password" and "Confirm Password" fields match). We use char[]
     * rather than String throughout the app so that password data can be
     * cleared from memory immediately after use.
     */
    public static boolean passwordsMatch(char[] password, char[] confirmPassword) {
        if (password == null || confirmPassword == null) return false;
        return java.util.Arrays.equals(password, confirmPassword);
    }
}
