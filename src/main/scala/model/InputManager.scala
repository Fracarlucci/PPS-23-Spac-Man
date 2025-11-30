package model

import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.JComponent

trait InputManager:
    def start(): Unit
    def processInput(): Option[Direction]
    def stop(): Unit

class SwingInputManager(component: JComponent) extends InputManager:
    @volatile private var pendingMove: Option[Direction] = None
    private var keyListener: KeyAdapter                  = _

    private def handleKey(keyCode: Int): Unit =
        keyCode match
            case KeyEvent.VK_W | KeyEvent.VK_UP    => pendingMove = Some(Direction.Up)
            case KeyEvent.VK_A | KeyEvent.VK_LEFT  => pendingMove = Some(Direction.Left)
            case KeyEvent.VK_S | KeyEvent.VK_DOWN  => pendingMove = Some(Direction.Down)
            case KeyEvent.VK_D | KeyEvent.VK_RIGHT => pendingMove = Some(Direction.Right)
            case _                                 =>
    def processInput(): Option[Direction] =
        pendingMove match
            case Some(dir) =>
                pendingMove = None
                Some(dir)
            case None => Option.empty

    def start(): Unit =
        keyListener = new KeyAdapter {
            override def keyPressed(e: KeyEvent): Unit =
                handleKey(e.getKeyCode)
        }

        component.addKeyListener(keyListener)
        component.setFocusable(true)
        component.requestFocusInWindow()

    def stop(): Unit =
        if keyListener != null then
            component.removeKeyListener(keyListener)
