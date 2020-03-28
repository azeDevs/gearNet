package application.stream

import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class LobbyView(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private val container: StackPane
    private val bountiesGui: MutableList<FighterScoreView> = ArrayList()

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

                stackpane {
                    for (i in 6 downTo 0) bountiesGui.add(FighterScoreView(parent, i))
                }

            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

    override fun applyData() = Platform.runLater {
        bountiesGui.forEach { it.applyData() }
    }

}