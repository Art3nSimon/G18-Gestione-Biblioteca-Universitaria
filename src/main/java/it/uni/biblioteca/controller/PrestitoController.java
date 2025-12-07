/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.controller;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.model.Prestito;
import it.uni.biblioteca.model.Utente;
import it.uni.biblioteca.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;

/**
 *
 * @author Utente
 */
public class PrestitoController {
    
    @FXML private TableView<Prestito> tabellaPrestiti;
    @FXML private TableColumn<Prestito, String> colonnaUtente;
    @FXML private TableColumn<Prestito, String> colonnaLibro;
    @FXML private TableColumn<Prestito, LocalDate> colonnaDataPrestito;
    @FXML private TableColumn<Prestito, LocalDate> colonnaDataScadenza;
    @FXML private TableColumn<Prestito, String> colonnaStato;
    
    @FXML private Label labelTotale;
    @FXML private Label labelRitardi;
    @FXML private Label labelScadenza;
    
    private Biblioteca biblioteca;
    private ObservableList<Prestito> listaPrestiti;
    
    @FXML
    public void initialize() {
        biblioteca = Biblioteca.getInstance();
        
        // Setup colonne tabella
        colonnaUtente.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getUtente().getNomeCognome() + 
                " (" + cellData.getValue().getUtente().getMatricola() + ")"
            )
        );
        
        colonnaLibro.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getLibro().getTitolo()
            )
        );
        
        colonnaDataPrestito.setCellValueFactory(
            new PropertyValueFactory<>("dataPrestito")
        );
        
        colonnaDataScadenza.setCellValueFactory(
            new PropertyValueFactory<>("dataRestituzioneRevista")
        );
        
        colonnaStato.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatoDescrizione()
            )
        );
        
        // Evidenzia prestiti in ritardo (UI-1.5)
        tabellaPrestiti.setRowFactory(tv -> new TableRow<Prestito>() {
            @Override
            protected void updateItem(Prestito prestito, boolean empty) {
                super.updateItem(prestito, empty);
                if (prestito == null || empty) {
                    setStyle("");
                } else if (prestito.isInRitardo()) {
                    // Rosso per ritardi (UI-1.5)
                    setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
                } else if (prestito.getGiorniAllaScadenza() <= 2) {
                    // Giallo per scadenza imminente
                    setStyle("-fx-background-color: #fff3cd;");
                } else {
                    // Verde per prestiti attivi
                    setStyle("-fx-background-color: #d4edda;");
                }
            }
        });
        
        // Carica dati iniziali
        aggiornaTabella();
    }
        /**
     * Apre il dialog per registrare un nuovo prestito
     * Requisito: UC-11
     */
    @FXML
    private void registraPrestito() {
        try {
            // Crea dialog personalizzato (inline, senza FXML separato)
            Dialog<Prestito> dialog = new Dialog<>();
            dialog.setTitle("Registra Nuovo Prestito");
            dialog.setHeaderText("Inserisci i dati del prestito");
            
            // Bottoni
            ButtonType confermaButtonType = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confermaButtonType, ButtonType.CANCEL);
            
            // Crea form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            ComboBox<Utente> comboUtente = new ComboBox<>();
            comboUtente.setItems(FXCollections.observableArrayList(
                biblioteca.getTuttiUtenti()
            ));
            comboUtente.setPromptText("Seleziona utente...");
            comboUtente.setPrefWidth(300);
            
            ComboBox<Libro> comboLibro = new ComboBox<>();
            comboLibro.setItems(FXCollections.observableArrayList(
                biblioteca.getTuttiLibri()
            ));
            comboLibro.setPromptText("Seleziona libro...");
            comboLibro.setPrefWidth(300);
            
            DatePicker datePicker = new DatePicker();
            datePicker.setValue(LocalDate.now().plusDays(14)); // Default 2 settimane
            datePicker.setPrefWidth(300);
            
            Label infoUtente = new Label();
            Label infoLibro = new Label();
            
            // Aggiorna info quando selezionato
            comboUtente.setOnAction(e -> {
                Utente u = comboUtente.getValue();
                if (u != null) {
                    if (u.haRaggiuntoLimite()) {
                        infoUtente.setText("⚠️ Limite prestiti raggiunto (" + 
                            u.getNumeroPrestitiAttivi() + "/3)");
                        infoUtente.setStyle("-fx-text-fill: red;");
                    } else {
                        infoUtente.setText("✓ Prestiti: " + 
                            u.getNumeroPrestitiAttivi() + "/3");
                        infoUtente.setStyle("-fx-text-fill: green;");
                    }
                }
            });
            
            comboLibro.setOnAction(e -> {
                Libro l = comboLibro.getValue();
                if (l != null) {
                    if (l.isDisponibile()) {
                        infoLibro.setText("✓ Copie disponibili: " + 
                            l.getNumeroCopieDisponibili() + "/" + 
                            l.getNumeroCopieTotali());
                        infoLibro.setStyle("-fx-text-fill: green;");
                    } else {
                        infoLibro.setText("⚠️ Nessuna copia disponibile");
                        infoLibro.setStyle("-fx-text-fill: red;");
                    }
                }
            });
            
            grid.add(new Label("Utente:"), 0, 0);
            grid.add(comboUtente, 1, 0);
            grid.add(infoUtente, 1, 1);
            
            grid.add(new Label("Libro:"), 0, 2);
            grid.add(comboLibro, 1, 2);
            grid.add(infoLibro, 1, 3);
            
            grid.add(new Label("Data Restituzione:"), 0, 4);
            grid.add(datePicker, 1, 4);
            
            dialog.getDialogPane().setContent(grid);
            
            // Converti result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confermaButtonType) {
                    try {
                        Utente utente = comboUtente.getValue();
                        Libro libro = comboLibro.getValue();
                        LocalDate data = datePicker.getValue();
                        
                        if (utente == null || libro == null || data == null) {
                            throw new Exception("Compila tutti i campi");
                        }
                        
                        biblioteca.registraPrestito(
                            utente.getMatricola(), 
                            libro.getIsbn(), 
                            data
                        );
                        
                        return new Prestito(utente, libro, data);
                        
                    } catch (Exception e) {
                        AlertHelper.mostraErrore("Errore", e.getMessage());
                        return null;
                    }
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(prestito -> {
                aggiornaTabella();
                AlertHelper.mostraConferma("Successo", 
                    "Prestito registrato con successo!");
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.mostraErrore("Errore", e.getMessage());
        }
    }
    
    /**
     * Registra la restituzione del prestito selezionato
     * Requisito: UC-12
     */
    @FXML
    private void registraRestituzione() {
        Prestito selezionato = tabellaPrestiti.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            AlertHelper.mostraErrore("Errore", "Seleziona un prestito da restituire");
            return;
        }
        
        // Mostra dialog di conferma con dettagli
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Restituzione");
        alert.setHeaderText("Confermi la restituzione del seguente prestito?");
        
        String dettagli = "Utente: " + selezionato.getUtente().getNomeCognome() + "\n" +
                          "Libro: " + selezionato.getLibro().getTitolo() + "\n" +
                          "Data Prestito: " + selezionato.getDataPrestito() + "\n" +
                          "Scadenza: " + selezionato.getDataRestituzioneRevista() + "\n" +
                          "Stato: " + selezionato.getStatoDescrizione();
        
        if (selezionato.isInRitardo()) {
            dettagli += "\n\n⚠️ ATTENZIONE: Prestito in ritardo di " + 
                       selezionato.getGiorniRitardo() + " giorni!";
        }
        
        alert.setContentText(dettagli);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    biblioteca.registraRestituzione(selezionato);
                    aggiornaTabella();
                    AlertHelper.mostraConferma("Successo", 
                        "Restituzione registrata con successo");
                } catch (Exception e) {
                    AlertHelper.mostraErrore("Errore", e.getMessage());
                }
            }
        });
    }
    
    /**
     * Aggiorna la tabella dei prestiti attivi
     * Requisito: UC-13
     */
    @FXML
    private void aggiornaTabella() {
        listaPrestiti = FXCollections.observableArrayList(
            biblioteca.getPrestitiAttivi()
        );
        tabellaPrestiti.setItems(listaPrestiti);
        
        // Aggiorna statistiche
        aggiornaStatistiche();
    }
    
    /**
     * Aggiorna le statistiche mostrate in alto
     */
    private void aggiornaStatistiche() {
        int totale = listaPrestiti.size();
        long ritardi = listaPrestiti.stream()
            .filter(Prestito::isInRitardo)
            .count();
        long inScadenza = listaPrestiti.stream()
            .filter(p -> !p.isInRitardo() && p.getGiorniAllaScadenza() <= 2)
            .count();
        
        labelTotale.setText("📊 Totale Prestiti Attivi: " + totale);
        labelRitardi.setText("⚠️ Prestiti in Ritardo: " + ritardi);
        labelScadenza.setText("⏰ In Scadenza (entro 2gg): " + inScadenza);
        
        // Colora label ritardi se > 0
        if (ritardi > 0) {
            labelRitardi.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        } else {
            labelRitardi.setStyle("-fx-text-fill: #155724;");
        }
    }
}