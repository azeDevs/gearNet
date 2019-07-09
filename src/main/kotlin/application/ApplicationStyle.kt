package application

import MyApp.Companion.GHOST_OPACITY
import MyApp.Companion.TRACE_BORDERS
import javafx.geometry.Pos
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.*
import utils.getRes

class ApplicationStyle : Stylesheet() {

    companion object {
        val fontFiraCodeRegular = loadFont("/fonts/FiraCode-Regular.ttf", 16.0)
        val fontFiraCodeLight = loadFont("/fonts/FiraCode-Light.ttf", 16.0)
        val fontPaladins = loadFont("/fonts/Paladins-Regular.ttf", 16.0)

        val utilsContainer by cssclass()
        val appContainer by cssclass()
        val streamContainer by cssclass()
        val lobbyName by cssclass()
        val consoleField by cssclass()
        val toggleStreamButton by cssclass()
    }

    init {
        appContainer {
            backgroundImage += getRes("gn_background.png")
            alignment = Pos.TOP_CENTER
        }

        utilsContainer {
            opacity = GHOST_OPACITY
            borderWidth += box(2.px)
            borderColor += box(c("#34081c"))
            borderStyle += BorderStrokeStyle(
                StrokeType.INSIDE,
                StrokeLineJoin.ROUND,
                StrokeLineCap.SQUARE,
                10.0,
                0.0,
                arrayListOf(1.0)
            )
            backgroundColor += c("#230412")
            alignment = Pos.BOTTOM_LEFT
        }

        streamContainer {
            backgroundColor += c("#FF00FFff")
            alignment = Pos.CENTER
            maxWidth = 1280.px
            minWidth = 1280.px
            maxHeight = 720.px
            minHeight = 720.px
        }

        if (TRACE_BORDERS) star {
            borderColor += box(c("#00CC44DD"))
            backgroundColor += c("#22664411")
            borderWidth += box(1.px)
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 5.0, 5.0, arrayListOf(1.0))
        }

        button {
            and(toggleStreamButton) {
                opacity = GHOST_OPACITY
                textFill = c("#52141f")
                backgroundColor += c("#00000000")
                alignment = Pos.BOTTOM_RIGHT
            }
        }

        label {
            fontFiraCodeRegular?.let { font = it }
            textFill = c("#cccccc")
            fontSize = 14.px

            and(consoleField) {
                fontFiraCodeLight?.let { font = it }
                alignment = Pos.BOTTOM_LEFT
                textFill = c("#966674")
                fontSize = 10.px
            }

            and(lobbyName) {
                fontPaladins?.let { font = it }
                fontSize = 18.px
                maxWidth = 420.px
                minWidth = 420.px
                maxHeight = 32.px
                minHeight = 32.px
                alignment = Pos.CENTER
            }
        }
    }
}
