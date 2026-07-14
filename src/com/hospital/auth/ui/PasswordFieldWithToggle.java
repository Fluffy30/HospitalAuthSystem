package com.hospital.auth.ui;

import javax.swing.*;
import java.awt.*;

/**
 * PasswordFieldWithToggle.java
 * ---------------------------------------------------------------------
 * A reusable composite component: a JPasswordField paired with a small
 * "show / hide" toggle button, laid out with BorderLayout so the toggle
 * always hugs the right edge and the field stretches to fill the rest
 * of the available width (fully responsive - no fixed pixel widths).
 *
 * This single component is reused for the "Password", "Confirm
 * Password" and "New Password" fields everywhere in the application,
 * avoiding duplicated show/hide wiring logic.
 * ---------------------------------------------------------------------
 */
public class PasswordFieldWithToggle extends JPanel {

    private final JPasswordField passwordField;
    private final JToggleButton toggleButton;

    /** The character used to mask the password when hidden. */
    private static final char MASK_CHAR = '\u2022'; // bullet •

    public PasswordFieldWithToggle() {
        super(new BorderLayout(0, 0));
        setOpaque(false);

        // --- The actual password input ---------------------------------
        passwordField = new JPasswordField();
        passwordField.setEchoChar(MASK_CHAR);
        UITheme.styleTextField(passwordField);
        // Reserve a little extra space on the right so text never runs
        // underneath the toggle button.
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                passwordField.getBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 4)
        ));

        // --- The show/hide toggle ---------------------------------------
        toggleButton = new JToggleButton("Show");
        toggleButton.setFont(UITheme.FONT_SMALL);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setForeground(UITheme.PRIMARY);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));

        // Flip the echo character on/off each time the toggle is clicked.
        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                passwordField.setEchoChar((char) 0); // 0 => show plain text
                toggleButton.setText("Hide");
            } else {
                passwordField.setEchoChar(MASK_CHAR);
                toggleButton.setText("Show");
            }
        });

        add(passwordField, BorderLayout.CENTER);
        add(toggleButton, BorderLayout.EAST);
    }

    /** @return the raw characters currently entered (caller should clear this array after use). */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /** @return true if the field currently contains no characters. */
    public boolean isEmpty() {
        return passwordField.getPassword().length == 0;
    }

    /** Clears the field, e.g. after a successful submission. */
    public void clear() {
        passwordField.setText("");
    }

    /** Exposes the underlying field in case a caller needs to attach extra listeners. */
    public JPasswordField getField() {
        return passwordField;
    }
}
