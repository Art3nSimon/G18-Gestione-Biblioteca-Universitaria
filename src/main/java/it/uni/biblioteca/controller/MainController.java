/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Matteo
 */
public class MainController {
    
    @FXML
    private BorderPane mainBorderPane;
    
    @FXML
    public void initialize() {
        // Carica la schermata dei libri di default
        apriGestioneLibri();
    }
    
    /**
     * Carica la schermata Gestione Libri
     */
    @FXML
    private void apriGestioneLibri() {
        caricaSchermata("/fxml/LibroView.fxml");
    }
    
    /**
     * Carica la schermata Gestione Utenti
     */
    @FXML
    private void apriGestioneUtenti() {
        caricaSchermata("/fxml/UtenteView.fxml");
    }
    
    /**
     * Carica la schermata Gestione Prestiti
     */
    @FXML
    private void apriGestionePrestiti() {
        caricaSchermata("/fxml/PrestitoView.fxml");
    }
    
    /**
     * Mostra informazioni sull'applicazione
     */
    @FXML
    private void mostraInformazioni() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazioni");
        alert.setHeaderText("Sistema Gestione Biblioteca Universitaria");
        alert.setContentText(
            "Versione: 1.0\n" +
            "Corso: Ingegneria del Software\n" +
            "Gruppo: 18\n" +
            "Anno: 2025"
        );
        alert.showAndWait();
    }
    
    /**
     * Esce dall'applicazione
     */
    @FXML
    private void esci() {
        System.exit(0);
    }
    
    /**
     * Carica una schermata FXML nel centro del BorderPane
     */
    private void caricaSchermata(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent schermata = loader.load();
            mainBorderPane.setCenter(schermata);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Impossibile caricare la schermata: " + e.getMessage());
            alert.showAndWait();
        }
    }
}