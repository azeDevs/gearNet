package application

import tornadofx.*

class ApplicationStyle : Stylesheet() {

    companion object {
        val fontPaladins = loadFont("/fonts/Paladins-Regular.ttf", 16.0)
        val streamContainer by cssclass()
    }

    init {
        streamContainer {
            backgroundColor += c("#FF00FFff")
        }

        label {
            fontPaladins?.let { font = it }
            textFill = c("#cccccc")
            fontSize = 16.px
        }
    }
}
