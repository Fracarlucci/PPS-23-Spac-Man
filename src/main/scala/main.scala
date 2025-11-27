import controller.GameController
import javax.swing.SwingUtilities

@main def main(): Unit =
    SwingUtilities.invokeLater(() => {
        GameController.showHome()
    })

    Thread.currentThread().join()
