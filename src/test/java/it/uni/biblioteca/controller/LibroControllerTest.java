/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.controller;
import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Libro;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Simona
 */
/**
 * Test TestFX per LibroController
 * Testa le funzionalità di gestione libri dell'interfaccia
 */

public class LibroControllerTest extends ApplicationTest {
    
    private Biblioteca biblioteca;
    private TableView<Libro> tabellaLibri;
    
    @Override
    public void start(Stage stage) throws Exception {
        // Inizializza la biblioteca
        biblioteca = Biblioteca.getInstance();
        
        // Carica l'FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/LibroView.fxml")
        );
        Parent root = loader.load();
        
        // Ottieni la tabella dal controller
        LibroController controller = loader.getController();
        tabellaLibri = controller.tabellaLibri;
        
        // Setup scene e stage
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }
    
    @BeforeEach
    public void setUp() {
        // Pulisci i dati prima di ogni test
        biblioteca.getTuttiLibri().clear();
        // Forza il refresh della tabella
        tabellaLibri.getItems().clear();
    }
    
    @AfterAll
    public static void cleanup() throws Exception {
        FxToolkit.hideStage();
    }
    
    @Test
    @DisplayName("Test: Visualizzazione iniziale tabella vuota")
    public void testTabellaVuota() {
        // Verifica che la tabella sia inizialmente vuota
        assertEquals(0, tabellaLibri.getItems().size(), 
            "La tabella dovrebbe essere vuota all'inizio");
    }
    
    @Test
    @DisplayName("Test: Inserimento nuovo libro tramite dialog")
    public void testInserimentoLibro() {
        //Aggiungi libro con ISBN univoco per ogni test 
        String isbnUnico = "978" + System.currentTimeMillis();
        // Click sul bottone "Inserisci Libro"
        clickOn("➕ Inserisci Libro");
        
        // Attendi che il dialog si apra
        sleep(300);
        
        // Compila i campi
        clickOn("#fieldTitolo").write("Il Signore degli Anelli");
        clickOn("#fieldIsbn").write("9788804123456");
        clickOn("#fieldAnno").write("2000");
        clickOn("#fieldCopie").write("5");
        
        // Aggiungi un autore
        clickOn("#fieldNomeAutore").write("John");
        clickOn("#fieldCognomeAutore").write("Tolkien");
        clickOn("➕ Aggiungi");
        
        sleep(200);
        
        // Conferma
        clickOn("✓ Conferma");
        
        sleep(300);
        
        // Verifica che il libro sia stato aggiunto
        assertEquals(1, tabellaLibri.getItems().size(), 
            "Dovrebbe esserci 1 libro nella tabella");
        
        Libro libroAggiunto = tabellaLibri.getItems().get(0);
        assertEquals("Il Signore degli Anelli", libroAggiunto.getTitolo());
        assertEquals("9788804123456", libroAggiunto.getIsbn());
    }
    
    @Test
    @DisplayName("Test: Ricerca libro per titolo")
    public void testRicercaPerTitolo() throws Exception {
        // Aggiungi alcuni libri di test
        Libro libro1 = new Libro("111", "Java Programming", 2020, 3);
        libro1.aggiungiAutore(new Libro.Autore("John", "Doe"));
        biblioteca.aggiungiLibro(libro1);
        
        Libro libro2 = new Libro("222", "Python Basics", 2021, 5);
        libro2.aggiungiAutore(new Libro.Autore("Jane", "Smith"));
        biblioteca.aggiungiLibro(libro2);
        
        // Clicca "Mostra Tutti" per aggiornare la tabella
        clickOn("↻ Mostra Tutti");
        sleep(200);
        
        // Seleziona criterio di ricerca "TITOLO"
        clickOn("#comboCriterio").clickOn("TITOLO");
        
        // Inserisci termine di ricerca
        clickOn("#fieldRicerca").write("Java");
        
        // Click su Cerca
        clickOn("Cerca");
        sleep(300);
        
        // Verifica che sia stato trovato solo 1 libro
        assertEquals(1, tabellaLibri.getItems().size(), 
            "Dovrebbe essere trovato 1 libro con 'Java' nel titolo");
        assertEquals("Java Programming", tabellaLibri.getItems().get(0).getTitolo());
    }
    
    @Test
    @DisplayName("Test: Eliminazione libro senza selezione mostra errore")
    public void testEliminazioneSenzaSelezione() {
        // Click su elimina senza selezionare nulla
        clickOn("Elimina");
        sleep(300);
        
        // Verifica che appaia un dialog di errore
        assertTrue(lookup(".alert").tryQuery().isPresent(), 
            "Dovrebbe apparire un alert di errore");
        
        // Chiudi l'alert
        clickOn("OK");
    }
    
    @Test
    @DisplayName("Test: Modifica libro esistente")
    public void testModificaLibro() throws Exception {
        // Aggiungi un libro di test
        Libro libro = new Libro("123", "Titolo Originale", 2020, 3);
        libro.aggiungiAutore(new Libro.Autore("Mario", "Rossi"));
        biblioteca.aggiungiLibro(libro);
        
        clickOn("↻ Mostra Tutti");
        sleep(200);
        
        // Seleziona il libro nella tabella
        clickOn("Titolo Originale");
        
        // Click su Modifica
        clickOn("Modifica");
        sleep(300);
        
        // Modifica il titolo
        doubleClickOn("#fieldTitolo").eraseText(16).write("Titolo Modificato");
        
        // Conferma
        clickOn("✓ Conferma");
        sleep(300);
        
        // Verifica che il titolo sia stato modificato
        Libro libroModificato = biblioteca.cercaLibroPerIsbn("123");
        assertEquals("Titolo Modificato", libroModificato.getTitolo());
    }
    
    @Test
    @DisplayName("Test: Validazione campi obbligatori nel dialog")
    public void testValidazioneCampi() {
        // Apri dialog inserimento
        clickOn("➕ Inserisci Libro");
        sleep(300);
        
        // Prova a confermare senza compilare i campi
        clickOn("✓ Conferma");
        sleep(300);
        
        // Verifica che appaia un errore
        assertTrue(lookup(".alert").tryQuery().isPresent(), 
            "Dovrebbe apparire un alert di errore per campi obbligatori");
        
        clickOn("OK");
    }
    
    @Test
    @DisplayName("Test: Mostra tutti i libri dopo una ricerca")
    public void testMostraTuttiDopoRicerca() throws Exception {
        // Aggiungi libri
        Libro l1 = new Libro("111", "Java", 2020, 3);
        l1.aggiungiAutore(new Libro.Autore("A", "B"));
        biblioteca.aggiungiLibro(l1);
        
        Libro l2 = new Libro("222", "Python", 2021, 5);
        l2.aggiungiAutore(new Libro.Autore("C", "D"));
        biblioteca.aggiungiLibro(l2);
        
        // Ricerca che restituisce solo 1 risultato
        clickOn("↻ Mostra Tutti");
        sleep(200);
        
        clickOn("#fieldRicerca").write("Java");
        clickOn("Cerca");
        sleep(200);
        
        assertEquals(1, tabellaLibri.getItems().size());
        
        // Click su "Mostra Tutti"
        clickOn("↻ Mostra Tutti");
        sleep(200);
        
        // Verifica che ora siano visibili tutti i libri
        assertEquals(2, tabellaLibri.getItems().size(), 
            "Dovrebbero essere visibili tutti i libri");
    }
    
    @Test
    @DisplayName("Test: Annullamento dialog inserimento")
    public void testAnnullaDialog() {
        int libroIniziali = tabellaLibri.getItems().size();
        
        // Apri dialog
        clickOn("➕ Inserisci Libro");
        sleep(300);
        
        // Compila alcuni campi
        clickOn("#fieldTitolo").write("Test");
        
        // Annulla
        clickOn("✗ Annulla");
        sleep(300);
        
        // Verifica che nessun libro sia stato aggiunto
        assertEquals(libroIniziali, tabellaLibri.getItems().size(), 
            "Nessun libro dovrebbe essere aggiunto dopo l'annullamento");
    }
}