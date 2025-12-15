package it.uni.biblioteca.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Simona
 * Test TestFX per MainController 
 */
public class MainControllerTest extends ApplicationTest {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/MainView.fxml")
        );
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }
    
    @AfterAll
    public static void cleanup() throws Exception {
        FxToolkit.hideStage();
    }
    
    @Test
    @DisplayName("Test: Presenza sidebar con navigazione")
    public void testPresenzaSidebar() {
        assertTrue(lookup("Biblioteca").tryQuery().isPresent(), 
            "Il titolo della sidebar dovrebbe essere presente");
        
        assertTrue(lookup("Universitaria").tryQuery().isPresent(), 
            "Il sottotitolo della sidebar dovrebbe essere presente");
    }
    
    @Test
    @DisplayName("Test: Presenza bottoni navigazione")
    public void testBottoniNavigazione() {
        assertTrue(lookup("🏠 Home").tryQuery().isPresent() || 
                   lookup("Home").tryQuery().isPresent(), 
            "Il bottone Home dovrebbe essere presente");
        
        assertTrue(lookup("📖 Gestione Libri").tryQuery().isPresent() || 
                   lookup("Gestione Libri").tryQuery().isPresent(), 
            "Il bottone Gestione Libri dovrebbe essere presente");
        
        assertTrue(lookup("👥 Gestione Utenti").tryQuery().isPresent() || 
                   lookup("Gestione Utenti").tryQuery().isPresent(), 
            "Il bottone Gestione Utenti dovrebbe essere presente");
        
        assertTrue(lookup("📋 Gestione Prestiti").tryQuery().isPresent() || 
                   lookup("Gestione Prestiti").tryQuery().isPresent(), 
            "Il bottone Gestione Prestiti dovrebbe essere presente");
    }
    
    @Test
    @DisplayName("Test: Presenza bottoni secondari")
    public void testBottoniSecondari() {
        boolean infoPresente = lookup("ℹ️ Info").tryQuery().isPresent() || 
                               lookup("Info").tryQuery().isPresent() ||
                               lookup("ℹ Info").tryQuery().isPresent();
        
        assertTrue(infoPresente, 
            "Il bottone Info dovrebbe essere presente");
        
        assertTrue(lookup("🚪 Esci").tryQuery().isPresent() || 
                   lookup("Esci").tryQuery().isPresent(), 
            "Il bottone Esci dovrebbe essere presente");
    }
    
    @Test
    @DisplayName("Test: Caricamento Home di default")
    public void testCaricamentoHomeDefault() {
        sleep(500);
        
        assertTrue(
            lookup("Sistema Gestione Biblioteca Universitaria").tryQuery().isPresent() ||
            lookup("GESTIONE LIBRI").tryQuery().isPresent() ||
            lookup("Gestione Biblioteca").tryQuery().isPresent(),
            "La Home dovrebbe essere caricata all'avvio"
        );
    }
    
    @Test
    @DisplayName("Test: Navigazione a Gestione Libri")
    public void testNavigazioneLibri() {
        if (lookup("📖 Gestione Libri").tryQuery().isPresent()) {
            clickOn("📖 Gestione Libri");
        } else {
            clickOn("Gestione Libri");
        }
        sleep(500);
        
        boolean vistaCaricata = lookup("Ricerca Libri").tryQuery().isPresent() ||
                                lookup("🔍 Ricerca Libri").tryQuery().isPresent() ||
                                lookup("Inserisci Libro").tryQuery().isPresent() ||
                                lookup("➕ Inserisci Libro").tryQuery().isPresent();
        
        assertTrue(vistaCaricata,
            "La vista Gestione Libri dovrebbe essere caricata"
        );
    }
    
    @Test
    @DisplayName("Test: Navigazione a Gestione Utenti")
    public void testNavigazioneUtenti() {
        if (lookup("👥 Gestione Utenti").tryQuery().isPresent()) {
            clickOn("👥 Gestione Utenti");
        } else {
            clickOn("Gestione Utenti");
        }
        sleep(500);
        
        assertTrue(lookup("Ricerca Utenti").tryQuery().isPresent() ||
                   lookup("Inserisci Utente").tryQuery().isPresent(),
            "La vista Gestione Utenti dovrebbe essere caricata"
        );
    }
    
    @Test
    @DisplayName("Test: Navigazione a Gestione Prestiti")
    public void testNavigazionePrestiti() {
        if (lookup("📋 Gestione Prestiti").tryQuery().isPresent()) {
            clickOn("📋 Gestione Prestiti");
        } else {
            clickOn("Gestione Prestiti");
        }
        sleep(500);
        
        assertTrue(lookup("PRESTITI ATTIVI").tryQuery().isPresent() ||
                   lookup("Nuovo Prestito").tryQuery().isPresent() ||
                   lookup("Registra Nuovo Prestito").tryQuery().isPresent(),
            "La vista Gestione Prestiti dovrebbe essere caricata"
        );
    }
    
    @Test
    @DisplayName("Test: Ritorno alla Home")
    public void testRitornoHome() {
        if (lookup("📖 Gestione Libri").tryQuery().isPresent()) {
            clickOn("📖 Gestione Libri");
        } else {
            clickOn("Gestione Libri");
        }
        sleep(300);
        
        if (lookup("🏠 Home").tryQuery().isPresent()) {
            clickOn("🏠 Home");
        } else {
            clickOn("Home");
        }
        sleep(500);
        
        assertTrue(
            lookup("Sistema Gestione Biblioteca Universitaria").tryQuery().isPresent() ||
            lookup("GESTIONE LIBRI").tryQuery().isPresent(),
            "Dovrebbe tornare alla vista Home"
        );
    }
    
    @Test
    @DisplayName("Test: Click su Info mostra dialog")
    public void testDialogInfo() {
        // Trova e click sul bottone Info
        if (lookup("ℹ️ Info").tryQuery().isPresent()) {
            clickOn("ℹ️ Info");
        } else if (lookup("ℹ Info").tryQuery().isPresent()) {
            clickOn("ℹ Info");
        } else if (lookup("Info").tryQuery().isPresent()) {
            clickOn("Info");
        }
        sleep(500);
        
        // Verifica apertura dialog con timeout più lungo
        boolean dialogPresente = lookup("Informazioni").tryQuery().isPresent() ||
                   lookup("Sistema Gestione Biblioteca").tryQuery().isPresent() ||
                   lookup("Versione").tryQuery().isPresent() ||
                   lookup(".dialog-pane").tryQuery().isPresent();
        
        assertTrue(dialogPresente,
            "Il dialog informazioni dovrebbe apparire"
        );
        
        // Chiudi il dialog se aperto
        if (lookup("OK").tryQuery().isPresent()) {
            clickOn("OK");
        }
    }
    
    @Test
    @DisplayName("Test: Status bar presente")
    public void testStatusBar() {
        assertTrue(
            lookup("Sistema Gestione Biblioteca Universitaria - Pronto").tryQuery().isPresent() ||
            lookup("v1.0").tryQuery().isPresent() ||
            lookup("v2.0").tryQuery().isPresent() ||
            lookup("Pronto").tryQuery().isPresent(),
            "La status bar dovrebbe essere presente"
        );
    }
    
    @Test
    @DisplayName("Test: Navigazione sequenziale tra sezioni")
    public void testNavigazioneSequenziale() {
        // Home -> Libri (cerca il testo completo o parziale)
        if (lookup("📖 Gestione Libri").tryQuery().isPresent()) {
            clickOn("📖 Gestione Libri");
        } else {
            clickOn("Gestione Libri");
        }
        sleep(1000);
        assertTrue(lookup("🔍 Ricerca Libri").tryQuery().isPresent() ||
                   lookup("➕ Inserisci Libro").tryQuery().isPresent());
        
        // Libri -> Utenti
        if (lookup("👥 Gestione Utenti").tryQuery().isPresent()) {
            clickOn("👥 Gestione Utenti");
        } else {
            clickOn("Gestione Utenti");
        }
        sleep(1000);
        assertTrue(lookup("🔍 Ricerca Utenti").tryQuery().isPresent());
        
        // Utenti -> Prestiti
        if (lookup("📋 Gestione Prestiti").tryQuery().isPresent()) {
            clickOn("📋 Gestione Prestiti");
        } else {
            clickOn("Gestione Prestiti");
        }
        sleep(1000);
        assertTrue(lookup("PRESTITI ATTIVI").tryQuery().isPresent() ||
                   lookup("Nuovo Prestito").tryQuery().isPresent());
        
        // Prestiti -> Home
        if (lookup("🏠 Home").tryQuery().isPresent()) {
            clickOn("🏠 Home");
        } else {
            clickOn("Home");
        }
        sleep(1000);
        assertTrue(lookup("GESTIONE LIBRI").tryQuery().isPresent() ||
                   lookup("Sistema Gestione Biblioteca").tryQuery().isPresent());
    }
    
    @Test
    @DisplayName("Test: Icona biblioteca nella sidebar")
    public void testIconaBiblioteca() {
        assertTrue(lookup("📚").tryQuery().isPresent(), 
            "L'icona della biblioteca dovrebbe essere presente nella sidebar");
    }
    
    @Test
    @DisplayName("Test: Sezioni NAVIGAZIONE e ALTRO presenti")
    public void testSezioniSidebar() {
        assertTrue(lookup("NAVIGAZIONE").tryQuery().isPresent(), 
            "La sezione NAVIGAZIONE dovrebbe essere presente");
        
        assertTrue(lookup("ALTRO").tryQuery().isPresent(), 
            "La sezione ALTRO dovrebbe essere presente");
    }
}