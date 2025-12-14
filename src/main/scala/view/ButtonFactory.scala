package view

import scala.swing.Button
import scala.swing.Dimension
import java.awt.Font

val BUTTON_BIG_HORIZONTAL_SIZE    = 300
val BUTTON_NORMAL_HORIZONTAL_SIZE = 100
val BUTTON_VERTICAL_SIZE          = 50

enum ButtonSize:
    case Big
    case Normal

object ButtonFactory:
    def apply(
        text: String,
        dimension: Dimension,
        fontType: Font = Font("Arial", java.awt.Font.BOLD, 20)
    ): Button =
        new Button(text):
            preferredSize = dimension
            font = fontType

    def dimension(size: ButtonSize): Dimension = size match
        case ButtonSize.Big    => new Dimension(BUTTON_BIG_HORIZONTAL_SIZE, BUTTON_VERTICAL_SIZE)
        case ButtonSize.Normal => new Dimension(BUTTON_NORMAL_HORIZONTAL_SIZE, BUTTON_VERTICAL_SIZE)
