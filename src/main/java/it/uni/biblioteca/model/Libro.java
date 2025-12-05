/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matteo
 */
public class Libro {
    //Classe annidata Autore
    public static class Autore{
        //Attributi Autore
        private String nome;
        private String cognome;
        
        //Costruttore Autore
        public Autore(String nome, String cognome) {
            this.nome = nome;
            this.cognome = cognome;
        }
        
        //Metodi get Autore
        public String getNome(){ 
            return nome; 
        }
        public String getCognome(){ 
            return cognome; 
        }
        
        //Metodo stampa Autore
        @Override
        public String toString() {
            return cognome + " " + nome;
        }
    }
    //Attributi Libro
    private String isbn;
    private String titolo;
    private List<Autore> autori;
    private int annoPubblicazione;
    private int numeroCopieTotali;
    private int numeroCopieDisponibili;
    
    //Costruttore Libro
    public Libro(String isbn, String titolo, int annoPubblicazione, int copie) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.annoPubblicazione = annoPubblicazione;
        this.numeroCopieTotali = copie;
        this.numeroCopieDisponibili = copie;
        this.autori = new ArrayList<>();
    }
    
    //Metodi get Libro
    public String getIsbn(){ 
        return isbn; 
    }
    
    public String getTitolo(){ 
        return titolo; 
    }
    
    public List<Autore> getAutori(){ 
        return autori;
    }
    
    public int getAnnoPubblicazione(){ 
        return annoPubblicazione;
    }
    
    public int getNumeroCopieDisponibili(){ 
        return numeroCopieDisponibili; 
    }
    
    public int getNumeroCopieTotali(){ 
        return numeroCopieTotali; 
    }
}
