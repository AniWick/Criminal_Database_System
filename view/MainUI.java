package view;

import javax.swing.*;

public class MainUI {

    public static void main(String[] args) {
        // Single unified entry point - Launch the modern GUI
        SwingUtilities.invokeLater(() -> new ModernCriminalManagementGUI());
    }
}