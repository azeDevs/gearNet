package application.stream

import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import memscan.MatchData
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class AtensionMetersView(override val root: Parent) : Fragment() {

    private lateinit var atensionGaugeR: AtensionGaugeView
    private lateinit var atensionGaugeB: AtensionGaugeView

    init {
        with(root) {
            stackpane {
                imageview(getRes("barc_atlas.png").toString()) { // WHEEL
                    viewport = Rectangle2D(640.0, 768.0, 256.0, 256.0)
                    fitWidth = 256.0
                    fitHeight = 256.0
                    translateY += 444
                }
                atensionGaugeR = AtensionGaugeView(parent, 0)
                atensionGaugeB = AtensionGaugeView(parent, 1)
            }
        }
    }

    fun applyData(m: MatchData) = Platform.runLater {
        atensionGaugeR.applyData(m)
        atensionGaugeB.applyData(m)
    }

}