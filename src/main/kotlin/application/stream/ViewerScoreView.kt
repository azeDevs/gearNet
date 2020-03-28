package application.stream

import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import models.Player
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.*
import utils.getRes
import utils.truncate

class ViewerScoreView(override val root: Parent, private val scaleIndex:Int, private val teamColor:Int) : Fragment(), ArcadeView{

    private var animationFrame: Int = 1

    private val a: Arcadia by inject()
    private val scaleFactor: Float = 0.80f
    private var container: StackPane
    private lateinit var teamBackground: ImageView
    private lateinit var alias: Label
    private lateinit var signs: Label


    init {
        with(root) {
            container = stackpane {
                scaleX *= scaleFactor
                scaleY *= scaleFactor
                scaleX -= (scaleIndex*0.0096)
                scaleY -= (scaleIndex*0.0096)

                when(teamColor) {
                    PLAYER_1 -> translateX -= 860.0 + (scaleIndex*1.6)
                    else -> translateX += 860.0 + (scaleIndex*1.6)
                }

                when(teamColor) {
                    PLAYER_1 -> translateY -= (scaleIndex*84)-300.0-(scaleIndex*48)
                    else -> translateY -= (scaleIndex*84)-300.0-(scaleIndex*48)
                }

                teamBackground = imageview(getRes("atlas.png").toString()) {
                    viewport = when(teamColor) {
                        PLAYER_1 -> Rectangle2D(1024.0, 1984.0, 256.0, 64.0)
                        else -> Rectangle2D(1280.0, 1984.0, 256.0, 64.0)
                    }
                    fitWidth = when(teamColor) {
                        PLAYER_1 -> 256.0
                        else -> 256.0
                    }
                    fitHeight = 64.0
                }

                alias = label {

                    when(teamColor) {
                        PLAYER_1 -> {
                            addClass(ScoreStyle.viewerHandleText)
                            translateX += 35.0
                            alignment = Pos.CENTER_LEFT
                        }
                        else -> {
                            addClass(ScoreStyle.viewerHandleText)
                            translateX -= 35.0
                            alignment = Pos.CENTER_RIGHT
                        }
                    }
                    scaleX *= 0.57
                    scaleY *= 0.77
                    translateY -= 2.0
                }

                signs = label {
                    addClass(ScoreStyle.signsTurnedText)
                    when(teamColor) {
                        PLAYER_1 -> {
                            translateX -= 95.0
                            alignment = Pos.CENTER
                        }
                        else -> {
                            translateX += 95.0
                            alignment = Pos.CENTER
                        }
                    }
                    translateY -= 2.0
                }

            }
        }
    }

    override fun applyData() = Platform.runLater {

        when(teamColor) {
            PLAYER_1 ->
            {
                if (a.getPlayersStaged().p1.getSignal()) animationFrame = 0
                val watchers = a.getWatchers().filter { it.isTeam(PLAYER_1) }
                if (watchers.size-1 >= scaleIndex) {
                    applyWatcherData(watchers[scaleIndex])
                } else applyEmptyData()
            }
            PLAYER_2 ->
            {
                if (a.getPlayersStaged().p2.getSignal()) animationFrame = 0
                val watchers = a.getWatchers().filter { it.isTeam(PLAYER_2) }
                if (watchers.size-1 >= scaleIndex) {
                    applyWatcherData(watchers[scaleIndex])
                } else applyEmptyData()
            }
            else -> applyEmptyData()

        }


    }

    private fun applyWatcherData(p: Player) = Platform.runLater {

        // Do stuff for Red Viewer Amunity, stage left (Red)
        if (p.isValid() && p.isTeam(PLAYER_1) && teamColor == PLAYER_1) {
            alias.text = truncate(p.getUserName(), 9)
            alias.isVisible = true
            signs.text = p.getSigns().toString()
            signs.isVisible = true
            container.isVisible = true

        // Do stuff for Blue Viewer Amunity, stage right (Blue)
        } else if (p.isValid() && p.isTeam(PLAYER_2) && teamColor == PLAYER_2) {
            alias.text = truncate(p.getUserName(), 9)
            alias.isVisible = true
            signs.text = p.getSigns().toString()
            signs.isVisible = true
            container.isVisible = true
        }
    }

    private fun applyEmptyData() = Platform.runLater {
        // Apply empty data
        alias.text = ""
        alias.isVisible = false
        signs.text = ""
        signs.isVisible = false
        container.isVisible = false
    }

    fun restartAnimation() { animationFrame = 0 }

    override fun updateAnimation() {
        val FRAME_1 = 1536.0
        val FRAME_2 = 1600.0
        val FRAME_3 = 1664.0
        val FRAME_4 = 1728.0
        val FRAME_5 = 1792.0
        val FRAME_6 = 1856.0
        val FRAME_7 = 1920.0
        val FRAME_8 = 1984.0

        val frameYViewport = when (animationFrame) {
            0 -> FRAME_1
            1 -> FRAME_2
            2 -> FRAME_1
            3 -> FRAME_2
            4 -> FRAME_1
            5 -> FRAME_3
            6 -> FRAME_2
            7 -> FRAME_3
            8 -> FRAME_4
            9 -> FRAME_3
            10 -> FRAME_4
            11 -> FRAME_5
            12 -> FRAME_4
            13 -> FRAME_5
            14 -> FRAME_6
            15 -> FRAME_5
            16 -> FRAME_7
            17 -> FRAME_6
            18 -> FRAME_7
            19 -> FRAME_6
            20 -> FRAME_7
            else -> {
                animationFrame = -1
                FRAME_8
            }
        }
        if (animationFrame != -1) animationFrame++
        teamBackground.viewport = when(teamColor) {
            PLAYER_1 -> Rectangle2D(1024.0, frameYViewport, 256.0, 64.0)
            PLAYER_2 -> Rectangle2D(1280.0, frameYViewport, 256.0, 64.0)
            else -> Rectangle2D(640.0, 128.0, 448.0, 64.0)
        }
    }

}