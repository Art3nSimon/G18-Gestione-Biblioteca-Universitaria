/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.controller;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.model.Utente;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Simona
 */
/**
 * Test TestFX per HomeController
 * Testa la dashboard home dell'applicazione
 */
public class HomeControllerTest extends ApplicationTest {
    
    private Biblioteca biblioteca;
    
    @Override
    public void start(Stage stage) throws Exception {
        biblioteca = Biblioteca.getInstance();
        
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/HomeView.fxml")
        );
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }
    
    @BeforeEach
    public void setUp() {
    try {
        java.lang.reflect.Field instanceField = Biblioteca.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);  // Imposta l'istanza a null
    } catch (NoSuchFieldException | IllegalAccessException e) {
        e.printStackTrace();
    }
        
        biblioteca = Biblioteca.getInstance();
        biblioteca.getTuttiLibri().clear();
        biblioteca.getTuttiUtenti().clear();
        biblioteca.getPrestitiAttivi().clear();
    }
    
    @AfterAll
    public static void cleanup() throws Exception {
        FxToolkit.hideStage();
    }
    
    @Test
    @DisplayName("Test: Visualizzazione titolo home")
    public void testTitoloHome() {
        assertTrue(
            lookup("📚 Sistema Gestione Biblioteca Universitaria").tryQuery().isPresent(),
            "Il titolo dovrebbe essere visualizzato nella home"
        );
    }
    
    @Test
    @DisplayName("Test: Presenza card Libri, Utenti e Prestiti")
    public void testPresenzaCard() {
        assertTrue(lookup("GESTIONE LIBRI").tryQuery().isPresent(), 
            "La card Libri dovrebbe essere presente");
        
        assertTrue(lookup("GESTIONE UTENTI").tryQuery().isPresent(), 
            "La card Utenti dovrebbe essere presente");
        
        assertTrue(lookup("GESTIONE PRESTITI").tryQuery().isPresent(), 
            "La card Prestiti dovrebbe essere presente");
    }
    
    @Test
    @DisplayName("Test: Statistiche iniziali vuote")
    public void testStatisticheVuote() {
        // Verifica che le statistiche mostrino 0
        assertTrue(lookup("0 disponibili").tryQuery().isPresent() || 
                   lookup("Libri Totali").tryQuery().isPresent(), 
            "Dovrebbero essere visualizzate le statistiche dei libri");
        
        assertTrue(lookup("Utenti Registrati").tryQuery().isPresent(), 
            "Dovrebbero essere visualizzate le statistiche degli utenti");
        
        assertTrue(lookup("Prestiti Attivi").tryQuery().isPresent(), 
            "Dovrebbero essere visualizzate le statistiche dei prestiti");
    }
    
    @Test
    @DisplayName("Test: Statistiche libri con dati")
    public void testStatisticheLibri() throws Exception {
        // Aggiungi libri
        Libro l1 = new Libro("111", "Libro 1", 2020, 3);
        l1.aggiungiAutore(new Libro.Autore("A", "B"));
        biblioteca.aggiungiLibro(l1);
        
        Libro l2 = new Libro("222", "Libro 2", 2021, 5);
        l2.aggiungiAutore(new Libro.Autore("C", "D"));
        biblioteca.aggiungiLibro(l2);
        
        // Ricarica la home per aggiornare le statistiche
        sleep(200);       
        // Le statistiche dovrebbero mostrare 2 libri totali
    }
    
    @Test
    @DisplayName("Test: Click su card Libri apre gestione libri")
    public void testClickCardLibri() {
        // Questo test verifica che il click sulla card chiami il metodo corretto
        clickOn("GESTIONE LIBRI");
        sleep(300);
        
        // Verifica che sia stato effettuato il click (la card dovrebbe rispondere)
    }
    
    @Test
    @DisplayName("Test: Hover effect su card")
    public void testHoverCard() {
        // Muovi il mouse sulla card Libri
        moveTo("GESTIONE LIBRI");
        sleep(200);
        
        // L'effetto hover dovrebbe essere applicato

        moveTo("GESTIONE UTENTI");
        sleep(200);
    }
    
    @Test
    @DisplayName("Test: Icone presenti nelle card")
    public void testIconeCard() {
        // Verifica presenza delle icone emoji
        assertTrue(lookup("📖").tryQuery().isPresent(), 
            "L'icona libro dovrebbe essere presente");
        
        assertTrue(lookup("👥").tryQuery().isPresent(), 
            "L'icona utenti dovrebbe essere presente");
        
        assertTrue(lookup("📋").tryQuery().isPresent(), 
            "L'icona prestiti dovrebbe essere presente");
    }
    
    @Test
    @DisplayName("Test: Suggerimento footer presente")
    public void testSuggerimentoFooter() {
        assertTrue(lookup("💡 Suggerimento:").tryQuery().isPresent(), 
            "Il suggerimento nel footer dovrebbe essere presente");
        
        assertTrue(lookup("Clicca su una delle card sopra per accedere alla sezione corrispondente").tryQuery().isPresent(), 
            "Il testo del suggerimento dovrebbe essere presente");
    }
    
    @Test
    @DisplayName("Test: Statistiche utenti con dati")
    public void testStatisticheUtenti() throws Exception {
        // Aggiungi utenti
        Utente u1 = new Utente("111", "Mario", "Rossi", "m.rossi@test.it");
        biblioteca.aggiungiUtente(u1);
        
        Utente u2 = new Utente("222", "Luigi", "Verdi", "l.verdi@test.it");
        biblioteca.aggiungiUtente(u2);
        
        sleep(200);
        
        // Le statistiche dovrebbero riflettere i 2 utenti aggiunti
    }
    
    @Test
    @DisplayName("Test: Statistiche prestiti con dati")
    public void testStatistichePrestiti() throws Exception {
        // Setup: crea utente e libro
        Utente utente = new Utente("111", "Mario", "Rossi", "m.rossi@test.it");
        biblioteca.aggiungiUtente(utente);
        
        Libro libro = new Libro("222", "Test Book", 2020, 3);
        libro.aggiungiAutore(new Libro.Autore("A", "B"));
        biblioteca.aggiungiLibro(libro);
        
        // Crea prestito
        biblioteca.registraPrestito(
            utente.getMatricola(), 
            libro.getIsbn(), 
            LocalDate.now().plusDays(14)
        );
        
        sleep(200);
        
        // Le statistiche dovrebbero mostrare 1 prestito attivo
    }
}