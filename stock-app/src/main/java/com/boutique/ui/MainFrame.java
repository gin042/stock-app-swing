package com.boutique.ui;

import com.boutique.model.Utilisateur;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Utilisateur loggedInUser;

    public MainFrame(Utilisateur loggedInUser) {
        this.loggedInUser = loggedInUser;
        initUI();
    }

    private void initUI() {
        setTitle("Stock Management Application - " + loggedInUser.getNomUtilisateur() + " (" + loggedInUser.getRole() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Both Vendeur and Admin can access Commandes and Clients
        tabbedPane.addTab("Commandes", new CommandePanel(loggedInUser));
        tabbedPane.addTab("Clients", new ClientPanel());
        
        // Only Admin can access Produits, Fournisseurs, and Utilisateurs
        if ("Admin".equals(loggedInUser.getRole())) {
            tabbedPane.addTab("Produits", new ProduitPanel());
            tabbedPane.addTab("Fournisseurs", new FournisseurPanel());
            tabbedPane.addTab("Utilisateurs", new UtilisateurPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}