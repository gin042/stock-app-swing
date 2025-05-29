package com.boutique;

    import com.boutique.ui.DashboardFrame;
    import com.boutique.ui.LoginFrame;

    import javax.swing.SwingUtilities;

    public class MainApp {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }