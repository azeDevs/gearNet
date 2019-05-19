package application.player

import javafx.geometry.Pos
import tornadofx.*

class BountyStyle : Stylesheet() {

    companion object {
        val fontFiraCodeMedium = loadFont("/fonts/FiraCode-Medium.ttf", 16.0)
        val fontPaladins = loadFont("/fonts/Paladins.ttf", 16.0)
        val fontRED = loadFont("/fonts/RED.ttf", 16.0)

        val bountyContainer by cssclass()
        val bountyHandleText by cssclass()
        val bountyBountyText by cssclass()
        val bountyBountyShadow by cssclass()
        val bountyChangeText by cssclass()
    }

    init {
        bountyContainer {
            minHeight = 140.px
            maxHeight = 140.px
            alignment = Pos.CENTER
        }

        label {
            fontFiraCodeMedium?.let { font = it }
            textFill = c("#78cbab")
            fontSize = 10.px

            and(bountyHandleText) {
                fontPaladins?.let { font = it }
                fontSize = 20.px
                textFill = c("#fffff4")
                maxWidth = 420.px
                minWidth = 420.px
                alignment = Pos.CENTER_LEFT
                backgroundColor += c("#00000000")
            }
            and(bountyBountyText) {
                fontRED?.let { font = it }
                fontSize = 40.px
                textFill = c("#ffcc33")
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