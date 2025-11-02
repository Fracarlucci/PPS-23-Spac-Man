# Sprint 1

## Obiettivo

L'obiettivo di questo primo Sprint è quello di ottenere una demo funzionante nella quale l’utente potrà muovere Pac-Man all’interno della mappa.

## Deadline

La scadenza dello sprint è il 09/11/2025.

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
    </tr>
    <tr>
      <td>Studio dell'architettura</td>
      <td>Marco e Francesco</td>
      <td>4</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Semantic Release e PR rules</td>
      <td>Marco</td>
      <td>2</td>
      <td></td>
    </tr>
    <tr>
        <td>Setup documentazione</td>
        <td>Francesco</td>
        <td>3</td>
        <td>0</td>
    </tr>
    <tr>
        <td>Setup progetto Scala</td>
        <td>Marco</td>
        <td>1</td>
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
    </tr>
    <tr>
      <td>Creazione mappa</td>
      <td>Marco</td>
      <td>3</td>
      <td>0</td>
    </tr>
    <tr>
      <td>Input utente</td>
      <td>Francesco</td>
      <td>3</td>
      <td>0</td>
    </tr>
    <tr>
      <td>DSL mappa</td>
      <td>Marco</td>
      <td>5</td>
      <td>0</td>
    </tr>
    <!-- <tr>
      <td>Fantasmi statici</td>
      <td>Francesco</td>
      <td>3</td>
      <td>0</td>
    </tr> -->
    </tbody>
</table>

---

## Sprint Review 
Lo stakeholder e gli sviluppatori sono contenti di questa prima settimana poichè gli obbiettivi prefissati sono stati raggiunti. Inoltre, è stata presentata anche una demo da terminale dove era possibile testare il movimento del pacman attraverso l'input da tastiera, il che ha reso la presentazione molto più interattiva.

## Sprint Retrospective

La suddivisione del lavoro è risultata bilanciata. Ci sono stati dei piccoli problemi che sono stati previsti durante la fase di studio dell'architettura e sono i seguenti:
- Alcune implementazioni si sovrapponevano tra loro (es. il PacMan e le posizioni dovevano essere creati prima della mappa) e questo ha creato degli iniziali rallentamenti
- Le github action per la semantic release e per le github pages sono fallite per via delle git protection rules mal settate. Questo tipo di problema non è stato un imprevisto in quanto questo tipo di actions si avviano solo tramite commit sul main e quindi difficilmente testabili.

Un aspetto su cui il team si focalizzerà nel prossimo sprint è la riduzione delle dipendenze tra gli sviluppatori, così che ciascuno possa lavorare in modo più autonomo.

0. [Introduzione](../../README.md)
1. [Sprint 1](process/sprint1.md)
2. [**Sprint 2 (next)**](process/sprint2.md)
3. [Sprint 3](process/sprint3.md)
4. [Sprint 4](process/sprint4.md)
