# Implementazione - Raggini Marco

## Panoramica dei contributi
Il mio contributo nel progetto si è focalizzato nelle seguenti aree:

- **Creazione e implementazione della** `GameMap`: implementazione di un **DSL** per semplificare la creazione della mappa, implementazione della `GameMap` e creazione concreta della mappa di gioco.
- **Implementazione delle entità di gioco**: in particolare, il contributo principale riguarda la scelta architetturale di adottare i mixin per rendere estendibile lo `SpacMan`. Tra i contributi è presente anche la creazione del `Tunnel`, `Position2D`, `Direction`, `DotFruit` e il `WallBuilder`
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

L’implementazione concreta GameMapImpl utilizza una struttura immutabile `Map[Position2D, Set[GameEntity]]` per rappresentare la griglia. Questo approccio è coerente con lo stile funzionale in cui ogni modifica restituisce una nuova versione della mappa. Questa scelta permette di tracciare facilmente gli stati e semplifica test e debugging.

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
Il **DSL** proposto ha come obbiettivo quello di rendere il codice per la creazione della mappa molto più leggibile e al tempo stesso che facilitasse la creazione di entità da inserire nella mappa.

Si è voluto dare particolare enfasi nel creare un DSL che a prima vista non sembrasse codice e nel renderlo simile ad un linguaggio naturale.
Le azioni possibili sono tre:

- `place the`: permette di piazzare nella mappa oggetti già creati in precedenza, alcuni oggetti come ad esempio lo SpacMan molto spesso sono già creati in precedenza per via delle variabili che si possono settare al suo interno ed anche perché si vuole mantenere un riferimento ad esso. In questo caso non conviene creare semplicemente piazzare l'entità prendendo il riferimento all'oggetto.
```scala
// esempio di 'place the'
val dsl    = MapDSL(board(5, 5))
val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)
import dsl.*
place the pacMan
```
- `place multiple`: la funzione è simile alla precedente, ma questa volta è permesso il piazzamento di più entità contemporaneamente. Anche in questo caso si tratta di piazzare oggetti già creati in precedenza, può tornare utile, ad esempio, per piazzare dei fantasmi.
```scala
// esempio di 'place multiple'
val dsl    = MapDSL(map)
val ghost1 = GhostBasic(Position2D(5, 1), Direction.Right, 1.0, 1)
val ghost2 = GhostBasic(Position2D(4, 1), Direction.Right, 1.0, 1)
val ghost3 = GhostBasic(Position2D(3, 1), Direction.Right, 1.0, 1)
val ghosts = Set(ghost1, ghost2, ghost3)

import dsl.*

place multiple ghosts
// oppure anche 
place multiple Set(ghost1, ghost2, ghost3)
```
- `place a genericEntity at position x`: questo metodo è stato pensato per tutte le entità in cui non c'è bisogno di una creazione precedente dell'oggetto e in cui il riferimento nella mappa è sufficiente. In questo modo attraverso questo metodo è possibile creare l'entità e piazzarla nello stesso momento. Per fare ciò è stato creato un enum che memorizza il tipo dell'entità, che poi verrà utilizzato dal DSL per la creazione dell'entità e il successivo piazzamento nella mappa. Per rendere il codice ancora più 'human-like', i casi dell'enum sono stati memorizzati in variabili.
```scala
val dsl    = MapDSL(map)
import dsl.*
// crea un DotBasic alla posizione (1, 1) e lo piazza nella mappa
place a genericDot at position(1, 1)
```
C'è un'ultima casistica disponibile in questo momento solo per i muri che serve a facilitare la creazione di più muri contemporaneamente ed è la seguente `place a genericWall from position x to position y`:
```scala
val dsl    = MapDSL(map)
import dsl.*
// crea e piazza i muri: Wall(0, 0), Wall(0, 1), ..., Wall(0, 5)
place a genericWall from position(0, 0) to position(0, 5)
```
### Creazione mappa senza DSL
```scala
val map    = GameMapImpl(30, 30)
val ghost1 = GhostBasic(Position2D(3, 3), Direction.Down, 1.0, 1)
val ghost2 = GhostBasic(Position2D(25, 3), Direction.Up, 1.0, 2)
val ghost3 = GhostBasic(Position2D(3, 17), Direction.Left, 1.0, 3)
val ghost4 = GhostBasic(Position2D(25, 13), Direction.Right, 1.0, 4)
val spacman = SpacManWithLife(Position2D(1, 1), Direction.Left, 0)
val dot     = DotBasic(Position2D(25, 18))
val dp      = DotPower(Position2D(2, 2))
val fruit   = DotFruit(Position2D(15, 12))
val walls   = WallBuilder.createWalls(Position2D(0, 0), Position2D(0, 10))

// meno leggibile, più lungo da scrivere
map = map.placeAll(Set(ghost1, ghost2, ghost3, ghost4))
map = map.place(spacman)
map = map.place(dot)
map = map.place(dp)
map = map.place(fruit)
map = map.placeAll(walls)
```
### Creazione mappa con DSL 
```scala
val dsl = MapDSL(board(30, 30))
val ghost1 = GhostBasic(Position2D(3, 3), Direction.Down, 1.0, 1)
val ghost2 = GhostBasic(Position2D(25, 3), Direction.Up, 1.0, 2)
val ghost3 = GhostBasic(Position2D(3, 17), Direction.Left, 1.0, 3)
val ghost4 = GhostBasic(Position2D(25, 13), Direction.Right, 1.0, 4)
val spacman = SpacManWithLife(Position2D(1, 1), Direction.Left, 0)
import dsl.*

// più facile da scrivere e leggibile 
place multiple Set(ghost1, ghost2, ghost3, ghost4)
place the spacman
place a genericDot at position(25, 18)
place a genericDotPower at position(2, 2)
place a genericDotFruit at position(15, 12)
place a genericWall from position(0, 0) to position(0, 10)
```

## Contributi nelle entità di gioco
I principali contributi riguardano le classi: `SpacMan`, `Tunnel`, `Position2D`, `Direction`, `DotFruit` e il `WallBuilder`. Non ci sono particolari note da fare, tranne per `SpacMan`, di cui parlerò nel sotto capitolo seguente e il `WallBuilder`. Questa factory permette la creazione di più muri partendo da una posizione iniziale ed una finale. Essa è in grado di riconoscere in autonomia quali muri devono essere creati e in che direzione. In particolare, le possibilità possono essere quattro: **Verticale**, **Orizzontale**, **Singolo** in caso di posizione iniziale e finale uguali, ed infine **Complesso**, che riguarda la creazione di quadrati o rettangoli quando la posizione iniziale differisce completamente con la posizione finale (es. posizione iniziale (0, 0) e posizione finale (5, 5)).

```scala
object WallBuilder:

  def createWalls(startPos: Position2D, endPos: Position2D): Set[Wall] =
    BuildDirection.understandBuildDirection(startPos, endPos) match
      case BuildDirection.Horizontal => createHorizontalWall(startPos, endPos)
      case BuildDirection.Vertical   => createVerticalWall(startPos, endPos)
      case BuildDirection.Complex    => createComplexWall(startPos, endPos)
      case BuildDirection.Single     => Set(Wall(startPos))
```
### SpacMan
Per l'implementazione dello SpacMan ho deciso di utilizzare un approccio basato su mixin, al fine di comporre l’entità di gioco combinando diversi comportamenti. Questa scelta consente di:
- separare le responsabilità (in questo caso movimento, vite e punteggio)
- favorisce il riuso di codice in quanto le interfacce sono già implementate
- classi e trait facilmente manutenibili ed estendibili

In questo caso i trait implementati sono `Life` e `Score`, progettati in modo da rendere la classe che li utilizza **immutabile**, restituendo una nuova istanza dell’oggetto a ogni modifica di stato.
```scala
trait Life[E <: Life[E]]:
    val lives: Int
    def addLife(): E =
        val newLives = lives + 1
        updateLife(newLives)
    def removeLife(): E =
        require(lives > 0)
        val newLives = lives - 1
        updateLife(newLives)
    protected def updateLife(newLives: Int): E

trait Score[E <: Score[E]]:
    val score: Int
    def addScore(points: Int): E =
        if points >= 0 then updateScore(score + points)
        else updateScore(score)
    protected def updateScore(points: Int): E

case class SpacManWithLife(
    position: Position2D,
    direction: Direction,
    score: Int,
    val lives: Int = DEFAULT_LIVES
) extends MovableEntity with Life[SpacManWithLife] with Score[SpacManWithLife]:
```
La scelta di utilizzare l’**F-bounded polymorphism** è stata adottata per garantire la type safety.
In assenza di questo vincolo, un tipo generico `E` non avrebbe garantito che i metodi restituissero il tipo concreto dell’oggetto, rendendo necessari cast espliciti e introducendo il rischio di errori a runtime.
Il vincolo `E <: Life[E]` (e analogamente per Score) assicura invece che le operazioni restituiscano sempre un’istanza del tipo corretto.

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
Per quanto riguarda l'interfaccia utente, ho lavorato su tutte le classi presenti che appartengono alla view. La libreria utilizzata per la rappresentazione grafica è **Scala Swing**, il noto framework di Java adattato a Scala, in modo da scrivere codice meno verboso e più funzionale. 

Come già specificato nel [design di dettaglio](4-design-dettaglio.md), `GameView` è il componente principale della GUI. Esso utilizza le classi `GameMapPanel` e `InfoPanel` per rappresentare l'interfaccia di gioco durante una partita e `ButtonFactory` e `LabelFactory` per costruire le schermate di vittoria/sconfitta.

Viene anche utilizzato lo `SpriteLoader` per tenere in cache le immagini delle entità di gioco o caricarle in caso non siano ancora in cache. 
## Testing
Come già accennato nella sezione precedente, i test sono stati fondamentali per garantire la qualità del codice e per facilitare il processo di sviluppo, specialmente durante la rifattorizzazione, e sono stati scritti con particolare attenzione alla leggibilità. 

È stato posto un impegno significativo nel raggiungere una percentuale di **code coverage** di almeno il **90%** per le classi appartenenti al model. Questo approccio ha permesso di individuare la maggior parte degli errori nelle prime fasi dell’implementazione e di garantire una maggiore stabilità del sistema anche in presenza di piccoli cambiamenti al codice.

Il **DSL** è stato ampiamente utilizzato per il testing della mappa e, sebbene non riduca in modo significativo il numero di righe di codice, ha consentito una maggiore velocità di scrittura dei test e una migliore leggibilità degli stessi.

---

0. [Introduzione](../../README.md)
1. [Processo di sviluppo](../1-processo.md)
2. [Requisiti](../2-requisiti.md)
3. [Design architetturale](../3-architettura.md)
4. [Design di dettaglio](../4-design-dettaglio.md)
5. [Implementazione](../5-implementazione.md)
    - [Francesco Carlucci *(prev*)](./carlucci.md)
    - [Marco Raggini](./raggini.md)
6. [**Testing (next)**](../6-testing.md)
7. [Retrospettiva](../7-retrospettiva.md)

