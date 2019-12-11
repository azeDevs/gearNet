package application.views.fighters

import application.views.generic.DebugLabelView
import javafx.application.Platform
import javafx.scene.Parent
import session.Fighter
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.singleAssign

class DebugFighterView(override val root: Parent) : Fragment() {

    private var seatIdText: DebugLabelView by singleAssign()

    init {
        with(root) {
            hbox { addClass(DebugFighterStyle.fighterFrame)
                seatIdText = DebugLabelView(parent)
            }
        }
    }

    fun updateFighter(fighter: Fighter) = Platform.runLater {
        seatIdText.update(fighter.getName(), "${fighter.getSeat()}")
    }

}

