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
    
    /** Attributi Prestito */
    private String id;
    private Utente utente;
    private Libro libro;
    private LocalDate dataPrestito;
    private LocalDate dataRestituzionePrevista;
    private LocalDate dataRestituzioneEffettiva;
    
    /** Costruttore Prestito */
    public Prestito(Utente utente, Libro libro, LocalDate dataRestituzione) {
        this.id = generaId();
        this.utente = utente;
        this.libro = libro;
        this.dataPrestito = LocalDate.now();
        this.dataRestituzionePrevista = dataRestituzione;
        this.dataRestituzioneEffettiva = null;
    }
    
    /** Metodi Get Prestito */
    
    /** Restituisce l'ID del prestito */
    public String getId() {
        return id;
    }
    
    /** Restituisce l'utente associato */
    public Utente getUtente() {
        return utente;
    }
    
    /** Restituisce il libro associato */
    public Libro getLibro() {
        return libro;
    }
    
    /** Restituisce la data di inizio prestito */
    public LocalDate getDataPrestito() {
        return dataPrestito;
    }
    
    /** Restituisce la data prevista di restituzione */
    public LocalDate getDataRestituzionePrevista(){
        return dataRestituzionePrevista;
    }
    
    /** Restituisce la data effettiva di restituzione */
    public LocalDate getDataRestituzioneEffettiva() {
        return dataRestituzioneEffettiva;
    }
    
    /** Altri metodi Prestito */
    
    /** Genera ID univoco */
    private String generaId() {
        return "P" + System.currentTimeMillis();
    }
    
    /** Verifica se il prestito è attivo */
    public boolean isAttivo(){
        return dataRestituzioneEffettiva == null;
    }
    
    /** Verifica se il prestito è in ritardo */
    public boolean isInRitardo(){
        return isAttivo() && LocalDate.now().isAfter(dataRestituzionePrevista);
    }
    
    /** Calcola i giorni di ritardo */
    public long getGiorniRitardo() {
        if (!isInRitardo()) return 0;
        return ChronoUnit.DAYS.between(dataRestituzionePrevista, LocalDate.now());
    }
    
    /** Calcola i giorni mancanti alla scadenza */
    public long getGiorniAllaScadenza() {
        if (!isAttivo())return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), dataRestituzionePrevista);
    }
    
    /** Imposta la data di restituzione ad oggi */
    public void registraRestituzione() {
        this.dataRestituzioneEffettiva = LocalDate.now();
    }
    
    /** Restituisce la descrizione dello stato del prestito */
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