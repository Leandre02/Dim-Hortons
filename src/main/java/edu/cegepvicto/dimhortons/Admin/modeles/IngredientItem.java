package edu.cegepvicto.dimhortons.Admin.modeles;


/* * Represente un ingredient utilise dans les items du menu.
 */
public class IngredientItem {

    private int ingredient_id;
    private double quantite;
    private boolean obligatoire;
    private String unite;

    // Le constructeur par defaut
    public IngredientItem() {
    }

    // Le constructeur avec parametres
    public IngredientItem(int ingredient_id, double quantite, boolean obligatoire, String unite) {
        this.ingredient_id = ingredient_id;
        this.quantite = quantite;
        this.obligatoire = obligatoire;
        this.unite = unite;
    }

    // Getters et Setters
    public int getIngredient_id() {
        return ingredient_id;
    }
    public void setIngredient_id(int ingredient_id) {
        this.ingredient_id = ingredient_id;
    }
    public double getQuantite() {
        return quantite;
    }
    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }
    public boolean isObligatoire() {
        return obligatoire;
    }
    public void setObligatoire(boolean obligatoire) {
        this.obligatoire = obligatoire;
    }
    public String getUnite() {
        return unite;
    }
    public void setUnite(String unite) {
        this.unite = unite;
    }

}
