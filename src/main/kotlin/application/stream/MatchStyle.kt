package application.stream

import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import tornadofx.*

class MatchStyle : Stylesheet() {

    companion object {
        val fontFiraCodeMedium = loadFont("/fonts/FiraCode-Medium.ttf", 16.0)
        val fontPaladins = loadFont("/fonts/Paladins-Regular.ttf", 16.0)
        val fontRED = loadFont("/fonts/RED.ttf", 16.0)

        val matchHealthText by cssclass()
        val matchBountyText by cssclass()
        val matchFreeText by cssclass()
    }

    init {
        Stylesheet.label {
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
                fontSize = 25.px
                maxWidth = 160.px
                minWidth = 160.px
                textFill = LinearGradient(0.0, -20.0, 0.0, 20.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.96, 0.96, 0.45)), Stop(0.45, c(0.96, 0.96, 0.45)), Stop(0.55, c(0.96, 0.76, 0.0)), Stop(1.0, c(0.91, 0.69, 0.0)))
            }
            and(matchFreeText) {
                fontRED?.let { font = it }
                fontSize = 25.px
                maxWidth = 160.px
                minWidth = 160.px
                textFill = LinearGradient(0.0, -20.0, 0.0, 20.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.49, 0.47, 0.48)), Stop(0.45, c(0.46, 0.44, 0.45)), Stop(0.55, c(0.38, 0.39, 0.38)), Stop(1.0, c(0.35, 0.36, 0.35)))
            }
        }
    }
}