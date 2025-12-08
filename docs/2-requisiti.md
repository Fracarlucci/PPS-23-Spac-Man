# Requisiti

L’analisi del problema svolta nella prima fase del progetto ha permesso di evidenziare i requisiti elencati di seguito.

## Requisiti di business

- Creare un sistema fedele alla versione classica
- Realizzare il progetto entro un mese e mezzo dall’avvio del progetto
- Rilasciare versioni stabili del software in modo regolare e automatizzato
- Ottenere un feedback positivo dallo stakeholder al termine di ogni sprint

## Modello di dominio

Il dominio del progetto si basa sui seguenti elementi principali:

- **Mappa**: l'ambiente di gioco, costituito da muri, teletrasporti e puntini da mangiare
- **Spac-Man**: il giocatore controllato dall'utente, che si muove all'interno della mappa per mangiare *tutti* i puntini ed evitare i fantasmi
- **Fantasma**: nemico controllato dal sistema, che si muove autonomamente all'interno della mappa cercando di prendere Spac-Man
- **Puntino**: oggetto posizionato sulla mappa che Spac-Man deve mangiare per completare il gioco

## Requisiti funzionali

### Utente

- **RFU1 - Interazione di gioco**

  - Controllare Pac-Man tramite input da tastiera (frecce direzionali o WASD) per muoversi nelle quattro direzioni
  - Raccogliere i punti ("dots") presenti nel labirinto
  - Utilizzare i teletrasporti per attraversare da un lato all'altro del labirinto
  - Visualizzare in tempo reale il punteggio accumulato

- **RFU2 - Fine partita**

  - Essere informato quando la partita termina (Game Win/Game Over)
  - Visualizzare il punteggio finale raggiunto

### Di sistema

- **RFS1 - Gestione del labirinto**
  - Definire e mantenere la struttura del labirinto con celle percorribili, muri, punti e teletrasporti
  - Impedire a Pac-Man di attraversare i muri durante il movimento
  - Gestire correttamente il comportamento dei teletrasporti

- **RFS2 - Gestione del movimento e delle collisioni**

  - Rilevare e processare gli input dell'utente per il movimento di Pac-Man
  - Aggiornare la posizione di Pac-Man in base agli input validi
  - Muovere autonomamente i quattro fantasmi nel labirinto
  - Rilevare le collisioni tra Pac-Man e i fantasmi
  - Rilevare quando Pac-Man raccoglie i dots

- **RFS3 - Gestione del punteggio**

  - Calcolare e aggiornare il punteggio in tempo reale:
    - +10 punti per ogni "dot" raccolto
  - Mantenere traccia del punteggio durante tutta la partita

- **RFS4 - Gestione dello stato di gioco**

  - Monitorare lo stato della partita
  - Riconoscere automaticamente le condizioni di fine partita:
    - Game Win: quando tutti i dots sono stati raccolti
    - Game Over: quando Pac-Man viene catturato da un fantasma

- **RFS5 - Interfaccia e visualizzazione**

  - Renderizzare graficamente il labirinto, Pac-Man, i fantasmi e gli elementi collezionabili
  - Aggiornare la visualizzazione del punteggio
  - Mostrare il menu iniziale con il pulsante "Gioca"

## Requisiti non funzionali

- **RNF1 - Prestazioni**
  - Il tempo di risposta ai comandi dell’utente deve essere sufficientemente rapido.

- **RNF2 - Usabilità**
  - I controlli devono essere intuitivi.
  - L’interfaccia deve essere chiara, leggibile e adatta a schermi di diverse risoluzioni.

- **RNF3 - Affidabilità**
  - Il sistema deve gestire correttamente collisioni e stati dei personaggi senza crash o blocchi.

- **RNF4 - Manutenibilità**
  - Il codice deve essere modulare, documentato e facilmente estendibile.

- **RNF5 - Scalabilità**
  - Il sistema deve poter supportare:
    - Aggiunta di nuovi tipi di nemici.
    - Modifiche ai parametri di velocità e intelligenza dei fantasmi.
    - Introduzione di power-up aggiuntivi.
    - Nuove mappe.

- **RNF6 - Testabilità**
  - testing per validare il corretto funzionamento del sistema.

## Requisiti di implementazione

- **RI1 - Linguaggio di programmazione**
  - Il sistema deve essere implementato in Scala.

- **RI2 - MVC**
  - Il progetto deve seguire il pattern architetturale Model-View-Controller (MVC) per separare la logica, l’interfaccia utente e il controllo del flusso.
  
- **RI3 - Testing**
  - Utilizzo del Test-Driven Development (TDD).

- **RI4 - Collaborazione tra sviluppatori**
  - Utilizzo di GitHub per la gestione del codice sorgente, della documentazione e del versioning.

---

0. [Introduzione](../README.md)
1. [Processo di sviluppo *(prev)*](1-processo.md)
2. [Requisiti](2-requisiti.md)
3. [**Design architetturale (next)**](3-architettura.md)
4. [Design di dettaglio](4-design-dettaglio.md)
5. [Implementazione](5-implementazione.md)
6. [Testing](6-testing.md)
7. [Retrospettiva](7-retrospettiva.md)
