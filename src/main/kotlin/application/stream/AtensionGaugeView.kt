package application.stream

import application.debug.ArcadeView
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import models.Player.Companion.MAX_ATENSION
import models.Player.Companion.MAX_MUNITY
import models.Player.Companion.MAX_RESPECT
import session.Session
import tornadofx.*
import utils.getRes

class AtensionGaugeView(override val root: Parent, private val teamColor:Int) : Fragment(), ArcadeView {

    private var animationFrame: Int = -1

    private var container: StackPane
    private lateinit var flintHammer: ImageView
    private lateinit var munityProgress: Rectangle
    private lateinit var respectBacking: Rectangle
    private lateinit var respectProgress: Rectangle
    private lateinit var atensionBacking: Rectangle
    private lateinit var atensionProgress: Rectangle
    private lateinit var bannerHandle: Label

//    val audioResourceURL = resources.url("sound.wav")

    private val genericHeight = 50.0
    private val atensionMaxWidth = 610.0
    private val respectMaxWidth = 210.0
    private val munityMaxWidth = 124.0

    init {
        with(root) {
            container = stackpane {
                translateY += 448
                when(teamColor) {
                    0 -> {
                        alignment = Pos.CENTER_LEFT
                        translateX += 168.0
                    }
                    1 -> {
                        alignment = Pos.CENTER_RIGHT
                        translateX -= 168.0
                    }
                }

                respectBacking = rectangle {
                    fill = c("#39322b")
                    width = respectMaxWidth
                    height = 10.0
                    translateY -= 44
                    when(teamColor) {
                        0 -> translateX += 110.0
                        else -> translateX -= 110.0
                    }
                }

                respectProgress = rectangle {
                    fill = c("#feffe4")
                    width = getPercentage(0, MAX_RESPECT, respectMaxWidth)
                    height = 10.0
                    translateY -= 44
                    when(teamColor) {
                        0 -> translateX += 110.0
                        else -> translateX -= 110.0
                    }
                }

                atensionBacking = rectangle {
                    fill = c("#2d2d23")
                    width = atensionMaxWidth
                    height = genericHeight
                    translateY += 16
                    when(teamColor) {
                        0 -> translateX += 84.0
                        else -> translateX -= 84.0
                    }
                }

                atensionProgress = rectangle {
                    when(teamColor) {
                        0 -> { // RED CREST
                            translateX += 84
                            fill = when {
                                getPercentage().toInt() == 100 -> c("#feffe4")
                                getPercentage().toInt() in 90..99 -> c("#fff49e")
                                getPercentage().toInt() in 80..89 -> c("#ffe692")
                                getPercentage().toInt() in 70..79 -> c("#ffd888")
                                getPercentage().toInt() in 60..69 -> c("#ffc77d")
                                getPercentage().toInt() in 50..59 -> c("#ffb572")
                                getPercentage().toInt() in 40..49 -> c("#ffa168")
                                getPercentage().toInt() in 30..39 -> c("#ff8c5e")
                                getPercentage().toInt() in 20..29 -> c("#ff7553")
                                getPercentage().toInt() in 10..19 -> c("#ff5c48")
                                else -> c("#ff413d")
                            }
                        }
                        1 -> { // BLUE CREST
                            translateX -= 84
                            fill = when {
                                getPercentage().toInt() == 100 -> c("#feffe4")
                                getPercentage().toInt() in 90..99 -> c("#fdf4ae")
                                getPercentage().toInt() in 80..89 -> c("#f6e7b4")
                                getPercentage().toInt() in 70..79 -> c("#eadabc")
                                getPercentage().toInt() in 60..69 -> c("#d9cbbf")
                                getPercentage().toInt() in 50..59 -> c("#c5bbbc")
                                getPercentage().toInt() in 40..49 -> c("#a3aabc")
                                getPercentage().toInt() in 30..39 -> c("#8098c3")
                                getPercentage().toInt() in 20..29 -> c("#5d84ce")
                                getPercentage().toInt() in 10..19 -> c("#3a70de")
                                else -> c("#1759f3")
                            }
                        }
                    }
                    width = getPercentage(0, MAX_ATENSION, atensionMaxWidth)
                    height = genericHeight
                    translateY += 16

                }

                imageview(getRes("atlas.png").toString()) { // GAUGE
                    when(teamColor) {
                        0 -> {
                            viewport = Rectangle2D(640.0, 512.0, 704.0, 160.0)
                            translateX += 72.0
                        }
                        else -> {
                            viewport = Rectangle2D(1344.0, 512.0, 704.0, 160.0)
                            translateX -= 72.0
                        }
                    }
                }

                flintHammer = imageview(getRes("atlas.png").toString()) { // HAMMER
                    translateY -= 36
                    viewport = Rectangle2D(640.0, 320.0, 192.0, 192.0)
                    when(teamColor) {
                        0 -> translateX += 587.0
                        else -> {
                            translateX -= 587.0
                            scaleX = -1.0
                        }
                    }
                }

                munityProgress = rectangle {
                    fill = c("#2d2d23")
                    width = getPercentage(0, MAX_MUNITY, munityMaxWidth)
                    height = 15.0
                    translateY -= 25
                    when(teamColor) {
                        0 -> translateX += 556.0
                        else -> translateX -= 556.0
                    }
                }

                bannerHandle = label {
                    addClass(ScoreStyle.signsTurnedText)
                    alignment = Pos.CENTER
                    when(teamColor) {
                        0 -> translateX -= 215.0
                        else -> translateX += 215.0
                    }
                    translateY -= 820.0
                }

            }
        }
    }

    override fun applyData(s: Session) = Platform.runLater {
        val f = s.getStagedFighers().p(teamColor)

        if (!f.isValid()) bannerHandle.text = "-" else {
            container.isVisible = true

            if (s.api.getClientMatch().getStrikeStun(teamColor)) bannerHandle.text = "X"
            else bannerHandle.text = f.getUserName()

            munityProgress.width = getPercentage(MAX_MUNITY-f.getMunity(), MAX_MUNITY, munityMaxWidth)
            respectProgress.width = getPercentage(f.getRespect(), MAX_RESPECT, respectMaxWidth)
            atensionProgress.width = getPercentage(f.getAtension(), MAX_ATENSION, atensionMaxWidth)
            if (f.getAtension() >= MAX_ATENSION) animationFrame = 0
            when (teamColor) {
                0 -> { // RED CREST
                    atensionProgress.fill = when {
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() == 100 -> c("#feffe4")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 90..99 -> c("#fff49e")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 80..89 -> c("#ffe692")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 70..79 -> c("#ffd888")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 60..69 -> c("#ffc77d")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 50..59 -> c("#ffb572")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 40..49 -> c("#ffa168")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 30..39 -> c("#ff8c5e")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 20..29 -> c("#ff7553")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 10..19 -> c("#ff5c48")
                        else -> c("#ff413d")
                    }
                }
                1 -> { // BLUE CREST
                    atensionProgress.fill = when {
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() == 100 -> c("#feffe4")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 90..99 -> c("#fdf4ae")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 80..89 -> c("#f6e7b4")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 70..79 -> c("#eadabc")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 60..69 -> c("#d9cbbf")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 50..59 -> c("#c5bbbc")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 40..49 -> c("#a3aabc")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 30..39 -> c("#8098c3")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 20..29 -> c("#5d84ce")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 10..19 -> c("#3a70de")
                        else -> c("#1759f3")
                    }
                }

            }
        }
    }

    override fun updateAnimation(s: Session)  {
        when (animationFrame) {
            0 -> flintHammer.viewport = Rectangle2D(640.0, 320.0, 192.0, 192.0)
            1 -> flintHammer.viewport = Rectangle2D(832.0, 320.0, 192.0, 192.0)
            2 -> flintHammer.viewport = Rectangle2D(1024.0, 320.0, 192.0, 192.0)
            3 -> flintHammer.viewport = Rectangle2D(1216.0, 320.0, 192.0, 192.0)
            4 -> flintHammer.viewport = Rectangle2D(1408.0, 320.0, 192.0, 192.0)
            5 -> flintHammer.viewport = Rectangle2D(1600.0, 320.0, 192.0, 192.0)
            6 -> flintHammer.viewport = Rectangle2D(1792.0, 320.0, 192.0, 192.0)
            else -> {
                flintHammer.viewport = Rectangle2D(640.0, 320.0, 192.0, 192.0)
                animationFrame = -1
            }
        }
        animationFrame++
    }

    private fun getPercentage(value:Int = 0, maximum:Int = 100, modifier:Double = 100.0) = (value.toDouble() / maximum) * modifier

}