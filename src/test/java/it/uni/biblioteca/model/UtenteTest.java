/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import it.uni.biblioteca.model.Utente;
import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.model.Prestito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Matteo
 */
public class UtenteTest {
    
    private Utente utente;
    
    @BeforeEach
    public void setUp() {
        utente = new Utente("12345", "Mario", "Rossi", "mario@unisa.it");
    }
    
    //Test Costruttore Utente
    @Test
    public void testCostruttore() {
        assertEquals("12345", utente.getMatricola());
        assertEquals("Mario", utente.getNome());
        assertEquals("Rossi", utente.getCognome());
        assertEquals("mario@unisa.it", utente.getEmail());
    }
    
    //Test Get Utente
    @Test
    public void testGetNomeCognome() {
        assertEquals("Rossi Mario", utente.getNomeCognome());
    }
    
    @Test
    public void testToString() {
        assertEquals("Rossi Mario (12345)", utente.toString());
    }
    
    @Test
    public void testGetNumeroPrestitiAttiviIniziale() {
        assertEquals(0, utente.getNumeroPrestitiAttivi());
    }
    
    //Test Set Utente
    @Test
    public void testSetNome() {
        utente.setNome("Luigi");
        assertEquals("Luigi", utente.getNome());
    }
    
    @Test
    public void testSetCognome() {
        utente.setCognome("Verdi");
        assertEquals("Verdi", utente.getCognome());
    }
    
    @Test
    public void testSetEmail() {
        utente.setEmail("nuovo@unisa.it");
        assertEquals("nuovo@unisa.it", utente.getEmail());
    }
    
    //Test Prestiti
    @Test
    public void testGetPrestitiAttiviVuoto() {
        assertTrue(utente.getPrestitiAttivi().isEmpty());
    }
    
    @Test
    public void testAggiungiPrestito() {
        Libro libro = new Libro("ISBN1", "Libro 1", 2020, 3);
        Prestito prestito = new Prestito(utente, libro, LocalDate.now().plusDays(7));
        
        utente.aggiungiPrestito(prestito);
        
        assertEquals(1, utente.getNumeroPrestitiAttivi());
    }
    
    @Test
    public void testAggiungiDuePrestiti() throws InterruptedException {
        Libro libro1 = new Libro("ISBN1", "Libro 1", 2020, 3);
        Libro libro2 = new Libro("ISBN2", "Libro 2", 2020, 3);
        Prestito p1 = new Prestito(utente, libro1, LocalDate.now().plusDays(7));
        Thread.sleep(2);
        Prestito p2 = new Prestito(utente, libro2, LocalDate.now().plusDays(7));
        utente.aggiungiPrestito(p1);
        utente.aggiungiPrestito(p2);
        assertEquals(2, utente.getNumeroPrestitiAttivi());
    }
    
    @Test
    public void testRimuoviPrestito() {
        Libro libro = new Libro("ISBN1", "Libro 1", 2020, 3);
        Prestito prestito = new Prestito(utente, libro, LocalDate.now().plusDays(7));
        
        utente.aggiungiPrestito(prestito);
        utente.rimuoviPrestito(prestito);
        
        assertEquals(0, utente.getNumeroPrestitiAttivi());
    }
    
    //Test Limite Prestiti
    @Test
    public void testHaRaggiuntoLimiteFalse() {
        assertFalse(utente.haRaggiuntoLimite());
    }
    
    @Test
    public void testHaRaggiuntoLimiteConUnPrestito() {
        Libro libro = new Libro("ISBN1", "Libro 1", 2020, 3);
        Prestito p = new Prestito(utente, libro, LocalDate.now().plusDays(7));
        utente.aggiungiPrestito(p);
        
        assertFalse(utente.haRaggiuntoLimite());
    }
    
    @Test
    public void testHaRaggiuntoLimiteConTrePrestiti() throws InterruptedException {
        Libro libro1 = new Libro("ISBN1", "Libro 1", 2020, 3);
        Libro libro2 = new Libro("ISBN2", "Libro 2", 2020, 3);
        Libro libro3 = new Libro("ISBN3", "Libro 3", 2020, 3);
        Prestito p1 = new Prestito(utente, libro1, LocalDate.now().plusDays(7));
        Thread.sleep(2);
        Prestito p2 = new Prestito(utente, libro2, LocalDate.now().plusDays(7));
        Thread.sleep(2);
        Prestito p3 = new Prestito(utente, libro3, LocalDate.now().plusDays(7));
        utente.aggiungiPrestito(p1);
        utente.aggiungiPrestito(p2);
        utente.aggiungiPrestito(p3);
        assertTrue(utente.haRaggiuntoLimite());
    }
    
    //Test Equals Utente
    @Test
    public void testEqualsStessoOggetto() {
        assertTrue(utente.equals(utente));
    }
    
    @Test
    public void testEqualsConNull() {
        assertFalse(utente.equals(null));
    }
    
    @Test
    public void testEqualsStessaMatricola() {
        Utente utente2 = new Utente("12345", "Luigi", "Verdi", "luigi@unisa.it");
        assertTrue(utente.equals(utente2));
    }
    
    @Test
    public void testEqualsMatricolaDiversa() {
        Utente utente2 = new Utente("54321", "Mario", "Rossi", "mario@unisa.it");
        assertFalse(utente.equals(utente2));
    }
    
    //Test HashCode Utente
    @Test
    public void testHashCodeUguali() {
        Utente utente2 = new Utente("12345", "Luigi", "Verdi", "luigi@unisa.it");
        assertEquals(utente.hashCode(), utente2.hashCode());
    }
    
    // Test per verificare che non vengano aggiunti prestiti duplicati
    @Test
    public void testAggiungiPrestitoDuplicato() {
        Libro libro = new Libro("ISBN1", "Libro 1", 2020, 3);
        Prestito prestito = new Prestito(utente, libro, LocalDate.now().plusDays(7));
        utente.aggiungiPrestito(prestito);
        utente.aggiungiPrestito(prestito);
        assertEquals(1, utente.getNumeroPrestitiAttivi());
    }

    // Test equals con classe diversa
    @Test
    public void testEqualsClasseDiversa() {
        assertFalse(utente.equals("stringa"));
    }
}