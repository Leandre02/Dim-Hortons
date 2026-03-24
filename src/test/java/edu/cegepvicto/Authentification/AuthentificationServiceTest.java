package edu.cegepvicto.Authentification;

import edu.cegepvicto.dimhortons.Authentification.AuthentificationService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthentificationServiceTest {

    private AuthentificationService service;

    @BeforeEach
    public void setUp() {
        service = new AuthentificationService();
    }

    @Test
    public void valider_identifiantsCorrects() {
        // Arrange
        String email = "dimitri@dimhortons.ca";
        String mdp = "1234";

        // Act
        AuthentificationService.ResultatConnexion resultat = service.valider(email, mdp);

        // Assert
        assertNotNull(resultat);
        assertEquals("Dimitri", resultat.nom);
        assertTrue(resultat.estAdmin);
    }

    @Test
    public void valider_identifiantsIncorrects() {
        // Arrange
        String email = "inconnu@dimhortons.com";
        String mdp = "MauvaisMDP";

        // Act
        AuthentificationService.ResultatConnexion resultat = service.valider(email, mdp);

        // Assert
        assertNull(resultat);
    }

    @Test
    public void valider_emailVide() {
        String email = "";
        String mdp = "test";
        AuthentificationService.ResultatConnexion resultat = service.valider(email, mdp);
        assertNull(resultat);
    }

    @Test
    public void valider_motDePasseVide() {
        String email = "admin@dimhortons.com";
        String mdp = "";
        AuthentificationService.ResultatConnexion resultat = service.valider(email, mdp);
        assertNull(resultat);
    }
}
