package edu.cegepvicto.dimhortons.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un item du menu DimHortons.
 * Cette classe encapsule les informations de base d'un produit disponible à la vente.
 * Pour l'instant, les données sont hardcodées, mais elles seront chargées depuis
 * une base de données dans une version future.
 */
public class Item {
    private int id;
    private String nom;
    private double prix;
    private String image;
    private String description;
    private Categorie categorie;

    // Liste des ingrédients associés à l'item (pour CU002)
    private List<String> ingredients = new ArrayList<>();
    // Liste des extras associés à la categorie de l'item (pour CU002)
    private List<String> extras = new ArrayList<>();
    // prix final de l'item apres ajout des extrat  (pour CU002)
    private double prixFinal;

    public Item() {}

    /**
     * Constructeur pour créer un nouvel item du menu.
     *
     * @param nom Le nom de l'item
     * @param prix Le prix de l'item en dollars
     * @param image L'image de l'item (chemin)
     */
    public Item(int id, String nom, double prix, String image, String description, Categorie categorie) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.image = image;
        this.description = description;
        this.categorie = categorie;
        this.prixFinal = prix;
    }

    // GETTERS ET SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Categorie getCategorie() { return categorie; }

    public String getDescription() {
        return description;
    }

    // Gestion des ingrédients pour la modification dans le panier
    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {this.ingredients = ingredients; }

    public List<String> getExtras() {return extras;}

    public void setExtras(List<String> extras) { this.extras = extras; }

    public double getPrixFinal() { return prixFinal;}

    public void setPrixFinal(double prixFinal) { this.prixFinal = prixFinal;}

}
