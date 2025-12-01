package view

import scala.swing.Dimension
import scala.swing.Label
import scala.swing.Alignment
import scala.swing.Font

object LabelFactory:
    def createTitleLabel(text: String): Label =
        new Label(text):
            horizontalAlignment = Alignment.Center
            font = new Font("Arial", java.awt.Font.BOLD, 36)

    def createScoreLabel(score: Int): Label =
        new Label("Score: " + score):
            horizontalAlignment = Alignment.Center
            font = new Font("Arial", java.awt.Font.PLAIN, 25)
