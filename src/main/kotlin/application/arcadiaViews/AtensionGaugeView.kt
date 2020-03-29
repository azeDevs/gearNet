package application.arcadiaViews

import arcadia.ArcadeView
import arcadia.Arcadia
import arcadia.Arcadia.Companion.MAX_ATENSION
import arcadia.Arcadia.Companion.MAX_MUNITY
import arcadia.Arcadia.Companion.MAX_RESPECT
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.*
import utils.getRes

class AtensionGaugeView(override val root: Parent, private val teamColor:Int) : Fragment(),
    ArcadeView {

    private var animationFrame: Int = -1

    private val a: Arcadia by inject()
    private var container: StackPane
    private lateinit var flintHammer: ImageView
    private lateinit var munityProgress: Rectangle
    private lateinit var respectBacking: Rectangle
    private lateinit var respectProgress: Rectangle
    private lateinit var atensionBacking: Rectangle
    private lateinit var atensionProgress: Rectangle

    private lateinit var bannerScore: Label
    private lateinit var disSign: ImageView

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
                    PLAYER_1 -> {
                        alignment = Pos.CENTER_LEFT
                        translateX += 168.0
                    }
                    PLAYER_2 -> {
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
                        PLAYER_1 -> translateX += 110.0
                        PLAYER_2 -> translateX -= 110.0
                    }
                }

                respectProgress = rectangle {
                    fill = c("#feffe4")
                    width = getPercentage(0, MAX_RESPECT, respectMaxWidth)
                    height = 10.0
                    translateY -= 44
                    when(teamColor) {
                        PLAYER_1 -> translateX += 110.0
                        PLAYER_2 -> translateX -= 110.0
                    }
                }

                atensionBacking = rectangle {
                    fill = c("#2d2d23")
                    width = atensionMaxWidth
                    height = genericHeight
                    translateY += 16
                    when(teamColor) {
                        PLAYER_1 -> translateX += 84.0
                        PLAYER_2 -> translateX -= 84.0
                    }
                }

                atensionProgress = rectangle {
                    when(teamColor) {
                        PLAYER_1 -> { // RED CREST
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
                        PLAYER_2 -> { // BLUE CREST
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
                        PLAYER_1 -> {
                            viewport = Rectangle2D(640.0, 512.0, 704.0, 160.0)
                            translateX += 72.0
                        }
                        PLAYER_2 -> {
                            viewport = Rectangle2D(1344.0, 512.0, 704.0, 160.0)
                            translateX -= 72.0
                        }
                    }
                }

                flintHammer = imageview(getRes("atlas.png").toString()) { // HAMMER
                    translateY -= 36
                    viewport = Rectangle2D(640.0, 320.0, 192.0, 192.0)
                    when(teamColor) {
                        PLAYER_1 -> translateX += 587.0
                        PLAYER_2 -> {
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
                        PLAYER_1 -> translateX += 556.0
                        PLAYER_2 -> translateX -= 556.0
                    }
                }

                bannerScore = label {
                    addClass(ScoreStyle.signsTurnedText)
                    alignment = Pos.CENTER
                    when(teamColor) {
                        PLAYER_1 -> translateX -= 218.0
                        PLAYER_2 -> translateX += 218.0
                    }
                    translateY -= 812.0
                    blendMode = BlendMode.ADD
                }

                disSign = imageview(getRes("atlas.png").toString()) { // DIS
                    translateY -= 33
                    when(teamColor) {
                        PLAYER_1 -> {
                            viewport = Rectangle2D(1536.0, 1920.0, 192.0, 128.0)
                            translateX -= 128.0
                        }
                        PLAYER_2 -> {
                            viewport = Rectangle2D(1728.0, 1920.0, 192.0, 128.0)
                            translateX += 128.0
                        }
                    }
                }

            }
        }
    }

    override fun update() = Platform.runLater {
        val f = a.getPlayersStaged().p(teamColor)

        if (!f.isValid()) {
            bannerScore.text = ""
            munityProgress.width = getPercentage(MAX_MUNITY, MAX_MUNITY, munityMaxWidth)
            respectProgress.width = getPercentage(0, MAX_RESPECT, respectMaxWidth)
            atensionProgress.width = getPercentage(0, MAX_ATENSION, atensionMaxWidth)
            animationFrame = -1
            f.setSignal(false)
        } else {
            container.isVisible = true
            bannerScore.text = f.getSigns().toString()
            munityProgress.width = getPercentage(MAX_MUNITY-f.getAmunity(), MAX_MUNITY, munityMaxWidth)
            respectProgress.width = getPercentage(f.getRespect(), MAX_RESPECT, respectMaxWidth)
            atensionProgress.width = getPercentage(f.getAtension(), MAX_ATENSION, atensionMaxWidth)
            if (f.getSignal()) {
                animationFrame = 0
                f.setSignal(false)
            }
            when (teamColor) {
                0 -> { // RED CREST
                    atensionProgress.fill = when {
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() == 100 -> c("#ffb572")//c("#feffe4")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 90..99 -> c("#ffa168")//c("#fff49e")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 80..89 -> c("#ff8c5e")//c("#ffe692")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 70..79 -> c("#ff7553")//c("#ffd888")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 60..69 -> c("#ff5c48")//c("#ffc77d")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 50..59 -> c("#ffb572")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 40..49 -> c("#ffa168")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 30..39 -> c("#ff8c5e")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 20..29 -> c("#ff7553")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 10..19 -> c("#ff5c48")
                        else -> c("#ff413d")
                    }
                }
                1 -> { // BLUE CREST
                    atensionProgress.fill = when {
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() == 100 -> c("#c5bbbc")//c("#feffe4")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 90..99 -> c("#a3aabc")//c("#fdf4ae")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 80..89 -> c("#8098c3")//c("#f6e7b4")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 70..79 -> c("#5d84ce")//c("#eadabc")
                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 60..69 -> c("#3a70de")//c("#d9cbbf")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 50..59 -> c("#c5bbbc")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 40..49 -> c("#a3aabc")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 30..39 -> c("#8098c3")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 20..29 -> c("#5d84ce")
//                        getPercentage(f.getAtension(), MAX_ATENSION).toInt() in 10..19 -> c("#3a70de")
                        else -> c("#1759f3")
                    }
                }

            }
        }
    }

    override fun animate()  {
        val disXPosition = if(teamColor == PLAYER_1) 1536.0 else 1728.0
        when (animationFrame) {
            0 -> flintHammer.viewport = Rectangle2D(640.0, 320.0, 192.0, 192.0)
            1 -> flintHammer.viewport = Rectangle2D(832.0, 320.0, 192.0, 192.0)
            2 -> flintHammer.viewport = Rectangle2D(1024.0, 320.0, 192.0, 192.0)
            3 -> flintHammer.viewport = Rectangle2D(1216.0, 320.0, 192.0, 192.0)
            4 -> flintHammer.viewport = Rectangle2D(1408.0, 320.0, 192.0, 192.0)
            5 -> flintHammer.viewport = Rectangle2D(1600.0, 320.0, 192.0, 192.0)
            6 -> flintHammer.viewport = Rectangle2D(1792.0, 320.0, 192.0, 192.0)
            7 -> disSign.viewport = Rectangle2D(disXPosition, 1536.0, 192.0, 128.0)
            8 -> disSign.viewport = Rectangle2D(disXPosition, 1664.0, 192.0, 128.0)
            9 -> disSign.viewport = Rectangle2D(disXPosition, 1792.0, 192.0, 128.0)
            10 -> disSign.viewport = Rectangle2D(disXPosition, 1920.0, 192.0, 128.0)
            11 -> disSign.viewport = Rectangle2D(disXPosition, 1920.0, 192.0, 128.0)
            12 -> flintHammer.viewport = Rectangle2D(640.0, 320.0, 192.0, 192.0)
            13 -> disSign.viewport = Rectangle2D(disXPosition, 1920.0, 192.0, 128.0)
            14 -> disSign.viewport = Rectangle2D(disXPosition, 1792.0, 192.0, 128.0)
            else -> {
                flintHammer.viewport = Rectangle2D(640.0, 320.0, 192.0, 192.0)
                disSign.viewport = Rectangle2D(disXPosition, 1536.0, 192.0, 128.0)
                animationFrame = -1
            }
        }
//        animationFrame++
        if (animationFrame != -1) animationFrame++
    }

    private fun getPercentage(value:Int = 0, maximum:Int = 100, modifier:Double = 100.0) = (value.toDouble() / maximum) * modifier

}