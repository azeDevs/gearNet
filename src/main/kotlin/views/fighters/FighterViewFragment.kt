package views.fighters

import views.generic.DebugLabelView
import javafx.application.Platform
import javafx.scene.Parent
import session.Fighter
import tornadofx.Fragment
import tornadofx.hbox
import tornadofx.runLater
import tornadofx.singleAssign
import views.ViewFrag

class FighterViewFragment(override val root: Parent) : Fragment("DebugFighterView") {

    private var seatIdText: DebugLabelView by singleAssign()

    init {
        with(root) {
            hbox {
                seatIdText = DebugLabelView(parent)
            }
        }
    }

    fun updateFighter(fighter: Fighter) = runLater {
        seatIdText.update(fighter.getName(), "${fighter.getSeat()}")
    }

}

