/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.controller;

import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.util.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controller per la gestione dei libri.
 * Requisiti: UC-1 a UC-5, IF-1.1 a IF-1.5
 * * @author Simona
 */
public class LibroController {
    
    @FXML
    public TableView<Libro> tabellaLibri;
    
    @FXML
    private TableColumn<Libro, String> colonnaTitolo;
    
    @FXML
    private TableColumn<Libro, String> colonnaAutori;
    
    @FXML
    private TableColumn<Libro, Integer> colonnaAnno;
    
    @FXML
    private TableColumn<Libro, String> colonnaIsbn;
    
    @FXML
    private TableColumn<Libro, String> colonnaCopie;
    
    @FXML
    private TextField fieldRicerca;
    
    @FXML
    private ComboBox<String> comboCriterio;
    
    private Biblioteca biblioteca;
    private ObservableList<Libro> listaLibri;
    
    /** Inizializza il controller e le colonne della tabella */
    @FXML
    public void initialize() {
        biblioteca = Biblioteca.getInstance();
        
        // Setup colonne tabella
        colonnaTitolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        
        colonnaAutori.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAutoriAsString())
        );
        
        colonnaAnno.setCellValueFactory(new PropertyValueFactory<>("annoPubblicazione"));
        colonnaIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        
        colonnaCopie.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getNumeroCopieDisponibili() + "/" + 
                cellData.getValue().getNumeroCopieTotali()
            )
        );
        
        // Evidenzia libri non disponibili
        tabellaLibri.setRowFactory(tv -> new TableRow<Libro>() {
            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);
                if (libro == null || empty) {
                    setStyle("");
                } else if (!libro.isDisponibile()) {
                    setStyle("-fx-background-color: #ffebee;");
                } else {
                    setStyle("");
                }
            }
        });
        
        // Setup combo criterio ricerca
        comboCriterio.setItems(FXCollections.observableArrayList(
            "TITOLO", "AUTORE", "ISBN"
        ));
        comboCriterio.getSelectionModel().selectFirst();
        
        // Carica dati iniziali
        aggiornaTabella();
    }
    
    /**
     * Apre il dialog per inserire un nuovo libro
     * Requisito: UC-1
     */
    @FXML
    private void inserisciLibro() {
        apriDialogLibro(null);
    }
    
    /**
     * Apre il dialog per modificare il libro selezionato
     * Requisito: UC-2
     */
    @FXML
    private void modificaLibro() {
        Libro selezionato = tabellaLibri.getSelectionModel().getSelectedItem();
        
        if (selezionato == null) {
            AlertHelper.mostraErrore("Errore", "Seleziona un libro da modificare");
            return;
        }
        
        apriDialogLibro(selezionato);
    }
    
    /**
     * Elimina il libro selezionato
     * Requisito: UC-3
     */
    @FXML
    private void eliminaLibro() {
        Libro selezionato = tabellaLibri.getSelectionModel().getSelectedItem();
        
        if (selezionato == null) {
            AlertHelper.mostraErrore("Errore", "Seleziona un libro da eliminare");
            return;
        }
        
        // Conferma cancellazione
        if (!AlertHelper.mostraConfermaCancellazione(
                "il libro '" + selezionato.getTitolo() + "'")) {
            return;
        }
        
        try {
            biblioteca.eliminaLibro(selezionato.getIsbn());
            aggiornaTabella();
            AlertHelper.mostraConferma("Successo", "Libro eliminato con successo");
        } catch (Exception e) {
            AlertHelper.mostraErrore("Errore", e.getMessage());
        }
    }
    
    /**
     * Cerca libri secondo il criterio selezionato
     * Requisito: UC-5
     */
    @FXML
    private void cercaLibro() {
        String criterio = fieldRicerca.getText().trim();
        String tipo = comboCriterio.getValue();
        
        if (criterio.isEmpty()) {
            AlertHelper.mostraErrore("Errore", "Inserisci un criterio di ricerca");
            return;
        }
        
        List<Libro> risultati = biblioteca.cercaLibri(criterio, tipo);
        
        if (risultati.isEmpty()) {
            AlertHelper.mostraInfo("Nessun risultato", 
                "Nessun libro trovato con i criteri specificati");
        }
        
        // Ordina i risultati della ricerca per titolo
        risultati.sort((l1, l2) -> 
            l1.getTitolo().compareToIgnoreCase(l2.getTitolo())
        );
        
        listaLibri = FXCollections.observableArrayList(risultati);
        tabellaLibri.setItems(listaLibri);
    }
    
    /**
     * Mostra tutti i libri
     * Requisito: UC-4
     */
    @FXML
    private void mostraTuttiLibri() {
        fieldRicerca.clear();
        aggiornaTabella();
    }
    
    /**
     * Aggiorna la tabella con tutti i libri
     * ORDINATI PER TITOLO
     */
    private void aggiornaTabella() {
        List<Libro> libri = biblioteca.getTuttiLibri();
        
        // Ordina per titolo (case-insensitive)
        libri.sort((l1, l2) -> 
            l1.getTitolo().compareToIgnoreCase(l2.getTitolo())
        );
        
        listaLibri = FXCollections.observableArrayList(libri);
        tabellaLibri.setItems(listaLibri);
    }
    
    /**
     * Apre il dialog per inserimento/modifica libro
     */
    private void apriDialogLibro(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/LibroDialog.fxml")
            );
            Scene scene = new Scene(loader.load());
            
            LibroDialogController controller = loader.getController();
            controller.setLibro(libro); // null per nuovo libro
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(libro == null ? "Inserisci Libro" : "Modifica Libro");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tabellaLibri.getScene().getWindow());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
            // Se confermato, aggiorna tabella
            if (controller.isConfermato()) {
                aggiornaTabella();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.mostraErrore("Errore", 
                "Impossibile aprire il dialog: " + e.getMessage());
        }
    }
}