package view

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object SpriteLoader:
    private val cache = scala.collection.mutable.Map[String, BufferedImage]()

    def load(name: String): Option[BufferedImage] =
        cache.get(name).orElse {
            try
                val stream = getClass.getResourceAsStream(s"/sprites/$name.png")
                if stream != null then
                    val image = ImageIO.read(stream)
                    cache(name) = image
                    Some(image)
                else
                    println(s"Sprite not found: $name")
                    None
            catch
                case e: Exception =>
                    println(s"Error loading sprite $name: ${e.getMessage}")
                    None
        }

    def preloadAll(): Unit =
        val sprites = List(
          "pacman_right",
          "pacman_left",
          "pacman_up",
          "pacman_down",
          "ghost_inky",
          "ghost_pinky",
          "ghost_clyde",
          "ghost_blue",
          "ghost_blinky",
          "arrow_left",
          "arrow_right",
          "arrow_down",
          "arrow_up",
          "wall",
          "dot",
          "apple",
          "strawberry",
          "fruit"
        )
        sprites.foreach(load)
