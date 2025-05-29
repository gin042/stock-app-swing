package com.boutique.ui;

import com.boutique.model.Produit;
import com.boutique.service.ProduitService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Added import for DefaultTableModel
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProduitPanel extends JPanel {
    private ProduitService produitService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProduitPanel() {
        produitService = new ProduitService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Description", "Prix", "Stock", "Catégorie"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton searchButton = new JButton("Rechercher");
        JButton resetButton = new JButton("Réinitialiser");

        addButton.addActionListener(e -> addProduit());
        updateButton.addActionListener(e -> updateProduit());
        deleteButton.addActionListener(e -> deleteProduit());
        searchButton.addActionListener(e -> searchProduits());
        resetButton.addActionListener(e -> loadProduits());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadProduits();
    }

    private void loadProduits() {
        try {
            List<Produit> produits = produitService.getAllProduits();
            tableModel.setRowCount(0);
            for (Produit produit : produits) {
                tableModel.addRow(new Object[]{
                        produit.getIdProduit(),
                        produit.getNom(),
                        produit.getDescription(),
                        produit.getPrix(),
                        produit.getQuantiteStock(),
                        produit.getCategorie()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProduit() {
        JTextField nomField = new JTextField(20);
        JTextField descField = new JTextField(20);
        JTextField prixField = new JTextField(10);
        JTextField stockField = new JTextField(10);
        JTextField catField = new JTextField(20);

        Object[] fields = {
                "Nom:", nomField,
                "Description:", descField,
                "Prix:", prixField,
                "Stock:", stockField,
                "Catégorie:", catField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Ajouter Produit", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nom = nomField.getText().trim();
                String prixText = prixField.getText().trim();
                String stockText = stockField.getText().trim();

                if (nom.isEmpty() || prixText.isEmpty() || stockText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nom, Prix et Stock sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double prix = Double.parseDouble(prixText);
                int stock = Integer.parseInt(stockText);

                if (prix < 0 || stock < 0) {
                    JOptionPane.showMessageDialog(this, "Prix et Stock doivent être positifs", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Produit produit = new Produit(
                        nom,
                        descField.getText().trim(),
                        prix,
                        stock,
                        catField.getText().trim()
                );
                produitService.addProduit(produit);
                loadProduits();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Prix et Stock doivent être des nombres valides", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du produit: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateProduit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idProduit = (int) tableModel.getValueAt(selectedRow, 0);
            JTextField nomField = new JTextField((String) tableModel.getValueAt(selectedRow, 1), 20);
            JTextField descField = new JTextField((String) tableModel.getValueAt(selectedRow, 2), 20);
            JTextField prixField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString(), 10);
            JTextField stockField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString(), 10);
            JTextField catField = new JTextField((String) tableModel.getValueAt(selectedRow, 5), 20);

            Object[] fields = {
                    "Nom:", nomField,
                    "Description:", descField,
                    "Prix:", prixField,
                    "Stock:", stockField,
                    "Catégorie:", catField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Produit", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String nom = nomField.getText().trim();
                    String prixText = prixField.getText().trim();
                    String stockText = stockField.getText().trim();

                    if (nom.isEmpty() || prixText.isEmpty() || stockText.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Nom, Prix et Stock sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double prix = Double.parseDouble(prixText);
                    int stock = Integer.parseInt(stockText);

                    if (prix < 0 || stock < 0) {
                        JOptionPane.showMessageDialog(this, "Prix et Stock doivent être positifs", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Produit produit = new Produit(
                            nom,
                            descField.getText().trim(),
                            prix,
                            stock,
                            catField.getText().trim()
                    );
                    produit.setIdProduit(idProduit);
                    produitService.updateProduit(produit);
                    loadProduits();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Prix et Stock doivent être des nombres valides", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification du produit: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idProduit = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce produit ?", "Confirmer", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    produitService.deleteProduit(idProduit);
                    loadProduits();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du produit: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProduits() {
        JTextField keywordField = new JTextField(20);
        JTextField catField = new JTextField(20);

        Object[] fields = {
                "Mot-clé:", keywordField,
                "Catégorie:", catField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Rechercher Produits", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String keyword = keywordField.getText().trim();
                String categorie = catField.getText().trim();
                List<Produit> produits = produitService.searchProduits(keyword, categorie.isEmpty() ? null : categorie);
                tableModel.setRowCount(0);
                for (Produit produit : produits) {
                    tableModel.addRow(new Object[]{
                            produit.getIdProduit(),
                            produit.getNom(),
                            produit.getDescription(),
                            produit.getPrix(),
                            produit.getQuantiteStock(),
                            produit.getCategorie()
                    });
                }
                if (produits.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Aucun produit trouvé", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la recherche des produits: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}