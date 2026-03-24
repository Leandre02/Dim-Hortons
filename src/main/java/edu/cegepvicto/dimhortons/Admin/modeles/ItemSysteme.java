package edu.cegepvicto.dimhortons.Admin.modeles;

/**
 * Represente un item du menu dans le systeme.
 * Contient toutes les infos d'un item (nom, prix, calories, etc).
 */
public class ItemSysteme {

    private int id;
    private String nom;
    private String description;
    private double prix;
    private Integer calories; // peut etre null
    private String urlImage;
    private int categorieId;
    private boolean actif;

    /**
     * Constructeur par defaut
     */
    public ItemSysteme() {
    }

    /**
     * Constructeur sans id pour les tests et l'insertion
     */
    public ItemSysteme(String nom, String description, double prix, int categorieId,
                       Integer calories, String urlImage, boolean actif) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.categorieId = categorieId;
        this.calories = calories;
        this.urlImage = urlImage;
        this.actif = actif;
    }

    /**
     * Constructeur avec id pour la lecture depuis la BD
     */
    public ItemSysteme(int id, String nom, String description, double prix,
                       Integer calories, String urlImage, int categorieId, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.calories = calories;
        this.urlImage = urlImage;
        this.categorieId = categorieId;
        this.actif = actif;
    }

    // ------- Getters et Setters -------

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    /* Pour afficher l'item dans les ListView et ComboBox
     * Permet d'eviter d'avoir l'affichage par defaut de l'objet en JavaFx
     * Debug Code inspiré /* @author OpenAI. (2025). ChatGPT (version 5.1 novembre 2025) [Modèle massif de
     *        langage]. https://chatgpt.com
     */
    @Override
    public String toString() {
        return nom + " - " + String.format("%.2f $", prix);
    }

    // Fin du code inspiré
}