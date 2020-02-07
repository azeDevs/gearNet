package views.generic

import views.AppStyle
import javafx.geometry.Pos
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.*

class DebugStyle : Stylesheet() {

    companion object {
        private const val DEBUG_FONTSIZE = 20.0
        val fontFiraBold = loadFont("/fonts/FiraCode-Bold.ttf",
            DEBUG_FONTSIZE
        )
        val statusText by cssclass()
        val debugFighter by cssclass()
        val debugText by cssclass()
        val wireText by cssclass()
        val wirePane by cssclass()
        val wireFrame by cssclass()
        val debugContainer by cssclass()
    }

    init {

        statusText {
            padding = box(8.px)
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 2.0, listOf(10.0, 1.0))
            borderColor += box(c("#FFAA00"))
            borderWidth += box(2.px)
            minWidth = AppStyle.OVERLAY_MARGIN_WIDTH.px*2
            maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH.px*2
            label {
                AppStyle.fontFiraBold?.let { font = it }
                textFill = c("#AACCFF")
                fontSize = 32.px
            }
        }

        debugFighter {
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 2.0, listOf(10.0, 1.0))
            borderColor += box(c("#FFAA00"))
            borderWidth += box(2.px)
            minWidth = AppStyle.OVERLAY_MARGIN_WIDTH.px*2
            maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH.px*2
            label {
                AppStyle.fontFiraBold?.let { font = it }
                textFill = c("#FFAA00")
                fontSize = 16.px
            }
        }

        debugText {
            AppStyle.fontFiraBold?.let { font = it }
            textFill = c("#AACCFF")
            padding = box(8.px)
            fontSize = 24.px
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
            borderColor += box(c("#AACCFF00"))
            borderWidth += box(2.px)
            padding = box(-2.px)
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

    }
}