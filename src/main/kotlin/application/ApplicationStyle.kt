package application

import MyApp.Companion.TRACE_BORDERS
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.*

class ApplicationStyle : Stylesheet() {

    companion object {
        val fontFiraCodeBold = loadFont("/fonts/FiraCode-Bold.ttf", 16.0)
        val fontPaladins = loadFont("/fonts/Paladins-Regular.ttf", 16.0)
        val streamContainer by cssclass()
        val debugLabel by cssclass()
        val debugList by cssclass()
    }

    init {
        streamContainer {
            backgroundColor += c("#FF00FFff")
        }

        if (TRACE_BORDERS) star {
            borderColor += box(c("#00CC44DD"))
            backgroundColor += c("#22664411")
            borderWidth += box(1.px)
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 5.0, 5.0, arrayListOf(1.0))
        }

        label {
            fontFiraCodeBold?.let { font = it }
            textFill = c("#cccccc")
            fontSize = 16.px

            and(debugLabel) {
                backgroundColor += c("#226644")
                fontFiraCodeBold?.let { font = it }
                fontSize = 30.px
                maxWidth = 440.px
                minWidth = 440.px
                maxHeight = 48.px
                minHeight = 48.px
                padding = box(6.px)
                textFill = c("#cdd8be")
            }
            and(debugList) {
                backgroundColor += c("#226644")
                fontFiraCodeBold?.let { font = it }
                fontSize = 20.px
                maxWidth = 240.px
                minWidth = 240.px
                padding = box(6.px)
                textFill = c("#cdd8be")
            }
        }
    }
}
