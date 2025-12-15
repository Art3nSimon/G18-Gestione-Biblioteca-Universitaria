package it.uni.biblioteca.controller;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.model.Prestito;
import it.uni.biblioteca.model.Utente;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Simona
 * Test TestFX per PrestitoController 
 */
public class PrestitoControllerTest extends ApplicationTest {
    
    private Biblioteca biblioteca;
    private TableView<Prestito> tabellaPrestiti;
    private Utente utenteTest;
    private Libro libroTest;
    
    @Override
    public void start(Stage stage) throws Exception {
        // Reset Singleton
        resetBibliotecaSingleton();
        biblioteca = Biblioteca.getInstance();
        
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/PrestitoView.fxml")
        );
        Parent root = loader.load();
        
        PrestitoController controller = loader.getController();
        tabellaPrestiti = controller.tabellaPrestiti;
        
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        // Pulisci dati
        interact(() -> {
            // Chiudi tutti i prestiti attivi
            biblioteca.getPrestitiAttivi().forEach(p -> {
                try {
                    if (p.isAttivo()) {
                        biblioteca.registraRestituzione(p);
                    }
                } catch (Exception e) {
                    // Ignora
                }
            });
            
            biblioteca.getTuttiLibri().clear();
            biblioteca.getTuttiUtenti().clear();
            biblioteca.getPrestitiAttivi().clear();
        });
        
        sleep(200);
        
        // Crea dati di test con ID univoci
        String timestamp = String.valueOf(System.currentTimeMillis());
        utenteTest = new Utente(timestamp, "Mario", "Rossi", "m.rossi@test.it");
        
        interact(() -> {
            try {
                biblioteca.aggiungiUtente(utenteTest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        sleep(10);
        
        String isbnUnico = "ISBN" + System.currentTimeMillis();
        libroTest = new Libro(isbnUnico, "Il Signore degli Anelli", 2000, 3);
        libroTest.aggiungiAutore(new Libro.Autore("John", "Tolkien"));
        
        interact(() -> {
            try {
                biblioteca.aggiungiLibro(libroTest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Aggiorna UI
        clickOn("Aggiorna");
        sleep(300);
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
    @DisplayName("Test: Tabella prestiti inizialmente vuota")
    public void testTabellaPrestiti() {
        assertEquals(0, tabellaPrestiti.getItems().size(), 
            "La tabella prestiti dovrebbe essere vuota all'inizio");
    }
    
    @Test
    @DisplayName("Test: Bottoni presenti")
    public void testBottoniPresenti() {
        assertTrue(lookup("Nuovo Prestito").tryQuery().isPresent(), 
            "Il bottone Nuovo Prestito dovrebbe essere presente");
        
        assertTrue(lookup("Registra Restituzione").tryQuery().isPresent(), 
            "Il bottone Registra Restituzione dovrebbe essere presente");
        
        assertTrue(lookup("Aggiorna").tryQuery().isPresent(), 
            "Il bottone Aggiorna dovrebbe essere presente");
    }
    
    @Test
    @DisplayName("Test: Visualizzazione statistiche")
    public void testStatistichePrestiti() {
        // Cerca le label con varianti possibili
        boolean totalePresente = lookup("Totale Prestiti Attivi").tryQuery().isPresent() ||
                                 lookup("Totale Prestiti Attivi: 0").tryQuery().isPresent() ||
                                 lookup("Totale").tryQuery().isPresent();
        
        assertTrue(totalePresente, 
            "Dovrebbe essere visualizzato il totale dei prestiti attivi");
        
        boolean ritardiPresente = lookup("Prestiti in Ritardo").tryQuery().isPresent() ||
                                  lookup("Prestiti in Ritardo: 0").tryQuery().isPresent() ||
                                  lookup("Ritardo").tryQuery().isPresent();
        
        assertTrue(ritardiPresente, 
            "Dovrebbe essere visualizzato il numero di prestiti in ritardo");
    }
    
    @Test
    @DisplayName("Test: Restituzione senza selezione mostra errore")
    public void testRestituzioneSenzaSelezione() {
        clickOn("Registra Restituzione");
        sleep(300);
        
        assertTrue(lookup(".alert").tryQuery().isPresent(), 
            "Dovrebbe apparire un errore se nessun prestito è selezionato");
        
        clickOn("OK");
    }
    
    @Test
    @DisplayName("Test: Apertura dialog nuovo prestito")
    public void testAperturaDialog() {
        clickOn("Nuovo Prestito");
        sleep(500);
        
        assertTrue(lookup("Registra Nuovo Prestito").tryQuery().isPresent() ||
                   lookup("Inserisci i dati del prestito").tryQuery().isPresent(),
            "Il dialog dovrebbe essere aperto");
        
        clickOn("Annulla");
    }
    
    @Test
    @DisplayName("Test: Aggiornamento tabella con bottone")
    public void testAggiornaTabella() throws Exception {
        // Crea prestito
        interact(() -> {
            try {
                biblioteca.registraPrestito(
                    utenteTest.getMatricola(), 
                    libroTest.getIsbn(), 
                    LocalDate.now().plusDays(14)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        sleep(200);
        
        // Aggiorna UI
        clickOn("Aggiorna");
        sleep(500);
        
        assertEquals(1, tabellaPrestiti.getItems().size(), 
            "La tabella dovrebbe mostrare 1 prestito dopo l'aggiornamento");
    }
    
    @Test
    @DisplayName("Test: Registrazione restituzione")
    public void testRegistrazioneRestituzione() throws Exception {
        // Crea prestito
        interact(() -> {
            try {
                biblioteca.registraPrestito(
                    utenteTest.getMatricola(), 
                    libroTest.getIsbn(), 
                    LocalDate.now().plusDays(14)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Aggiorna tabella
        clickOn("Aggiorna");
        sleep(500);
        
        assertEquals(1, tabellaPrestiti.getItems().size(), 
            "Dovrebbe esserci 1 prestito");
        
        // Seleziona il prestito (click sulla riga)
        interact(() -> {
            tabellaPrestiti.getSelectionModel().select(0);
        });
        sleep(200);
        
        // Registra restituzione
        clickOn("Registra Restituzione");
        sleep(300);
        
        // Conferma
        clickOn("OK");
        sleep(500);
        
        // Riaggiorna per vedere i cambiamenti
        clickOn("Aggiorna");
        sleep(300);
        
        assertEquals(0, tabellaPrestiti.getItems().size(), 
            "Non dovrebbero esserci prestiti attivi dopo la restituzione");
        
        assertEquals(3, libroTest.getNumeroCopieDisponibili(),
            "Il libro dovrebbe avere tutte le copie disponibili");
    }
}