package com.boutique.ui;

import com.boutique.model.Client;
import com.boutique.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClientPanel extends JPanel {
    private ClientService clientService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ClientPanel() {
        clientService = new ClientService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Prénom", "Email", "Téléphone", "Adresse"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter");
        JButton updateButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(e -> addClient());
        updateButton.addActionListener(e -> updateClient());
        deleteButton.addActionListener(e -> deleteClient());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadClients();
    }

    private void loadClients() {
        try {
            List<Client> clients = clientService.getAllClients();
            tableModel.setRowCount(0);
            for (Client client : clients) {
                tableModel.addRow(new Object[]{
                        client.getIdClient(),
                        client.getNom(),
                        client.getPrenom(),
                        client.getEmail(),
                        client.getTelephone(),
                        client.getAdresse()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addClient() {
        JTextField nomField = new JTextField(20);
        JTextField prenomField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField telephoneField = new JTextField(15);
        JTextField adresseField = new JTextField(30);

        Object[] fields = {
                "Nom:", nomField,
                "Prénom:", prenomField,
                "Email:", emailField,
                "Téléphone:", telephoneField,
                "Adresse:", adresseField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Ajouter Client", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String email = emailField.getText().trim();
                String telephone = telephoneField.getText().trim();
                String adresse = adresseField.getText().trim();

                if (nom.isEmpty() || prenom.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nom et Prénom sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Client client = new Client();
                client.setNom(nom);
                client.setPrenom(prenom);
                client.setEmail(email);
                client.setTelephone(telephone);
                client.setAdresse(adresse);

                clientService.addClient(client);
                loadClients();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du client: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idClient = (int) tableModel.getValueAt(selectedRow, 0);
            JTextField nomField = new JTextField((String) tableModel.getValueAt(selectedRow, 1), 20);
            JTextField prenomField = new JTextField((String) tableModel.getValueAt(selectedRow, 2), 20);
            JTextField emailField = new JTextField((String) tableModel.getValueAt(selectedRow, 3), 20);
            JTextField telephoneField = new JTextField((String) tableModel.getValueAt(selectedRow, 4), 15);
            JTextField adresseField = new JTextField((String) tableModel.getValueAt(selectedRow, 5), 30);

            Object[] fields = {
                    "Nom:", nomField,
                    "Prénom:", prenomField,
                    "Email:", emailField,
                    "Téléphone:", telephoneField,
                    "Adresse:", adresseField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Modifier Client", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String nom = nomField.getText().trim();
                    String prenom = prenomField.getText().trim();
                    String email = emailField.getText().trim();
                    String telephone = telephoneField.getText().trim();
                    String adresse = adresseField.getText().trim();

                    if (nom.isEmpty() || prenom.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Nom et Prénom sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Client client = new Client();
                    client.setIdClient(idClient);
                    client.setNom(nom);
                    client.setPrenom(prenom);
                    client.setEmail(email);
                    client.setTelephone(telephone);
                    client.setAdresse(adresse);

                    clientService.updateClient(client);
                    loadClients();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification du client: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client à modifier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int idClient = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce client ?", "Confirmer", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    clientService.deleteClient(idClient);
                    loadClients();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du client: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}