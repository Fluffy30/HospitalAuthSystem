package com.hospital.auth.ui;

import com.hospital.auth.model.Role;
import com.hospital.auth.model.User;
import com.hospital.auth.service.AuthService;
import com.hospital.auth.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.prefs.Preferences;

/**
 * SignInPanel.java
 * ---------------------------------------------------------------------
 * The "Sign In" form. Responsibilities:
 *
 *   - Collects username/e-mail + password + expected role.
 *   - Validates that no field is empty before calling AuthService.
 *   - Offers a "Remember Me" checkbox that persists the last used
 *     username between application runs (via java.util.prefs).
 *   - Shows inline error messages instead of blocking modal dialogs
 *     for a smoother, modern UX.
 *   - On success, hands the authenticated User back to LoginFrame for
 *     role-based redirection.
 *
 * Layout: GridBagLayout inside a centred "card" panel. GridBagLayout
 * (rather than absolute setBounds calls) lets every row stretch
 * horizontally and re-flow correctly when the window is resized.
 * ---------------------------------------------------------------------
 */
public class SignInPanel extends JPanel {

    private final LoginFrame parentFrame;
    private final AuthService authService = AuthService.getInstance();

    // Preferences node used to remember the last signed-in username.
    private final Preferences prefs = Preferences.userNodeForPackage(SignInPanel.class);
    private static final String PREF_REMEMBERED_USERNAME = "rememberedUsername";

    private JTextField usernameField;
    private PasswordFieldWithToggle passwordField;
    private JComboBox<Role> roleComboBox;
    private JCheckBox rememberMeCheckBox;
    private JLabel messageLabel;

    public SignInPanel(LoginFrame parentFrame) {
        this.parentFrame = parentFrame;
        setOpaque(false);
        setLayout(new GridBagLayout());
        buildUI();
        restoreRememberedUsername();
    }

    /**
     * Builds and lays out every component of the Sign In card using
     * GridBagLayout so the form remains centred and responsive at any
     * window size.
     */
    private void buildUI() {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(16, new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;
        int row = 0;

        // --- Titles -------------------------------------------------
        gbc.gridy = row++;
        card.add(UITheme.createTitleLabel("Welcome Back"), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(2, 0, 18, 0);
        card.add(UITheme.createSubtitleLabel("Sign in to access your hospital account"), gbc);

        // --- Username / Email ----------------------------------------
        gbc.insets = new Insets(6, 0, 4, 0);
        gbc.gridy = row++;
        card.add(UITheme.createFieldLabel("Username or Email"), gbc);

        usernameField = new JTextField();
        UITheme.styleTextField(usernameField);
        gbc.gridy = row++;
        card.add(usernameField, gbc);

        // --- Password --------------------------------------------------
        gbc.gridy = row++;
        card.add(UITheme.createFieldLabel("Password"), gbc);

        passwordField = new PasswordFieldWithToggle();
        gbc.gridy = row++;
        card.add(passwordField, gbc);

        // --- Role selection (used to confirm the portal the user expects) --
        gbc.gridy = row++;
        card.add(UITheme.createFieldLabel("Sign in as"), gbc);

        roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setFont(UITheme.FONT_FIELD);
        roleComboBox.setBackground(Color.WHITE);
        gbc.gridy = row++;
        card.add(roleComboBox, gbc);

        // --- Remember me + Forgot password row -------------------------
        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);

        rememberMeCheckBox = new JCheckBox("Remember me");
        rememberMeCheckBox.setOpaque(false);
        rememberMeCheckBox.setFont(UITheme.FONT_SMALL);
        rememberMeCheckBox.setForeground(UITheme.TEXT_SECONDARY);
        rememberMeCheckBox.setFocusPainted(false);

        JButton forgotPasswordLink = UITheme.createLinkButton("Forgot Password?");
        forgotPasswordLink.addActionListener(e -> parentFrame.showCard(LoginFrame.CARD_FORGOT_PASSWORD));

        optionsRow.add(rememberMeCheckBox, BorderLayout.WEST);
        optionsRow.add(forgotPasswordLink, BorderLayout.EAST);

        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 4, 0);
        card.add(optionsRow, gbc);

        // --- Inline error/success message -------------------------------
        messageLabel = UITheme.createMessageLabel();
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 4, 0);
        card.add(messageLabel, gbc);

        // --- Sign In button ----------------------------------------------
        JButton signInButton = UITheme.createPrimaryButton("Sign In");
        signInButton.addActionListener(e -> attemptSignIn());
        gbc.gridy = row++;
        gbc.insets = new Insets(8, 0, 4, 0);
        card.add(signInButton, gbc);

        // Pressing Enter in the password field also triggers sign-in.
        passwordField.getField().addActionListener(e -> attemptSignIn());

        // --- Link to Sign Up ------------------------------------------
        JPanel signUpRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signUpRow.setOpaque(false);
        JLabel prompt = new JLabel("Don't have an account?");
        prompt.setFont(UITheme.FONT_LINK);
        prompt.setForeground(UITheme.TEXT_SECONDARY);
        JButton createOneLink = UITheme.createLinkButton("Create one");
        createOneLink.addActionListener(e -> parentFrame.showCard(LoginFrame.CARD_SIGN_UP));
        signUpRow.add(prompt);
        signUpRow.add(createOneLink);

        gbc.gridy = row++;
        gbc.insets = new Insets(14, 0, 0, 0);
        card.add(signUpRow, gbc);

        // --- Centre the card inside this panel using an outer GridBagLayout --
        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.weightx = 1.0;
        outer.weighty = 1.0;
        outer.fill = GridBagConstraints.NONE;
        outer.anchor = GridBagConstraints.CENTER;
        card.setPreferredSize(new Dimension(420, card.getPreferredSize().height));
        add(card, outer);
    }

    /**
     * Validates the form and, if valid, asks AuthService to authenticate
     * the user. Displays an inline message on failure, or hands off to
     * LoginFrame for role-based redirection on success.
     */
    private void attemptSignIn() {
        String usernameOrEmail = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        // --- Basic "required field" validation ---------------------------
        if (!ValidationUtils.isNotEmpty(usernameOrEmail)) {
            UITheme.setMessage(messageLabel, "Please enter your username or e-mail.", true);
            return;
        }
        if (passwordField.isEmpty()) {
            UITheme.setMessage(messageLabel, "Please enter your password.", true);
            return;
        }

        // --- Delegate credential checking to AuthService -----------------
        User authenticatedUser = authService.authenticate(usernameOrEmail, password);
        // Clear the sensitive char[] from memory as soon as we're done with it.
        Arrays.fill(password, '\0');

        if (authenticatedUser == null) {
            UITheme.setMessage(messageLabel, "Invalid username/e-mail or password.", true);
            return;
        }

        // --- Confirm the selected role matches the account's actual role --
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        if (selectedRole != authenticatedUser.getRole()) {
            UITheme.setMessage(messageLabel,
                    "This account is registered as " + authenticatedUser.getRole().getDisplayName()
                            + ", not " + selectedRole.getDisplayName() + ".", true);
            return;
        }

        // --- Handle "Remember Me" -----------------------------------------
        if (rememberMeCheckBox.isSelected()) {
            prefs.put(PREF_REMEMBERED_USERNAME, authenticatedUser.getUsername());
        } else {
            prefs.remove(PREF_REMEMBERED_USERNAME);
        }

        UITheme.setMessage(messageLabel, "Success! Redirecting...", false);
        passwordField.clear();

        // --- Role-based redirection handled centrally by LoginFrame -------
        parentFrame.handleLoginSuccess(authenticatedUser);
    }

    /**
     * If a username was previously remembered (via java.util.prefs),
     * pre-fill the username field and tick the checkbox automatically.
     */
    private void restoreRememberedUsername() {
        String remembered = prefs.get(PREF_REMEMBERED_USERNAME, null);
        if (remembered != null) {
            usernameField.setText(remembered);
            rememberMeCheckBox.setSelected(true);
        }
    }
}
