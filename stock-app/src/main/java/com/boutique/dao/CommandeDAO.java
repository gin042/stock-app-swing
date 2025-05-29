package com.boutique.dao;

import com.boutique.model.Commande;
import com.boutique.model.Client;
import com.boutique.model.Produit;
import com.boutique.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    public void addCommande(Commande commande) throws SQLException {
        String sql = "INSERT INTO Commande (dateCommande, idClient, total, statut) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, new java.sql.Date(commande.getDateCommande().getTime()));
            stmt.setInt(2, commande.getClient().getIdClient());
            stmt.setDouble(3, commande.getTotal());
            stmt.setString(4, commande.getStatut());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    commande.setIdCommande(rs.getInt(1));
                }
            }

            // Insert products
            String produitSql = "INSERT INTO Commande_Produit (idCommande, idProduit, quantite) VALUES (?, ?, ?)";
            try (PreparedStatement produitStmt = conn.prepareStatement(produitSql)) {
                for (Produit produit : commande.getListeProduits()) {
                    produitStmt.setInt(1, commande.getIdCommande());
                    produitStmt.setInt(2, produit.getIdProduit());
                    produitStmt.setInt(3, 1); // Assuming quantity 1 for simplicity
                    produitStmt.executeUpdate();
                }
            }
        }
    }

    public void updateCommande(Commande commande) throws SQLException {
        String sql = "UPDATE Commande SET dateCommande = ?, idClient = ?, total = ?, statut = ? WHERE idCommande = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(commande.getDateCommande().getTime()));
            stmt.setInt(2, commande.getClient().getIdClient());
            stmt.setDouble(3, commande.getTotal());
            stmt.setString(4, commande.getStatut());
            stmt.setInt(5, commande.getIdCommande());
            stmt.executeUpdate();
        }
    }

    public void deleteCommande(int idCommande) throws SQLException {
        String sql = "DELETE FROM Commande WHERE idCommande = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            stmt.executeUpdate();
        }
    }

    public List<Commande> getAllCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, cl.* FROM Commande c JOIN Client cl ON c.idClient = cl.idClient";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("idClient"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));

                Commande commande = new Commande();
                commande.setIdCommande(rs.getInt("idCommande"));
                commande.setDateCommande(rs.getDate("dateCommande"));
                commande.setClient(client);
                commande.setTotal(rs.getDouble("total"));
                commande.setStatut(rs.getString("statut"));
                commandes.add(commande);
            }
        }
        return commandes;
    }
}