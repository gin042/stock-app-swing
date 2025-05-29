package com.boutique.service;

import com.boutique.dao.CommandeDAO;
import com.boutique.model.Commande;

import java.sql.SQLException;
import java.util.List;

public class CommandeService {
    private CommandeDAO commandeDAO;

    public CommandeService() {
        this.commandeDAO = new CommandeDAO();
    }

    public void addCommande(Commande commande) throws SQLException {
        commandeDAO.addCommande(commande);
    }

    public void updateCommande(Commande commande) throws SQLException {
        commandeDAO.updateCommande(commande);
    }

    public void deleteCommande(int idCommande) throws SQLException {
        commandeDAO.deleteCommande(idCommande);
    }

    public List<Commande> getAllCommandes() throws SQLException {
        return commandeDAO.getAllCommandes();
    }
}