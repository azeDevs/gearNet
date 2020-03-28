package application.stream

import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import tornadofx.Fragment
import tornadofx.c
import tornadofx.rectangle
import tornadofx.stackpane

class StunGaugeView(override val root: Parent, private val teamColor:Int) : Fragment() {

    private val a: Arcadia by inject()
    private var container: StackPane
    private lateinit var backing: Rectangle
    private lateinit var progress: Rectangle
    private val stunGaugeMaxWidth = 168.0


    init {
        with(root) {
            container = stackpane {
                translateY -= 390
                when(teamColor) {
                    0 -> {
                        alignment = Pos.CENTER_RIGHT
                        translateX -= 1488
                    }
                    1 -> {
                        alignment = Pos.CENTER_LEFT
                        translateX += 1488
                    }
                }
                backing = rectangle {
                    fill = c("#474234")
                    width = stunGaugeMaxWidth
                    height = 4.0
                }
                progress = rectangle {
                    fill = c("#04d4f7")
                    width = 0.0
                    height = 3.0
                }
            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

    fun applyData() = Platform.runLater {

        a.getPlayersStaged().p(teamColor).getStunProgress()

        val stunProgress = a.getPlayersStaged().p(teamColor).getStunProgress()
        val stunMaximum = a.getPlayersStaged().p(teamColor).getStunMaximum()
        progress.width = (stunProgress.toDouble() / stunMaximum) * stunGaugeMaxWidth
        container.isVisible = true
    }

}