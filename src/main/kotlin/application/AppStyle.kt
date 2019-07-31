package application

import javafx.geometry.Pos
import tornadofx.*

class AppStyle : Stylesheet() {

    companion object {
        val fontFiraCodeRegular = loadFont("/fonts/FiraCode-Regular.ttf", 16.0)
        val fontFiraCodeBold = loadFont("/fonts/FiraCode-Bold.ttf", 16.0)
        val appContainer by cssclass()
        val debugConsole by cssclass()
        val debugWatchKey by cssclass()
        val debugWatchVal by cssclass()
        val debugContainer by cssclass()

        val debugFontSize = 14.px
        val debugViewHeight = 640.px
    }

    init {

        appContainer { backgroundColor += c("#FF00FFff") }

        debugContainer {

            padding = box(0.px, 6.px, 4.px, 6.px)
            backgroundColor += c("#00000066")
            borderColor += box(c("#333333"))
            borderWidth += box(2.px)
            minHeight = debugViewHeight
            maxHeight = debugViewHeight
        }

        label {
            fontFiraCodeBold?.let { font = it }
            alignment = Pos.BOTTOM_LEFT
            textFill = c("#cccccc")
            backgroundColor += c("#000000")
            borderColor += box(c("#ffff00"))
            borderWidth += box(1.px)
            minHeight = debugViewHeight-16
            maxHeight = debugViewHeight-16
            padding = box(0.px, 6.px, 4.px, 6.px)
            spacing = 4.px

            and(debugConsole) {
                fontSize = debugFontSize
                minWidth = 800.px
                maxWidth = 800.px
            }
            and(debugWatchKey) {
                fontSize = debugFontSize
                minWidth = 320.px
                maxWidth = 320.px
            }
            and(debugWatchVal) {
                fontSize = debugFontSize
                minWidth = 100.px
                maxWidth = 100.px
                textFill = c("#aaff33")
            }

        }
    }
}
