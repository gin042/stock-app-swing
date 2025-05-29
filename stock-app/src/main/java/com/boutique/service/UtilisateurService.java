package com.boutique.service;

import com.boutique.dao.UtilisateurDAO;
import com.boutique.model.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public class UtilisateurService {
    private UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public void addUtilisateur(Utilisateur utilisateur) throws SQLException {
        utilisateurDAO.addUtilisateur(utilisateur);
    }

    public void updateUtilisateur(Utilisateur utilisateur) throws SQLException {
        utilisateurDAO.updateUtilisateur(utilisateur);
    }

    public void deleteUtilisateur(int idUtilisateur) throws SQLException {
        utilisateurDAO.deleteUtilisateur(idUtilisateur);
    }

    public List<Utilisateur> getAllUtilisateurs() throws SQLException {
        return utilisateurDAO.getAllUtilisateurs();
    }

    public Utilisateur authenticate(String nomUtilisateur, String motDePasse) throws SQLException {
        return utilisateurDAO.authenticate(nomUtilisateur, motDePasse);
    }
}