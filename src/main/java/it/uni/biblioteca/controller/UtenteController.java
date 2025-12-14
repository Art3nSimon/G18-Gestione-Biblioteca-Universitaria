/* * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.controller;

import it.uni.biblioteca.controller.UtenteDialogController;
import it.uni.biblioteca.model.Biblioteca;
import it.uni.biblioteca.model.Utente;
import it.uni.biblioteca.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

/**
 *
 * @author Utente
 */
public class UtenteController {

    @FXML
    private TableView<Utente> tabellaUtenti;
    
    @FXML
    private TableColumn<Utente, String> colonnaMatricola;
    
    @FXML
    private TableColumn<Utente, String> colonnaCognome;
    
    @FXML
    private TableColumn<Utente, String> colonnaNome;
    
    @FXML
    private TableColumn<Utente, String> colonnaEmail;
    
    @FXML
    private TableColumn<Utente, String> colonnaPrestiti;  // CAMBIATO da Integer a String
    
    @FXML
    private TextField fieldRicerca;
    
    @FXML
    private ComboBox<String> comboCriterio;
    
    private Biblioteca biblioteca;
    private ObservableList<Utente> listaUtenti;

    /** Inizializza il controller */
    @FXML
    public void initialize() {
        biblioteca = Biblioteca.getInstance();
        
        /** Setup colonne tabella */
        colonnaMatricola.setCellValueFactory(new PropertyValueFactory<>("matricola"));
        colonnaCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        colonnaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colonnaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colonnaPrestiti.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(  // CAMBIATO SimpleObjectProperty in SimpleStringProperty
                cellData.getValue().getNumeroPrestitiAttivi() + "/3"
            )
        );
        
        /** Evidenzia utenti con limite prestiti raggiunto */
        tabellaUtenti.setRowFactory(tv -> new TableRow<Utente>() {
            @Override
            protected void updateItem(Utente utente, boolean empty) {
                super.updateItem(utente, empty);
                if (utente == null || empty) {
                    setStyle("");
                } else if (utente.haRaggiuntoLimite()) {
                    setStyle("-fx-background-color: #fff3cd;"); // Giallo warning
                } else {
                    setStyle("");
                }
            }
        });
        
        /** Setup combo criterio ricerca */
        comboCriterio.setItems(FXCollections.observableArrayList(
            "COGNOME", "MATRICOLA"
        ));
        comboCriterio.getSelectionModel().selectFirst();
        
        /** Carica dati iniziali */
        aggiornaTabella();
    }

    /**
     * Apre il dialog per inserire un nuovo utente
     * Requisito: UC-6
     */
    @FXML
    private void inserisciUtente() {
        apriDialogUtente(null);
    }

    /**
     * Apre il dialog per modificare l'utente selezionato
     * Requisito: UC-7
     */
    @FXML
    private void modificaUtente() {
        Utente selezionato = tabellaUtenti.getSelectionModel().getSelectedItem();
        
        if (selezionato == null) {
            AlertHelper.mostraErrore("Errore", "Seleziona un utente da modificare");
            return;
        }
        
        apriDialogUtente(selezionato);
    }

    /**
     * Elimina l'utente selezionato
     * Requisito: UC-8
     */
    @FXML
    private void eliminaUtente() {
        Utente selezionato = tabellaUtenti.getSelectionModel().getSelectedItem();
        
        if (selezionato == null) {
            AlertHelper.mostraErrore("Errore", "Seleziona un utente da eliminare");
            return;
        }
        
        /** Conferma cancellazione */
        if (!AlertHelper.mostraConfermaCancellazione(
                "l'utente '" + selezionato.getNomeCognome() + "'")) {
            return;
        }
        
        try {
            biblioteca.eliminaUtente(selezionato.getMatricola());
            aggiornaTabella();
            AlertHelper.mostraConferma("Successo", "Utente eliminato con successo");
        } catch (Exception e) {
            AlertHelper.mostraErrore("Errore", e.getMessage());
        }
    }

    /**
     * Cerca utenti secondo il criterio selezionato
     * Requisito: UC-10
     */
    @FXML
    private void cercaUtente() {
        String criterio = fieldRicerca.getText().trim();
        String tipo = comboCriterio.getValue();
        
        if (criterio.isEmpty()) {
            AlertHelper.mostraErrore("Errore", "Inserisci un criterio di ricerca");
            return;
        }
        
        List<Utente> risultati = biblioteca.cercaUtenti(criterio, tipo);
        
        if (risultati.isEmpty()) {
            AlertHelper.mostraInfo("Nessun risultato", 
                "Nessun utente trovato con i criteri specificati");
        }
        
        listaUtenti = FXCollections.observableArrayList(risultati);
        tabellaUtenti.setItems(listaUtenti);
    }

    /**
     * Mostra tutti gli utenti
     * Requisito: UC-9
     */
    @FXML
    private void mostraTuttiUtenti() {
        fieldRicerca.clear();
        aggiornaTabella();
    }

    /**
     * Aggiorna la tabella con tutti gli utenti
     */
    private void aggiornaTabella() {
        listaUtenti = FXCollections.observableArrayList(biblioteca.getTuttiUtenti());
        tabellaUtenti.setItems(listaUtenti);
    }

    /**
     * Apre il dialog per inserimento/modifica utente
     */
    private void apriDialogUtente(Utente utente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/UtenteDialog.fxml")
            );
            Scene scene = new Scene(loader.load());
            
            UtenteDialogController controller = loader.getController();
            controller.setUtente(utente); // null per nuovo utente
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(utente == null ? "Inserisci Utente" : "Modifica Utente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tabellaUtenti.getScene().getWindow());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
            // Se confermato, aggiorna tabella
            if (controller.isConfermato()) {
                aggiornaTabella();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.mostraErrore("Errore", 
                "Impossibile aprire il dialog: " + e.getMessage());
        }
    }
}