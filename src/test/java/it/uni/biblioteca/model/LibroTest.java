/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.model.Libro.Autore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Matteo
 */
public class LibroTest {
    
    private Libro libro;
    
    @BeforeEach
    public void setUp() {
        libro = new Libro("ISBN123", "Il Signore degli Anelli", 1954, 5);
    }
    
    //Test Costruttore Libro
    @Test
    public void testCostruttore() {
        assertEquals("ISBN123", libro.getIsbn());
        assertEquals("Il Signore degli Anelli", libro.getTitolo());
        assertEquals(1954, libro.getAnnoPubblicazione());
        assertEquals(5, libro.getNumeroCopieTotali());
        assertEquals(5, libro.getNumeroCopieDisponibili());
    }
    
    @Test
    public void testCostruttoreUnaCopia() {
        Libro libro2 = new Libro("ISBN456", "Test", 2020, 1);
        assertEquals(1, libro2.getNumeroCopieTotali());
    }
    
    @Test
    public void testCostruttoreZeroCopie() {
        Libro libro2 = new Libro("ISBN789", "Test", 2020, 0);
        assertEquals(0, libro2.getNumeroCopieDisponibili());
    }
    
    //Test Get Autore
    @Test
    public void testAggiungiAutore() {
        Autore autore = new Autore("J.R.R.", "Tolkien");
        libro.aggiungiAutore(autore);
        
        assertEquals(1, libro.getAutori().size());
    }
    
    @Test
    public void testGetAutoriAsStringSingoloAutore() {
        Autore autore = new Autore("J.R.R.", "Tolkien");
        libro.aggiungiAutore(autore);
        
        assertEquals("Tolkien J.R.R.", libro.getAutoriAsString());
    }
    
    @Test
    public void testGetAutoriAsStringMultipliAutori() {
        libro.aggiungiAutore(new Autore("Mario", "Rossi"));
        libro.aggiungiAutore(new Autore("Luigi", "Verdi"));
        
        assertEquals("Rossi Mario, Verdi Luigi", libro.getAutoriAsString());
    }
    
    @Test
    public void testGetAutoriAsStringVuoto() {
        assertEquals("", libro.getAutoriAsString());
    }
    
    //Test Disponibilità
    @Test
    public void testIsDisponibile() {
        assertTrue(libro.isDisponibile());
    }
    
    @Test
    public void testIsDisponibileFalse() {
        Libro libro2 = new Libro("ISBN000", "Test", 2020, 0);
        assertFalse(libro2.isDisponibile());
    }
    
    //Test Decrementa Copie
    @Test
    public void testDecrementaCopie() {
        libro.decrementaCopie();
        assertEquals(4, libro.getNumeroCopieDisponibili());
    }
    
    @Test
    public void testDecrementaCopieFinoAZero() {
        for (int i = 0; i < 5; i++) {
            libro.decrementaCopie();
        }
        assertEquals(0, libro.getNumeroCopieDisponibili());
    }
    
    @Test
    public void testDecrementaCopieSottoZero() {
        for (int i = 0; i < 10; i++) {
            libro.decrementaCopie();
        }
        assertEquals(0, libro.getNumeroCopieDisponibili());
    }
    
    //Test Incrementa Copie
    @Test
    public void testIncrementaCopie() {
        libro.decrementaCopie();
        libro.incrementaCopie();
        assertEquals(5, libro.getNumeroCopieDisponibili());
    }
    
    @Test
    public void testIncrementaCopieNonSuperaTotale() {
        libro.incrementaCopie();
        assertEquals(5, libro.getNumeroCopieDisponibili());
    }
    
    //Test Setter Libro
    @Test
    public void testSetTitolo() {
        libro.setTitolo("Nuovo Titolo");
        assertEquals("Nuovo Titolo", libro.getTitolo());
    }
    
    @Test
    public void testSetAnnoPubblicazione() {
        libro.setAnnoPubblicazione(2023);
        assertEquals(2023, libro.getAnnoPubblicazione());
    }
    
    //Test Metodi Autore
    @Test
    public void testAutoreCostruttore() {
        Autore autore = new Autore("Mario", "Rossi");
        assertEquals("Mario", autore.getNome());
        assertEquals("Rossi", autore.getCognome());
    }
    
    @Test
    public void testAutoreToString() {
        Autore autore = new Autore("Mario", "Rossi");
        assertEquals("Rossi Mario", autore.toString());
    }
    // Test per l'autore con getters
    @Test
    public void testAutoreGetters() {
        Autore autore = new Autore("Mario", "Rossi");
        assertEquals("Mario", autore.getNome());
        assertEquals("Rossi", autore.getCognome());
    }
}