package application.stream

import application.debug.ArcadeView
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import models.Player
import session.Session
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class LobbyView(override val root: Parent) : Fragment(), ArcadeView {

    private val s: Session by inject()
    private val container: StackPane
    private val bountiesGui: MutableList<FighterScoreView> = ArrayList()
    private val viewersGuiC: MutableList<ViewerScoreView> = ArrayList()

    init {
        with(root) {
            container = stackpane {

                imageview(getRes("atlas.png").toString()) { // UPPER GATE
                    viewport = Rectangle2D(384.0, 1024.0, 1664.0, 256.0)
                    fitWidth = 1664.0
                    fitHeight = 256.0
                    translateY -= 282
                    translateX -= 2
                }
                imageview(getRes("atlas.png").toString()) { // LOWER GATE
                    viewport = Rectangle2D(384.0, 1280.0, 1664.0, 256.0)
                    fitWidth = 1664.0
                    fitHeight = 256.0
                    translateY += 318
                    translateX -= 2
                }

                for (i in 0..7) bountiesGui.add(FighterScoreView(parent, i))
                for (i in 0..15) viewersGuiC.add(ViewerScoreView(parent, i, 2))

            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

    override fun applyData() = Platform.runLater {
//        for (i in 0..7) {
//            if (s.getPlayersList().size > i) {
//                bountiesGui[i].applyData(s, i)
//                bountiesGui[i].setVisibility(true)
//            }
//        }
        val viewerTeamC = s.getWatchers().filter { item -> item.getScoreTotal() > -1 }.sortedByDescending { item -> item.getScoreTotal() }

        for (i in 0..15) {
            if (viewerTeamC.size > i) {
                viewersGuiC[i].applyData(viewerTeamC[i])
                viewersGuiC[i].setVisibility(true)
            } else viewersGuiC[i].applyData(Player())
        }
    }

}