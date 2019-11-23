package application

import javafx.geometry.Pos
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.*

class AppStyle : Stylesheet() {

    companion object {
        val entryWidth = 962.px
        private const val fontSize = 16.0

        val fontFiraLight = loadFont("/fonts/FiraCode-Light.ttf", fontSize)
        val fontFiraRegular = loadFont("/fonts/FiraCode-Regular.ttf", fontSize)
        val fontFiraBold = loadFont("/fonts/FiraCode-Bold.ttf", fontSize)

        val wireText by cssclass()
        val wirePane by cssclass()
        val wireFrame by cssclass()
        val appContainer by cssclass()
        val debugContainer by cssclass()
        val stageContainer by cssclass()
        val debugConsole by cssclass()
        val stageConsole by cssclass()
    }

    init {
        wireText {
            backgroundColor += c("#fbfdfc16")
            fontFiraBold?.let { font = it }
            textFill = c("#fbfdfc32")
            padding = box(3.px, 6.px)
            fontSize = 12.px
        }

        wireFrame {
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 2.0, listOf(10.0, 1.0))
            borderColor += box(c("#fbfdfc24"))
            borderWidth += box(1.px)
            padding = box(-1.px)
        }

        wirePane {
            alignment = Pos.TOP_CENTER
            backgroundColor += c("#fbfdfc16")
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 1.0, 1.0, listOf(1.0))
            borderColor += box(c("#fbfdfc10"))
            borderWidth += box(1.px)
            fontFiraBold?.let { font = it }
            textFill = c("#fbfdfc32")
            padding = box(8.px)
            fontSize = 16.px
        }

        appContainer {
            backgroundColor += Color.TRANSPARENT
        }

        debugContainer {
            padding = box(0.px, 6.px)
            backgroundColor += c("#080808")
            borderColor += box(c("#444"),c("#333"))
            borderWidth += box(2.px,6.px,2.px,6.px)
            minWidth = entryWidth+22
            maxWidth = entryWidth+22
            minHeight = 600.px
            maxHeight = 600.px
            alignment = Pos.BOTTOM_LEFT
        }

        stageContainer {
            padding = box(0.px, 6.px)
            backgroundColor += c("#111111")
            borderColor += box(c("#444"),c("#333"))
            borderWidth += box(2.px,6.px,2.px,6.px)
            minWidth = entryWidth+22
            maxWidth = entryWidth+22
            minHeight = 32.px
            maxHeight = 32.px
            alignment = Pos.BOTTOM_LEFT
        }

        debugConsole {
            textFill = c("#faa61a")
            minWidth = entryWidth
            maxWidth = entryWidth
            fillHeight = false
            maxHeight = 660.px
        }

        stageConsole {
            minWidth = 200.px
            maxWidth = 200.px
            fillHeight = false
            maxHeight = 32.px
            fontFiraBold?.let { font = it }
        }

    }
}
