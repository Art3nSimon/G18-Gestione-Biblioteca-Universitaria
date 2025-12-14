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
public class Utente implements Serializable{
    private static final long serialVersionUID = 1L;
    
    /** Attributi Utente */
    private String matricola;
    private String nome;
    private String cognome;
    private String email;
    private List<Prestito> prestitiAttivi;
    
    /** Costruttore Utente */
    public Utente(String matricola, String nome, String cognome, String email) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.prestitiAttivi = new ArrayList<>();
    }
    
    /** Metodi get Utente */
    
    /** Restituisce la matricola */
    public String getMatricola() {
        return matricola;
    }
    
    /** Restituisce il nome */
    public String getNome() {
        return nome;
    }
    
    /** Restituisce il cognome */
    public String getCognome() {
        return cognome;
    }
    
    /** Restituisce l'email */
    public String getEmail() {
        return email;
    }
    
    /** Restituisce la lista dei prestiti attivi */
    public List<Prestito> getPrestitiAttivi() {
        return new ArrayList<>(prestitiAttivi);
    }
    
    /** Restituisce il numero di prestiti in corso */
    public int getNumeroPrestitiAttivi() {
        return prestitiAttivi.size();
    }
    
    /** Restituisce nome e cognome concatenati */
    public String getNomeCognome() {
        return cognome + " " + nome;
    }
    
    /** Imposta il nome */
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    /** Imposta il cognome */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    
    /** Imposta l'email */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /** Altri metodi Utente */
    
    /** Verifica se raggiunto il limite massimo di prestiti */
    public boolean haRaggiuntoLimite() {
        return prestitiAttivi.size() >= 3;
    }
    
    /** Aggiunge un prestito alla lista dell'utente */
    public void aggiungiPrestito(Prestito prestito) {
        if (!prestitiAttivi.contains(prestito)) {
            prestitiAttivi.add(prestito);
        }
    }
    
    /** Rimuove un prestito dalla lista dell'utente */
    public void rimuoviPrestito(Prestito prestito) {
        prestitiAttivi.remove(prestito);
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