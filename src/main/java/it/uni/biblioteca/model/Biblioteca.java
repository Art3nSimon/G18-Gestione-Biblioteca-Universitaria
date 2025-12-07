/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uni.biblioteca.model;

import it.uni.biblioteca.model.Libro;
import it.uni.biblioteca.model.Prestito;
import it.uni.biblioteca.model.Utente;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Matteo
 */
public class Biblioteca implements Serializable {
    private static final String FILE_DATI = "biblioteca_data.ser";
    
    //Attributi Biblioteca
    private List<Libro> libri;
    private List<Utente> utenti;
    private List<Prestito> prestiti;
    private static Biblioteca instance;
    
    //Costrutto Biblioteca
    private Biblioteca() {
        this.libri = new ArrayList<>();
        this.utenti = new ArrayList<>();
        this.prestiti = new ArrayList<>();
    }
    
    //Metodo get Biblioteca
    public static Biblioteca getInstance() {
        if (instance == null) {
            instance = new Biblioteca();
        }
        return instance;
    }
    
    //Gestione Libri
    public void aggiungiLibro(Libro libro) throws Exception {
        if (libro.getTitolo() == null || libro.getTitolo().trim().isEmpty()) {
            throw new Exception("Titolo obbligatorio");
        }
        if (cercaLibroPerIsbn(libro.getIsbn()) != null) {
            throw new Exception("ISBN già esistente");
        }
        if (libro.getAutori().isEmpty()) {
            throw new Exception("Almeno un autore obbligatorio");
        }
        libri.add(libro);
        salvaDati();
    }
    
    public void modificaLibro(Libro libro) throws Exception {
        for (int i = 0; i < libri.size(); i++) {
            if (libri.get(i).getIsbn().equals(libro.getIsbn())) {
                libri.set(i, libro);
                salvaDati();
                return;
            }
        }
        throw new Exception("Libro non trovato");
    }
    
    public void eliminaLibro(String isbn) throws Exception {
        for (Prestito p : prestiti) {
            if (p.isAttivo() && p.getLibro().getIsbn().equals(isbn)) {
                throw new Exception("Impossibile eliminare: prestiti attivi");
            }
        }
        libri.removeIf(l -> l.getIsbn().equals(isbn));
        salvaDati();
    }
    
    public List<Libro> getTuttiLibri() {
        List<Libro> ordinati = new ArrayList<>(libri);
        ordinati.sort((l1, l2) -> l1.getTitolo().compareToIgnoreCase(l2.getTitolo()));
        return ordinati;
    }
    
    public List<Libro> cercaLibri(String criterio, String tipo) {
        String critLower = criterio.toLowerCase();
        return libri.stream()
            .filter(l -> {
                if ("TITOLO".equals(tipo)) {
                    return l.getTitolo().toLowerCase().contains(critLower);
                } else if ("AUTORE".equals(tipo)) {
                    return l.getAutoriAsString().toLowerCase().contains(critLower);
                } else if ("ISBN".equals(tipo)) {
                    return l.getIsbn().equals(criterio);
                }
                return false;
            })
            .collect(Collectors.toList());
    }
    
    public Libro cercaLibroPerIsbn(String isbn) {
        for (Libro l : libri) {
            if (l.getIsbn().equals(isbn)) return l;
        }
        return null;
    }
    
    //Gestione Utenti
    public void aggiungiUtente(Utente utente) throws Exception {
        if (utente.getNome() == null || utente.getNome().trim().isEmpty()) {
            throw new Exception("Nome obbligatorio");
        }
        if (cercaUtentePerMatricola(utente.getMatricola()) != null) {
            throw new Exception("Matricola già esistente");
        }
        if (!utente.getEmail().contains("@")) {
            throw new Exception("Email non valida");
        }
        utenti.add(utente);
        salvaDati();
    }
    
    public void modificaUtente(Utente utente) throws Exception {
        for (int i = 0; i < utenti.size(); i++) {
            if (utenti.get(i).getMatricola().equals(utente.getMatricola())) {
                utenti.set(i, utente);
                salvaDati();
                return;
            }
        }
        throw new Exception("Utente non trovato");
    }
    
    public void eliminaUtente(String matricola) throws Exception {
        // Verifica nessun prestito attivo
        for (Prestito p : prestiti) {
            if (p.isAttivo() && p.getUtente().getMatricola().equals(matricola)) {
                throw new Exception("Impossibile eliminare: prestiti attivi");
            }
        }
        
        utenti.removeIf(u -> u.getMatricola().equals(matricola));
        salvaDati();
    }
    
    public List<Utente> getTuttiUtenti() {
        List<Utente> ordinati = new ArrayList<>(utenti);
        ordinati.sort((u1, u2) -> {
            int cmp = u1.getCognome().compareToIgnoreCase(u2.getCognome());
            if (cmp == 0) {
                return u1.getNome().compareToIgnoreCase(u2.getNome());
            }
            return cmp;
        });
        return ordinati;
    }
    
    public List<Utente> cercaUtenti(String criterio, String tipo) {
        String critLower = criterio.toLowerCase();
        return utenti.stream()
            .filter(u -> {
                if ("COGNOME".equals(tipo)) {
                    return u.getCognome().toLowerCase().contains(critLower);
                } else if ("MATRICOLA".equals(tipo)) {
                    return u.getMatricola().equals(criterio);
                }
                return false;
            })
            .collect(Collectors.toList());
    }
    
    public Utente cercaUtentePerMatricola(String matricola) {
        for (Utente u : utenti) {
            if (u.getMatricola().equals(matricola)) return u;
        }
        return null;
    }
    
    //Gestione Prestiti
    public void registraPrestito(String matricola, String isbn, LocalDate dataRestituzione) throws Exception {
        Utente utente = cercaUtentePerMatricola(matricola);
        if (utente == null) {
            throw new Exception("Utente non trovato");
        }
        Libro libro = cercaLibroPerIsbn(isbn);
        if (libro == null) {
            throw new Exception("Libro non trovato");
        }
        if (!libro.isDisponibile()) {
            throw new Exception("Nessuna copia disponibile");
        }
        if (utente.haRaggiuntoLimite()) {
            throw new Exception("Limite prestiti raggiunto (3/3)");
        }
        if (dataRestituzione.isBefore(LocalDate.now())) {
            throw new Exception("La data deve essere futura");
        }
        Prestito prestito = new Prestito(utente, libro, dataRestituzione);
        prestiti.add(prestito);
        libro.decrementaCopie();
        utente.aggiungiPrestito(prestito);
        salvaDati();
    }
    
    public void registraRestituzione(Prestito prestito) throws Exception {
        if (!prestito.isAttivo()) {
            throw new Exception("Prestito già chiuso");
        }
        prestito.registraRestituzione();
        prestito.getLibro().incrementaCopie();
        prestito.getUtente().rimuoviPrestito(prestito);
        salvaDati();
    }
    
    public List<Prestito> getPrestitiAttivi() {
        List<Prestito> attivi = prestiti.stream()
            .filter(Prestito::isAttivo)
            .collect(Collectors.toList());
        attivi.sort((p1, p2) -> 
            p1.getDataRestituzionePrevista().compareTo(p2.getDataRestituzionePrevista())
        );
        return attivi;
    }
    
    //Gestione Dati
    public void salvaDati() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_DATI))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Errore salvataggio: " + e.getMessage());
        }
    }
    
    public void caricaDati() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_DATI))) {
            Biblioteca caricata = (Biblioteca) ois.readObject();
            this.libri = caricata.libri;
            this.utenti = caricata.utenti;
            this.prestiti = caricata.prestiti;
        } catch (FileNotFoundException e) {
            System.out.println("Nessun file dati trovato, inizio con dati vuoti");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore caricamento: " + e.getMessage());
        }
    }
}