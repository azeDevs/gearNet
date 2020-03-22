package application.stream

import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import models.Player
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class ViewersView(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private val container: StackPane
    private lateinit var atensionMeters: AtensionMetersView
    private val viewersGuiR: MutableList<ViewerScoreView> = ArrayList()
    private val viewersGuiB: MutableList<ViewerScoreView> = ArrayList()

    init {
        with(root) {
            container = stackpane {

                imageview(getRes("atlas.png").toString()) { // RED BANNER
                    viewport = Rectangle2D(1536.0, 704.0, 256.0, 320.0)
                    fitWidth = 256.0
                    fitHeight = 320.0
                    translateX -= 880
                    translateY -= 400
                    scaleX *= 0.88
                    scaleY *= 0.88
                }

                imageview(getRes("atlas.png").toString()) { // BLUE BANNER
                    viewport = Rectangle2D(1792.0, 704.0, 256.0, 320.0)
                    fitWidth = 256.0
                    fitHeight = 320.0
                    translateX += 880
                    translateY -= 400
                    scaleX *= 0.88
                    scaleY *= 0.88
                }

                atensionMeters = AtensionMetersView(parent)
                for (i in 0..15) {
                    viewersGuiR.add(ViewerScoreView(parent, i, 0))
                    viewersGuiB.add(ViewerScoreView(parent, i, 1))
                }
            }
        }
    }

    override fun updateAnimation() {
        atensionMeters.updateAnimation()
    }

    override fun applyData() = Platform.runLater {
        val viewerTeamR = a.getWatchers().filter { item -> item.isTeam(PLAYER_1) }.sortedByDescending { item -> item.getScoreTotal() }
        val viewerTeamB = a.getWatchers().filter { item -> item.isTeam(PLAYER_2) }.sortedByDescending { item -> item.getScoreTotal() }
        for (i in 0..15) if (viewerTeamR.size > i) viewersGuiR[i].applyData(viewerTeamR[i])
        else viewersGuiR[i].applyData(Player())
        for (i in 0..15) if (viewerTeamB.size > i) viewersGuiB[i].applyData(viewerTeamB[i])
        else viewersGuiB[i].applyData(Player())
        atensionMeters.applyData()
    }

}