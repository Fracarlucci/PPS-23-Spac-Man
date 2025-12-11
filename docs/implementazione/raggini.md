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