# Design architetturale

Il design architetturale del sistema è stato definito a partire dai requisiti funzionali e non funzionali individuati nella fase di analisi. L’obiettivo principale è stato realizzare una struttura modulare, facilmente manutenibile ed estendibile, in grado di garantire una netta separazione delle responsabilità tra i diversi componenti del sistema.

## Model-View-Controller

Per la progettazione dell’architettura è stato adottato il pattern MVC (Model-View-Controller). Questo modello consente di separare in maniera chiara la logica dell’applicazione, la sua rappresentazione grafica e la gestione dell’interazione con l’utente.
I tre componenti principali del pattern svolgono i seguenti ruoli:

- **Model**: rappresenta la logica di business e i dati dell'applicazione. In questo progetto, il Model gestirà la mappa, i personaggi (Pac-Man e fantasmi) e le regole del gioco.
- **View**: si occupa della presentazione dei dati all'utente, senza contenere alcuna logica di business. La View mostrerà la mappa, il punteggio e le informazioni di gioco in tempo reale.
- **Controller**: gestisce l'interazione dell'utente e coordina le azioni tra Model e View. Il Controller riceverà gli input dell'utente e aggiornerà il Model e la View di conseguenza.

Questa separazione facilita la manutenibilità e l'estensibilità del codice, consentendo di apportare modifiche a una parte del sistema senza influenzare le altre. Questa scelta architetturale favorisce il raggiungimento degli obiettivi di modularità, riusabilità e scalabilità prefissati nel progetto.

## Struttura del progetto

<img src="./img/Design_architetturale.png" alt="Struttura del progetto" style="width: 50%; display: block; margin: 0 auto;">

<div style="text-align: center;">
*Struttura del progetto evidenziando Controller (blu); Model (rosso); View (giallo)*
</div>

---

Come da pattern MVC, la struttura del progetto è divisa in 3 moduli principali:

- **Model**: contiene le parti logiche del gioco.
    - `GameManager`: si occupa di gestire i movimenti all'interno del gioco (sfruttando la `GameMap`) e l'eventuale vittoria/sconfitta.
    - `GameMap`: rappresenta la mappa di gioco e si occupa di mantenerla aggiornata, fornendo anche metodi utili per posizionare/prendere/spostare gli elementi al suo interno.
    - `GhostBasic`: rappresenta i fantasmi all'interno del gioco.
    - `SpacMan`: rappresenta il Pacman all'interno del gioco. 

- **Controller**: coordina la comunicazione tra Model e View.
    - `GameController`: si occupa di gestire le varie schermate di gioco, comunicando con la view per la rappresentazione grafica e con il `GameManager` per la logica di gioco.
    - `GameLoop`: si occupa di gestire l'esecuzione del gioco in tempo reale.
    - `InputManager`: si occupa di prendere l'input del giocatore e mapparlo nella direzione corrispondente.

- **View**: contiente i componenti per la rappresentazione grafica dell'applicazione:
    - `GameView`: è il componente principale della view, contiene la rappresentazione grafica del gioco.
    - `SpriteLoader`: reperisce le immagini per gli sprites.

---

0. [Introduzione](../README.md)
1. [Processo di sviluppo](1-processo.md)
2. [Requisiti *(prev)*](2-requisiti.md)
3. [Design architetturale](3-architettura.md)
4. [**Design di dettaglio (next)**](4-design-dettaglio.md)
5. [Implementazione](5-implementazione.md)
6. [Testing](6-testing.md)
7. [Retrospettiva](7-retrospettiva.md)
