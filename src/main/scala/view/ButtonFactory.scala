package view

import scala.swing.Button
import scala.swing.Dimension
import java.awt.Font

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
        case ButtonSize.Big    => new Dimension(300, 50)
        case ButtonSize.Normal => new Dimension(100, 50)
