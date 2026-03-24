package edu.cegepvicto.dimhortons.Paiement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commande {

    private int numeroCommande;
    private String numeroCommandeStr;
    private LocalDateTime dateCommande;
    private double montantTotal;
    private double taxe;
    private double montantFinal;
    private String statut;
    private List<LigneCommande> lignes;

    public Commande() {
        this.lignes = new ArrayList<>();
        this.dateCommande = LocalDateTime.now();
        this.statut = "PAYEE";
    }

    public int getNumeroCommande() {
        return numeroCommande;
    }

    public void setNumeroCommande(int numeroCommande) {
        this.numeroCommande = numeroCommande;
    }

    public String getNumeroCommandeStr() {
        return numeroCommandeStr;
    }

    public void setNumeroCommandeStr(String numeroCommandeStr) {
        this.numeroCommandeStr = numeroCommandeStr;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public double getTaxe() {
        return taxe;
    }

    public void setTaxe(double taxe) {
        this.taxe = taxe;
    }

    public double getMontantFinal() {
        return montantFinal;
    }

    public void setMontantFinal(double montantFinal) {
        this.montantFinal = montantFinal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void ajouterLigne(LigneCommande ligne) {
        this.lignes.add(ligne);
    }
}