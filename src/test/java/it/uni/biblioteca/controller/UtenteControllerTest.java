package it.uni.biblioteca.controller;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Utente;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Simona
 * Test TestFX per UtenteController
 */
public class UtenteControllerTest extends ApplicationTest {
    
    private Biblioteca biblioteca;
    private TableView<Utente> tabellaUtenti;
    
    @Override
    public void start(Stage stage) throws Exception {
        // Reset Singleton
        resetBibliotecaSingleton();
        biblioteca = Biblioteca.getInstance();
        
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/UtenteView.fxml")
        );
        Parent root = loader.load();
        
        UtenteController controller = loader.getController();
        tabellaUtenti = controller.tabellaUtenti;
        
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }
    
    @BeforeEach
    public void setUp() {
        // Pulisci dati
        interact(() -> {
            biblioteca.getTuttiLibri().clear();
            biblioteca.getTuttiUtenti().clear();
            biblioteca.getPrestitiAttivi().clear();
            tabellaUtenti.getItems().clear();
        });
        sleep(200);
    }
    
    @AfterEach
    public void tearDown() {
        interact(() -> {
            biblioteca.getTuttiLibri().clear();
            biblioteca.getTuttiUtenti().clear();
            biblioteca.getPrestitiAttivi().clear();
        });
    }
    
    @AfterAll
    public static void cleanup() throws Exception {
        FxToolkit.hideStage();
    }
    
    private void resetBibliotecaSingleton() {
        try {
            java.lang.reflect.Field instanceField = Biblioteca.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @DisplayName("Test: Inserimento nuovo utente")
    public void testInserimentoUtente() {
        String matricolaUnica = "MAT" + System.currentTimeMillis();
        
        clickOn("Inserisci Utente");
        sleep(300);
        
        clickOn("#fieldNome").write("Mario");
        clickOn("#fieldCognome").write("Rossi");
        clickOn("#fieldMatricola").write(matricolaUnica);
        clickOn("#fieldEmail").write("m.rossi@studenti.unisa.it");
        
        clickOn("✓ Conferma");
        sleep(500);
        
        assertEquals(1, tabellaUtenti.getItems().size(), 
            "Dovrebbe esserci 1 utente nella tabella");
        
        Utente utenteAggiunto = tabellaUtenti.getItems().get(0);
        assertEquals("Mario", utenteAggiunto.getNome());
        assertEquals("Rossi", utenteAggiunto.getCognome());
        assertEquals(matricolaUnica, utenteAggiunto.getMatricola());
    }
    
    @Test
    @DisplayName("Test: Ricerca utente per cognome")
    public void testRicercaPerCognome() throws Exception {
        String mat1 = "MAT" + System.currentTimeMillis();
        Utente u1 = new Utente(mat1, "Mario", "Rossi", "m.rossi@test.it");
        
        sleep(10);
        String mat2 = "MAT" + System.currentTimeMillis();
        Utente u2 = new Utente(mat2, "Luigi", "Verdi", "l.verdi@test.it");
        
        interact(() -> {
            try {
                biblioteca.aggiungiUtente(u1);
                biblioteca.aggiungiUtente(u2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        clickOn("Mostra Tutti");
        sleep(300);
        
        assertEquals(2, tabellaUtenti.getItems().size(), "Dovrebbero esserci 2 utenti");
        
        // Ricerca
        clickOn("#fieldRicerca").write("Rossi");
        clickOn("Cerca");
        sleep(300);
        
        assertEquals(1, tabellaUtenti.getItems().size(), 
            "Dovrebbe essere trovato 1 utente con cognome 'Rossi'");
        assertEquals("Rossi", tabellaUtenti.getItems().get(0).getCognome());
    }
    
    @Test
    @DisplayName("Test: Ricerca utente per matricola")
    public void testRicercaPerMatricola() throws Exception {
        String mat1 = "MAT" + System.currentTimeMillis();
        Utente u1 = new Utente(mat1, "Mario", "Rossi", "m.rossi@test.it");
        
        sleep(10);
        String mat2 = "MAT" + System.currentTimeMillis();
        Utente u2 = new Utente(mat2, "Luigi", "Verdi", "l.verdi@test.it");
        
        interact(() -> {
            try {
                biblioteca.aggiungiUtente(u1);
                biblioteca.aggiungiUtente(u2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        clickOn("Mostra Tutti");
        sleep(300);
        
        // Seleziona criterio MATRICOLA
        clickOn("#comboCriterio").clickOn("MATRICOLA");
        
        clickOn("#fieldRicerca").write(mat1);
        clickOn("Cerca");
        sleep(300);
        
        assertEquals(1, tabellaUtenti.getItems().size());
        assertEquals(mat1, tabellaUtenti.getItems().get(0).getMatricola());
    }
    
    @Test
    @DisplayName("Test: Modifica utente esistente")
    public void testModificaUtente() throws Exception {
        String matricolaUnica = "MAT" + System.currentTimeMillis();
        Utente utente = new Utente(matricolaUnica, "Mario", "Rossi", "m.rossi@test.it");
        
        interact(() -> {
            try {
                biblioteca.aggiungiUtente(utente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        clickOn("Mostra Tutti");
        sleep(300);
        
        // Seleziona utente (click sulla riga)
        interact(() -> {
            tabellaUtenti.getSelectionModel().select(0);
        });
        sleep(200);
        
        clickOn("Modifica");
        sleep(300);
        
        // Modifica email
        clickOn("#fieldEmail");
        eraseText(16);
        write("mario.rossi@studenti.unisa.it");
        
        clickOn("✓ Conferma");
        sleep(500);
        
        Utente modificato = biblioteca.cercaUtentePerMatricola(matricolaUnica);
        assertEquals("mario.rossi@studenti.unisa.it", modificato.getEmail());
    }
    
    @Test
    @DisplayName("Test: Validazione email nel dialog")
    public void testValidazioneEmail() {
        clickOn("Inserisci Utente");
        sleep(300);
        
        clickOn("#fieldNome").write("Mario");
        clickOn("#fieldCognome").write("Rossi");
        clickOn("#fieldMatricola").write("MAT" + System.currentTimeMillis());
        clickOn("#fieldEmail").write("emailnonvalida");
        
        clickOn("✓ Conferma");
        sleep(300);
        
        assertTrue(lookup(".alert").tryQuery().isPresent(), 
            "Dovrebbe apparire errore per email non valida");
        
        clickOn("OK");
    }
    
    @Test
    @DisplayName("Test: Eliminazione utente")
    public void testEliminazioneUtente() throws Exception {
        String matricolaUnica = "MAT" + System.currentTimeMillis();
        Utente utente = new Utente(matricolaUnica, "Mario", "Rossi", "m.rossi@test.it");
        
        interact(() -> {
            try {
                biblioteca.aggiungiUtente(utente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        clickOn("Mostra Tutti");
        sleep(300);
        
        // Seleziona utente
        interact(() -> {
            tabellaUtenti.getSelectionModel().select(0);
        });
        sleep(200);
        
        clickOn("Elimina");
        sleep(300);
        
        clickOn("OK");
        sleep(500);
        
        assertEquals(0, tabellaUtenti.getItems().size(), 
            "La tabella dovrebbe essere vuota dopo l'eliminazione");
        assertNull(biblioteca.cercaUtentePerMatricola(matricolaUnica));
    }
    
    @Test
    @DisplayName("Test: Annulla eliminazione utente")
    public void testAnnullaEliminazione() throws Exception {
        String matricolaUnica = "MAT" + System.currentTimeMillis();
        Utente utente = new Utente(matricolaUnica, "Mario", "Rossi", "m.rossi@test.it");
        
        interact(() -> {
            try {
                biblioteca.aggiungiUtente(utente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        clickOn("Mostra Tutti");
        sleep(300);
        
        // Seleziona utente
        interact(() -> {
            tabellaUtenti.getSelectionModel().select(0);
        });
        sleep(200);
        
        clickOn("Elimina");
        sleep(300);
        
        clickOn("Annulla");
        sleep(300);
        
        assertEquals(1, tabellaUtenti.getItems().size());
        assertNotNull(biblioteca.cercaUtentePerMatricola(matricolaUnica));
    }
    
    @Test
    @DisplayName("Test: Visualizzazione numero prestiti attivi")
    public void testVisualizzazionePrestitiAttivi() throws Exception {
        String matricolaUnica = "MAT" + System.currentTimeMillis();
        Utente utente = new Utente(matricolaUnica, "Mario", "Rossi", "m.rossi@test.it");
        
        interact(() -> {
            try {
                biblioteca.aggiungiUtente(utente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        clickOn("Mostra Tutti");
        sleep(300);
        
        assertTrue(lookup("0/3").tryQuery().isPresent(), 
            "Dovrebbe essere visualizzato il numero di prestiti (0/3)");
    }
    
    @Test
    @DisplayName("Test: Matricola non modificabile in modifica")
    public void testMatricolaNonModificabile() throws Exception {
        String matricolaUnica = "MAT" + System.currentTimeMillis();
        Utente utente = new Utente(matricolaUnica, "Mario", "Rossi", "m.rossi@test.it");
        
        interact(() -> {
            try {
                biblioteca.aggiungiUtente(utente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        clickOn("Mostra Tutti");
        sleep(300);
        
        // Seleziona utente
        interact(() -> {
            tabellaUtenti.getSelectionModel().select(0);
        });
        sleep(200);
        
        clickOn("Modifica");
        sleep(300);
        
        assertTrue(lookup("#fieldMatricola").queryAs(javafx.scene.control.TextField.class).isDisabled(), 
            "Il campo matricola dovrebbe essere disabilitato in modalità modifica");
        
        clickOn("✗ Annulla");
    }
}