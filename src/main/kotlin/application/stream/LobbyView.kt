package application.stream

import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import models.Fighter
import models.Viewer
import session.Session
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import twitch.ViewerData
import utils.getRes

class LobbyView(override val root: Parent) : Fragment() {

    private val container: StackPane
    private val bountiesGui: MutableList<FighterScoreView> = ArrayList()
    private val viewersGuiC: MutableList<ViewerScoreView> = ArrayList()

    init {
        with(root) {
            container = stackpane {
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

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

    fun applyData(fighters: List<Fighter>, s: Session, showHud: Boolean) = Platform.runLater {
        for (i in 0..7) {
            if (fighters.size > i) {
                bountiesGui[i].applyData(fighters[i], s)
                bountiesGui[i].setVisibility(showHud)
            }
        }
        val viewerTeamC = s.viewers.values.filter { item -> item.getScoreTotal() > -1 }.sortedByDescending { item -> item.getScoreTotal() }

        for (i in 0..15) {
            if (viewerTeamC.size > i) {
                viewersGuiC[i].applyData(viewerTeamC[i])
                viewersGuiC[i].setVisibility(showHud)
            } else viewersGuiC[i].applyData(Viewer(ViewerData()))
        }
    }

}