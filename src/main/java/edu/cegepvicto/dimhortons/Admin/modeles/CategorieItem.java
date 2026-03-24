package edu.cegepvicto.dimhortons.Admin.modeles;

/**
 * Represente une categorie d'items dans le menu.
 */
public class CategorieItem {

    private int id;
    private String nom;

    /**
     * Constructeur par defaut
     */
    public CategorieItem() {
    }

    /**
     * Constructeur avec parametres
     */
    public CategorieItem(int id, String nom) {
        this.id = id;
        this.nom = nom;
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

    /**
     * Pour afficher le nom dans les ListView et ComboBox
     * Permet d'eviter d'avoir l'affichage par defaut de l'objet en JavaFX
     * Debug Code inspiré : @author OpenAI. (2025). ChatGPT (version 5.1 novembre 2025) [Modèle massif de
     *        langage]. https://chatgpt.com
     */

    @Override
    public String toString() {
        return nom;
    }

    // Fin du code inspiré
}