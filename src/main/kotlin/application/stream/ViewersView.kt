package application.stream

import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class ViewersView(override val root: Parent) : Fragment(), ArcadeView {

    private var animationFrameR: Int = -1
    private var animationFrameB: Int = -1

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

                for (i in 0..15) {
                    viewersGuiR.add(ViewerScoreView(parent, i, 0))
                    viewersGuiB.add(ViewerScoreView(parent, i, 1))
                }

                atensionMeters = AtensionMetersView(parent)
            }
        }
    }

    override fun updateAnimation() {
        val viewerTeamR = a.getTeam(PLAYER_1)
        val viewerTeamB = a.getTeam(PLAYER_2)

        if (animationFrameR == -1 && a.getPlayersStaged().p1.getSignal()) animationFrameR = 0
        if (animationFrameB == -1 && a.getPlayersStaged().p2.getSignal()) animationFrameB = 0

        if (animationFrameR > -1 && animationFrameR < viewerTeamR.size) {
            viewersGuiR[animationFrameR].restartAnimation()
            animationFrameR++
        } else animationFrameR = -1

        if (animationFrameB > -1 && animationFrameB < viewerTeamB.size) {
            viewersGuiB[animationFrameB].restartAnimation()
            animationFrameB++
        } else animationFrameB = -1

        for (i in 0..15) if (viewerTeamR.size > i) viewersGuiR[i].updateAnimation()
        for (i in 0..15) if (viewerTeamB.size > i) viewersGuiB[i].updateAnimation()

        atensionMeters.updateAnimation()
    }

    override fun applyData() = Platform.runLater {
        if (a.getPlayersStaged().p1.getSignal()) animationFrameR = 0
        if (a.getPlayersStaged().p2.getSignal()) animationFrameB = 0
        viewersGuiR.forEach { it.applyData() }
        viewersGuiB.forEach { it.applyData() }
        atensionMeters.applyData()
    }

}