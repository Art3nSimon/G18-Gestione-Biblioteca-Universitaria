/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

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
    }
}
