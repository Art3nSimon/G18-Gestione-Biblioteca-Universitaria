/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import it.uni.biblioteca.model.Prestito;
import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.model.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Matteo
 */
public class PrestitoTest {
    
    private Utente utente;
    private Libro libro;
    private Prestito prestito;
    
    /** Inizializzazione dati */
    @BeforeEach
    public void setUp() {
        utente = new Utente("12345", "Mario", "Rossi", "mario@unisa.it");
        libro = new Libro("ISBN123", "Il Signore degli Anelli", 1954, 5);
        prestito = new Prestito(utente, libro, LocalDate.now().plusDays(14));
    }
    
    /** Test Costruttore Prestito */
    @Test
    public void testCostruttore() {
        assertNotNull(prestito.getId());
        assertTrue(prestito.getId().startsWith("P"));
        assertEquals(utente, prestito.getUtente());
        assertEquals(libro, prestito.getLibro());
        assertEquals(LocalDate.now(), prestito.getDataPrestito());
        assertNull(prestito.getDataRestituzioneEffettiva());
    }
    
    /** Test Get Prestito */
    @Test
    public void testGetDataRestituzionePrevista() {
        assertEquals(LocalDate.now().plusDays(14), prestito.getDataRestituzionePrevista());
    }
    
    @Test
    public void testIdUnivoco() {
        Prestito prestito1 = new Prestito(utente, libro, LocalDate.now().plusDays(14));
        
        try {
            Thread.sleep(10); //Attende 10 millisecondi
        } catch (InterruptedException e){}
        
        Prestito prestito2 = new Prestito(utente, libro, LocalDate.now().plusDays(14));
        assertNotEquals(prestito.getId(), prestito2.getId());
    }
    
    /** Test IsAttivo */
    @Test
    public void testIsAttivoIniziale() {
        assertTrue(prestito.isAttivo());
    }
    
    @Test
    public void testIsAttivoDopoRestituzione() {
        prestito.registraRestituzione();
        assertFalse(prestito.isAttivo());
    }
    
    /** Test IsInRitardo */
    @Test
    public void testIsInRitardoFalse() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().plusDays(7));
        assertFalse(p.isInRitardo());
    }
    
    @Test
    public void testIsInRitardoTrue() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().minusDays(5));
        assertTrue(p.isInRitardo());
    }
    
    @Test
    public void testIsInRitardoOggi() {
        Prestito p = new Prestito(utente, libro, LocalDate.now());
        assertFalse(p.isInRitardo());
    }
    
    @Test
    public void testIsInRitardoDopoRestituzione() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().minusDays(5));
        p.registraRestituzione();
        assertFalse(p.isInRitardo());
    }
    
    /** Test Giorni ritardo */
    @Test
    public void testGetGiorniRitardoZero() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().plusDays(7));
        assertEquals(0, p.getGiorniRitardo());
    }
    
    @Test
    public void testGetGiorniRitardoCinque() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().minusDays(5));
        assertEquals(5, p.getGiorniRitardo());
    }
    
    /** Test Giorni allo scadere */
    @Test
    public void testGetGiorniAllaScadenza() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().plusDays(7));
        assertEquals(7, p.getGiorniAllaScadenza());
    }
    
    @Test
    public void testGetGiorniAllaScadenzaZeroSeChiuso() {
        prestito.registraRestituzione();
        assertEquals(0, prestito.getGiorniAllaScadenza());
    }
    
    /** Test Registrazione Prestito */
    @Test
    public void testRegistraRestituzione() {
        assertNull(prestito.getDataRestituzioneEffettiva());
        
        prestito.registraRestituzione();
        
        assertNotNull(prestito.getDataRestituzioneEffettiva());
        assertEquals(LocalDate.now(), prestito.getDataRestituzioneEffettiva());
    }
    
    /** Test Stato Descrizione */
    @Test
    public void testGetStatoDescrizioneCHIUSO() {
        prestito.registraRestituzione();
        assertEquals("CHIUSO", prestito.getStatoDescrizione());
    }
    
    @Test
    public void testGetStatoDescrizioneRITARDO() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().minusDays(5));
        String stato = p.getStatoDescrizione();
        assertTrue(stato.startsWith("RITARDO"));
    }
    
    @Test
    public void testGetStatoDescrizioneSCADE() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().plusDays(1));
        String stato = p.getStatoDescrizione();
        assertTrue(stato.startsWith("SCADE"));
    }
    
    @Test
    public void testGetStatoDescrizioneATTIVO() {
        Prestito p = new Prestito(utente, libro, LocalDate.now().plusDays(7));
        String stato = p.getStatoDescrizione();
        assertTrue(stato.startsWith("ATTIVO"));
    }
    
    /** Test Equals Prestito */
    @Test
    public void testEqualsStessoOggetto() {
        assertTrue(prestito.equals(prestito));
    }
    
    @Test
    public void testEqualsConNull() {
        assertFalse(prestito.equals(null));
    }
    
    @Test
    public void testEqualsIdDiversi() {
        Prestito p1 = new Prestito(utente, libro, LocalDate.now().plusDays(14));
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
        
        Prestito p2 = new Prestito(utente, libro, LocalDate.now().plusDays(14));
        assertFalse(prestito.equals(p2));
    }
    
    /** Test HashCode Utente */
    @Test
    public void testHashCodeDiversi() throws InterruptedException {
        Prestito p1 = new Prestito(utente, libro, LocalDate.now().plusDays(14));
        Thread.sleep(2);
        Prestito p2 = new Prestito(utente, libro, LocalDate.now().plusDays(14));
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }
    
    /** Test ToString Prestito */
    @Test
    public void testToString() {
        String str = prestito.toString();
        assertTrue(str.contains("Rossi Mario"));
        assertTrue(str.contains("Il Signore degli Anelli"));
    }
    
    /** Test per verificare che non vengano aggiunti prestiti duplicati */
    @Test
    public void testAggiungiPrestitoDuplicato() {
        Libro libro1 = new Libro("ISBN1", "Libro 1", 2020, 3);
        Prestito prestito1 = new Prestito(utente, libro1, LocalDate.now().plusDays(7));

        utente.aggiungiPrestito(prestito);
        utente.aggiungiPrestito(prestito1); 
        assertEquals(1, utente.getNumeroPrestitiAttivi());
    }

    /** Test equals con classe diversa */
    @Test
    public void testEqualsClasseDiversa() {
        assertFalse(prestito.equals("stringa"));
    }
}