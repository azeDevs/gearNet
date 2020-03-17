package application.stream

import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import models.Viewer
import session.Session
import tornadofx.Fragment
import tornadofx.stackpane
import twitch.ViewerData

class ViewersView(override val root: Parent) : Fragment() {

    private val container: StackPane
    private lateinit var atensionMeters: AtensionMetersView
    private val viewersGuiR: MutableList<ViewerScoreView> = ArrayList()
    private val viewersGuiB: MutableList<ViewerScoreView> = ArrayList()

    init {
        with(root) {
            container = stackpane {
                atensionMeters = AtensionMetersView(parent)
                for (i in 0..15) {
                    viewersGuiR.add(ViewerScoreView(parent, i, 0))
                    viewersGuiB.add(ViewerScoreView(parent, i, 1))
                }
            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

    fun applyData(s: Session) = Platform.runLater {
        val viewerTeamR = s.viewers.values.filter { item -> item.isTeamR() }.sortedByDescending { item -> item.getScoreTotal() }
        val viewerTeamB = s.viewers.values.filter { item -> item.isTeamB() }.sortedByDescending { item -> item.getScoreTotal() }
        for (i in 0..15) if (viewerTeamR.size > i) viewersGuiR[i].applyData(viewerTeamR[i])
        else viewersGuiR[i].applyData(Viewer(ViewerData()))
        for (i in 0..15) if (viewerTeamB.size > i) viewersGuiB[i].applyData(viewerTeamB[i])
        else viewersGuiB[i].applyData(Viewer(ViewerData()))
    }

}