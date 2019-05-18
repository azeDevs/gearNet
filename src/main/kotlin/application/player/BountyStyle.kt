package application.player

import application.MainStyle.Companion.fontPaladins
import javafx.geometry.Pos
import tornadofx.*

class BountyStyle : Stylesheet() {

    companion object {
        val fontFiraCodeBold = loadFont("/fonts/FiraCode-Bold.ttf", 16.0)
        val fontFiraCodeMedium = loadFont("/fonts/FiraCode-Medium.ttf", 16.0)
        val fontPaladinsStraight = loadFont("/fonts/Paladins-Straight.ttf", 16.0)
        val fontRED = loadFont("/fonts/RED.ttf", 16.0)

        val bountyContainer by cssclass()
        val statsBackdrop by cssclass()

        val handleText by cssclass()
        val statusBar by cssclass()
        val bountyText by cssclass()
        val bountyShadow by cssclass()
        val changeText by cssclass()
        val riskText by cssclass()
        val recordText by cssclass()
        val rulingText by cssclass()
        val chainShadow by cssclass()
    }

    init {
        bountyContainer {
//            borderWidth += box(2.px)
//            borderColor += box(c("#34081c"))
//            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 10.0, 0.0, arrayListOf(1.0))
//            backgroundColor += c("#23041288")
            minHeight = 140.px
            maxHeight = 140.px
            alignment = Pos.CENTER
        }

        statsBackdrop {
            minWidth = 134.px
            maxWidth = 134.px
            minHeight = 36.px
            maxHeight = 36.px
        }

        statusBar {
//            backgroundColor += c("#02627eee")
        }

        label {
            fontFiraCodeMedium?.let { font = it }
            textFill = c("#78cbab")
            fontSize = 10.px

            and(handleText) {
                fontPaladins?.let { font = it }
                fontSize = 20.px
                textFill = c("#fffff4")
                maxWidth = 420.px
                minWidth = 420.px
                alignment = Pos.CENTER_LEFT
                backgroundColor += c("#00000000")
            }
            and(bountyText) {
                fontRED?.let { font = it }
                fontSize = 40.px
                textFill = c("#ffcc33")
                maxWidth = 200.px
                minWidth = 200.px
                alignment = Pos.CENTER_LEFT
            }
            and(bountyShadow) {
                fontRED?.let { font = it }
                fontSize = 40.px
                textFill = c("#9b332acc")
                maxWidth = 200.px
                minWidth = 200.px
                alignment = Pos.CENTER_LEFT
            }
            and(changeText) {
                fontRED?.let { font = it }
                fontSize = 18.px
                alignment = Pos.CENTER
            }
            and(riskText) {
                padding = box(0.0.px, 8.0.px)
                textFill = c("#0094a4")
                fontSize = 10.px
                alignment = Pos.CENTER_RIGHT
            }
            and(recordText) {
                fontFiraCodeBold?.let { font = it }
                fontSize = 9.px
                minWidth = 200.px
                maxWidth = 200.px
                alignment = Pos.CENTER_LEFT
            }
            and(rulingText) {
                fontPaladinsStraight?.let { font = it }
                fontSize = 26.px
                textFill = c("#f7e09c")
                maxWidth = 512.px
                minWidth = 512.px
                alignment = Pos.CENTER
            }
            and(chainShadow) {
                fontPaladinsStraight?.let { font = it }
                fontSize = 24.px
                textFill = c("#0094a4cc")
                maxWidth = 200.px
                minWidth = 200.px
                alignment = Pos.CENTER
            }
        }
    }
}