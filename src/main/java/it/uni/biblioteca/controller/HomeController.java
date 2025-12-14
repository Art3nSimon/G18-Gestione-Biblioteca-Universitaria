/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Controller per la Home Dashboard
 * Mostra statistiche e card navigazione
 */
package it.uni.biblioteca.controller;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Prestito;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller per la schermata Home
 * @author Simona
 */
public class HomeController {
    
    @FXML private Label labelTotaleLibri;
    @FXML private Label labelLibriDisponibili;
    @FXML private Label labelTotaleUtenti;
    @FXML private Label labelUtentiAttivi;
    @FXML private Label labelPrestitiAttivi;
    @FXML private Label labelPrestitiRitardo;
    
    @FXML private VBox cardLibri;
    @FXML private VBox cardUtenti;
    @FXML private VBox cardPrestiti;
    
    private Biblioteca biblioteca;
    private MainController mainController;
    
    /** Inizializza il controller */
    @FXML
    public void initialize() {
        biblioteca = Biblioteca.getInstance();
        aggiornaStatistiche();
        setupCardHover();
    }
    
    /**
     * Imposta il riferimento al MainController per la navigazione
     */
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
    
    /**
     * Aggiorna le statistiche mostrate nella dashboard
     */
    private void aggiornaStatistiche() {
        // Statistiche Libri
        int totaleLibri = biblioteca.getTuttiLibri().size();
        long libriDisponibili = biblioteca.getTuttiLibri().stream()
            .filter(l -> l.isDisponibile())
            .count();
        
        labelTotaleLibri.setText(String.valueOf(totaleLibri));
        labelLibriDisponibili.setText(libriDisponibili + " disponibili");
        
        // Statistiche Utenti
        int totaleUtenti = biblioteca.getTuttiUtenti().size();
        long utentiConPrestiti = biblioteca.getTuttiUtenti().stream()
            .filter(u -> u.getNumeroPrestitiAttivi() > 0)
            .count();
        
        labelTotaleUtenti.setText(String.valueOf(totaleUtenti));
        labelUtentiAttivi.setText(utentiConPrestiti + " con prestiti");
        
        // Statistiche Prestiti
        int prestitiAttivi = biblioteca.getPrestitiAttivi().size();
        long prestitiRitardo = biblioteca.getPrestitiAttivi().stream()
            .filter(Prestito::isInRitardo)
            .count();
        
        labelPrestitiAttivi.setText(String.valueOf(prestitiAttivi));
        labelPrestitiRitardo.setText(prestitiRitardo + " in ritardo");
    }
    
    /**
     * Setup effetti hover sulle card
     */
    private void setupCardHover() {
        setupCardEffect(cardLibri);
        setupCardEffect(cardUtenti);
        setupCardEffect(cardPrestiti);
    }
    
    /** Setup effetto zoom sulla card singola */
    private void setupCardEffect(VBox card) {
        card.setOnMouseEntered(e -> 
            card.setStyle(card.getStyle() + "-fx-scale-x: 1.02; -fx-scale-y: 1.02;")
        );
        card.setOnMouseExited(e -> 
            card.setStyle(card.getStyle().replace("-fx-scale-x: 1.02; -fx-scale-y: 1.02;", ""))
        );
    }
    
    /**
     * Naviga alla gestione libri
     */
    @FXML
    private void apriGestioneLibri() {
        if (mainController != null) {
            mainController.apriGestioneLibri();
        }
    }
    
    /**
     * Naviga alla gestione utenti
     */
    @FXML
    private void apriGestioneUtenti() {
        if (mainController != null) {
            mainController.apriGestioneUtenti();
        }
    }
    
    /**
     * Naviga alla gestione prestiti
     */
    @FXML
    private void apriGestionePrestiti() {
        if (mainController != null) {
            mainController.apriGestionePrestiti();
        }
    }
}