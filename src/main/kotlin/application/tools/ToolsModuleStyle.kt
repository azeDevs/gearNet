package application.tools

import javafx.geometry.Pos
import tornadofx.*

class ToolsModuleStyle : Stylesheet() {

    companion object {
        val fontFiraCodeRegular = loadFont("/fonts/FiraCode-Regular.ttf", 16.0)

        val moduleTitle by cssclass()
    }

    init {
        label {
            fontFiraCodeRegular?.let { font = it }
            textFill = c("#cccccc")
            fontSize = 14.px

            and(moduleTitle) {
                fontFiraCodeRegular?.let { font = it }
                textFill = c("#857d53cc")
                fontSize = 12.px
                maxWidth = 128.px
                minWidth = 128.px
                maxHeight = 32.px
                minHeight = 32.px
                alignment = Pos.CENTER
            }
        }
    }
}