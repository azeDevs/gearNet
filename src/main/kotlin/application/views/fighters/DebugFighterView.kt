package application.views.fighters

import application.AppStyle
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import tornadofx.*

class DebugFighterView(override val root: VBox) : Fragment() {
    // TODO: Get this to populate automatically
    val controller: DebugFighterController by inject()
    private var handleText: Label by singleAssign()
//    private var ratingText: Label by singleAssign()
    private var bountyText: Label by singleAssign()
//    private var deltaText: Label by singleAssign()

    init {
        with(root) {
            vbox { translateX += 16
                hbox { addClass(AppStyle.fighterView)
//                ratingText = label("F+")
                    handleText = label("unknown")
                }
                hbox { addClass(AppStyle.fighterView)
                    bountyText = label("129,876,543 W$")
//                deltaText = label("876,543 W$")
                }
            }
        }
    }
}

