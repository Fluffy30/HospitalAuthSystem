package com.hospital.auth.ui.dashboard;

import com.hospital.auth.model.User;
import com.hospital.auth.ui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * DoctorDashboard.java
 * ---------------------------------------------------------------------
 * The screen a user is redirected to after signing in with the DOCTOR
 * role. Demonstrates role-based redirection with doctor-relevant
 * placeholder widgets (today's schedule, patient queue, messages).
 * ---------------------------------------------------------------------
 */
public class DoctorDashboard extends BaseDashboard {

    public DoctorDashboard(User user) {
        super(user, "City Hospital \u2013 Doctor Portal");
    }

    @Override
    protected JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        content.setBackground(UITheme.BACKGROUND);

        JLabel heading = new JLabel("Today's Patient Queue");
        heading.setFont(UITheme.FONT_TITLE);
        heading.setForeground(UITheme.TEXT_PRIMARY);

        String[] columns = {"Time", "Patient", "Reason for Visit", "Status"};
        Object[][] data = {
                {"09:00 AM", "J. Nakato", "Routine Checkup", "Waiting"},
                {"09:30 AM", "M. Okello", "Follow-up", "Waiting"},
                {"10:00 AM", "A. Kintu", "Consultation", "Scheduled"},
        };
        JTable table = new JTable(data, columns);
        table.setFont(UITheme.FONT_FIELD);
        table.setRowHeight(28);
        table.getTableHeader().setFont(UITheme.FONT_BUTTON.deriveFont(13f));
        table.getTableHeader().setBackground(UITheme.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new UITheme.RoundedLineBorder(UITheme.BORDER, 10, 1));

        content.add(heading, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        return content;
    }
}
