/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import it.uni.biblioteca.model.Utente;
import java.time.LocalDate;

/**
 *
 * @author Matteo
 */
public class Prestito{
    //Attributi Prestito
    private String id;
    private Utente utente;
    private Libro libro;
    private LocalDate dataPrestito;
    private LocalDate dataRestituzionePrevista;
    private LocalDate dataRestituzioneEffettiva;
    
    //Costruttore Prestito
    public Prestito(String id, Utente utente, Libro libro, LocalDate dataRestituzione) {
        this.id = id;
        this.utente = utente;
        this.libro = libro;
        this.dataPrestito = LocalDate.now();
        this.dataRestituzionePrevista = dataRestituzione;
        this.dataRestituzioneEffettiva = null;
    }
    
    //Metodi Get Prestito
    public String getId() {
        return id;
    }
    
    public Utente getUtente() {
        return utente;
    }
    
    public Libro getLibro() {
        return libro;
    }
    
    public LocalDate getDataPrestito() {
        return dataPrestito;
    }
    
    public LocalDate getDataRestituzionePrevista() {
        return dataRestituzionePrevista;
    }
    
    public LocalDate getDataRestituzioneEffettiva() {
        return dataRestituzioneEffettiva;
    }
    
}