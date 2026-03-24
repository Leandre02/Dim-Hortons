package edu.cegepvicto.dimhortons.Admin.modeles;

/**
 * Represente un ingredient utilise dans les items du menu.
 */
public class Ingredient {

    private int id;
    private String nom;
    private String categorie;
    private boolean actif;

    // Le constructeur par defaut
    public Ingredient() {
    }

    // Le constructeur avec parametres
    public Ingredient(int id, String nom, String categorie, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.actif = actif;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getCategorie() {
        return categorie;
    }
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    public boolean isActif() {
        return actif;
    }
    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
