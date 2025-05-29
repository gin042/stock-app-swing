package com.boutique.service;

import com.boutique.dao.FournisseurDAO;
import com.boutique.model.Fournisseur;

import java.sql.SQLException;
import java.util.List;

public class FournisseurService {
    private FournisseurDAO fournisseurDAO;

    public FournisseurService() {
        this.fournisseurDAO = new FournisseurDAO();
    }

    public void addFournisseur(Fournisseur fournisseur) throws SQLException {
        fournisseurDAO.addFournisseur(fournisseur);
    }

    public void updateFournisseur(Fournisseur fournisseur) throws SQLException {
        fournisseurDAO.updateFournisseur(fournisseur);
    }

    public void deleteFournisseur(int idFournisseur) throws SQLException {
        fournisseurDAO.deleteFournisseur(idFournisseur);
    }

    public List<Fournisseur> getAllFournisseurs() throws SQLException {
        return fournisseurDAO.getAllFournisseurs();
    }
}