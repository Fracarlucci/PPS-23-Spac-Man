package view

import scala.swing.Label
import scala.swing.Alignment
import scala.swing.Font

private final val TITLE_SIZE  = 36
private final val NORMAL_SIZE = 25

object LabelFactory:
    def createTitleLabel(text: String): Label =
        new Label(text):
            horizontalAlignment = Alignment.Center
            font = new Font("Arial", java.awt.Font.BOLD, TITLE_SIZE)

    def createScoreLabel(score: Int): Label =
        new Label("Score: " + score):
            horizontalAlignment = Alignment.Center
            font = new Font("Arial", java.awt.Font.PLAIN, NORMAL_SIZE)
