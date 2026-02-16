/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo
 */
public class Libro implements Serializable{
    private static final long serialVersionUID = 1L;
    
    /** Classe annidata Autore */
    public static class Autore implements Serializable{
        
        /** Attributi Autore */
        private String nome;
        private String cognome;
        
        /** Costruttore Autore */
        public Autore(String nome, String cognome) {
            this.nome = nome;
            this.cognome = cognome;
        }
        
        /** Metodi get Autore */
        
        /** Restituisce il nome dell'autore */
        public String getNome(){ 
            return nome; 
        }
        
        /** Restituisce il cognome dell'autore */
        public String getCognome(){ 
            return cognome; 
        }
        
        /** Metodo stampa Autore */
        @Override
        public String toString() {
            return cognome + " " + nome;
        }
    }
    
    /** Attributi Libro */
    private String isbn;
    private String titolo;
    private List<Autore> autori;
    private int annoPubblicazione;
    private int numeroCopieTotali;
    private int numeroCopieDisponibili;
    
    /** Costruttore Libro */
    public Libro(String isbn, String titolo, int annoPubblicazione, int copie) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.annoPubblicazione = annoPubblicazione;
        this.numeroCopieTotali = copie;
        this.numeroCopieDisponibili = copie;
        this.autori = new ArrayList<>();
    }
    
    /** Metodi get e set Libro */
    
    /** Restituisce il codice ISBN */
    public String getIsbn(){ 
        return isbn; 
    }
    
    /** Restituisce il titolo del libro */
    public String getTitolo(){ 
        return titolo; 
    }
    
    /** Restituisce la lista degli autori */
    public List<Autore> getAutori(){ 
        return autori;
    }
    
    /** Restituisce l'anno di pubblicazione */
    public int getAnnoPubblicazione(){ 
        return annoPubblicazione;
    }
    
    /** Restituisce il numero di copie attualmente disponibili */
    public int getNumeroCopieDisponibili(){ 
        return numeroCopieDisponibili; 
    }
    
    /** Restituisce il numero totale di copie */
    public int getNumeroCopieTotali(){ 
        return numeroCopieTotali; 
    }
    
    /** Imposta il titolo del libro */
    public void setTitolo(String titolo){
        this.titolo = titolo;
    }
    
    /** Imposta l'anno di pubblicazione */
    public void setAnnoPubblicazione(int anno){
        this.annoPubblicazione = anno;
    }
    
    /** Altri metodi Libro */
    
    /** Aggiunge un autore alla lista */
    public void aggiungiAutore(Autore autore) {
        autori.add(autore);
    }
    
    /** Decrementa le copie disponibili */
    public void decrementaCopie() {
        if (numeroCopieDisponibili > 0) {
            numeroCopieDisponibili--;
        }
    }
    
    /** Incrementa le copie disponibili */
    public void incrementaCopie() {
        if (numeroCopieDisponibili < numeroCopieTotali) {
            numeroCopieDisponibili++;
        }
    }
    
    /** Verifica se ci sono copie disponibili */
    public boolean isDisponibile() {
        return numeroCopieDisponibili > 0;
    }
    
    /** Restituisce gli autori come stringa formattata */
    public String getAutoriAsString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < autori.size(); i++) {
            sb.append(autori.get(i).toString());
            if (i < autori.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
  /**
     * Restituisce una rappresentazione testuale del libro
     * Usato per visualizzare il libro nei ComboBox
     */
    @Override
    public String toString() {
        return titolo + " - " + getAutoriAsString() + " (" + annoPubblicazione + ")";
    }
}