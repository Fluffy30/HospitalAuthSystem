package com.hospital.auth.model;

/**
 * Role.java
 * ---------------------------------------------------------------------
 * Enumeration of the three account types supported by the Hospital
 * Authentication System.
 *
 * Using an enum (instead of raw Strings) gives us compile-time safety
 * when checking a user's role, and lets us attach a human-friendly
 * display label to each constant for use in the UI (JComboBox items,
 * dashboard titles, etc.).
 * ---------------------------------------------------------------------
 */
public enum Role {

    PATIENT("Patient"),
    DOCTOR("Doctor"),
    ADMINISTRATOR("Administrator");

    /** Human-readable label shown in the UI (combo boxes, headers, etc.) */
    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * toString() is overridden so that this enum can be dropped directly
     * into a JComboBox and render nicely without any custom renderer.
     */
    @Override
    public String toString() {
        return displayName;
    }
}
