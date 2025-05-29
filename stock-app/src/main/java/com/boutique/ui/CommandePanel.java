package com.boutique.ui;

import com.boutique.model.Commande;
import com.boutique.model.Client;
import com.boutique.model.Produit;
import com.boutique.model.Utilisateur;
import com.boutique.service.CommandeService;
import com.boutique.service.ClientService;
import com.boutique.service.ProduitService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandePanel extends JPanel {
    private CommandeService commandeService;
    private ClientService clientService;
    private ProduitService produitService;
    private JTable table;
    private DefaultTableModel tableModel;
    private Utilisateur loggedInUser;

    public CommandePanel(Utilisateur loggedInUser) {
        this.loggedInUser = loggedInUser;
        commandeService = new CommandeService();
        clientService = new ClientService();
        produitService = new ProduitService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Client", "Total", "Statut"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        // Vendeur can only add and update status, not delete
        addButton.addActionListener(e -> addCommande());
        updateButton.addActionListener(e -> {
            if ("Vendeur".equals(loggedInUser.getRole())) {
                updateCommandeStatusOnly();
            } else {
                updateCommande();
            }
        });
        deleteButton.addActionListener(e -> deleteCommande());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        // Only Admin can delete orders
        if ("Admin".equals(loggedInUser.getRole())) {
            buttonPanel.add(deleteButton);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        loadCommandes();
    }

    private void loadCommandes() {
        try {
            List<Commande> commandes = commandeService.getAllCommandes();
            tableModel.setRowCount(0);
            for (Commande commande : commandes) {
                String clientName = (commande.getClient() != null) 
                    ? commande.getClient().getNom() + " " + (commande.getClient().getPrenom() != null ? commande.getClient().getPrenom() : "")
                    : "N/A";
                tableModel.addRow(new Object[]{
                        commande.getIdCommande(),
                        commande.getDateCommande(),
                        clientName,
                        commande.getTotal(),
                        commande.getStatut()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des commandes: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCommande() {
        try {
            List<Client> clients = clientService.getAllClients();
            List<Produit> produits = produitService.getAllProduits();

            if (clients.isEmpty() || produits.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun client ou produit disponible. Vérifiez la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
            DefaultListModel<Produit> produitModel = new DefaultListModel<>();
            for (Produit produit : produits) {
                produitModel.addElement(produit);
            }
            JList<Produit> produitList = new JList<>(produitModel);
            produitList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JTextField totalField = new JTextField(10);
            JComboBox<String> statutCombo = new JComboBox<>(new String[]{"En attente", "Expediee", "Annulee"});

            // Panel for quantity inputs
            Map<Produit, JTextField> quantityFields = new HashMap<>();
            JPanel quantityPanel = new JPanel(new GridLayout(produits.size(), 2, 5, 5));
            for (Produit produit : produits) {
                quantityPanel.add(new JLabel(produit.getNom() + ":"));
                JTextField qtyField = new JTextField("0", 5);
                quantityFields.put(produit, qtyField);
                quantityPanel.add(qtyField);
            }

            Object[] fields = {
                    "Client:", clientCombo,
                    "Produits:", new JScrollPane(produitList),
                    "Quantités:", new JScrollPane(quantityPanel),
                    "Total:", totalField,
                    "Statut:", statutCombo
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Ajouter Commande", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String totalText = totalField.getText().trim();
                List<Produit> selectedProduits = produitList.getSelectedValuesList();

                if (selectedProduits.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un produit", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (totalText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Le total est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double total;
                try {
                    total = Double.parseDouble(totalText);
                    if (total < 0) {
                        JOptionPane.showMessageDialog(this, "Le total doit être positif", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Total invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate quantities
                Map<Produit, Integer> produitQuantities = new HashMap<>();
                for (Produit produit : selectedProduits) {
                    String qtyText = quantityFields.get(produit).getText().trim();
                    int quantity;
                    try {
                        quantity = Integer.parseInt(qtyText);
                        if (quantity <= 0) {
                            JOptionPane.showMessageDialog(this, "La quantité pour " + produit.getNom() + " doit être positive", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (quantity > produit.getQuantiteStock()) {
                            JOptionPane.showMessageDialog(this, "Stock insuffisant pour " + produit.getNom() + " (Stock: " + produit.getQuantiteStock() + ")", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Quantité invalide pour " + produit.getNom(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    produitQuantities.put(produit, quantity);
                }

                Commande commande = new Commande();
                commande.setDateCommande(new Date());
                commande.setClient((Client) clientCombo.getSelectedItem());
                commande.setTotal(total);
                commande.setStatut((String) statutCombo.getSelectedItem());
                commande.setListeProduits(new ArrayList<>(selectedProduits));
                commandeService.addCommande(commande);
                loadCommandes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCommandeStatusOnly() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
                Commande existingCommande = commandeService.getAllCommandes().stream()
                        .filter(c -> c.getIdCommande() == idCommande)
                        .findFirst()
                        .orElse(null);

                if (existingCommande == null) {
                    JOptionPane.showMessageDialog(this, "Commande introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JComboBox<String> statutCombo = new JComboBox<>(new String[]{"En attente", "Expediee", "Annulee"});
                statutCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 4));

                Object[] fields = {
                        "Statut:", statutCombo
                };

                int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Statut de la Commande", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    existingCommande.setStatut((String) statutCombo.getSelectedItem());
                    commandeService.updateCommande(existingCommande);
                    loadCommandes();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification du statut de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCommande() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
                List<Client> clients = clientService.getAllClients();
                List<Produit> produits = produitService.getAllProduits();

                if (clients.isEmpty() || produits.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Aucun client ou produit disponible. Vérifiez la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
                DefaultListModel<Produit> produitModel = new DefaultListModel<>();
                for (Produit produit : produits) {
                    produitModel.addElement(produit);
                }
                JList<Produit> produitList = new JList<>(produitModel);
                produitList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                String totalValue = tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "0.0";
                JTextField totalField = new JTextField(totalValue, 10);
                JComboBox<String> statutCombo = new JComboBox<>(new String[]{"En attente", "Expediee", "Annulee"});
                String statutValue = tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "En attente";
                statutCombo.setSelectedItem(statutValue);

                // Panel for quantity inputs
                Map<Produit, JTextField> quantityFields = new HashMap<>();
                JPanel quantityPanel = new JPanel(new GridLayout(produits.size(), 2, 5, 5));
                for (Produit produit : produits) {
                    quantityPanel.add(new JLabel(produit.getNom() + ":"));
                    JTextField qtyField = new JTextField("0", 5);
                    quantityFields.put(produit, qtyField);
                    quantityPanel.add(qtyField);
                }

                Object[] fields = {
                        "Client:", clientCombo,
                        "Produits:", new JScrollPane(produitList),
                        "Quantités:", new JScrollPane(quantityPanel),
                        "Total:", totalField,
                        "Statut:", statutCombo
                };

                int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Commande", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String totalText = totalField.getText().trim();
                    List<Produit> selectedProduits = produitList.getSelectedValuesList();

                    if (selectedProduits.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un produit", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (totalText.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Le total est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double total;
                    try {
                        total = Double.parseDouble(totalText);
                        if (total < 0) {
                            JOptionPane.showMessageDialog(this, "Le total doit être positif", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Total invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validate quantities
                    Map<Produit, Integer> produitQuantities = new HashMap<>();
                    for (Produit produit : selectedProduits) {
                        String qtyText = quantityFields.get(produit).getText().trim();
                        int quantity;
                        try {
                            quantity = Integer.parseInt(qtyText);
                            if (quantity <= 0) {
                                JOptionPane.showMessageDialog(this, "La quantité pour " + produit.getNom() + " doit être positive", "Erreur", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (quantity > produit.getQuantiteStock()) {
                                JOptionPane.showMessageDialog(this, "Stock insuffisant pour " + produit.getNom() + " (Stock: " + produit.getQuantiteStock() + ")", "Erreur", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Quantité invalide pour " + produit.getNom(), "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        produitQuantities.put(produit, quantity);
                    }

                    Commande commande = new Commande();
                    commande.setIdCommande(idCommande);
                    commande.setDateCommande(new Date());
                    commande.setClient((Client) clientCombo.getSelectedItem());
                    commande.setTotal(total);
                    commande.setStatut((String) statutCombo.getSelectedItem());
                    commande.setListeProduits(new ArrayList<>(selectedProduits));
                    commandeService.updateCommande(commande);
                    loadCommandes();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCommande() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette commande ?", "Confirmer", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    commandeService.deleteCommande(idCommande);
                    loadCommandes();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}