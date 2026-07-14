package com.hospital.auth.ui.dashboard;

import com.hospital.auth.model.User;
import com.hospital.auth.ui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * PatientDashboard.java
 * ---------------------------------------------------------------------
 * The screen a user is redirected to after signing in with the PATIENT
 * role. This is a lightweight placeholder demonstrating role-based
 * redirection - in a real hospital system this would show appointment
 * history, prescriptions, lab results, billing, etc.
 * ---------------------------------------------------------------------
 */
public class PatientDashboard extends BaseDashboard {

    public PatientDashboard(User user) {
        super(user, "City Hospital \u2013 Patient Portal");
    }

    @Override
    protected JComponent buildContent() {
        JPanel content = new JPanel(new GridLayout(2, 2, 20, 20));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        content.setBackground(UITheme.BACKGROUND);

        content.add(createInfoCard("Upcoming Appointments", "You have no upcoming appointments scheduled."));
        content.add(createInfoCard("Prescriptions", "No active prescriptions on file."));
        content.add(createInfoCard("Lab Results", "No new lab results to review."));
        content.add(createInfoCard("Billing", "Your account balance is $0.00."));

        return content;
    }

    /** Small reusable "info card" widget used across the placeholder dashboard tiles. */
    private JPanel createInfoCard(String title, String body) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(14, new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_TITLE.deriveFont(16f));
        titleLabel.setForeground(UITheme.PRIMARY);

        JLabel bodyLabel = new JLabel("<html><body style='width: 220px'>" + body + "</body></html>");
        bodyLabel.setFont(UITheme.FONT_SUBTITLE);
        bodyLabel.setForeground(UITheme.TEXT_SECONDARY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(bodyLabel, BorderLayout.CENTER);
        return card;
    }
}
