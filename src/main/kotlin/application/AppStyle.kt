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
        val TOP_GOALS_HEIGHT = 140.0
        val BATTLE_STAGE_WIDTH = 1480.0
        val FIGHTER_STAT_HEIGHT = 140.0

        val OVERLAY_MARGIN_HEIGHT = 750.0
        val OVERLAY_MARGIN_WIDTH = 220.0

        val BET_CONTAINER_HEIGHT = 140.0
        val BET_TABLE_HEIGHT = 60.0
        val BET_TABLE_WIDTH = 390.0
        val BET_SCALE_WIDTH = 180.0
        val TENSION_PULSE_HEIGHT = 80.0

        val TICKER_HEIGHT = 50.0

        private const val fontSize = 22.0

//        val fontFiraLight = loadFont("/fonts/FiraCode-Light.ttf", fontSize)
//        val fontFiraRegular = loadFont("/fonts/FiraCode-Regular.ttf", fontSize)
        val fontFiraBold = loadFont("/fonts/FiraCode-Bold.ttf", fontSize)
        val fighterView by cssclass()
        val fighterZone by cssclass()
        val viewerZone by cssclass()
        val redPane by cssclass()
        val bluPane by cssclass()
        val appContainer by cssclass()
        val stageContainer by cssclass()
    }

    init {
        fighterZone {
            alignment = Pos.TOP_CENTER
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 1.0, 1.0, listOf(1.0))
            borderColor += box(c("#FFAA00"))
            borderWidth += box(4.px)
            label {
                fontFiraBold?.let { font = it }
                textFill = c("#FFAA00")
                padding = box(8.px)
                fontSize = 20.px
            }
        }
        fighterView {
            label {
                alignment = Pos.TOP_LEFT
                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 1.0, 1.0, listOf(1.0))
                borderColor += box(c("#FFAA00"),c("#CC8800"),c("#AA4400"),c("#CC8800"))
                borderWidth += box(2.px)
                textFill = c("#FFAA00")
                padding = box(2.px)
                fontSize = 16.px
                minWidth = OVERLAY_MARGIN_WIDTH.px // 2
                maxWidth = OVERLAY_MARGIN_WIDTH.px // 2
            }
        }
        viewerZone {
            alignment = Pos.TOP_CENTER
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 1.0, 1.0, listOf(1.0))
            borderColor += box(c("#AACCFF"))
            borderWidth += box(4.px)
            label {
                fontFiraBold?.let { font = it }
                textFill = c("#AACCFF")
                padding = box(8.px)
                fontSize = 20.px
            }
        }
        redPane {
            alignment = Pos.TOP_CENTER
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 1.0, 1.0, listOf(1.0))
            borderColor += box(c("#ff2222"))
            borderWidth += box(4.px)
            label {
                fontFiraBold?.let { font = it }
                textFill = c("#ff2222")
                padding = box(8.px)
                fontSize = 30.px
            }
        }
        bluPane {
            alignment = Pos.TOP_CENTER
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 1.0, 1.0, listOf(1.0))
            borderColor += box(c("#2299ff"))
            borderWidth += box(4.px)
            label {
                fontFiraBold?.let { font = it }
                textFill = c("#2299ff")
                padding = box(8.px)
                fontSize = 30.px
            }
        }
        stageContainer {
            minWidth = BATTLE_STAGE_WIDTH.px
            maxWidth = BATTLE_STAGE_WIDTH.px
            minHeight = 32.px
            maxHeight = 32.px
            alignment = Pos.BOTTOM_LEFT
        }
        appContainer {
            backgroundColor += Color.TRANSPARENT
        }
    }
}
