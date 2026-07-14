package com.hospital.auth.ui;

import com.hospital.auth.model.Role;
import com.hospital.auth.model.User;
import com.hospital.auth.service.AuthService;
import com.hospital.auth.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * SignUpPanel.java
 * ---------------------------------------------------------------------
 * The "Sign Up" (registration) form. Collects every field required by
 * the spec:
 *      Full Name, Username, Email, Phone Number,
 *      Password, Confirm Password, Role selection
 *
 * All validation is delegated to {@link ValidationUtils} so the rules
 * stay consistent with the rest of the application, and the actual
 * account creation is delegated to {@link AuthService#register}.
 *
 * Like {@link SignInPanel}, this form is laid out with GridBagLayout
 * inside a centred rounded "card", so it reflows responsively when the
 * window is resized instead of relying on fixed coordinates.
 * ---------------------------------------------------------------------
 */
public class SignUpPanel extends JPanel {

    private final LoginFrame parentFrame;
    private final AuthService authService = AuthService.getInstance();

    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private PasswordFieldWithToggle passwordField;
    private PasswordFieldWithToggle confirmPasswordField;
    private JComboBox<Role> roleComboBox;
    private JLabel messageLabel;

    public SignUpPanel(LoginFrame parentFrame) {
        this.parentFrame = parentFrame;
        setOpaque(false);
        setLayout(new GridBagLayout());
        buildUI();
    }

    /**
     * Builds the registration form. Two text fields are placed
     * side-by-side per row where it makes sense (e.g. Username/Email)
     * using a nested GridBagLayout so the form uses horizontal space
     * efficiently on wide windows while still stacking cleanly if the
     * window becomes narrow (GridBagLayout recalculates preferred sizes
     * automatically on every resize/validate pass).
     */
    private void buildUI() {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(16, new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(32, 44, 32, 44));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;
        int row = 0;

        gbc.gridy = row++;
        card.add(UITheme.createTitleLabel("Create Your Account"), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 0, 14, 0);
        card.add(UITheme.createSubtitleLabel("Register as a patient, doctor, or administrator"), gbc);
        gbc.insets = new Insets(5, 0, 5, 0);

        // --- Full Name -----------------------------------------------------
        gbc.gridy = row++;
        card.add(UITheme.createFieldLabel("Full Name"), gbc);
        fullNameField = new JTextField();
        UITheme.styleTextField(fullNameField);
        gbc.gridy = row++;
        card.add(fullNameField, gbc);

        // --- Username --------------------------------------------------
        gbc.gridy = row++;
        card.add(UITheme.createFieldLabel("Username"), gbc);
        usernameField = new JTextField();
        UITheme.styleTextField(usernameField);
        gbc.gridy = row++;
        card.add(usernameField, gbc);

        // --- Email -----------------------------------------------------
        gbc.gridy = row++;
        card.add(UITheme.createFieldLabel("Email Address"), gbc);
        emailField = new JTextField();
        UITheme.styleTextField(emailField);
        gbc.gridy = row++;
        card.add(emailField, gbc);

        // --- Phone number ------------------------------------------------
        gbc.gridy = row++;
        card.add(UITheme.createFieldLabel("Phone Number"), gbc);
        phoneField = new JTextField();
        UITheme.styleTextField(phoneField);
        gbc.gridy = row++;
        card.add(phoneField, gbc);

        // --- Password / Confirm Password (share a row on wide windows) ----
        JPanel passwordRow = new JPanel(new GridLayout(1, 2, 14, 0));
        passwordRow.setOpaque(false);

        JPanel pwCol = new JPanel(new BorderLayout(0, 4));
        pwCol.setOpaque(false);
        pwCol.add(UITheme.createFieldLabel("Password"), BorderLayout.NORTH);
        passwordField = new PasswordFieldWithToggle();
        pwCol.add(passwordField, BorderLayout.CENTER);

        JPanel confirmCol = new JPanel(new BorderLayout(0, 4));
        confirmCol.setOpaque(false);
        confirmCol.add(UITheme.createFieldLabel("Confirm Password"), BorderLayout.NORTH);
        confirmPasswordField = new PasswordFieldWithToggle();
        confirmCol.add(confirmPasswordField, BorderLayout.CENTER);

        passwordRow.add(pwCol);
        passwordRow.add(confirmCol);

        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 5, 0);
        card.add(passwordRow, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);

        // --- Role selection --------------------------------------------
        gbc.gridy = row++;
        card.add(UITheme.createFieldLabel("Register As"), gbc);
        roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setFont(UITheme.FONT_FIELD);
        roleComboBox.setBackground(Color.WHITE);
        gbc.gridy = row++;
        card.add(roleComboBox, gbc);

        // --- Inline message ----------------------------------------------
        messageLabel = UITheme.createMessageLabel();
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 4, 0);
        card.add(messageLabel, gbc);

        // --- Submit button -------------------------------------------------
        JButton signUpButton = UITheme.createPrimaryButton("Create Account");
        signUpButton.addActionListener(e -> attemptSignUp());
        gbc.gridy = row++;
        gbc.insets = new Insets(6, 0, 4, 0);
        card.add(signUpButton, gbc);

        // --- Link back to Sign In -------------------------------------------
        JPanel signInRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signInRow.setOpaque(false);
        JLabel prompt = new JLabel("Already have an account?");
        prompt.setFont(UITheme.FONT_LINK);
        prompt.setForeground(UITheme.TEXT_SECONDARY);
        JButton signInLink = UITheme.createLinkButton("Sign in");
        signInLink.addActionListener(e -> parentFrame.showCard(LoginFrame.CARD_SIGN_IN));
        signInRow.add(prompt);
        signInRow.add(signInLink);

        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 0, 0);
        card.add(signInRow, gbc);

        // --- Wrap in a scroll pane so short windows never clip the form ------
        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        card.setPreferredSize(new Dimension(460, card.getPreferredSize().height));

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Runs full validation across every field, then (if everything
     * checks out) builds a new {@link User} and registers it via
     * {@link AuthService}.
     */
    private void attemptSignUp() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();
        Role selectedRole = (Role) roleComboBox.getSelectedItem();

        // --- Required-field checks, in a sensible top-to-bottom order -------
        if (!ValidationUtils.isNotEmpty(fullName)) {
            showError("Please enter your full name.");
            return;
        }
        if (!ValidationUtils.isValidUsername(username)) {
            showError("Username must be 4-20 characters (letters, numbers, underscore).");
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            showError("Please enter a valid e-mail address.");
            return;
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            showError("Please enter a valid phone number (7-15 digits).");
            return;
        }
        if (!ValidationUtils.isStrongPassword(password)) {
            showError("Password must be 8+ characters and include a letter and a number.");
            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
            return;
        }
        if (!ValidationUtils.passwordsMatch(password, confirmPassword)) {
            showError("Passwords do not match.");
            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
            return;
        }

        // --- Build and register the new account -----------------------------
        String hashedPassword = authService.hashPassword(password);
        User newUser = new User(fullName, username, email, phone, hashedPassword, selectedRole);
        String errorMessage = authService.register(newUser);

        // Clear sensitive data from memory now that hashing is complete.
        Arrays.fill(password, '\0');
        Arrays.fill(confirmPassword, '\0');

        if (errorMessage != null) {
            showError(errorMessage);
            return;
        }

        UITheme.setMessage(messageLabel, "Account created successfully! You can now sign in.", false);
        clearForm();
    }

    /** Displays a red inline error message under the form. */
    private void showError(String text) {
        UITheme.setMessage(messageLabel, text, true);
    }

    /** Resets every field after a successful registration. */
    private void clearForm() {
        fullNameField.setText("");
        usernameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setSelectedIndex(0);
    }
}
