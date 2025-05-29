CREATE DATABASE boutique\_db;
USE boutique\_db;

\-- Table for Produit
CREATE TABLE Produit (
idProduit INT PRIMARY KEY AUTO\_INCREMENT,
nom VARCHAR(100) NOT NULL,
description TEXT,
prix DECIMAL(10, 2) NOT NULL,
quantiteStock INT NOT NULL,
categorie VARCHAR(50)
);

\-- Table for Client
CREATE TABLE Client (
idClient INT PRIMARY KEY AUTO\_INCREMENT,
nom VARCHAR(50) NOT NULL,
prenom VARCHAR(50),
adresse VARCHAR(200),
telephone VARCHAR(20),
email VARCHAR(100)
);

\-- Table for Fournisseur
CREATE TABLE Fournisseur (
idFournisseur INT PRIMARY KEY AUTO\_INCREMENT,
nom VARCHAR(100) NOT NULL,
contact VARCHAR(200),
produitsFournis TEXT
);

\-- Table for Utilisateur
CREATE TABLE Utilisateur (
idUtilisateur INT PRIMARY KEY AUTO\_INCREMENT,
nomUtilisateur VARCHAR(50) NOT NULL UNIQUE,
motDePasse VARCHAR(100) NOT NULL,
role ENUM('Admin', 'Vendeur') NOT NULL
);

\-- Table for Commande
CREATE TABLE Commande (
idCommande INT PRIMARY KEY AUTO\_INCREMENT,
dateCommande DATE NOT NULL,
idClient INT,
total DECIMAL(10, 2) NOT NULL,
statut ENUM('En attente', 'Expediee', 'Annulee') NOT NULL,
FOREIGN KEY (idClient) REFERENCES Client(idClient)
);

\-- Table for Commande\_Produit (junction table for Commande and Produit)
CREATE TABLE Commande\_Produit (
idCommande INT,
idProduit INT,
quantite INT NOT NULL,
PRIMARY KEY (idCommande, idProduit),
FOREIGN KEY (idCommande) REFERENCES Commande(idCommande),
FOREIGN KEY (idProduit) REFERENCES Produit(idProduit)
);

\-- Insert sample data
INSERT INTO Utilisateur (nomUtilisateur, motDePasse, role) VALUES ('admin', 'admin123', 'Admin');
INSERT INTO Utilisateur (nomUtilisateur, motDePasse, role) VALUES ('vendeur', 'vendeur123', 'Vendeur');