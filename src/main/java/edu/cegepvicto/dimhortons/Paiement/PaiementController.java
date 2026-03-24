package edu.cegepvicto.dimhortons.Paiement;

import edu.cegepvicto.dimhortons.Menu.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la vue de paiement de l'application DimHortons.
 * Gère l'affichage du panier et le processus de paiement.
 */
public class PaiementController {

    @FXML
    private Button boutonRetour;

    @FXML
    private ListView<String> listViewPanier;

    @FXML
    private Label labelTotal;

    @FXML
    private Button boutonPayer;

    private List<Item> panier;
    private double totalPanier = 0.0;

    /**
     * Définit le panier à afficher et met à jour l'interface.
     *
     * @param panier La liste des items du panier
     */
    public void setPanier(List<Item> panier) {
        this.panier = panier;
        afficherPanier();
        calculerTotal();
    }

    /**
     * Affiche les items du panier dans la ListView.
     * Chaque item est affiché avec son nom et son prix.
     */
    private void afficherPanier() {
        if (panier == null || panier.isEmpty()) {
            listViewPanier.setItems(FXCollections.observableArrayList("Votre panier est vide"));
            return;
        }

        ObservableList<String> itemsAffichage = FXCollections.observableArrayList();

        // Regrouper les items identiques
        Map<Integer, Integer> compteur = new HashMap<>();
        for (Item item : panier) {
            compteur.put(item.getId(), compteur.getOrDefault(item.getId(), 0) + 1);
        }

        // Afficher avec quantités
        for (Item item : panier) {
            if (compteur.get(item.getId()) > 0) {
                int qte = compteur.get(item.getId());
                String ligne = String.format("%dx %s - %.2f $", qte, item.getNom(), item.getPrixFinal() * qte);
                itemsAffichage.add(ligne);
                compteur.put(item.getId(), 0); // Marquer comme affiché
            }
        }

        listViewPanier.setItems(itemsAffichage);
    }

    /**
     * Calcule le total du panier et met à jour le label.
     */
    private void calculerTotal() {
        totalPanier = 0.0;

        if (panier != null) {
            for (Item item : panier) {
                totalPanier += item.getPrixFinal();
            }
        }

        labelTotal.setText(String.format("Total : %.2f $", totalPanier));
    }

    /**
     * Gère le retour au menu principal.
     */
    @FXML
    private void onRetourMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/cegepvicto/dimhortons/Menu/menu-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) boutonRetour.getScene().getWindow();

            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            Scene scene = new Scene(root, currentWidth, currentHeight);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du retour au menu: " + e.getMessage());
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de retourner au menu", Alert.AlertType.ERROR);
        }
    }

    /**
     * Gère l'annulation du paiement et retourne au menu.
     */
    @FXML
    private void onAnnuler() {
        // Demander confirmation avant d'annuler
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Annuler le paiement");
        confirmation.setContentText("Voulez-vous vraiment annuler le paiement et retourner au menu ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                onRetourMenu();
            }
        });
    }

    /**
     * Traite le paiement.
     * Affiche une confirmation avant d'enregistrer la commande.
     */
    @FXML
    private void onPayer() {
        if (panier == null || panier.isEmpty()) {
            afficherAlerte("Panier vide", "Votre panier est vide. Ajoutez des articles avant de payer.", Alert.AlertType.WARNING);
            return;
        }

        // Afficher une confirmation de paiement
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de paiement");
        confirmation.setHeaderText(String.format("Montant total : %.2f $", totalPanier));
        confirmation.setContentText("Confirmer le paiement ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                traiterPaiement();
            }
        });
    }

    /**
     * Traite effectivement le paiement.
     * Enregistre la commande dans la base de données et affiche le reçu.
     */
    private void traiterPaiement() {
        try {
            // Créer la commande
            Commande commande = new Commande();
            commande.setDateCommande(LocalDateTime.now());
            commande.setMontantTotal(totalPanier);
            commande.setStatut("PAYEE");

            // Regrouper les items identiques et créer les lignes de commande
            Map<Integer, LigneCommande> lignesMap = new HashMap<>();

            for (Item item : panier) {
                if (lignesMap.containsKey(item.getId())) {
                    // Item déjà présent, augmenter la quantité
                    LigneCommande ligne = lignesMap.get(item.getId());
                    ligne.setQuantite(ligne.getQuantite() + 1);
                } else {
                    // Nouvel item
                    LigneCommande ligne = new LigneCommande();
                    ligne.setItemId(item.getId());
                    ligne.setNomItem(item.getNom());
                    ligne.setPrixUnitaire(item.getPrixFinal());
                    ligne.setQuantite(1);
                    lignesMap.put(item.getId(), ligne);
                    commande.ajouterLigne(ligne);
                }
            }

            // Enregistrer dans la base de données
            // Utilisateur par défaut : 1 (Keven Touil) si non connecté
            CommandeDAO commandeDAO = new CommandeDAO();
            int numeroCommande = commandeDAO.enregistrerCommande(commande, 1);

            // Afficher le reçu dans une alerte
            afficherRecu(commande);

            // Vider le panier
            panier.clear();

            // Retourner au menu
            onRetourMenu();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de la commande: " + e.getMessage());
            e.printStackTrace();
            afficherAlerte("Erreur", "Une erreur s'est produite lors de l'enregistrement de la commande.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Affiche le faux reçu de la commande dans une alerte.
     *
     * @param commande La commande pour laquelle afficher le reçu
     */
    private void afficherRecu(Commande commande) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        StringBuilder recu = new StringBuilder();
        recu.append("═══════════════════════════════════════\n");
        recu.append("          DIM HORTONS\n");
        recu.append("        Toujours Frette™\n");
        recu.append("═══════════════════════════════════════\n\n");

        recu.append("Commande: ").append(commande.getNumeroCommandeStr()).append("\n");
        recu.append("Date: ").append(commande.getDateCommande().format(formatter)).append("\n\n");

        recu.append("Articles:\n");
        recu.append("───────────────────────────────────────\n");

        for (LigneCommande ligne : commande.getLignes()) {
            recu.append(String.format("%dx %-20s %6.2f $\n",
                    ligne.getQuantite(),
                    ligne.getNomItem(),
                    ligne.getSousTotal()));
        }

        recu.append("\n───────────────────────────────────────\n");
        recu.append(String.format("Sous-total:          %10.2f $\n", commande.getMontantTotal()));
        recu.append(String.format("TPS (5%%):            %10.2f $\n", commande.getMontantTotal() * 0.05));
        recu.append(String.format("TVQ (9.975%%):        %10.2f $\n", commande.getMontantTotal() * 0.09975));
        recu.append("═══════════════════════════════════════\n");
        recu.append(String.format("TOTAL:               %10.2f $\n", commande.getMontantFinal()));
        recu.append("═══════════════════════════════════════\n\n");
        recu.append("Statut: ").append(commande.getStatut()).append("\n");
        recu.append("Mode de paiement: Comptant\n\n");
        recu.append("Merci de votre visite!\n");
        recu.append("À bientôt chez Dim Hortons!");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paiement réussi");
        alert.setHeaderText("✓ Commande enregistrée avec succès!");
        alert.setContentText(recu.toString());

        TextArea textArea = new TextArea(recu.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px;");

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(500, 600);

        alert.showAndWait();
    }

    /**
     * Affiche une alerte à l'utilisateur.
     *
     * @param titre Le titre de l'alerte
     * @param message Le message à afficher
     * @param type Le type d'alerte
     */
    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}