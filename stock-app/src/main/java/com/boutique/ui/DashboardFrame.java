package com.boutique.ui;

import com.boutique.model.Utilisateur;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private Utilisateur utilisateur;

    public DashboardFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initUI();
    }

    private void initUI() {
        setTitle("Boutique Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Both Vendeur and Admin can access Commandes, Clients, and Produits
        tabbedPane.addTab("Commandes", new CommandePanel(utilisateur));
        tabbedPane.addTab("Clients", new ClientPanel());
        tabbedPane.addTab("Produits", new ProduitPanel());

        // Only Admin can access Fournisseurs and Utilisateurs
        if ("Admin".equals(utilisateur.getRole())) {
            tabbedPane.addTab("Utilisateurs", new UtilisateurPanel());
            tabbedPane.addTab("Fournisseurs", new FournisseurPanel());
        }

        add(tabbedPane);
    }
}