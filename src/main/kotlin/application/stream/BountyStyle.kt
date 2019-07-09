package application.stream

import javafx.geometry.Pos
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import tornadofx.*

class BountyStyle : Stylesheet() {

    companion object {
        val fontFiraCodeMedium = loadFont("/fonts/FiraCode-Medium.ttf", 16.0)
        val fontPaladins = loadFont("/fonts/Paladins-Regular.ttf", 16.0)
        val fontRED = loadFont("/fonts/RED.ttf", 16.0)

        val bountyContainer by cssclass()
        val bountyHandleText by cssclass()
        val bountyHandleShadow by cssclass()
        val bountyBountyText by cssclass()
        val bountyFreeText by cssclass()
        val bountyBountyShadow by cssclass()
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

            and(bountyHandleText) {
                fontPaladins?.let { font = it }
                fontSize = 20.px
                textFill = c("#fffcf4")
                maxWidth = 420.px
                minWidth = 420.px
                textFill = LinearGradient(0.0, -16.0, 0.0, 0.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.9, 0.9, 0.9)), Stop(0.45, c(1.0, 1.0, 1.0)), Stop(0.60, c(1.0, 0.9, 0.8)), Stop(1.0, c(0.9, 0.9, 0.9)))
                alignment = Pos.CENTER_LEFT
            }
            and(bountyHandleShadow) {
                fontPaladins?.let { font = it }
                fontSize = 20.px
                textFill = c("#011a27")
                maxWidth = 420.px
                minWidth = 420.px
                alignment = Pos.CENTER_LEFT
            }
            and(bountyBountyText) {
                fontRED?.let { font = it }
                fontSize = 40.px
                maxWidth = 200.px
                minWidth = 200.px
                textFill = LinearGradient(0.0, -30.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.8, 0.8, 0.3)), Stop(0.48, c(0.9, 0.9, 0.4)), Stop(0.52, c(0.7, 0.5, 0.1)), Stop(1.0, c(0.9, 0.8, 0.2)))
            }
            and(bountyFreeText) {
                fontRED?.let { font = it }
                fontSize = 40.px
                maxWidth = 200.px
                minWidth = 200.px
                textFill = LinearGradient(0.0, -20.0, 0.0, 5.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.4, 0.3, 0.2)), Stop(0.45, c(0.6, 0.4, 0.2)), Stop(0.55, c(0.4, 0.2, 0.2)), Stop(1.0, c(0.8, 0.4, 0.2)))
                opacity = 0.32
            }
            and(bountyBountyShadow) {
                fontRED?.let { font = it }
                fontSize = 40.px
                textFill = c("#9b332acc")
                maxWidth = 200.px
                minWidth = 200.px
                alignment = Pos.CENTER_LEFT
            }
            and(bountyChangeText) {
                fontRED?.let { font = it }
                fontSize = 18.px
                alignment = Pos.CENTER
            }
        }
    }
}