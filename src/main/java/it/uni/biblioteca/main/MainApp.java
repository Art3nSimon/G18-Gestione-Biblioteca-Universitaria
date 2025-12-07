/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.main;

import it.uni.biblioteca.controller.MainController;
import it.uni.biblioteca.model.Biblioteca;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Simona
 */
public class MainApp extends Application {
    
    private Biblioteca biblioteca;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Carica dati all'avvio
            biblioteca = Biblioteca.getInstance();
            biblioteca.caricaDati();
            
            // Carica FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MainView.fxml")
            );
            Parent root = loader.load();
            
            // Configura scena
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
            );
            
            // Configura stage
            primaryStage.setTitle("Sistema Gestione Biblioteca Universitaria");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore avvio applicazione: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        // Salva dati alla chiusura
        if (biblioteca != null) {
            biblioteca.salvaDati();
            System.out.println("Dati salvati alla chiusura");
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}