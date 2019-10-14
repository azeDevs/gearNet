package application

import javafx.geometry.Pos
import tornadofx.*

class AppStyle : Stylesheet() {

    companion object {
        val entryWidth = 962.px
        private const val fontSize = 16.0

        val fontFiraLight = loadFont("/fonts/FiraCode-Light.ttf", fontSize)
        val fontFiraRegular = loadFont("/fonts/FiraCode-Regular.ttf", fontSize)
        val fontFiraBold = loadFont("/fonts/FiraCode-Bold.ttf", fontSize)
        val appContainer by cssclass()

        val debugContainer by cssclass()
        val stageContainer by cssclass()
        val debugConsole by cssclass()
        val stageConsole by cssclass()
    }

    init {
        appContainer {
            backgroundColor += c("#FF00FFff")
            backgroundColor += c("#000")
            alignment = Pos.TOP_CENTER
        }

        debugContainer {
            padding = box(0.px, 6.px)
            backgroundColor += c("#080808")
            borderColor += box(c("#444"),c("#333"))
            borderWidth += box(2.px,6.px,2.px,6.px)
            minWidth = entryWidth+22
            maxWidth = entryWidth+22
            minHeight = 660.px
            maxHeight = 660.px
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
