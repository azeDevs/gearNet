package application.stream

import javafx.application.Platform
import javafx.scene.Parent
import memscan.MatchData
import tornadofx.Fragment
import tornadofx.stackpane

class AtensionMetersView(override val root: Parent) : Fragment() {

    private lateinit var atensionGaugeR: AtensionGaugeView
    private lateinit var atensionGaugeB: AtensionGaugeView

    init {
        with(root) {
            stackpane {
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