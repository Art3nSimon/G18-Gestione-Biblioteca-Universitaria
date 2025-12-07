/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.controller;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Utente
 */
public class LibroDialogController {
    
    @FXML private TextField fieldTitolo;
    @FXML private TextField fieldIsbn;
    @FXML private TextField fieldAnno;
    @FXML private TextField fieldCopie;
    @FXML private ListView<Libro.Autore> listaAutori;
    @FXML private TextField fieldNomeAutore;
    @FXML private TextField fieldCognomeAutore;
    
    private Biblioteca biblioteca;
    private Libro libroCorrente;
    private boolean confermato = false;
    private ObservableList<Libro.Autore> autori;
    
    @FXML
    public void initialize() {
        biblioteca = Biblioteca.getInstance();
        autori = FXCollections.observableArrayList();
        listaAutori.setItems(autori);
    }
    
    /**
     * Imposta il libro da modificare (null per nuovo libro)
     */
    public void setLibro(Libro libro) {
        this.libroCorrente = libro;
        
        if (libro != null) {
            // Modalità modifica - precompila i campi
            fieldTitolo.setText(libro.getTitolo());
            fieldIsbn.setText(libro.getIsbn());
            fieldIsbn.setDisable(true); // ISBN non modificabile
            fieldAnno.setText(String.valueOf(libro.getAnnoPubblicazione()));
            fieldCopie.setText(String.valueOf(libro.getNumeroCopieTotali()));
            autori.addAll(libro.getAutori());
        }
    }
    
    /**
     * Aggiunge un autore alla lista
     */
    @FXML
    private void aggiungiAutore() {
        String nome = fieldNomeAutore.getText().trim();
        String cognome = fieldCognomeAutore.getText().trim();
        
        if (nome.isEmpty() || cognome.isEmpty()) {
            AlertHelper.mostraErrore("Errore", "Inserisci nome e cognome dell'autore");
            return;
        }
        
        Libro.Autore autore = new Libro.Autore(nome, cognome);
        autori.add(autore);
        
        fieldNomeAutore.clear();
        fieldCognomeAutore.clear();
    }
    
    /**
     * Rimuove l'autore selezionato dalla lista
     */
    @FXML
    private void rimuoviAutore() {
        Libro.Autore selezionato = listaAutori.getSelectionModel().getSelectedItem();
        if (selezionato != null) {
            autori.remove(selezionato);
        }
    }
    
    /**
     * Conferma e salva il libro
     */
    @FXML
    private void conferma() {
        try {
            // Validazione input
            String titolo = fieldTitolo.getText().trim();
            String isbn = fieldIsbn.getText().trim();
            String annoStr = fieldAnno.getText().trim();
            String copieStr = fieldCopie.getText().trim();
            
            if (titolo.isEmpty()) {
                throw new Exception("Il titolo è obbligatorio");
            }
            if (isbn.isEmpty()) {
                throw new Exception("L'ISBN è obbligatorio");
            }
            if (autori.isEmpty()) {
                throw new Exception("Inserisci almeno un autore");
            }
            
            int anno = Integer.parseInt(annoStr);
            int copie = Integer.parseInt(copieStr);
            
            if (anno < 1000 || anno > java.time.Year.now().getValue()) {
                throw new Exception("Anno non valido (1000 - " + 
                    java.time.Year.now().getValue() + ")");
            }
            if (copie < 0) {
                throw new Exception("Numero copie deve essere >= 0");
            }
            
            // Crea o modifica libro
            if (libroCorrente == null) {
                // Nuovo libro
                Libro nuovoLibro = new Libro(isbn, titolo, anno, copie);
                for (Libro.Autore autore : autori) {
                    nuovoLibro.aggiungiAutore(autore);
                }
                biblioteca.aggiungiLibro(nuovoLibro);
            } else {
                // Modifica libro esistente
                libroCorrente.setTitolo(titolo);
                libroCorrente.setAnnoPubblicazione(anno);
                // Gli autori non possono essere modificati per semplicità
                // o implementare logica di modifica
                biblioteca.modificaLibro(libroCorrente);
            }
            
            confermato = true;
            chiudi();
            
        } catch (NumberFormatException e) {
            AlertHelper.mostraErrore("Errore", "Anno e Copie devono essere numeri");
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
        Stage stage = (Stage) fieldTitolo.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Restituisce true se l'utente ha confermato
     */
    public boolean isConfermato() {
        return confermato;
    }
}
