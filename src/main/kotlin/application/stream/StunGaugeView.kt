package application.stream

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import memscan.MatchData
import tornadofx.Fragment
import tornadofx.c
import tornadofx.rectangle
import tornadofx.stackpane

class StunGaugeView(override val root: Parent, private val teamColor:Int) : Fragment() {

    private var wholeThing: StackPane
    private lateinit var backing: Rectangle
    private lateinit var progress: Rectangle
    private var stunProgress: Int = 0
    private var stunMaximum: Int = 1


    init {
        with(root) {
            wholeThing = stackpane {
                when(teamColor) {
                    0 -> {
                        alignment = Pos.CENTER_LEFT
                        translateX += 373
                    }
                    1 -> {
                        alignment = Pos.CENTER_RIGHT
                        translateX -= 373
                    }
                }
                backing = rectangle {
                    fill = c("#474234")
                    width = 170.0
                    height = 6.0
                    translateY -= 344
                }
                progress = rectangle {
                    fill = c("#04d4f7")
                    width = 170.0
                    height = 3.0
                    translateY -= 344
                }
            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { wholeThing.isVisible = flag }

    fun applyData(m: MatchData) = Platform.runLater {
        stunProgress = if (teamColor == 0) m.stunProgress.first else if (teamColor == 1) m.stunProgress.second else 0
        stunMaximum = if (teamColor == 0) m.stunMaximum.first else if (teamColor == 1) m.stunMaximum.second else 1
        progress.width = (stunProgress.toDouble() / stunMaximum) * 170
        wholeThing.isVisible = true
    }

}