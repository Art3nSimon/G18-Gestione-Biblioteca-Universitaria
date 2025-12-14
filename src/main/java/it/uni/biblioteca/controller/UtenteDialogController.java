/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.controller;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Utente;
import it.uni.biblioteca.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Utente
 */
public class UtenteDialogController {
    
    @FXML private TextField fieldNome;
    @FXML private TextField fieldCognome;
    @FXML private TextField fieldMatricola;
    @FXML private TextField fieldEmail;
    @FXML private Label labelPrestitiAttivi;
    @FXML private Label labelStato;
    
    private Biblioteca biblioteca;
    private Utente utenteCorrente;
    private boolean confermato = false;
    
    /** Inizializza il controller */
    @FXML
    public void initialize() {
        biblioteca = Biblioteca.getInstance();
    }
    
    /**
     * Imposta l'utente da modificare (null per nuovo utente)
     */
    public void setUtente(Utente utente) {
        this.utenteCorrente = utente;
        
        if (utente != null) {
            /** Modalità modifica - precompila i campi */
            fieldNome.setText(utente.getNome());
            fieldCognome.setText(utente.getCognome());
            fieldMatricola.setText(utente.getMatricola());
            fieldMatricola.setDisable(true); /** Matricola non modificabile (IF-2.2) */
            fieldEmail.setText(utente.getEmail());
            
            /** Mostra info prestiti */
            labelPrestitiAttivi.setText("Prestiti Attivi: " + 
                utente.getNumeroPrestitiAttivi());
            labelPrestitiAttivi.setVisible(true);
            
            if (utente.haRaggiuntoLimite()) {
                labelStato.setText("Stato: ⚠️ Limite prestiti raggiunto");
                labelStato.setStyle("-fx-text-fill: #856404; -fx-background-color: #fff3cd;");
            } else {
                labelStato.setText("Stato: ✓ Può effettuare prestiti");
                labelStato.setStyle("-fx-text-fill: #155724; -fx-background-color: #d4edda;");
            }
            labelStato.setVisible(true);
        } else {
            /** Modalità inserimento */
            labelPrestitiAttivi.setVisible(false);
            labelStato.setVisible(false);
        }
    }
    
    /**
     * Conferma e salva l'utente
     */
    @FXML
    private void conferma() {
        try {
            /** Validazione input */
            String nome = fieldNome.getText().trim();
            String cognome = fieldCognome.getText().trim();
            String matricola = fieldMatricola.getText().trim();
            String email = fieldEmail.getText().trim();
            
            if (nome.isEmpty()) {
                throw new Exception("Il nome è obbligatorio");
            }
            if (cognome.isEmpty()) {
                throw new Exception("Il cognome è obbligatorio");
            }
            if (matricola.isEmpty()) {
                throw new Exception("La matricola è obbligatoria");
            }
            if (email.isEmpty()) {
                throw new Exception("L'email è obbligatoria");
            }
            
            /** Validazione lunghezze (DF-1.2) */
            if (nome.length() > 50) {
                throw new Exception("Nome troppo lungo (max 50 caratteri)");
            }
            if (cognome.length() > 50) {
                throw new Exception("Cognome troppo lungo (max 50 caratteri)");
            }
            if (matricola.length() > 10) {
                throw new Exception("Matricola troppo lunga (max 10 caratteri)");
            }
            
            /** Validazione formato email */
            if (!email.contains("@") || !email.contains(".")) {
                throw new Exception("Formato email non valido");
            }
            
            /** Crea o modifica utente */
            if (utenteCorrente == null) {
                // Nuovo utente
                Utente nuovoUtente = new Utente(matricola, nome, cognome, email);
                biblioteca.aggiungiUtente(nuovoUtente);
            } else {
                // Modifica utente esistente
                utenteCorrente.setNome(nome);
                utenteCorrente.setCognome(cognome);
                utenteCorrente.setEmail(email);
                biblioteca.modificaUtente(utenteCorrente);
            }
            
            confermato = true;
            chiudi();
            
        } catch (Exception e) {
            AlertHelper.mostraErrore("Errore", e.getMessage());
        }
    }
    
    /**
     * Annulla e chiude il dialog
     */
    @FXML
    private void annulla() {
        confermato = false;
        chiudi();
    }
    
    /**
     * Chiude il dialog
     */
    private void chiudi() {
        Stage stage = (Stage) fieldNome.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Restituisce true se l'utente ha confermato
     */
    public boolean isConfermato() {
        return confermato;
    }
}