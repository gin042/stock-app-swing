package com.boutique.dao;

import com.boutique.model.Fournisseur;
import com.boutique.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {
    public void addFournisseur(Fournisseur fournisseur) throws SQLException {
        String sql = "INSERT INTO Fournisseur (nom, contact, produitsFournis) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getContact());
            stmt.setString(3, fournisseur.getProduitsFournis());
            stmt.executeUpdate();
        }
    }

    public void updateFournisseur(Fournisseur fournisseur) throws SQLException {
        String sql = "UPDATE Fournisseur SET nom = ?, contact = ?, produitsFournis = ? WHERE idFournisseur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getContact());
            stmt.setString(3, fournisseur.getProduitsFournis());
            stmt.setInt(4, fournisseur.getIdFournisseur());
            stmt.executeUpdate();
        }
    }

    public void deleteFournisseur(int idFournisseur) throws SQLException {
        String sql = "DELETE FROM Fournisseur WHERE idFournisseur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFournisseur);
            stmt.executeUpdate();
        }
    }

    public List<Fournisseur> getAllFournisseurs() throws SQLException {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM Fournisseur";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fournisseur fournisseur = new Fournisseur();
                fournisseur.setIdFournisseur(rs.getInt("idFournisseur"));
                fournisseur.setNom(rs.getString("nom"));
                fournisseur.setContact(rs.getString("contact"));
                fournisseur.setProduitsFournis(rs.getString("produitsFournis"));
                fournisseurs.add(fournisseur);
            }
        }
        return fournisseurs;
    }
}