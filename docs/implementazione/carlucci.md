# Implementazione - Carlucci Francesco

## Panoramica dei contributi

Il mio contributo nel progetto si è focalizzato nelle seguenti aree:

- **Game Manager:** implementazione del game manager per gestire i movimenti nella mappa di SpacMan e dei fantasmi.
- **Collisions Manager:** implementazione del collisions manager per gestire le collisioni tra entità.
- **Input Manager:** implementazione della gestione degli input da tastiera.
- **Game Entity:** implementazione di entità come `DotBasic`, `DotPower`, `GhostBasic`.
- **Ghost Behaviors:** implementazione dei vari comportamenti dei fantasmi.
- **Game Controller:** implementazione del controller del gioco.
- **Testing:** scrittura dei test per i sistemi implementati, come `GameManagerTest`, `CollisionsManagerTest`, `GhostTest`.

## Game Manager

Il `GameManager` permette di muovere lo SpacMan e i fantasmi, aggiornare lo stato del gioco, gestire le collisioni, sapere se la partita è vinta, persa o in modalità inseguimento (chase) ed aggiornare il tempo di inseguimento.

Per mantenere lo stato del gioco è stata utilizzata la case class `GameState`, quindi ogni sua modifica, effettuata solamente dal `GameManager`, ne produce una nuova istanza tramite `copy`.

I metodi principali del GameManager sono:

- `moveGhosts()`: serve a muovere tutti i fantasmi e si articola così:

    1. Calcolo del movimento di un singolo fantasma tramite `attemptMove`, il cui compito è determinare se e come un fantasma può muoversi e se il movimento è valido;
    
    2. Applicazione del movimento sulla mappa tramite `applyMove`, che si occupa di sostituire il fantasma originale con quello spostato se l’aggiornamento va a buon fine;

    3. Tutti i fantasmi presenti nella mappa vengono processati tramite una riduzione (foldLeft) che per ogni fantasma esegue la `attemptMove` ed in caso di successo l'`applyMove`;

    4. Dopo aver completato i movimenti, il metodo verifica le collisioni tra ciascun fantasma e SpacMan tramite `checkGhostCollision` ed aggiorna, se necessario, lo stato del gioco;

- `moveSpacMan(direction: Direction)`: muove lo SpacMan in una determinata direzione:

    1. Viene controllato che SpacMan possa muoversi nella direzione richiesta;
    
    2. Se il movimento è valido viene creato un nuovo SpacMan nella posizione aggiornata;
    
    3. Dopo lo spostamento vengono recuperate tutte le entità presenti nella cella occupata da SpacMan per determinare se è avvenuta una collisione e di che tipo;
    
    4. In caso di collisione questa viene gestita tramite `applyCollisionEffect` e se la collisione ha prodotto degli effetti validi, lo stato del gioco viene aggiornato.

- `updateChaseTime(deltaTime: Long)`: aggiorna il tempo di inseguimento sottraendo il deltaTime passato dal `GameLoop`.

Gli elementi di Scala più rilevanti utilizzati sono:

- Uso di `Option` come controllo del flusso nel movimento dei fantasmi per evitare if/else annidati e compatibile naturalmente con il case matching:

    ```scala
    Option.when(currentMap.canMove(ghost, nextDirection)) {
        ghost.move(nextDirection).asInstanceOf[GhostBasic]
    }
    ```
- uso di `foldLeft`
    - per accumulare sia la mappa aggiornata che la lista dei ghost mossi:

        ```scala
        val (updatedMap, movedGhosts) =
            state.gameMap.getGhosts.foldLeft((state.gameMap, List.empty[GhostBasic])):
                case ((currentMap, ghosts), ghost) =>
        ```
    - ogni iterazione può modificare mappa, SpacMan, vite e flag gameOver

        ```scala
        val finalState =
            movedGhosts.foldLeft(state.copy(gameMap = updatedMap)):
                (currentState, ghost) =>
        ```

- pattern matching e gestione errori con `Either`:

    ```scala
    currentMap.replaceEntityTo(ghost, movedGhost) match
    case Right(updatedMap) => updatedMap
    case Left(error) =>
        println(s"Warning: Could not move ghost ${ghost.id} - $error")
        currentMap
    ```
    ```scala
    val updatedMapAfterMove = state.gameMap.replaceEntityTo(state.spacMan, movedSpacMan) match
        case Right(updatedMap) => updatedMap
        case Left(error) =>
            println(s"Warning: Could not move SpacMan - $error")
            return
    ```
- companion object per factory method per istanziare il manager senza new, secondo le convenzioni di Scala.

    ```scala
    object SimpleGameManager:
        def apply(...): SimpleGameManager = 
            new SimpleGameManager(GameState(...))
    ```

## Collisions Manager

Il `CollisionsManager` si occupa di gestire le collisioni tra le entità del gioco.

I metodi principali del GameManager sono:

- `detectCollision(entities: Set[GameEntity], direction: Direction)`: ha il compito di identificare se SpacMan entra in contatto con qualcosa nella cella in cui si è appena mosso, ritorna il tipo di collisione avvenuta.
- `applyCollisionEffect(...)`: ha il compito di gestire le collisioni tra le entità del gioco e ritorna la mappa aggiornata e lo SpacMan aggiornato.
- `handleGhostCollision(...)`: ha il compito di gestire le collisioni tra lo SpacMan e i fantasmi, distinguendo il caso in cui il gioco è in modalità inseguimento (chase) o normale.
- `checkGhostCollision(...)`: ha il compito di verificare se un fantasma ha colpito SpacMan, in caso affermativo chiama `handleGhostCollision`.

Gli elementi di Scala più rilevanti utilizzati sono:

- uso di `collectFirst` per rilevare le collisioni avvenute che combina filtro e mappatura in un’unica operazione:
    ```scala
    entities.collectFirst { case g: GhostBasic => GhostCollision(g) }
        .orElse(entities.collectFirst { case f: DotFruit => DotFruitCollision(f) })
        .orElse(entities.collectFirst { case p: DotPower => DotPowerCollision(p) })
        .orElse(entities.collectFirst { case d: DotBasic => DotBasicCollision(d) })
        .orElse(
            entities.collectFirst {
                case t: Tunnel if t.canTeleport(direction) => TunnelCollision(t)
            }
        )
        .getOrElse(NoCollision)
    ```
- uso di `Option` per indicare se c'è stato un aggiornamento delle entità o meno:
    ```scala
    def applyCollisionEffect(...): Option[(GameMap, SpacManWithLife)]
    ```
- pattern matching per gestire le collisioni avvenute:
    ```scala
    collision match
        case GhostCollision(ghost) => ...
        case DotBasicCollision(dot) => ...
        case DotPowerCollision(dot) => ...
        case DotFruitCollision(fruit) => ...
        case TunnelCollision(tunnel) => ...
        case NoCollision => ...
    ```
## Ghost Behaviour

Il `GhostBehaviour` si occupa di gestire il comportamento dei fantasmi.

I metodi principali del GhostBehaviour sono:

- `chooseDirection(context: GhostContext)`: è il metodo che ogni strategia concreta deve implementare. Il suo compito è decidere la direzione in cui il fantasma deve muoversi in un determinato istante di gioco, sulla base delle informazioni contenute nel GhostContext.

- `selectDirection(context: GhostContext)`: il suo ruolo è scegliere, tra le direzioni valide, quella “migliore” secondo un criterio fornito dall’esterno. Il suo funzionamento è il seguente:

    - riceve l’insieme delle direzioni percorribili;

    - per ciascuna direzione simula la posizione successiva del fantasma;

    - calcola la distanza tra quella posizione e un obiettivo (target);

    - seleziona la direzione che ottimizza tale distanza, secondo un ordinamento specificato.

Gli elementi di Scala più rilevanti utilizzati sono:

- selectDirection accetta un Ordering come parametro implicito esplicito:
    - consente di riutilizzare lo stesso algoritmo di selezione;
    - il comportamento varia semplicemente cambiando l’ordinamento;

    ```scala
    protected final def selectDirection(...)(ordering: Ordering[Int]): Direction =
        validDirs
            .map(dir => dir -> manhattanDistance(ghostPos.calculatePos(dir), targetPos))
            .minByOption(_._2)(ordering)
            .map(_._1)
            .getOrElse(currentDir)
    ```

- uso di pattern matching per predirre la posizione del target:
    ```scala
    private def predictTarget(context: GhostContext): Position2D =
        val offset = context.spacManDir match
            case Direction.Right => (PredictionDistance, 0)
            case Direction.Left  => (-PredictionDistance, 0)
            case Direction.Down  => (0, PredictionDistance)
            case Direction.Up    => (0, -PredictionDistance)
        Position2D(context.spacManPos.x + offset._1, context.spacManPos.y + offset._2)
    ```

- companion object come registry per i comportamenti dei fantasmi, espandibile e centralizzato:
    ```
    object GhostBehavior:
        private val behaviorRegistry: Map[Int, GhostBehavior] = Map(
            1 -> ChaseBehavior,
            2 -> PredictiveBehavior,
            3 -> RandomBehavior,
            4 -> MixedBehavior
        )
        
        def forId(id: Int): GhostBehavior = 
            behaviorRegistry.getOrElse(id, ChaseBehavior)
    ```

## Game Controller

Il `GameController` si occupa di inizializzare e gestire il gioco per permettere l'interazione tra View e Model nel pattern architetturale MVC. Quando il giocatore clicca sul tasto Gioca, il `GameController` inizializza il gioco, fa partire l'`InputManager` e avvia il loop di gioco tramite un thread separato.

Quando il `GameLoop` termina la sua esecuzione, il `GameController` notifica la `View` per aggiornare lo stato del gioco in base al risultato ottenuto.

```scala
private def handleFinalState(
        state: GameState,
        gameManager: GameManager,
        inputManager: InputManager,
        view: GameView
    ): Unit =
        inputManager.stop()
        state match
            case GameState.Win =>
                Swing.onEDT:
                    view.displayWin(gameManager.getState.spacMan.score)
            case GameState.GameOver =>
                Swing.onEDT:
                    view.displayGameOver(gameManager.getState.spacMan.score)
            case _ => ()
```

## Testing

Per quanto riguarda i test è stato largamente utilizzato l'approccio TDD, in quanto ha permesso di velocizzare il debug del codice in seguito a modifiche o aggiunte di funzionalità.

Inoltre la parte di model sviluppata è stata totalmente testata per raggiungere la più alta copertura del codice possibile, fatta eccezione dei rami di codice che riguardano la strategia di Programmazione Difensiva.

---

0. [Introduzione](../../README.md)
1. [Processo di sviluppo](../1-processo.md)
2. [Requisiti](../2-requisiti.md)
3. [Design architetturale](../3-architettura.md)
4. [Design di dettaglio](../4-design-dettaglio.md)
5. [Implementazione *(prev)*](../5-implementazione.md)
    - [Francesco Carlucci](./carlucci.md)
    - [**Marco Raggini (next)**](./raggini.md)
6. [Testing](../6-testing.md)
7. [Retrospettiva](../7-retrospettiva.md)
