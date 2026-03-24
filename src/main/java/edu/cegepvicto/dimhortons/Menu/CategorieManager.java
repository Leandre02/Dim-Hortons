package edu.cegepvicto.dimhortons.Menu;

import java.util.List;
import java.util.stream.Collectors;

public class CategorieManager {

    private String categorieSelection = null;

    public void setCategorie(String categorie){
        this.categorieSelection = categorie;
    }

    public String getCategorie(){
        return this.categorieSelection;
    }

    public boolean estTous(){
        return categorieSelection == null;
    }

    /*
     * Permet de filtrer les items via la Catégorie (Aide de ChatGPT)
     */
    public List<Item> filtrer(List<Item> items) {
        if (categorieSelection == null) {
            return items;
        }

        return items.stream()
                .filter(item -> item.getCategorie() != null)
                .filter(item -> categorieSelection.equalsIgnoreCase(item.getCategorie().getNom()))
                .collect(Collectors.toList());
    }
}
