package com.hospital.auth.ui.dashboard;

import com.hospital.auth.model.User;
import com.hospital.auth.ui.LoginFrame;
import com.hospital.auth.ui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * BaseDashboard.java
 * ---------------------------------------------------------------------
 * Abstract base class shared by {@link PatientDashboard},
 * {@link DoctorDashboard} and {@link AdminDashboard}.
 *
 * It provides the common "shell" every dashboard needs:
 *      - A themed header bar showing a welcome message and the user's role
 *      - A "Log Out" button that returns to the LoginFrame
 *      - A responsive BorderLayout content area that subclasses fill in
 *        via the abstract {@link #buildContent()} method.
 *
 * Concrete subclasses only need to implement buildContent() with their
 * role-specific widgets - all window plumbing (title, sizing, logout)
 * is handled once, here, avoiding duplicated code across the three
 * dashboards.
 * ---------------------------------------------------------------------
 */
public abstract class BaseDashboard extends JFrame {

    protected final User currentUser;

    protected BaseDashboard(User user, String windowTitle) {
        super(windowTitle);
        this.currentUser = user;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BACKGROUND);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    /**
     * Builds the shared top banner: greeting + role badge + logout button.
     * Uses BorderLayout so it stretches responsively across any window
     * width instead of relying on fixed coordinates.
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel greeting = new JLabel("Welcome, " + currentUser.getFullName()
                + "  \u2022  " + currentUser.getRole().getDisplayName());
        greeting.setFont(UITheme.FONT_TITLE.deriveFont(18f));
        greeting.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(UITheme.FONT_BUTTON);
        logoutButton.setForeground(UITheme.PRIMARY);
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logOut());

        header.add(greeting, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);
        return header;
    }

    /**
     * Closes this dashboard and re-opens a fresh LoginFrame, returning
     * the user to the Sign In screen.
     */
    private void logOut() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    /**
     * Subclasses implement this to supply their role-specific main
     * content panel (patient records, doctor schedule, admin controls...).
     */
    protected abstract JComponent buildContent();
}
