package application.stream

import javafx.geometry.Pos
import javafx.scene.effect.BlendMode
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import tornadofx.*


class ScoreStyle : Stylesheet() {

    companion object {
        val fontXiaoWeiRegular = loadFont("/fonts/XiaoWei-Regular.ttf", 16.0)
        val fontFiraCodeMedium = loadFont("/fonts/FiraCode-Medium.ttf", 16.0)
        val fontPaladins = loadFont("/fonts/Paladins-Regular.ttf", 16.0)
        val fontItalicT = loadFont("/fonts/ItalicT-Regular.ttf", 16.0)
        val fontRED = loadFont("/fonts/RED.ttf", 16.0)

        val bountyContainer by cssclass()

        val viewerHandleText by cssclass()
        val signsTurnedText by cssclass()
        val bountyHandleText by cssclass()
        val bountyBountyText by cssclass()
        val bountyChangeText by cssclass()
    }

    init {
        bountyContainer {
            minHeight = 135.px
            maxHeight = 135.px
            alignment = Pos.CENTER
        }

        label {
            fontFiraCodeMedium?.let { font = it }
            textFill = c("#78cbab")
            fontSize = 10.px
            and(signsTurnedText) {
                fontXiaoWeiRegular?.let { font = it }
                fontSize = 24.px
                maxWidth = 256.px
                minWidth = 256.px
                textFill = c("#fcfa72")
                effect = DropShadow(BlurType.ONE_PASS_BOX, c("#bf6910"), 8.0, 888.0, 0.0, 0.0)
            }
            and(viewerHandleText) {
                fontItalicT?.let { font = it }
                fontSize = 23.px
                maxWidth = 192.px
                minWidth = 192.px
                scaleX = 0.77
                textFill = c("#ffe4b8")
                effect = DropShadow(BlurType.ONE_PASS_BOX, c("#361204"), 8.0, 888.0, 0.0, 0.0)
                blendMode = BlendMode.HARD_LIGHT
            }
            and(bountyHandleText) {
                fontPaladins?.let { font = it }
                fontSize = 24.px
                textFill = c("#fffcf4")
                maxWidth = 420.px
                minWidth = 420.px
                textFill = LinearGradient(0.0, -16.0, 0.0, 0.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.9, 0.9, 0.9)), Stop(0.45, c(1.0, 1.0, 1.0)), Stop(0.60, c(1.0, 0.9, 0.8)), Stop(1.0, c(0.9, 0.9, 0.9)))
                alignment = Pos.CENTER_LEFT
                effect = DropShadow(BlurType.ONE_PASS_BOX, c("#011a27"), 8.0, 888.0, 0.0, 0.0)
            }
            and(bountyBountyText) {
                fontRED?.let { font = it }
                fontSize = 40.px
                maxWidth = 256.px
                minWidth = 256.px
                textFill = LinearGradient(0.0, -30.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.8, 0.8, 0.3)), Stop(0.48, c(0.9, 0.9, 0.4)), Stop(0.52, c(0.7, 0.5, 0.1)), Stop(1.0, c(0.9, 0.8, 0.2)))
            }
            and(bountyChangeText) {
                fontRED?.let { font = it }
                fontSize = 18.px
                alignment = Pos.CENTER
            }
        }
    }
}