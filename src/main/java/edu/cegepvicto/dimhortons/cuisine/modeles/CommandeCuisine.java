package edu.cegepvicto.dimhortons.cuisine.modeles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un modele d'une commande côté cuisine
 */
public class CommandeCuisine {

    /* Map des états possible d'une commande
    * Me permet de contrôler les états possibles d'une commande à travers le code
     */
    public enum EtatCommande {
        EN_ATTENTE,
        EN_PREPARATION,
        PRETE
    }


    // Représente une ligne d'une commande
    public static class LigneCommande {
        private final String nomItem;
        private final int quantite;
        private final String note;

        // Le constructeur de LigneCommande
        public LigneCommande(String nomItemParam, int quantiteParam, String noteParam) {
            this.nomItem = nomItemParam;
            this.quantite = quantiteParam;
            this.note = noteParam;
        }

        // Getters pour les attributs de LigneCommande
        public String getNomItem() {
            return nomItem;
        }

        public int getQuantite() {
            return quantite;
        }

        public String getNote() {
            return note;
        }
    }

    // Les attributs de CommandeCuisine
    private final int idCommande;
    private final String nomClient;
    private final String nomTable;
    private EtatCommande etatCommande;
    private final LocalDateTime dateCommande;
    private final List<LigneCommande> listeLignes = new ArrayList<>();

    // Le constructeur de CommandeCuisine
    public CommandeCuisine(int idCommande, String nomClient, String nomTable, EtatCommande etatCommande, LocalDateTime dateCommande) {
        this.idCommande = idCommande;
        this.nomClient = nomClient;
        this.nomTable = nomTable;
        this.etatCommande = etatCommande;
        this.dateCommande = dateCommande;
    }

    // Les getters et setters pour les attributs de CommandeCuisine
    public int getIdCommande() {
        return idCommande;
    }

    public String getNomClient() {
        return nomClient;
    }

    public String getNomTable() {
        return nomTable;
    }

    public EtatCommande getEtatCommande() {
        return etatCommande;
    }

    public void setEtatCommande(EtatCommande etatCommandeParam) {
        this.etatCommande = etatCommandeParam;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public List<LigneCommande> getListeLignes() {
        return listeLignes;
    }
}
