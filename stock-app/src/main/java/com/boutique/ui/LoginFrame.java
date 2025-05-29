package com.boutique.ui;

    import com.boutique.model.Utilisateur;
    import com.boutique.service.UtilisateurService;

    import javax.swing.*;
    import java.awt.*;
    import java.sql.SQLException;
    import java.util.List;

    public class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private UtilisateurService utilisateurService;

        public LoginFrame() {
            utilisateurService = new UtilisateurService();
            initUI();
        }

        private void initUI() {
            setTitle("Login");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(300, 200);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel usernameLabel = new JLabel("Nom Utilisateur:");
            usernameField = new JTextField(15);
            JLabel passwordLabel = new JLabel("Mot de Passe:");
            passwordField = new JPasswordField(15);
            JButton loginButton = new JButton("Connexion");

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(usernameLabel, gbc);
            gbc.gridy = 1;
            panel.add(passwordLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(usernameField, gbc);
            gbc.gridy = 1;
            panel.add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(loginButton, gbc);

            add(panel);

            loginButton.addActionListener(e -> {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nom d'utilisateur et mot de passe sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
                    Utilisateur user = utilisateurs.stream()
                            .filter(u -> u.getNomUtilisateur().equals(username) && u.getMotDePasse().equals(password))
                            .findFirst()
                            .orElse(null);

                    if (user != null) {
                        dispose(); // Close login frame
                        SwingUtilities.invokeLater(() -> {
                            new DashboardFrame(user).setVisible(true); // Launch DashboardFrame with user
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Nom d'utilisateur ou mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la vérification des identifiants: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }