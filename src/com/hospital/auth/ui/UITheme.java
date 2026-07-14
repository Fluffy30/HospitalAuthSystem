package com.hospital.auth.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.*;

/**
 * UITheme.java
 * ---------------------------------------------------------------------
 * Central place for every colour, font and reusable Swing "widget
 * factory" method used across the application. Concentrating all of
 * this styling logic here achieves two things:
 *
 *   1. A single, consistent hospital-themed look everywhere (soft teal
 *      / blue palette that reads as clean and clinical).
 *   2. Panels stop repeating boilerplate styling code and instead call
 *      short, descriptive helper methods such as
 *      UITheme.createPrimaryButton("Sign In").
 * ---------------------------------------------------------------------
 */
public final class UITheme {

    // ----------------------------------------------------------------
    // Palette - a calm, professional "medical" teal/blue colour scheme
    // ----------------------------------------------------------------
    public static final Color PRIMARY = new Color(0, 121, 140);       // deep teal
    public static final Color PRIMARY_DARK = new Color(0, 94, 110);   // hover/pressed
    public static final Color ACCENT = new Color(0, 168, 150);        // mint accent
    public static final Color BACKGROUND = new Color(244, 249, 250);  // near-white
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 45, 51);
    public static final Color TEXT_SECONDARY = new Color(105, 122, 128);
    public static final Color BORDER = new Color(210, 224, 227);
    public static final Color ERROR = new Color(198, 40, 40);
    public static final Color SUCCESS = new Color(46, 139, 87);

    // ----------------------------------------------------------------
    // Fonts
    // ----------------------------------------------------------------
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LINK = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private UITheme() {
        // Static-only utility class - never instantiated.
    }

    // ==================================================================
    //  Reusable component factories
    // ==================================================================

    /**
     * Creates the solid, filled "call to action" button style used for
     * primary actions like Sign In / Create Account / Reset Password.
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Simple hover feedback: darken the background on mouse-enter.
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY_DARK);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY);
            }
        });
        return button;
    }

    /**
     * Creates a lightweight, borderless "text link" style button, used
     * for secondary navigation actions such as "Create one" or "Back to
     * Sign In".
     */
    public static JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_LINK.deriveFont(Font.BOLD));
        button.setForeground(PRIMARY);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Styles a JTextField / JPasswordField consistently: font, padding,
     * and a soft rounded border that highlights on focus is handled by
     * {@link #createFieldBorder()}.
     */
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_FIELD);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(CARD_BACKGROUND);
        field.setBorder(createFieldBorder());
        field.setCaretColor(PRIMARY);
    }

    /**
     * A rounded, padded border used for every input field, giving the
     * form a soft, modern, "card-like" appearance instead of the harsh
     * default Swing rectangle border.
     */
    public static Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 8, 1),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        );
    }

    /** Section/field label (e.g. "Username", "Password"). */
    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /** Large screen title, e.g. "Welcome Back". */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /** Small grey helper/subtitle text under a title. */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /**
     * Inline error/status label. Text colour switches between ERROR and
     * SUCCESS depending on the isError flag, and the label is hidden
     * entirely (empty text) when there is nothing to report.
     */
    public static JLabel createMessageLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(FONT_SMALL.deriveFont(Font.BOLD, 12f));
        label.setForeground(ERROR);
        return label;
    }

    /**
     * Convenience helper to update a message label created via
     * {@link #createMessageLabel()} with either an error or success style.
     */
    public static void setMessage(JLabel label, String text, boolean isError) {
        label.setText(text == null || text.isEmpty() ? " " : text);
        label.setForeground(isError ? ERROR : SUCCESS);
    }

    // ==================================================================
    //  Custom rounded border (small inner class, used by input fields
    //  and by RoundedPanel for the card container behind each form).
    // ==================================================================
    public static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    /**
     * A JPanel subclass that paints itself as a white, rounded-corner
     * "card" with a subtle border - used as the container behind every
     * form so the login window doesn't look like a flat grey dialog.
     */
    public static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius, LayoutManager layout) {
            super(layout);
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_BACKGROUND);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
