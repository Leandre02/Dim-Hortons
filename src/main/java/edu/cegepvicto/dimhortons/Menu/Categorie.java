package edu.cegepvicto.dimhortons.Menu;

public class Categorie {
    protected int id;
    protected String nom;
    protected String code;

    public Categorie(int id, String nom, String code) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
}
