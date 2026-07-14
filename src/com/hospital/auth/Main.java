package com.hospital.auth;

import com.hospital.auth.ui.LoginFrame;

import javax.swing.*;

/**
 * Main.java
 * ---------------------------------------------------------------------
 * Application entry point.
 *
 * Responsibilities:
 *   1. Apply the operating system's native Look and Feel so the app
 *      blends in with the user's desktop (Windows, macOS, Linux) while
 *      our custom UITheme colours/fonts still layer on top cleanly.
 *   2. Launch the single LoginFrame window on the Swing Event Dispatch
 *      Thread (EDT), as required for thread-safe Swing UI construction.
 *
 * Demo credentials (see AuthService#seedDemoAccount):
 *      Username: admin      Password: Admin123      Role: Administrator
 * ---------------------------------------------------------------------
 */
public class Main {

    public static void main(String[] args) {
        // Use the native OS Look and Feel as a base; fall back silently
        // to the cross-platform default if it can't be applied for any
        // reason (headless environments, unusual JVMs, etc.).
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Non-fatal: Swing will simply use its default Look and Feel.
        }

        // All Swing components must be created/updated on the Event
        // Dispatch Thread - SwingUtilities.invokeLater guarantees that.
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

