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
//                textFill = c("#ffcc33")
                textFill = LinearGradient(0.0, -30.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.96, 0.96, 0.45)), Stop(0.45, c(0.96, 0.96, 0.45)), Stop(0.55, c(0.96, 0.76, 0.0)), Stop(1.0, c(0.91, 0.69, 0.0)))
                maxWidth = 200.px
                minWidth = 200.px
                alignment = Pos.CENTER_LEFT
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