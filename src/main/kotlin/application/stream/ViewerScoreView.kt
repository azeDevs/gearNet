package application.stream

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import tornadofx.*
import twitch.Viewer
import utils.getRes
import utils.isWithin

class ViewerScoreView(override val root: Parent, private val scaleIndex:Int, private val teamColor:Int) : Fragment() {

    private val scaleFactor: Float = if(isWithin(teamColor)) 0.75f else 0.75f
    private var wholeThing: StackPane
    private lateinit var teamBackground: ImageView
    private lateinit var handle: Label
    private lateinit var score: Label


    init {
        with(root) {
            wholeThing = stackpane { isVisible = false
                translateY += (scaleIndex*(64*scaleFactor))-330.0

                when(teamColor) {
                    0 -> translateX -= 936.0
                    1 -> translateX += 936.0
                }

                teamBackground = imageview(getRes("gn_stream.png").toString()) {
                    viewport = when(teamColor) {
                        0 -> Rectangle2D(576.0, 512.0, 448.0, 64.0)
                        1 -> Rectangle2D(576.0, 640.0, 448.0, 64.0)
                        else -> Rectangle2D(576.0, 576.0, 448.0, 64.0)
                    }
                    fitWidth = 448.0 * scaleFactor
                    fitHeight = 64.0 * scaleFactor
                }

                handle = label {
                    addClass(ScoreStyle.viewerHandleText)
                    when(teamColor) {
                        0 -> {
                            translateX += (144.0 * scaleFactor)
                            alignment = Pos.CENTER_LEFT
                        }
                        1 -> {
                            translateX -= (144.0 * scaleFactor)
                            alignment = Pos.CENTER_RIGHT
                        }
                        else -> {
                            translateX -= (144.0 * scaleFactor)
                            alignment = Pos.CENTER_RIGHT
                        }
                    }
                    scaleX *= scaleFactor
                    scaleY *= scaleFactor
                    translateY -= (2.0 * scaleFactor)
                }

                score = label {
                    addClass(InMatchStyle.matchBountyText)
                    when(teamColor) {
                        0 -> translateX -= (128.0 *  scaleFactor)
                        1 -> translateX += (128.0 *  scaleFactor)
                        else -> translateX += (128.0 *  scaleFactor)
                    }
                    scaleX *= scaleFactor
                    scaleY *= scaleFactor
                    translateY -= (4.0 * scaleFactor)
                }

            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { wholeThing.isVisible = flag }

    fun applyData(v: Viewer) = Platform.runLater {
        if (v.isValid() && !isWithin(teamColor)) {
            handle.text = v.data.name; handle.isVisible = true
            score.text = v.getScoreString()
            wholeThing.isVisible = true
        } else if (v.isValid() && v.isTeamR() && teamColor == 0) {
            handle.text = v.data.name; handle.isVisible = true
            score.text = v.getScoreString()
            wholeThing.isVisible = true
        } else if (v.isValid() && v.isTeamB() && teamColor == 1) {
            handle.text = v.data.name; handle.isVisible = true
            score.text = v.getScoreString()
            wholeThing.isVisible = true
        } else {
            handle.text = ""; handle.isVisible = false
            score.text = "FREE"
            wholeThing.isVisible = false
        }
    }

}