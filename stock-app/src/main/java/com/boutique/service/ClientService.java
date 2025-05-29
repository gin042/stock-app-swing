package com.boutique.service;

import com.boutique.dao.ClientDAO;
import com.boutique.model.Client;

import java.sql.SQLException;
import java.util.List;

public class ClientService {
    private ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
    }

    public void addClient(Client client) throws SQLException {
        clientDAO.addClient(client);
    }

    public void updateClient(Client client) throws SQLException {
        clientDAO.updateClient(client);
    }

    public void deleteClient(int idClient) throws SQLException {
        clientDAO.deleteClient(idClient);
    }

    public List<Client> getAllClients() throws SQLException {
        return clientDAO.getAllClients();
    }
}