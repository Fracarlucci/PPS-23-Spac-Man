# Processo di sviluppo

Il gruppo ha adottato una metodologia **Agile** per la gestione e lo sviluppo del progetto.

In particolare, è stato scelto il metodo **Scrum**, apprezzato per la sua flessibilità e capacità di adattarsi alle esigenze del team e del progetto, consentendo di produrre, a ogni iterazione, nuove funzionalità o miglioramenti delle esistenti.

Per la coordinazione del team e la gestione del progetto sono stati utilizzati i seguenti strumenti:

- **GitHub**: per la collaborazione tra i membri del team, il versionamento del codice e la gestione delle *pull request* al termine di ogni sprint.
- **Microsoft Teams**: per le riunioni e le comunicazioni tra i membri del gruppo.
- **Visual Studio Code**: come ambiente di sviluppo integrato (IDE) per la scrittura del codice.

Il team è stato organizzato nel seguente modo:

- **Product Owner**: responsabile della redazione del *Product Backlog* e della verifica del corretto funzionamento del sistema sviluppato.  
  *Ruolo ricoperto da Francesco Carlucci.*
- **Committente**: esperto del dominio applicativo, garante dell’usabilità e della qualità del risultato finale.  
  *Ruolo ricoperto da Marco Raggini.*
- **Sviluppatori**: tutti i membri del team hanno partecipato allo sviluppo:  
  - Francesco Carlucci  
  - Marco Raggini

## Modalità di divisione in itinere dei task

La suddivisione dei task è stata gestita in modo collaborativo durante le riunioni di pianificazione degli sprint, consentendo a tutti i membri del gruppo di partecipare attivamente alla definizione delle attività da svolgere.

Nel primo incontro (*Sprint Planning*) sono stati individuati i task principali e ne sono state assegnate le priorità in base alla rilevanza delle funzionalità, costituendo così il *Product Backlog*. In questa fase è stata inoltre definita la **Definition of Done**, secondo la quale una funzionalità si considera completata solo quando:

- è stata implementata e testata con esito positivo  
- rispetta i requisiti richiesti dall’utente  
- è stata revisionata e approvata da tutti i membri del team

Durante le successive pianificazioni degli sprint, i task sono stati ulteriormente scomposti in attività più piccole (*Sprint Backlog*), favorendo un’equa distribuzione del lavoro e una gestione più agevole delle attività operative.  
Al termine di ogni sprint, vengono inoltre redatti una **Sprint Review** e una **Sprint Retrospective**, volte rispettivamente a:

- valutare i progressi funzionali e il grado di soddisfazione dello stakeholder
- analizzare il processo di sviluppo e individuare eventuali aree di miglioramento

## Meeting e iterazioni pianificate

In una prima fase di analisi e progettazione, il gruppo ha svolto un meeting iniziale finalizzato alla definizione dell’architettura generale del progetto, durante il quale sono stati stabiliti anche la durata degli sprint e le modalità delle iterazioni successive.

Il team ha deciso di adottare *sprint settimanali/bisettimanali* in base alle disponibilità del team.

La scelta di organizzare sprint di lunghezza moderata è stata motivata dall’obiettivo di sviluppare funzionalità in tempi ridotti e ottenere feedback frequenti dallo stakeholder, mantenendo un equilibrio tra la qualità del prodotto e la velocità di sviluppo.

## Modalità di revisione in itinere dei task

La revisione del lavoro è stata gestita tramite un meccanismo basato sulle *pull request*.  
Ogni funzionalità è stata sviluppata in un branch dedicato e successivamente integrata nel branch *develop*, consentendo ai membri del team di lavorare in parallelo senza interferire con il flusso principale.

Al termine di ciascuno sprint, le modifiche consolidate in *develop* sono state unite nel branch *main* mediante *pull request*.
Per essere approvata, ogni *pull request* su develop ha dovuto ricevere l’approvazione di tutti i membri del gruppo.
Questo approccio ha garantito un aggiornamento costante tra i membri e un ulteriore livello di controllo e validazione del codice prima della sua integrazione definitiva.

## Scelta degli strumenti di test, build e Continuous Integration (CI)

Per il **testing automatico** è stato scelto **ScalaTest**, strumento noto e facilmente integrabile con l’ambiente di sviluppo.  
Come **build tool** è stato adottato **sbt**, pensato specificamente per progetti Scala.  
Per mantenere una formattazione coerente del codice è stato impiegato **scalafmt**, che assicura uno stile uniforme all’interno del team.

L’intero progetto e la relativa relazione sono stati gestiti tramite **GitHub**.  
Per automatizzare i processi di test e controllo qualità, è stata configurata una pipeline di Continuous Integration mediante **GitHub Actions**, la quale si attiva automaticamente a ogni nuova *pull request* sul branch di sviluppo.

I workflow configurati eseguono le seguenti azioni:

- **Build e test**: compilazione e test automatici del codice su più piattaforme (Ubuntu, Windows, macOS) e versioni di Java (17 e 21), garantendo compatibilità cross-platform e prevenendo regressioni. Il processo viene eseguito a ogni push, assicurando un monitoraggio continuo dello stato del software.  
- **Controllo della formattazione**: verifica della conformità del codice agli standard di formattazione definiti dal team tramite il comando `scalafmtCheckAll`, per mantenere una codebase coerente e leggibile.  
- **Validazione dei commit**: implementazione di un controllo automatico sui messaggi di commit, secondo le specifiche di **Conventional Commits**, al fine di garantire chiarezza e coerenza nella cronologia del progetto.
- **Rilascio automatico**: al momento del merge di una *pull request* sul branch *main* viene eseguito un rilascio automatico tramite **Semantic Release** che aggiorna la versione del software e genera le note di rilascio, semplificando la gestione delle release e garantendo una documentazione accurata di ogni nuova versione

---

0. [Introduzione *(prev)*](../README.md)
1. [Processo di sviluppo](1-processo.md)
2. [**Requisiti (next)**](2-requisiti.md)
3. [Design architetturale](3-architettura.md)
4. [Design di dettaglio](4-design-dettaglio.md)
5. [Implementazione](5-implementazione.md)
    - [Francesco Carlucci](./implementazione/carlucci.md)
    - [Marco Raggini](./implementazione/raggini.md)
6. [Testing](6-testing.md)
7. [Retrospettiva](7-retrospettiva.md)
