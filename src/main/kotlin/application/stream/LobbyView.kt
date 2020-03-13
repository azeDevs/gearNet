package application.stream

import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import memscan.MatchData
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class LobbyView(override val root: Parent) : Fragment() {

    private val bountiesGui: MutableList<FighterScoreView> = ArrayList()
    private val viewersGuiC: MutableList<ViewerScoreView> = ArrayList()

    init {
        with(root) {
            stackpane {
                imageview(getRes("barc_lobby.png").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 1920.0, 1080.0)
                    fitWidth = 1920.0
                    fitHeight = 1080.0
                }
                for (i in 0..7) {
                    bountiesGui.add(FighterScoreView(parent, i))
                }
                for (i in 0..15) {
                    viewersGuiC.add(ViewerScoreView(parent, i, 2))
                }
            }
        }
    }

    fun applyData(m: MatchData) = Platform.runLater {

    }

}