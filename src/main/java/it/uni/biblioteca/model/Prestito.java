/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import it.uni.biblioteca.model.Utente;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Matteo
 */
public class Prestito implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //Attributi Prestito
    private String id;
    private Utente utente;
    private Libro libro;
    private LocalDate dataPrestito;
    private LocalDate dataRestituzionePrevista;
    private LocalDate dataRestituzioneEffettiva;
    
    //Costruttore Prestito
    public Prestito(Utente utente, Libro libro, LocalDate dataRestituzione) {
        this.id = generaId();
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
    
    public LocalDate getDataRestituzionePrevista(){
        return dataRestituzionePrevista;
    }
    
    public LocalDate getDataRestituzioneEffettiva() {
        return dataRestituzioneEffettiva;
    }
    
    //Altri metodi Prestito
    private String generaId() {
        return "P" + System.currentTimeMillis();
    }
    
    public boolean isAttivo(){
        return dataRestituzioneEffettiva == null;
    }
    
    public boolean isInRitardo(){
        return isAttivo() && LocalDate.now().isAfter(dataRestituzionePrevista);
    }
    
    public long getGiorniRitardo() {
        if (!isInRitardo()) return 0;
        return ChronoUnit.DAYS.between(dataRestituzionePrevista, LocalDate.now());
    }
    
    public long getGiorniAllaScadenza() {
        if (!isAttivo())return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), dataRestituzionePrevista);
    }
    
    public void registraRestituzione() {
        this.dataRestituzioneEffettiva = LocalDate.now();
    }
    
    public String getStatoDescrizione() {
        if (!isAttivo()) {
            return "CHIUSO";
        } else if (isInRitardo()) {
            return "RITARDO " + getGiorniRitardo() + " gg";
        }else{
            long giorni = getGiorniAllaScadenza();
            if (giorni <= 2) {
                return "SCADE " + giorni + " gg";
            } else {
                return "ATTIVO " + giorni + " gg";
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Prestito prestito = (Prestito) obj;
        return id.equals(prestito.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "Prestito{" +
                "utente=" + utente.getNomeCognome() +
                ", libro=" + libro.getTitolo() +
                ", stato=" + getStatoDescrizione() +
                '}';
    }
}