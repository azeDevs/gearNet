package application.arcadiaViews

import arcadia.ArcadeView
import arcadia.Arcadia
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
import utils.XrdCharacter
import utils.getRes
import utils.isWithin
import utils.truncate

class LesserScoreView(override val root: Parent, private val scaleIndex:Int, private val teamColor:Int) : Fragment(), ArcadeView{

    private var animationFrame: Int = 1

    private val a: Arcadia by inject()
    private val scaleFactor: Float = 0.77f
    private var container: StackPane
    private lateinit var character: ImageView
    private lateinit var teamBackground: ImageView
    private lateinit var alias: Label
    private lateinit var signs: Label
    private lateinit var score: Label
    private lateinit var delta: Label


    init {
        with(root) {
            container = stackpane {
                scaleX *= scaleFactor
                scaleY *= scaleFactor
                scaleX -= (scaleIndex*0.0096)
                scaleY -= (scaleIndex*0.0096)

                when(teamColor) {
                    PLAYER_1 -> translateX -= 860.0 + (scaleIndex*1.6)
                    PLAYER_2 -> translateX += 860.0 + (scaleIndex*1.6)
                    else -> translateX += 400.0 - (scaleIndex*scaleIndex*3.2)
                }

                when(teamColor) {
                    PLAYER_1 -> translateY -= (scaleIndex*84)-300.0-(scaleIndex*48)
                    PLAYER_2 -> translateY -= (scaleIndex*84)-300.0-(scaleIndex*48)
                    else -> translateY += (scaleIndex*48)+64.0-(scaleIndex*11)
                }

                character = imageview(getRes("atlas.png").toString()) {
                    viewport = XrdCharacter.getCharacterTrademark()
                    translateX -= 258
                    translateY += 2
                    fitWidth = 42.0
                    fitHeight = 42.0
                }

                teamBackground = imageview(getRes("atlas.png").toString()) {
                    viewport = when(teamColor) {
                        PLAYER_1 -> Rectangle2D(1024.0, 1984.0, 256.0, 64.0)
                        PLAYER_2 -> Rectangle2D(1280.0, 1984.0, 256.0, 64.0)
                        else -> Rectangle2D(640.0, 0.0, 640.0, 64.0)
                    }
                    fitWidth = when(teamColor) {
                        PLAYER_1 -> 256.0
                        PLAYER_2 -> 256.0
                        else -> 640.0
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
                        PLAYER_2 -> {
                            addClass(ScoreStyle.viewerHandleText)
                            translateX -= 35.0
                            alignment = Pos.CENTER_RIGHT
                        }
                        else -> {
                            addClass(ScoreStyle.bountyHandleText)
                            translateX -= 96.0
                            alignment = Pos.CENTER_LEFT
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
                        PLAYER_2 -> {
                            translateX += 95.0
                            alignment = Pos.CENTER
                        }
                        else -> {
                            translateX += 240.0
                            alignment = Pos.CENTER
                        }
                    }
                    translateY -= 2.0
                }

                score = label {
                    addClass(InMatchStyle.matchBountyText)
                    when(teamColor) {
                        PLAYER_1 -> {
                            translateX -= 360.0
                            alignment = Pos.CENTER_RIGHT
                        }
                        PLAYER_2 -> {
                            translateX += 360.0
                            alignment = Pos.CENTER_LEFT
                        }
                        else -> {
                            translateX += 116.0
                            alignment = Pos.CENTER_LEFT
                        }
                    }
                    scaleX *= 0.77
                    scaleY *= 0.77
                    translateY -= 5.0
                }

                delta = label {
                    addClass(ScoreStyle.bountyChangeText)
                    when(teamColor) {
                        PLAYER_1 -> {
                            translateX -= 72.0
                            alignment = Pos.CENTER_LEFT
                        }
                        PLAYER_2 -> {
                            translateX += 72.0
                            alignment = Pos.CENTER_RIGHT
                        }
                        else -> {
                            translateX += 222.0
                            alignment = Pos.CENTER_RIGHT
                        }
                    }
                    translateY += 12.0
                }

            }
        }
    }

    override fun update() = Platform.runLater {

        when(teamColor) {
            PLAYER_1 -> {
                if (a.getPlayersStaged().p1.getSignal()) animationFrame = 0
                val watchers = a.getWatchers().filter { it.isTeam(PLAYER_1) }
                if (watchers.size-1 >= scaleIndex) {
                    applyWatcherData(watchers[scaleIndex])
                } else applyEmptyData()
            }
            PLAYER_2 -> {
                if (a.getPlayersStaged().p2.getSignal()) animationFrame = 0
                val watchers = a.getWatchers().filter { it.isTeam(PLAYER_2) }
                if (watchers.size-1 >= scaleIndex) {
                    applyWatcherData(watchers[scaleIndex])
                } else applyEmptyData()
            }
            else -> applyEmptyData()
//            {
//                val watchers = a.getPlayersList().drop(3)
//                if (watchers.size-1 >= scaleIndex) {
//                    applyWatcherData(watchers[scaleIndex])
//                } else applyEmptyData()
//            }
        }


    }

    private fun applyWatcherData(p: Player) = Platform.runLater {

        // Do stuff for central Viewer Leaderboard
        if (p.isValid() && !isWithin(teamColor)) {

            character.viewport = XrdCharacter.getCharacterTrademark(p.getPlayerData().characterId)
            character.isVisible = true
            alias.text = truncate(p.getUserName(), 18)
            alias.isVisible = true
            signs.text = p.getSigns().toString()
            signs.isVisible = true
            score.text = p.getScoreTotalString()
            delta.text = p.getScoreDeltaString()
            setChangeTextColor(p.getScoreDelta())
            container.isVisible = true

        // Do stuff for Red Viewer Amunity, stage left (Red)
        } else if (p.isValid() && p.isTeam(PLAYER_1) && teamColor == PLAYER_1) {
            character.isVisible = false
            alias.text = truncate(p.getUserName(), 9)
            alias.isVisible = true
            signs.text = p.getSigns().toString()
            signs.isVisible = true
            score.text = p.getScoreTotalString()
            delta.text = ""
            container.isVisible = true

        // Do stuff for Blue Viewer Amunity, stage right (Blue)
        } else if (p.isValid() && p.isTeam(PLAYER_2) && teamColor == PLAYER_2) {
            character.isVisible = false
            alias.text = truncate(p.getUserName(), 9)
            alias.isVisible = true
            signs.text = p.getSigns().toString()
            signs.isVisible = true
            score.text = p.getScoreTotalString()
            delta.text = ""
            container.isVisible = true

        }
    }

    private fun applyEmptyData() = Platform.runLater {
        // Apply empty data
        character.viewport = XrdCharacter.getCharacterTrademark()
        character.isVisible = false
        alias.text = ""
        alias.isVisible = false
        signs.text = ""
        signs.isVisible = false
        score.text = "FREE"
        delta.text = ""
        container.isVisible = false
    }

    private fun setChangeTextColor(changeInt: Int) = when {
        changeInt > 0 -> delta.textFill = LinearGradient(0.0, -20.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.2, 1.0, 0.6)), Stop(0.48, c(0.2, 1.0, 0.6)), Stop(0.58, c(0.0, 0.8, 0.4)), Stop(1.0, c(0.0, 0.8, 0.4))) // c("#17e07f")
        changeInt < 0 -> delta.textFill = LinearGradient(0.0, -20.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(1.0, 0.4, 0.5)), Stop(0.48, c(1.0, 0.4, 0.5)), Stop(0.58, c(0.9, 0.1, 0.0)), Stop(1.0, c(0.9, 0.1, 0.0))) // c("#fd2832")
        else -> delta.textFill = c("#4b3f3a")
    }

    override fun onBeforeShow() {
        animationFrame = 0
    }

    fun restartAnimation() { animationFrame = 0 }

    override fun animate() {
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