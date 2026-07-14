package com.hospital.auth.ui.dashboard;

import com.hospital.auth.model.User;
import com.hospital.auth.ui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * AdminDashboard.java
 * ---------------------------------------------------------------------
 * The screen a user is redirected to after signing in with the
 * ADMINISTRATOR role. Demonstrates role-based redirection with
 * administrative placeholder widgets (system stats, quick actions).
 * ---------------------------------------------------------------------
 */
public class AdminDashboard extends BaseDashboard {

    public AdminDashboard(User user) {
        super(user, "City Hospital \u2013 Administrator Portal");
    }

    @Override
    protected JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        content.setBackground(UITheme.BACKGROUND);

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        statsRow.setOpaque(false);
        statsRow.add(createStatCard("Registered Patients", "1,248"));
        statsRow.add(createStatCard("Active Doctors", "63"));
        statsRow.add(createStatCard("Pending Approvals", "4"));

        JLabel actionsHeading = new JLabel("Quick Actions");
        actionsHeading.setFont(UITheme.FONT_TITLE.deriveFont(16f));
        actionsHeading.setForeground(UITheme.TEXT_PRIMARY);
        actionsHeading.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionsRow.setOpaque(false);
        actionsRow.add(UITheme.createPrimaryButton("Manage Staff Accounts"));
        actionsRow.add(UITheme.createPrimaryButton("View System Logs"));
        actionsRow.add(UITheme.createPrimaryButton("Hospital Settings"));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(actionsHeading, BorderLayout.NORTH);
        bottom.add(actionsRow, BorderLayout.CENTER);

        content.add(statsRow, BorderLayout.NORTH);
        content.add(bottom, BorderLayout.CENTER);
        return content;
    }

    /** Small "stat card" tile used to summarise a single system metric. */
    private JPanel createStatCard(String label, String value) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(14, new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.FONT_LOGO.deriveFont(28f));
        valueLabel.setForeground(UITheme.PRIMARY);

        JLabel captionLabel = new JLabel(label);
        captionLabel.setFont(UITheme.FONT_SUBTITLE);
        captionLabel.setForeground(UITheme.TEXT_SECONDARY);

        card.add(valueLabel, BorderLayout.NORTH);
        card.add(captionLabel, BorderLayout.CENTER);
        return card;
    }
}
