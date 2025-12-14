/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.model.Libro.Autore;
import it.uni.biblioteca.model.Utente;
import it.uni.biblioteca.model.Prestito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Matteo
 */
public class BibliotecaTest {
    
    private Biblioteca biblioteca;
    private Libro libro1;
    private Utente utente1;
    
    /** Configurazione iniziale per ogni test */
    @BeforeEach
    public void setUp() {
        try {
            java.lang.reflect.Field instance = Biblioteca.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        biblioteca = Biblioteca.getInstance();
        pulisciBiblioteca();
        libro1 = new Libro("ISBN001", "Clean Code", 2008, 3);
        libro1.aggiungiAutore(new Autore("Robert", "Martin"));
        utente1 = new Utente("MAT001", "Mario", "Rossi", "mario@unisa.it");
    }
    
    /** Pulisce i dati della biblioteca */
    private void pulisciBiblioteca() {
        List<Prestito> prestitiAttivi = biblioteca.getPrestitiAttivi();
        for (Prestito p : prestitiAttivi) {
            try {
                biblioteca.registraRestituzione(p);
            } catch (Exception e) {
            }
        }
        List<Utente> utenti = biblioteca.getTuttiUtenti();
        for (Utente u : utenti) {
            try {
                biblioteca.eliminaUtente(u.getMatricola());
            } catch (Exception e) {
            }
        }
        List<Libro> libri = biblioteca.getTuttiLibri();
        for (Libro l : libri) {
            try {
                biblioteca.eliminaLibro(l.getIsbn());
            } catch (Exception e) {
            }
        }
    }
    
    /** Test Singleton */
    @Test
    public void testGetInstance() {
        Biblioteca b1 = Biblioteca.getInstance();
        Biblioteca b2 = Biblioteca.getInstance();
        assertSame(b1, b2);
    }
    
    /** Test Aggiungi Libro */
    @Test
    public void testAggiungiLibroValido() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        Libro trovato = biblioteca.cercaLibroPerIsbn("ISBN001");
        assertNotNull(trovato);
    }
    
    @Test
    public void testAggiungiLibroTitoloVuoto() {
        Libro libro = new Libro("ISBN999", "", 2020, 1);
        libro.aggiungiAutore(new Autore("Test", "Test"));
        assertThrows(Exception.class, () -> {
            biblioteca.aggiungiLibro(libro);
        });
    }
    
    @Test
    public void testAggiungiLibroSenzaAutori() {
        Libro libro = new Libro("ISBN999", "Test", 2020, 1);
        assertThrows(Exception.class, () -> {
            biblioteca.aggiungiLibro(libro);
        });
    }
    
    @Test
    public void testAggiungiLibroISBNDuplicato() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        
        Libro libro2 = new Libro("ISBN001", "Altro", 2020, 1);
        libro2.aggiungiAutore(new Autore("Test", "Test"));
        assertThrows(Exception.class, () -> {
            biblioteca.aggiungiLibro(libro2);
        });
    }
    
    /** Test Modifica Libro */
    @Test
    public void testModificaLibro() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        
        libro1.setTitolo("Clean Code - Nuovo");
        biblioteca.modificaLibro(libro1);
        Libro trovato = biblioteca.cercaLibroPerIsbn("ISBN001");
        assertEquals("Clean Code - Nuovo", trovato.getTitolo());
    }
    
    @Test
    public void testModificaLibroNonEsistente() {
        Libro libro = new Libro("ISBN999", "Test", 2020, 1);
        libro.aggiungiAutore(new Autore("Test", "Test"));
        assertThrows(Exception.class, () -> {
            biblioteca.modificaLibro(libro);
        });
    }
    
    /** Test Elimina Libro */
    @Test
    public void testEliminaLibro() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.eliminaLibro("ISBN001");
        Libro trovato = biblioteca.cercaLibroPerIsbn("ISBN001");
        assertNull(trovato);
    }
    
    @Test
    public void testEliminaLibroConPrestitoAttivo() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.aggiungiUtente(utente1);
        biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().plusDays(7));
        
        assertThrows(Exception.class, () -> {
            biblioteca.eliminaLibro("ISBN001");
        });
    }
    
    /** Test Cerca Libri */
    @Test
    public void testCercaLibriPerTitolo() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        List<Libro> risultati = biblioteca.cercaLibri("Clean", "TITOLO");
        assertTrue(risultati.size() > 0);
    }
    
    @Test
    public void testCercaLibriPerAutore() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        List<Libro> risultati = biblioteca.cercaLibri("Martin", "AUTORE");
        assertTrue(risultati.size() > 0);
    }
    
    @Test
    public void testCercaLibriPerISBN() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        List<Libro> risultati = biblioteca.cercaLibri("ISBN001", "ISBN");
        assertEquals(1, risultati.size());
    }
    
    @Test
    public void testCercaLibroPerIsbn() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        Libro trovato = biblioteca.cercaLibroPerIsbn("ISBN001");
        assertNotNull(trovato);
        assertEquals("Clean Code", trovato.getTitolo());
    }
    
    /** Test Aggiungi Utente */
    @Test
    public void testAggiungiUtenteValido() throws Exception {
        biblioteca.aggiungiUtente(utente1);
        Utente trovato = biblioteca.cercaUtentePerMatricola("MAT001");
        assertNotNull(trovato);
    }
    
    @Test
    public void testAggiungiUtenteNomeVuoto() {
        Utente utente = new Utente("MAT999", "", "Rossi", "test@unisa.it");
        assertThrows(Exception.class, () -> {
            biblioteca.aggiungiUtente(utente);
        });
    }
    
    @Test
    public void testAggiungiUtenteMatricolaDuplicata() throws Exception {
        biblioteca.aggiungiUtente(utente1);
        Utente utente2 = new Utente("MAT001", "Luigi", "Verdi", "luigi@unisa.it");
        assertThrows(Exception.class, () -> {
            biblioteca.aggiungiUtente(utente2);
        });
    }
    
    @Test
    public void testAggiungiUtenteEmailNonValida() {
        Utente utente = new Utente("MAT999", "Test", "Test", "emailsenzachiocciola");
        assertThrows(Exception.class, () -> {
            biblioteca.aggiungiUtente(utente);
        });
    }
    
    /** Test Modifica Utente */
    @Test
    public void testModificaUtente() throws Exception {
        biblioteca.aggiungiUtente(utente1);
        utente1.setEmail("nuovo@unisa.it");
        biblioteca.modificaUtente(utente1);
        Utente trovato = biblioteca.cercaUtentePerMatricola("MAT001");
        assertEquals("nuovo@unisa.it", trovato.getEmail());
    }
    
    /** Test Elimina Utente */
    @Test
    public void testEliminaUtente() throws Exception {
        biblioteca.aggiungiUtente(utente1);
        biblioteca.eliminaUtente("MAT001");
        Utente trovato = biblioteca.cercaUtentePerMatricola("MAT001");
        assertNull(trovato);
    }
    
    @Test
    public void testEliminaUtenteConPrestitoAttivo() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.aggiungiUtente(utente1);
        biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().plusDays(7));
        assertThrows(Exception.class, () -> {
            biblioteca.eliminaUtente("MAT001");
        });
    }
    
    /** Test Cerca Utente */
    @Test
    public void testCercaUtentiPerCognome() throws Exception {
        biblioteca.aggiungiUtente(utente1);
        List<Utente> risultati = biblioteca.cercaUtenti("Rossi", "COGNOME");
        assertTrue(risultati.size() > 0);
    }
    
    @Test
    public void testCercaUtentiPerMatricola() throws Exception {
        biblioteca.aggiungiUtente(utente1);
        List<Utente> risultati = biblioteca.cercaUtenti("MAT001", "MATRICOLA");
        assertEquals(1, risultati.size());
    }
    
    /** Test Registra Prestito */
    @Test
    public void testRegistraPrestitoValido() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.aggiungiUtente(utente1);
        biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().plusDays(14));
        
        assertEquals(2, libro1.getNumeroCopieDisponibili());
        assertEquals(1, utente1.getNumeroPrestitiAttivi());
    }
    
    @Test
    public void testRegistraPrestitoUtenteNonEsistente() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        assertThrows(Exception.class, () -> {
            biblioteca.registraPrestito("MAT999", "ISBN001", LocalDate.now().plusDays(14));
        });
    }
    
    @Test
    public void testRegistraPrestitoLibroNonEsistente() throws Exception {
        biblioteca.aggiungiUtente(utente1);
        assertThrows(Exception.class, () -> {
            biblioteca.registraPrestito("MAT001", "ISBN999", LocalDate.now().plusDays(14));
        });
    }
    
    @Test
    public void testRegistraPrestitoLibroNonDisponibile() throws Exception {
        Libro libro = new Libro("ISBN002", "Test", 2020, 0);
        libro.aggiungiAutore(new Autore("Test", "Test"));
        biblioteca.aggiungiLibro(libro);
        biblioteca.aggiungiUtente(utente1);
        assertThrows(Exception.class, () -> {
            biblioteca.registraPrestito("MAT001", "ISBN002", LocalDate.now().plusDays(14));
        });
    }
    
    @Test
    public void testRegistraPrestitoDataPassata() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.aggiungiUtente(utente1);
        
        assertThrows(Exception.class, () -> {
            biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().minusDays(1));
        });
    }
    
    /** Test Registra e Restituzione */
    @Test
    public void testRegistraRestituzione() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.aggiungiUtente(utente1);
        biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().plusDays(14));
        List<Prestito> prestiti = biblioteca.getPrestitiAttivi();
        Prestito prestito = prestiti.get(prestiti.size() - 1);
        biblioteca.registraRestituzione(prestito);
        assertEquals(3, libro1.getNumeroCopieDisponibili());
        assertEquals(0, utente1.getNumeroPrestitiAttivi());
    }
    
    @Test
    public void testRegistraRestituzionePrestitoChiuso() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.aggiungiUtente(utente1);
        biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().plusDays(14));
        List<Prestito> prestiti = biblioteca.getPrestitiAttivi();
        Prestito prestito = prestiti.get(prestiti.size() - 1);
        biblioteca.registraRestituzione(prestito);
        assertThrows(Exception.class, () -> {
            biblioteca.registraRestituzione(prestito);
        });
    }
    
    /** Test GetPrestitiAttivi */
    @Test
    public void testGetPrestitiAttivi() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.aggiungiUtente(utente1);
        biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().plusDays(14));
        List<Prestito> prestiti = biblioteca.getPrestitiAttivi();
        assertTrue(prestiti.size() > 0);
    }
    
    /** Test per coprire il caso in cui modificaUtente non trova l'utente */
    @Test
    public void testModificaUtenteNonEsistente() {
        Utente utente = new Utente("MAT999", "Test", "Test", "test@unisa.it");
        assertThrows(Exception.class, () -> {
            biblioteca.modificaUtente(utente);
        });
    }

    /** Test ordinamento libri vuoto */
    @Test
    public void testGetTuttiLibriVuoto() {
        List<Libro> libri = biblioteca.getTuttiLibri();
        assertEquals(0, libri.size());
    }

    /** Test ordinamento utenti vuoto */
    @Test
    public void testGetTuttiUtentiVuoto() {
        List<Utente> utenti = biblioteca.getTuttiUtenti();
        assertEquals(0, utenti.size());
    }

    /** Test limite prestiti raggiunto */
    @Test
    public void testRegistraPrestitoLimiteRaggiunto() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        Libro libro2 = new Libro("ISBN002", "Test2", 2020, 3);
        libro2.aggiungiAutore(new Autore("Test", "Test"));
        biblioteca.aggiungiLibro(libro2);
        Libro libro3 = new Libro("ISBN003", "Test3", 2020, 3);
        libro3.aggiungiAutore(new Autore("Test", "Test"));
        biblioteca.aggiungiLibro(libro3);
        Libro libro4 = new Libro("ISBN004", "Test4", 2020, 3);
        libro4.aggiungiAutore(new Autore("Test", "Test"));
        biblioteca.aggiungiLibro(libro4);
        biblioteca.aggiungiUtente(utente1);
        biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().plusDays(7));
        Thread.sleep(2);
        biblioteca.registraPrestito("MAT001", "ISBN002", LocalDate.now().plusDays(7));
        Thread.sleep(2);
        biblioteca.registraPrestito("MAT001", "ISBN003", LocalDate.now().plusDays(7));
        assertTrue(utente1.haRaggiuntoLimite());
        assertThrows(Exception.class, () -> {
            biblioteca.registraPrestito("MAT001", "ISBN004", LocalDate.now().plusDays(7));
        });
    }

    /** Test per testare il salvataggio dati */
    @Test
    public void testSalvaDati() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        assertNotNull(biblioteca.cercaLibroPerIsbn("ISBN001"));
    }

    /** Test caricamento dati */
    @Test
    public void testCaricaDati() {
        biblioteca.caricaDati();
        assertNotNull(biblioteca);
    }
    
    /** Test Performance */
    @Test
    public void testPerformanceAggiungi100Libri() throws Exception {
        long startTime = System.currentTimeMillis();

        // Aggiungi 100 libri
        for (int i = 0; i < 100; i++) {
            Libro libro = new Libro("ISBN" + i, "Libro Test " + i, 2020 + (i % 5), 5);
            libro.aggiungiAutore(new Autore("Nome" + i, "Cognome" + i));
            biblioteca.aggiungiLibro(libro);
        }

        long endTimeAggiungi = System.currentTimeMillis();
        long tempoAggiungi = endTimeAggiungi - startTime;

        // Verifica che tutti i libri siano stati aggiunti
        List<Libro> tuttiLibri = biblioteca.getTuttiLibri();
        assertEquals(100, tuttiLibri.size());

        long endTimeRecupero = System.currentTimeMillis();
        long tempoRecupero = endTimeRecupero - endTimeAggiungi;

        // Test di ricerca per ISBN
        long startRicerca = System.currentTimeMillis();
        Libro trovato = biblioteca.cercaLibroPerIsbn("ISBN50");
        long tempoRicerca = System.currentTimeMillis() - startRicerca;

        assertNotNull(trovato);
        assertEquals("Libro Test 50", trovato.getTitolo());

        // Test di ricerca per titolo
        long startRicercaTitolo = System.currentTimeMillis();
        List<Libro> risultati = biblioteca.cercaLibri("Test 5", "TITOLO");
        long tempoRicercaTitolo = System.currentTimeMillis() - startRicercaTitolo;

        assertTrue(risultati.size() > 0);

        // Stampa i risultati delle performance
        System.out.println("\n=== TEST PERFORMANCE 100 LIBRI ===");
        System.out.println("Tempo aggiunta 100 libri: " + tempoAggiungi + " ms");
        System.out.println("Tempo recupero tutti i libri (ordinati): " + tempoRecupero + " ms");
        System.out.println("Tempo ricerca per ISBN: " + tempoRicerca + " ms");
        System.out.println("Tempo ricerca per titolo: " + tempoRicercaTitolo + " ms");
        System.out.println("Risultati ricerca titolo: " + risultati.size() + " libri trovati");
        System.out.println("==================================\n");

        // Verifica che le operazioni siano ragionevolmente veloci
        assertTrue(tempoAggiungi < 2000, "Aggiunta di 100 libri troppo lenta: " + tempoAggiungi + " ms");
        assertTrue(tempoRecupero < 100, "Recupero e ordinamento troppo lento: " + tempoRecupero + " ms");
        assertTrue(tempoRicerca < 50, "Ricerca per ISBN troppo lenta: " + tempoRicerca + " ms");
    }
    
    @Test
    public void testPerformanceAggiungi100Utenti() throws Exception {
        long startTime = System.currentTimeMillis();

        // Aggiungi 100 utenti
        for (int i = 0; i < 100; i++) {
            Utente utente = new Utente("MAT" + i, "Nome" + i, "Cognome" + i, "email" + i + "@unisa.it");
            biblioteca.aggiungiUtente(utente);
        }

        long endTimeAggiungi = System.currentTimeMillis();
        long tempoAggiungi = endTimeAggiungi - startTime;

        // Verifica che tutti gli utenti siano stati aggiunti
        List<Utente> tuttiUtenti = biblioteca.getTuttiUtenti();
        assertEquals(100, tuttiUtenti.size());

        long endTimeRecupero = System.currentTimeMillis();
        long tempoRecupero = endTimeRecupero - endTimeAggiungi;

        // Test di ricerca per matricola
        long startRicerca = System.currentTimeMillis();
        Utente trovato = biblioteca.cercaUtentePerMatricola("MAT50");
        long tempoRicerca = System.currentTimeMillis() - startRicerca;

        assertNotNull(trovato);
        assertEquals("Nome50", trovato.getNome());

        // Test di ricerca per cognome
        long startRicercaCognome = System.currentTimeMillis();
        List<Utente> risultati = biblioteca.cercaUtenti("Cognome5", "COGNOME");
        long tempoRicercaCognome = System.currentTimeMillis() - startRicercaCognome;

        assertTrue(risultati.size() > 0);

        // Stampa i risultati delle performance
        System.out.println("\n=== TEST PERFORMANCE 100 UTENTI ===");
        System.out.println("Tempo aggiunta 100 utenti: " + tempoAggiungi + " ms");
        System.out.println("Tempo recupero tutti gli utenti (ordinati): " + tempoRecupero + " ms");
        System.out.println("Tempo ricerca per matricola: " + tempoRicerca + " ms");
        System.out.println("Tempo ricerca per cognome: " + tempoRicercaCognome + " ms");
        System.out.println("Risultati ricerca cognome: " + risultati.size() + " utenti trovati");
        System.out.println("===================================\n");

        // Verifica che le operazioni siano ragionevolmente veloci
        assertTrue(tempoAggiungi < 2000, "Aggiunta di 100 utenti troppo lenta: " + tempoAggiungi + " ms");
        assertTrue(tempoRecupero < 100, "Recupero e ordinamento troppo lento: " + tempoRecupero + " ms");
        assertTrue(tempoRicerca < 50, "Ricerca per matricola troppo lenta: " + tempoRicerca + " ms");
    }
}