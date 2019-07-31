package application

import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import tornadofx.*

class AppStyle : Stylesheet() {

    companion object {
        val fontFiraCodeLight = loadFont("/fonts/FiraCode-Light.ttf", 16.0)
        val fontFiraCodeRegular = loadFont("/fonts/FiraCode-Regular.ttf", 16.0)
        val fontFiraCodeBold = loadFont("/fonts/FiraCode-Bold.ttf", 16.0)
        val appContainer by cssclass()
        val debugConsole by cssclass()
        val debugWatchKey by cssclass()
        val debugWatchVal by cssclass()
        val debugContainer by cssclass()
    }

    init {

        appContainer {
            backgroundColor += c("#FF00FFff")
        }

        debugContainer {
            padding = box(0.px, 6.px, 4.px, 6.px)
            backgroundColor += c("#00000066")
            borderColor += box(c("#333333"))
            borderWidth += box(2.px)
        }

        label {
            fontFiraCodeRegular?.let { font = it }
            alignment = Pos.CENTER_LEFT
            textFill = c("#faa61a")
            fontSize = 18.px
            backgroundColor += c("#151517")
            borderColor += box(c("#2f3033"))
            borderWidth += box(1.px)
            padding = box(0.px, 8.px, 6.px, 8.px)

            and(debugConsole) {
                fontFiraCodeLight?.let { font = it }
                textFill = c("#dcddde")
                borderWidth += box(2.px)
                minWidth = 800.px
                maxWidth = 800.px
                minHeight = 600.px
                maxHeight = 600.px
            }
            and(debugWatchKey) {
                borderWidth += box(2.px)
                alignment = Pos.CENTER_LEFT
                minWidth = 220.px
                maxWidth = 220.px
            }
            and(debugWatchVal) {
                fontFiraCodeBold?.let { font = it }
                borderWidth += box(2.px)
                alignment = Pos.CENTER_RIGHT
                textAlignment = TextAlignment.RIGHT
                minWidth = 112.px
                maxWidth = 112.px
                textFill = c("#aaff33")
            }

        }
    }
}
