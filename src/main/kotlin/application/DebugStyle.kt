package application

import javafx.geometry.Pos
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.*

class DebugStyle : Stylesheet() {

    companion object {
        private const val DEBUG_FONTSIZE = 20.0
        val fontFiraBold = loadFont("/fonts/FiraCode-Bold.ttf", DEBUG_FONTSIZE)
        val debugText by cssclass()
        val wireText by cssclass()
        val wirePane by cssclass()
        val wireFrame by cssclass()
        val debugConsole by cssclass()
        val debugContainer by cssclass()
    }

    init {

        debugText {
            AppStyle.fontFiraBold?.let { font = it }
            textFill = c("#AACCFF")
            padding = box(8.px)
            fontSize = 20.px
        }

        wireText {
            backgroundColor += c("#fbfdfc16")
            fontFiraBold?.let { font = it }
            textFill = c("#fbfdfcFF")
            padding = box(3.px, 6.px)
            fontSize = 16.px
        }

        wireFrame {
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 2.0, listOf(10.0, 1.0))
            borderColor += box(c("#00000055"))
            borderWidth += box(1.px)
            padding = box(-1.px)
        }

        wirePane {
            alignment = Pos.CENTER
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 1.0, 1.0, listOf(1.0))
            borderColor += box(c("#bbff44"))
            borderWidth += box(4.px)
            fontFiraBold?.let { font = it }
            textFill = c("#bbff44")
            padding = box(8.px)
            fontSize = 20.px
        }

        debugContainer {
            padding = box(0.px, 6.px)
            minWidth = AppStyle.BATTLE_STAGE_WIDTH.px-160
            maxWidth = AppStyle.BATTLE_STAGE_WIDTH.px-160
            minHeight = 540.px
            maxHeight = 540.px
            alignment = Pos.BOTTOM_LEFT
        }

        debugConsole {
            textFill = c("#faa61a")
            minWidth = AppStyle.BATTLE_STAGE_WIDTH.px-160
            maxWidth = AppStyle.BATTLE_STAGE_WIDTH.px-160
            fillHeight = false
            maxHeight = 540.px
        }

    }
}