package application.stream

import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import models.Player
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.*
import utils.getRes
import utils.isWithin
import utils.truncate

class ViewerScoreView(override val root: Parent, private val scaleIndex:Int, private val teamColor:Int) : Fragment() {

    private val a: Arcadia by inject()
    private val scaleFactor: Float = 0.8f
    private var container: StackPane
    private lateinit var teamBackground: ImageView
    private lateinit var alias: Label
    private lateinit var signs: Label
    private lateinit var score: Label
    private lateinit var delta: Label


    init {
        with(root) {
            container = stackpane {
                isVisible = false
                scaleX *= scaleFactor
                scaleY *= scaleFactor
                translateY += (scaleIndex*52)-320.0-scaleIndex
                scaleX -= (scaleIndex*0.016)
                scaleY -= (scaleIndex*0.016)

                when(teamColor) {
                    PLAYER_1 -> translateX -= 930.0
                    PLAYER_2 -> translateX += 930.0
                }

                teamBackground = imageview(getRes("atlas.png").toString()) {
                    viewport = when(teamColor) {
                        PLAYER_1 -> Rectangle2D(640.0, 64.0, 448.0, 64.0)
                        PLAYER_2 -> Rectangle2D(640.0, 196.0, 448.0, 64.0)
                        else -> Rectangle2D(640.0, 128.0, 448.0, 64.0)
                    }
                    fitWidth = 448.0
                    fitHeight = 64.0
                }

                alias = label {
                    addClass(ScoreStyle.viewerHandleText)
                    when(teamColor) {
                        PLAYER_1 -> {
                            translateX += (120.0)
                            alignment = Pos.CENTER_LEFT
                        }
                        PLAYER_2 -> {
                            translateX -= (124.0)
                            translateY -= 4.0
                            alignment = Pos.CENTER_RIGHT
                        }
                        else -> {
                            translateX -= (136.0)
                            alignment = Pos.CENTER_RIGHT
                        }
                    }
                    translateY -= 2.0
                }

                signs = label("88") {
                    addClass(ScoreStyle.signsTurnedText)
                }

                score = label {
                    addClass(InMatchStyle.matchBountyText)
                    when(teamColor) {
                        PLAYER_1 -> translateX -= (160.0)
                        else -> translateX += (160.0)
                    }
                    translateY -= 5.0
                }

                delta = label {
                    addClass(ScoreStyle.bountyChangeText)
                    translateY += 14.0
                }

            }
        }
    }

    fun applyData(p: Player) = Platform.runLater {
        if (p.isValid() && !isWithin(teamColor)) {
            // Do stuff for central Viewer Leaderboard
            alias.text = truncate(p.getUserName(), 11)
            alias.isVisible = true
            signs.text = p.getSigns().toString()
            signs.isVisible = p.getSigns() > 0
            score.text = p.getScoreTotalString()
            delta.text = p.getScoreDeltaString()
            setChangeTextColor(p.getScoreDelta())
            container.isVisible = true
        } else if (p.isValid() && p.isTeam(PLAYER_1) && teamColor == PLAYER_1) {
            // Do stuff for Viewer Atension, stage left (Red)
            alias.text = truncate(p.getUserName(), 11)
            alias.isVisible = true
            signs.text = p.getSigns().toString()
            signs.isVisible = p.getSigns() > 0
            score.text = p.getScoreTotalString()
            delta.text = ""
            container.isVisible = true
        } else if (p.isValid() && p.isTeam(PLAYER_2) && teamColor == PLAYER_2) {
            // Do stuff for Viewer Atension, stage right (Blue)
            alias.text = truncate(p.getUserName(), 11)
            alias.isVisible = true
            signs.text = p.getSigns().toString()
            signs.isVisible = p.getSigns() > 0
            score.text = p.getScoreTotalString()
            delta.text = ""
            container.isVisible = true
        } else {
            // Apply empty data
            alias.text = ""
            alias.isVisible = false
            signs.text = ""
            signs.isVisible = false
            score.text = "FREE"
            delta.text = ""
            container.isVisible = false
        }
    }

    private fun setChangeTextColor(changeInt: Int) = when {
        changeInt > 0 -> delta.textFill = LinearGradient(0.0, -20.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.2, 1.0, 0.6)), Stop(0.48, c(0.2, 1.0, 0.6)), Stop(0.58, c(0.0, 0.8, 0.4)), Stop(1.0, c(0.0, 0.8, 0.4))) // c("#17e07f")
        changeInt < 0 -> delta.textFill = LinearGradient(0.0, -20.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(1.0, 0.4, 0.5)), Stop(0.48, c(1.0, 0.4, 0.5)), Stop(0.58, c(0.9, 0.1, 0.0)), Stop(1.0, c(0.9, 0.1, 0.0))) // c("#fd2832")
        else -> delta.textFill = c("#4b3f3a")
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

}