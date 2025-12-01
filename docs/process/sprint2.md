# Sprint 2

## Obiettivo

L'obiettivo di questo secondo Sprint è quello di riuscire a presentare, al termine dello sprint, 
la logica di gioco completa o semi completa, senza interfaccia grafica.

## Deadline

La scadenza dello sprint è il 17/11/2025.

## Backlog

<table>
  <thead>
    <tr>
      <th>Priorità</th>
      <th>Nome</th>
      <th>Descrizione</th>
      <th>Sprint Task</th>
      <th>Volontario</th>
      <th>Stima iniziale</th>
      <th>Stima Sprint 1</th>
      <th>Stima Sprint 2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="5">1</td>
      <td rowspan="5">Organizzazione progetto</td>
      <td rowspan="5">Creare e configurare il repository GitHub, impostare il progetto Scala</td>
      <td>Git Flow Setup</td>
      <td>Francesco</td>
      <td>1</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Studio dell'architettura</td>
      <td>Marco e Francesco</td>
      <td>4</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Semantic Release e PR rules</td>
      <td>Marco</td>
      <td>2</td>
      <td>0</td>
      <td>0</td>
      <td></td>
    </tr>
    <tr>
        <td>Setup documentazione</td>
        <td>Francesco</td>
        <td>2</td>
        <td>0</td>
        <td>0</td>
    </tr>
    <tr>
        <td>Setup progetto Scala</td>
        <td>Marco</td>
        <td>1</td>
      <td>0</td>
        <td>0</td>
    </tr>
    <tr>
      <td rowspan="5">2</td>
      <td rowspan="5">Movimento nella mappa</td>
      <td rowspan="5">Come utente, vorrei muovere Pac-Man all'interno della mappa</td>
      <td>Creazione Pac-Man</td>
      <td>Francesco</td>
      <td>4</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Implementazione posizione e direzione</td>
      <td>Marco</td>
      <td>1</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Creazione mappa</td>
      <td>Marco</td>
      <td>3</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Input utente</td>
      <td>Francesco</td>
      <td>3</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>DSL mappa</td>
      <td>Marco</td>
      <td>8</td>
      <td>3</td>
      <td>0</td>
    </tr>
     <tr>
      <td rowspan="2">3</td>
      <td rowspan="2">Fantasmi</td>
      <td rowspan="2">Introduzione dei fantasmi nella mappa con movimenti semplici</td>
      <td>Implementazione fantasmi</td>
      <td>Marco</td>
      <td>1</td>
      <td>1</td>
      <td>0</td>
    </tr>
    <tr>
        <td>Creazione logica di movimento semplice</td>
        <td>Marco</td>
        <td>3</td>
        <td>3</td>
        <td>0</td>
    </tr>
     <tr>
      <td rowspan="3">4</td>
      <td rowspan="3">Muri e tunnel</td>
      <td rowspan="3">Come utente, mi aspetto di non poter oltrepassare un muro e di poter utilizzare i tunnel presenti nella mappa</td>
      <td>Creazione muri e builder</td>
      <td>Marco</td>
      <td>4</td>
      <td>4</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Gestione delle collisioni</td>
      <td>Francesco</td>
      <td>4</td>
      <td>4</td>
      <td>0</td>
    </tr>
      <tr>
      <td>Creazione dei tunnel</td>
      <td>Marco</td>
      <td>3</td>
      <td>3</td>
      <td>0</td>
    </tr>
     <tr>
      <td rowspan="1">5</td>
      <td rowspan="1">Punti</td>
      <td rowspan="1">Introduzione dei punti e dello score che permettono di concludere il gioco</td>
      <td>Creazione punti e assegnazione score</td>
      <td>Francesco</td>
      <td>1</td>
      <td>1</td>
      <td>0</td>
    </tr>
    <tr>
      <td rowspan="2">6</td>
      <td rowspan="2">Gestione generale del gioco</td>
      <td rowspan="2">Creazione di un manager che gestisce tutti gli elementi implementati in precedenza</td>
      <td>Implementazione del game manager</td>
      <td>Francesco</td>
      <td>10</td>
      <td>10</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Creazione di un game loop</td>
      <td>Marco</td>
      <td>3</td>
      <td>3</td>
      <td>0</td>
    </tr>
    <tr>
      <td rowspan="2">7</td>
      <td rowspan="2">Documentazione</td>
      <td rowspan="2">Creare una documentazione chiara ed esaustiva </td>
      <td>Documentazione</td>
      <td>Francesco</td>
      <td>4</td>
      <td>4</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Documentazione</td>
      <td>Marco</td>
      <td>4</td>
      <td>4</td>
      <td>3</td>
    </tr>
    </tbody>
</table>

---

## Sprint Review 
Lo stakeholder e gli sviluppatori sono contenti di questa settimana poichè gli obbiettivi prefissati sono stati raggiunti. Inoltre, è stato possibile presentare una demo da terminale che presentava la versione completa del gioco.

## Sprint Retrospective

La suddivisione del lavoro è risultata bilanciata. C'è stata una notevole riduzione delle dipendenze che ha permesso uno sviluppo del progetto in maniera più indipendente tra le parti.
Inoltre, è emersa la possibilità di una rifattorizzazione di alcune parti del codice per una maggiore leggibilità.

0. [Introduzione](../../README.md)
1. [Sprint 1](./sprint1.md)
2. [Sprint 2](./sprint2.md)
3. [**Sprint 3 (next)**](./sprint3.md)
4. [Sprint 4](./sprint4.md)
