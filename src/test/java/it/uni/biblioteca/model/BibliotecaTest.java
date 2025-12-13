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
    
    @BeforeEach
    public void setUp() {
        biblioteca = Biblioteca.getInstance();
        pulisciBiblioteca();
        libro1 = new Libro("ISBN001", "Clean Code", 2008, 3);
        libro1.aggiungiAutore(new Autore("Robert", "Martin"));
        utente1 = new Utente("MAT001", "Mario", "Rossi", "mario@unisa.it");
    }
    
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
    
    //Test Singleton
    @Test
    public void testGetInstance() {
        Biblioteca b1 = Biblioteca.getInstance();
        Biblioteca b2 = Biblioteca.getInstance();
        assertSame(b1, b2);
    }
    
    //Test Aggiungi Libro
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
    
    //Test Modifica Libro
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
    
    //Test Elimina Libro
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
    
    //Test Cerca Libri
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
    
    //Test Aggiungi Utente
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
    
    //Test Modifica Utente
    @Test
    public void testModificaUtente() throws Exception {
        biblioteca.aggiungiUtente(utente1);
        utente1.setEmail("nuovo@unisa.it");
        biblioteca.modificaUtente(utente1);
        Utente trovato = biblioteca.cercaUtentePerMatricola("MAT001");
        assertEquals("nuovo@unisa.it", trovato.getEmail());
    }
    
    //Test Elimina Utente
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
    
    //Test Cerca Utente
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
    
    //Test Registra Prestito
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
    
    //Test Registra e Restituzione
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
    
    //Test GetPrestitiAttivi
    @Test
    public void testGetPrestitiAttivi() throws Exception {
        biblioteca.aggiungiLibro(libro1);
        biblioteca.aggiungiUtente(utente1);
        biblioteca.registraPrestito("MAT001", "ISBN001", LocalDate.now().plusDays(14));
        List<Prestito> prestiti = biblioteca.getPrestitiAttivi();
        assertTrue(prestiti.size() > 0);
    }
}