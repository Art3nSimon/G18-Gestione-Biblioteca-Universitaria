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
public class Utente {
    //Attributi Utente
    private String matricola;
    private String nome;
    private String cognome;
    private String email;
    private List<Prestito> prestitiAttivi;
    
    //Costruttore Utente
    public Utente(String matricola, String nome, String cognome, String email) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.prestitiAttivi = new ArrayList<>();
    }
    
    //Metodi get Utente
    public String getMatricola() {
        return matricola;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getCognome() {
        return cognome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public List<Prestito> getPrestitiAttivi() {
        return new ArrayList<>(prestitiAttivi);
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    //Altri metodi Utente
    public String getNomeCognome() {
        return cognome + " " + nome;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utente utente = (Utente) obj;
        return matricola.equals(utente.matricola);
    }
    
    @Override
    public int hashCode() {
        return matricola.hashCode();
    }
    
    @Override
    public String toString() {
        return getNomeCognome() + " (" + matricola + ")";
    }
}
