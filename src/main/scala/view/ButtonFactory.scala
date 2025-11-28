package view

import scala.swing.Button
import scala.swing.Dimension
import java.awt.Font

object ButtonFactory:
    def apply(text: String, dimension: Dimension, fontType: Font = Font("Arial", java.awt.Font.BOLD, 20)): Button = 
        new Button(text):
            preferredSize = dimension
            font = fontType


