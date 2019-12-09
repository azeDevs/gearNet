package application.views.fighters

import application.views.generic.DebugStyle
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import session.Fighter
import tornadofx.*

class DebugFighterView : Fragment() {

    override val root = VBox()
    private var handleText: Label by singleAssign()
    private var seatIdText: Label by singleAssign()

    init {
        with(root) {
            vbox { translateX += 16
                hbox { addClass(DebugStyle.debugFighter)
                    handleText = label("fighterName")
                    seatIdText = label("[-]")
                }
            }
        }
    }

    fun updateFighter(fighter: Fighter) = Platform.runLater {
        handleText.text = fighter.getName()
        seatIdText.text = "[${fighter.getSeat()}]"
    }

}

