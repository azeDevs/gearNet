package application.stream

import javafx.geometry.Pos
import tornadofx.*

class StreamStyle : Stylesheet() {

    companion object {
        val fontFiraCodeMedium = loadFont("/fonts/FiraCode-Medium.ttf", 16.0)

        val streamContainer by cssclass()
    }

    init {
        streamContainer {
            backgroundColor += c("#FF00FFff")
            alignment = Pos.CENTER
            maxWidth = 1280.px
            minWidth = 1280.px
            maxHeight = 720.px
            minHeight = 720.px
            visibility = FXVisibility.HIDDEN
        }

        label {
            fontFiraCodeMedium?.let { font = it }
            textFill = c("#78cbab")
            fontSize = 10.px
        }
    }
}