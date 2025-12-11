/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Controller principale con navigazione migliorata
 */
package it.uni.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * MainController con Home Dashboard
 * @author Matteo
 */
public class MainController {
    
    @FXML private BorderPane mainBorderPane;
    @FXML private Button btnHome;
    @FXML private Button btnLibri;
    @FXML private Button btnUtenti;
    @FXML private Button btnPrestiti;
    
    private String currentView = "";
    
    @FXML
    public void initialize() {
        // Carica la HOME di default invece di LibroView
        apriHome();
    }
    
    /**
     * Carica la schermata HOME 
     */
    @FXML
    public void apriHome() {
        if (caricaSchermata("/fxml/HomeView.fxml")) {
            currentView = "HOME";
            evidenziaBottoneAttivo(btnHome);
        }
    }
    
    /**
     * Carica la schermata Gestione Libri
     */
    @FXML
    public void apriGestioneLibri() {
        if (caricaSchermata("/fxml/LibroView.fxml")) {
            currentView = "LIBRI";
            evidenziaBottoneAttivo(btnLibri);
        }
    }
    
    /**
     * Carica la schermata Gestione Utenti
     */
    @FXML
    public void apriGestioneUtenti() {
        if (caricaSchermata("/fxml/UtenteView.fxml")) {
            currentView = "UTENTI";
            evidenziaBottoneAttivo(btnUtenti);
        }
    }
    
    /**
     * Carica la schermata Gestione Prestiti
     */
    @FXML
    public void apriGestionePrestiti() {
        if (caricaSchermata("/fxml/PrestitoView.fxml")) {
            currentView = "PRESTITI";
            evidenziaBottoneAttivo(btnPrestiti);
        }
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
     * @return true se caricamento riuscito
     */
    private boolean caricaSchermata(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent schermata = loader.load();
            
            // Se è la HomeView, passa il riferimento al MainController
            if (fxmlPath.contains("HomeView")) {
                HomeController homeController = loader.getController();
                homeController.setMainController(this);
            }
            
            mainBorderPane.setCenter(schermata);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Impossibile caricare la schermata: " + e.getMessage());
            alert.showAndWait();
            return false;
        }
    }
    
    /**
     * Evidenzia il bottone della sezione attiva
     */
    private void evidenziaBottoneAttivo(Button bottoneDaEvidenziare) {
        // Rimuovi evidenziazione da tutti
        btnHome.getStyleClass().remove("nav-button-active");
        btnLibri.getStyleClass().remove("nav-button-active");
        btnUtenti.getStyleClass().remove("nav-button-active");
        btnPrestiti.getStyleClass().remove("nav-button-active");
        
        // Aggiungi al bottone attivo
        if (bottoneDaEvidenziare != null) {
            bottoneDaEvidenziare.getStyleClass().add("nav-button-active");
        }
    }
}