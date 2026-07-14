package com.hospital.auth.ui;

import com.hospital.auth.service.AuthService;
import com.hospital.auth.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * ForgotPasswordPanel.java
 * ---------------------------------------------------------------------
 * Implements a simple two-step "Forgot Password" flow entirely within
 * a single card panel (no extra dialog windows):
 *
 *   STEP 1 - Identity verification: the user enters their username and
 *            the e-mail address on file. AuthService confirms these
 *            two pieces of data belong to the same account (standing
 *            in for a real "we e-mailed you a reset link" flow).
 *
 *   STEP 2 - New password entry: once verified, the same panel reveals
 *            "New Password" / "Confirm New Password" fields and lets
 *            the user finish the reset.
 *
 * A CardLayout-within-a-CardLayout (this panel's own internal
 * CardLayout) is used to switch between step 1 and step 2 without
 * needing a second top-level window.
 * ---------------------------------------------------------------------
 */
public class ForgotPasswordPanel extends JPanel {

    private final LoginFrame parentFrame;
    private final AuthService authService = AuthService.getInstance();

    // Internal CardLayout constants for this panel's two steps.
    private static final String STEP_VERIFY = "VERIFY";
    private static final String STEP_RESET = "RESET";

    private final CardLayout stepLayout = new CardLayout();
    private final JPanel stepContainer = new JPanel(stepLayout);

    // Step 1 fields
    private JTextField verifyUsernameField;
    private JTextField verifyEmailField;
    private JLabel verifyMessageLabel;

    // Step 2 fields
    private PasswordFieldWithToggle newPasswordField;
    private PasswordFieldWithToggle confirmNewPasswordField;
    private JLabel resetMessageLabel;

    /** Remembers which account passed verification, ready for the reset step. */
    private String verifiedUsername;
    private String verifiedEmail;

    public ForgotPasswordPanel(LoginFrame parentFrame) {
        this.parentFrame = parentFrame;
        setOpaque(false);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(16, new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));
        card.setPreferredSize(new Dimension(420, 460));

        stepContainer.setOpaque(false);
        stepContainer.add(buildVerifyStep(), STEP_VERIFY);
        stepContainer.add(buildResetStep(), STEP_RESET);
        card.add(stepContainer, BorderLayout.CENTER);

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.weightx = 1.0;
        outer.weighty = 1.0;
        add(card, outer);
    }

    /**
     * Builds Step 1: enter username + e-mail to verify account ownership.
     */
    private JPanel buildVerifyStep() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);
        int row = 0;

        gbc.gridy = row++;
        panel.add(UITheme.createTitleLabel("Reset Your Password"), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 0, 18, 0);
        panel.add(UITheme.createSubtitleLabel("Verify your identity to continue"), gbc);
        gbc.insets = new Insets(6, 0, 6, 0);

        gbc.gridy = row++;
        panel.add(UITheme.createFieldLabel("Username"), gbc);
        verifyUsernameField = new JTextField();
        UITheme.styleTextField(verifyUsernameField);
        gbc.gridy = row++;
        panel.add(verifyUsernameField, gbc);

        gbc.gridy = row++;
        panel.add(UITheme.createFieldLabel("Registered Email Address"), gbc);
        verifyEmailField = new JTextField();
        UITheme.styleTextField(verifyEmailField);
        gbc.gridy = row++;
        panel.add(verifyEmailField, gbc);

        verifyMessageLabel = UITheme.createMessageLabel();
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 4, 0);
        panel.add(verifyMessageLabel, gbc);

        JButton verifyButton = UITheme.createPrimaryButton("Verify Identity");
        verifyButton.addActionListener(e -> attemptVerify());
        gbc.gridy = row++;
        gbc.insets = new Insets(6, 0, 4, 0);
        panel.add(verifyButton, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(14, 0, 0, 0);
        panel.add(buildBackToSignInLink(), gbc);

        return panel;
    }

    /**
     * Builds Step 2: enter and confirm the new password, revealed only
     * after successful verification in Step 1.
     */
    private JPanel buildResetStep() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);
        int row = 0;

        gbc.gridy = row++;
        panel.add(UITheme.createTitleLabel("Choose a New Password"), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 0, 18, 0);
        panel.add(UITheme.createSubtitleLabel("Your identity has been verified"), gbc);
        gbc.insets = new Insets(6, 0, 6, 0);

        gbc.gridy = row++;
        panel.add(UITheme.createFieldLabel("New Password"), gbc);
        newPasswordField = new PasswordFieldWithToggle();
        gbc.gridy = row++;
        panel.add(newPasswordField, gbc);

        gbc.gridy = row++;
        panel.add(UITheme.createFieldLabel("Confirm New Password"), gbc);
        confirmNewPasswordField = new PasswordFieldWithToggle();
        gbc.gridy = row++;
        panel.add(confirmNewPasswordField, gbc);

        resetMessageLabel = UITheme.createMessageLabel();
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 4, 0);
        panel.add(resetMessageLabel, gbc);

        JButton resetButton = UITheme.createPrimaryButton("Reset Password");
        resetButton.addActionListener(e -> attemptReset());
        gbc.gridy = row++;
        gbc.insets = new Insets(6, 0, 4, 0);
        panel.add(resetButton, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(14, 0, 0, 0);
        panel.add(buildBackToSignInLink(), gbc);

        return panel;
    }

    /** Small reusable "Back to Sign In" row used on both steps. */
    private JPanel buildBackToSignInLink() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        row.setOpaque(false);
        JButton backLink = UITheme.createLinkButton("\u2190 Back to Sign In");
        backLink.addActionListener(e -> {
            resetInternalState();
            parentFrame.showCard(LoginFrame.CARD_SIGN_IN);
        });
        row.add(backLink);
        return row;
    }

    /**
     * Step 1 handler: confirms the username + e-mail combination exists,
     * then advances to Step 2 if so.
     */
    private void attemptVerify() {
        String username = verifyUsernameField.getText().trim();
        String email = verifyEmailField.getText().trim();

        if (!ValidationUtils.isNotEmpty(username) || !ValidationUtils.isValidEmail(email)) {
            UITheme.setMessage(verifyMessageLabel, "Please enter a valid username and e-mail.", true);
            return;
        }

        boolean matches = authService.findByUsernameOrEmail(username) != null
                && authService.findByUsernameOrEmail(username).getEmail().equalsIgnoreCase(email);

        if (!matches) {
            UITheme.setMessage(verifyMessageLabel,
                    "No account matches that username and e-mail combination.", true);
            return;
        }

        this.verifiedUsername = username;
        this.verifiedEmail = email;
        UITheme.setMessage(verifyMessageLabel, " ", false);
        stepLayout.show(stepContainer, STEP_RESET);
    }

    /**
     * Step 2 handler: validates the new password and asks AuthService to
     * apply it to the previously-verified account.
     */
    private void attemptReset() {
        char[] newPassword = newPasswordField.getPassword();
        char[] confirmPassword = confirmNewPasswordField.getPassword();

        if (!ValidationUtils.isStrongPassword(newPassword)) {
            UITheme.setMessage(resetMessageLabel,
                    "Password must be 8+ characters and include a letter and a number.", true);
            Arrays.fill(newPassword, '\0');
            Arrays.fill(confirmPassword, '\0');
            return;
        }
        if (!ValidationUtils.passwordsMatch(newPassword, confirmPassword)) {
            UITheme.setMessage(resetMessageLabel, "Passwords do not match.", true);
            Arrays.fill(newPassword, '\0');
            Arrays.fill(confirmPassword, '\0');
            return;
        }

        String error = authService.resetPassword(verifiedUsername, verifiedEmail, newPassword);
        Arrays.fill(newPassword, '\0');
        Arrays.fill(confirmPassword, '\0');

        if (error != null) {
            UITheme.setMessage(resetMessageLabel, error, true);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Your password has been reset successfully. Please sign in.",
                "Password Reset", JOptionPane.INFORMATION_MESSAGE);

        resetInternalState();
        parentFrame.showCard(LoginFrame.CARD_SIGN_IN);
    }

    /** Clears all fields and returns this panel to Step 1 for next use. */
    private void resetInternalState() {
        verifyUsernameField.setText("");
        verifyEmailField.setText("");
        newPasswordField.clear();
        confirmNewPasswordField.clear();
        UITheme.setMessage(verifyMessageLabel, " ", false);
        UITheme.setMessage(resetMessageLabel, " ", false);
        verifiedUsername = null;
        verifiedEmail = null;
        stepLayout.show(stepContainer, STEP_VERIFY);
    }
}
