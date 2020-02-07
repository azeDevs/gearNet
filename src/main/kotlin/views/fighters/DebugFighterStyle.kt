package views.fighters

import views.AppStyle
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.*

class DebugFighterStyle : Stylesheet() {

    companion object {
        val fighterFrame by cssclass()
        val handleText by cssclass()
    }

    init {

        fighterFrame {
            padding = box(8.px)
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 2.0, listOf(10.0, 1.0))
            borderColor += box(c("#FFAA00"))
            borderWidth += box(2.px)
            minWidth = AppStyle.OVERLAY_MARGIN_WIDTH.px*2
            maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH.px*2
        }

        label {
            handleText {
                AppStyle.fontFiraBold?.let { font = it }
                textFill = c("#AACCFF")
                fontSize = 20.px
            }
        }

    }
}