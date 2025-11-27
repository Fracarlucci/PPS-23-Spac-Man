# Design di dettaglio

## Model

### Creazione mappa
Per la creazione della mappa si è optato per l'utilizzo dei **Factory Methods**, in particolare l'oggetto `GameMapFactory` permette la creazione di mappe vuote. 
Per agevolare e velocizzare il riempimento delle mappe(anche in funzione dei test), si è deciso di creare un **DSL**. Esso permette di piazzare le entità di dominio all'interno della mappa con un linguaggio naturale e più veloce. Inoltre offre la possibilità di creare e piazzare nella mappa file di muri.  

### Creazione entità del gioco
Qualsiasi entità di gioco viene rappresentata nell'applicazione come un'interfaccia di tipo `GameEntity`. Essa viene poi estesa dall'interfaccia `MovableEntity` per tutte le entità in grado di muoversi all'interno della mappa. Essendo i componenti di gioco molto semplici è stato deciso di definirli come case class, quindi dotate in automatico di metodi come **apply** per la creazione dell'oggetto. L'unica classe al quale è stato utilizzato l'approccio dei **Factory Methods** è il `Wall`, per la necessità di creare set di muri utilizzando un singolo metodo.


