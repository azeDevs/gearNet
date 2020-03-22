package application.debug

import MyApp.Companion.BORDER_TRACINGS
import javafx.geometry.Pos
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.*

class DebugStyle : Stylesheet() {

    companion object {
        val fontFiraCodeBold = loadFont("/fonts/FiraCode-Bold.ttf", 16.0)

        val debugContainer by cssclass()
        val debugListGeneric by cssclass()
        val debugListRed by cssclass()
        val debugListBlue by cssclass()
        val debugListYellow by cssclass()
        val tempListGeneric by cssclass()
        val tempListRed by cssclass()
        val tempListBlue by cssclass()
        val tempListYellow by cssclass()
        val debugTextGeneric by cssclass()
        val debugTextRed by cssclass()
        val debugTextBlue by cssclass()
        val debugTextYellow by cssclass()

        val debugLiteGreen = c("#cdd8be")
        val debugSimpGreen = c("#4db584")
        val debugDarkGreen = c("#226644")
        val debugLiteYellow = c("#d1c8b4")
        val debugSimpYellow = c("#f3b017")
        val debugDarkYellow = c("#685f26")
        val debugLiteRed = c("#caadad")
        val debugSimpRed = c("#ff413d")
        val debugDarkRed = c("#5f1f1f")
        val debugLiteBlue = c("#b4bdd1")
        val debugSimpBlue = c("#1759f3")
        val debugDarkBlue = c("#262f68aa")

    }

    init {
        debugContainer {
            backgroundColor += c("#171213FF")
        }

        if (BORDER_TRACINGS) star {
            borderColor += box(c("#00CC44DD"))
            backgroundColor += c("#22664411")
            borderWidth += box(2.px)
            borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 5.0, 0.0, arrayListOf(1.0))
        }

        label {
            fontFiraCodeBold?.let { font = it }
            textFill = debugLiteYellow
            fontSize = 12.px

            and(debugListGeneric) {
                backgroundColor += debugDarkGreen
//                borderColor += box(debugSimpGreen)
                textFill = debugLiteGreen
                maxWidth = 208.px
                minWidth = 208.px
                padding = box(8.px)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(debugListRed) {
                backgroundColor += debugDarkRed
//                borderColor += box(debugSimpRed)
                textFill = debugLiteRed
                maxWidth = 180.px
                minWidth = 180.px
                padding = box(8.px)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(debugListBlue) {
                backgroundColor += debugDarkBlue
//                borderColor += box(debugSimpBlue)
                textFill = debugLiteBlue
                maxWidth = 180.px
                minWidth = 180.px
                padding = box(8.px)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(debugListYellow) {
                backgroundColor += debugSimpYellow
//                borderColor += box(debugLiteYellow)
                textFill = c("#000000") //debugLiteYellow
                maxWidth = 160.px
                minWidth = 160.px
                padding = box(8.px)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }

            and(tempListGeneric) {
//                borderColor += box(debugSimpGreen)
                textFill = debugLiteGreen
                maxWidth = 208.px
                minWidth = 208.px
                padding = box(8.px)
                effect = DropShadow(BlurType.ONE_PASS_BOX, c("#226644"), 4.0, 400.0, 0.0, 0.0)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(tempListRed) {
//                borderColor += box(debugSimpRed)
                textFill = debugLiteRed
                maxWidth = 208.px
                minWidth = 208.px
                padding = box(8.px)
                effect = DropShadow(BlurType.ONE_PASS_BOX, c("#5f1f1f"), 4.0, 400.0, 0.0, 0.0)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(tempListBlue) {
//                borderColor += box(debugSimpBlue)
                textFill = debugLiteBlue
                maxWidth = 208.px
                minWidth = 208.px
                padding = box(8.px)
                effect = DropShadow(BlurType.ONE_PASS_BOX, c("#262f68"), 4.0, 400.0, 0.0, 0.0)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(tempListYellow) {
                textFill = debugLiteYellow
                alignment = Pos.TOP_LEFT
//                fontSize = 16.px
                maxWidth = 1280.px
                minWidth = 1280.px
                maxHeight = 720.px
                minHeight = 720.px
                effect = DropShadow(BlurType.ONE_PASS_BOX, c("#000000"), 3.0, 777.0, 0.0, 0.0)
            }

            and(debugTextGeneric) {
//                backgroundColor += debugDarkGreen
//                borderColor += box(debugSimpGreen)
                textFill = debugLiteGreen
                fontSize = 16.px
                maxWidth = 360.px
                minWidth = 360.px
                maxHeight = 48.px
                minHeight = 48.px
                padding = box(8.px)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(debugTextRed) {
                textFill = debugLiteRed
                backgroundColor += debugDarkRed
//                borderColor += box(debugSimpRed)
                fontSize = 16.px
                maxWidth = 360.px
                minWidth = 360.px
                maxHeight = 48.px
                minHeight = 48.px
                padding = box(8.px)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(debugTextBlue) {
                textFill = debugLiteBlue
                backgroundColor += debugDarkBlue
//                borderColor += box(debugSimpBlue)
                fontSize = 16.px
                maxWidth = 360.px
                minWidth = 360.px
                maxHeight = 48.px
                minHeight = 48.px
                padding = box(8.px)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }
            and(debugTextYellow) {
                textFill = c("#000000")//debugLiteYellow
//                backgroundColor += debugSimpYellow
//                borderColor += box(debugLiteYellow)
                fontSize = 16.px
                maxWidth = 360.px
                minWidth = 360.px
                maxHeight = 48.px
                minHeight = 48.px
                padding = box(8.px)
//                borderWidth += box(2.px)
//                borderStyle += BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10.0, 0.0, null)
            }

        }
    }
}
