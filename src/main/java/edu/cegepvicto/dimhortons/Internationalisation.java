package edu.cegepvicto.dimhortons;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe pour gérer l'internationalisation de l'application.
 * Source : <a href="https://docs.oracle.com/javase/tutorial/i18n/intro/steps.html">...</a>
 */
public class Internationalisation {
    private static final String NOM_FICHIER = "messages"; // Nom de base du fichier de ressources
    private static Locale localeActuelle = Locale.FRENCH;
    private static ResourceBundle RESSOURCES = // Précise la locale par défaut du système
            ResourceBundle.getBundle(NOM_FICHIER, localeActuelle);

    // Méthode pour obtenir le texte localisé à partir d'une clé
    public static String texte(String cle) {
        return RESSOURCES.getString(cle);
    }
    public static void changerLocale(Locale nouvelleLocale) {
        localeActuelle = nouvelleLocale;
        RESSOURCES = ResourceBundle.getBundle(NOM_FICHIER, localeActuelle);
    }

    public static Locale getLocaleActuelle() {
        return localeActuelle;
    }

    public static boolean estFrancais() {
        return Locale.FRENCH.getLanguage().equals(localeActuelle.getLanguage());
    }
}
