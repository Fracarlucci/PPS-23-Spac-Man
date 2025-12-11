# Implementazione - Raggini Marco

## Panoramica dei contributi
Il mio contributo nel progetto si è focalizzato nelle seguenti aree:

- **Creazione e implementazione della** `GameMap`: implementazione di un **DSL** per semplificare la creazione della mappa, implementazione della `GameMap` e creazione concreta della mappa di gioco.
- **Implementazione delle entità di gioco**: in particolare, il contributo principale è dovuto dalla scelta architetturale di fare utilizzo di mixin per rendere estendibile lo `SpacMan`. Tra i contributi è presente anche la creazione del `Tunnel`, `Position2D`, `Direction`, `DotFruit` e il `WallBuilder`
- **Implementazione del** `GameLoop`: creazione di un loop per la gestione del flusso di gioco.
- **Sviluppo interfaccia utente**: implementazione della parte grafica del gioco.
- **Testing**: Scrittura dei test per i sistemi implementati, come `GameMapTest`, `TunnelTest`, `WallBuilderTest` e anche alcuni test presenti in altre classi.
## Mappa di gioco
### GameMap
La classe GameMap definisce le dimensioni dell’area di gioco, le posizioni di spawn e la disposizione degli oggetti presenti sulla griglia.

L'interfaccia espone un insieme di operazioni fondamentali:
- **Manipolazione dello stato della mappa**: inserimento, sostituzione e rimozione di entità.
- **Accesso alla mappa**: recupero delle entità presenti in una determinata cella o il recupero di determinati tipi di entità (`Ghost`, `Wall`, `Dot`)
- **Verifica del movimento**: verifica se un’entità può muoversi in una direzione specifica.

L’implementazione concreta GameMapImpl utilizza una struttura immutabile `Map[Position2D, Set[GameEntity]]` per rappresentare la griglia. Questo approccio è coerente con lo stile funzionale di Scala: ogni modifica restituisce una nuova versione della mappa, questa scelta permette di tracciare facilmente gli stati e semplifica test e debugging.

Tra gli aspetti rilevanti:
- **Largo uso di pattern matching**: quasi ogni metodo fa uso di pattern matching

- **Uso di Either per la gestione degli errori**: l'utilizzo degli `Either` viene dall'esigenza di gestire gli errori che potrebbero esserci durante l'utilizzo dei metodi della mappa, se non gestiti, questi errori possono portare ad eccezioni. Usando gli `Either` viene forzata la gestione degli errori evitando malfunzionamenti dell'applicazione.
- **GameMapFactory**: per la creazione di mappe vuote creando già la griglia. 

```scala
// Esempio di uso di pattern matching con Either
override def remove(entity: GameEntity): Either[String, GameMap] =
    grid.get(entity.position) match
        case Some(entities) => entities.contains(entity) match
                case true =>
                    Right(copy(grid = grid.updated(entity.position, entities - entity)))
                case false => Left("No entity found")
        case None => Left("Invalid position" + entity.position)
```

### DSL

## Contributi nelle entità di gioco
### SpacMan

## GameLoop
Nel progetto è stato implementato un game loop, ovvero il ciclo principale che gestisce l’intera esecuzione del gioco. Il suo scopo è mantenere un flusso continuo e controllato di aggiornamento dello stato di gioco e di rendering, permettendo così un comportamento fluido e costante.

Il game loop nasce dall’esigenza di separare in modo chiaro due operazioni fondamentali:
- Aggiornare la logica del gioco (movimenti, controlli, collisioni, fine della partita).
- Aggiornare l'interfaccia grafica in modo coerente e stabile.

Senza un loop dedicato, il gioco dipenderebbe direttamente dalla velocità di esecuzione della macchina che potrebbe essere diversa a seconda di essa, causando comportamenti imprevedibili, animazioni irregolari o rallentamenti. Inoltre questa classe consente di far comunicare l'`InputManager` con il `GameManager`, consentendo l'utilizzo della tastiera per il movimento dello SpacMan.

Il `GameLoop` è stato implementato attraverso una funzione ricorsiva che ritorna lo stato del gioco. Quando la partita termina, che sia vittoria o sconfitta, il loop finisce la sua esecuzione e ritorna l'esito della partita. Sono state inserite delle costanti che rappresentano il periodo temporale che passa tra un'azione di movimento e l'altra, in questo modo il movimento risulta costante.

```scala
// core della funzione loop
state match
    case GameState.Running | GameState.Chase =>
        if isTimeToMove(now, lastGhostMove, currentGhostDelay) then
            gameManager.moveGhosts()
            leatestGhostMove = now
        if isTimeToMove(now, lastPacmanMove, spacmanDelay) then
            val directionToMove = inputManager.processInput() match
                case Some(dir) => dir
                case None      => gameManager.getSpacMan.direction
            gameManager.moveSpacManAndCheck(directionToMove)
            leatestSpacManMove = now
            Swing.onEDT:
                view.update(
                    gameManager.getGameMap,
                    gameManager.getSpacMan.lives,
                    gameManager.getSpacMan.score
                )
        Thread.sleep(50)
        val newState = checkGameState(gameManager)
        loop(newState, leatestGhostMove, leatestSpacManMove, now)
    case finalState: GameState => finalState
```


## Interfaccia utente

## Testing