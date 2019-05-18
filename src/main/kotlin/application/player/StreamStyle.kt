package application.player

import javafx.geometry.Pos
import tornadofx.*

class StreamStyle : Stylesheet() {

    companion object {
        val fontFiraCodeMedium = loadFont("/fonts/FiraCode-Medium.ttf", 16.0)

        val streamContainer by cssclass()
    }

    init {
        streamContainer {
            //            visibility = FXVisibility.COLLAPSE
            backgroundColor += c("#FF00FFff")
            alignment = Pos.CENTER
            maxWidth = 1280.px
            minWidth = 1280.px
            maxHeight = 720.px
            minHeight = 720.px
        }

        label {
            fontFiraCodeMedium?.let { font = it }
            textFill = c("#78cbab")
            fontSize = 10.px
        }
    }
}