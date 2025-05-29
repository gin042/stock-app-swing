package com.boutique.service;

import com.boutique.dao.ProduitDAO;
import com.boutique.model.Produit;

import java.sql.SQLException;
import java.util.List;

public class ProduitService {
    private ProduitDAO produitDAO;

    public ProduitService() {
        this.produitDAO = new ProduitDAO();
    }

    public void addProduit(Produit produit) throws SQLException {
        produitDAO.addProduit(produit);
    }

    public void updateProduit(Produit produit) throws SQLException {
        produitDAO.updateProduit(produit);
    }

    public void deleteProduit(int idProduit) throws SQLException {
        produitDAO.deleteProduit(idProduit);
    }

    public List<Produit> getAllProduits() throws SQLException {
        return produitDAO.getAllProduits();
    }

    public List<Produit> searchProduits(String keyword, String categorie) throws SQLException {
        return produitDAO.searchProduits(keyword, categorie);
    }
}