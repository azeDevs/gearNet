package application.arcadiaViews

import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import tornadofx.*

class InMatchStyle : Stylesheet() {

    companion object {
        val fontFiraCodeMedium = loadFont("/fonts/FiraCode-Medium.ttf", 16.0)
        val fontPaladins = loadFont("/fonts/Paladins-Regular.ttf", 16.0)
        val fontRED = loadFont("/fonts/RED.ttf", 16.0)

        val matchHealthText by cssclass()
        val matchBountyText by cssclass()
        val matchFreeText by cssclass()
    }

    init {

        label {
            fontFiraCodeMedium?.let { font = it }
            textFill = c("#78cbab")
            fontSize = 10.px

            and(matchHealthText) {
                fontPaladins?.let { font = it }
                fontSize = 23.px
                textFill = c("#d45916")
                maxWidth = 160.px
                minWidth = 160.px
            }

            and(matchBountyText) {
                fontRED?.let { font = it }
                fontSize = 28.px
                maxWidth = 160.px
                minWidth = 160.px
                textFill = LinearGradient(0.0, -23.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.8, 0.8, 0.3)), Stop(0.48, c(0.9, 0.9, 0.4)), Stop(0.52, c(0.7, 0.5, 0.1)), Stop(1.0, c(0.9, 0.8, 0.2)))
            }
            and(matchFreeText) {
                fontRED?.let { font = it }
                fontSize = 25.px
                maxWidth = 160.px
                minWidth = 160.px
                textFill = LinearGradient(
                    0.0,
                    -20.0,
                    0.0,
                    10.0,
                    false,
                    CycleMethod.NO_CYCLE,
                    Stop(0.0, c(0.3, 0.3, 0.6)),
                    Stop(0.45, c(0.6, 0.5, 0.4)),
                    Stop(0.55, c(0.4, 0.3, 0.1)),
                    Stop(1.0, c(0.4, 0.4, 0.8))
                )
                opacity = 0.64
            }

        }
    }
}