package application.stream

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import memscan.MatchData
import tornadofx.*
import utils.getRes
import kotlin.random.Random

class AtensionGaugeView(override val root: Parent, private val teamColor:Int) : Fragment() {

    private var atensionVal: Int = Random.nextInt(0, 10000)
    private var atensionMax: Int = 10000

    private var container: StackPane
    private lateinit var munityProgress: Rectangle
    private lateinit var respectBacking: Rectangle
    private lateinit var respectProgress: Rectangle
    private lateinit var atensionBacking: Rectangle
    private lateinit var atensionProgress: Rectangle

//    val audioResourceURL = resources.url("sound.wav")

    private val genericHeight = 50.0
    private val maximumWidth = 610.0
    private val horizontalPosition = 980.0

    init {
        with(root) {
            container = stackpane {
                translateY += 448
                when(teamColor) {
                    0 -> {
                        alignment = Pos.CENTER_RIGHT
                        translateX -= horizontalPosition + 72
                    }
                    1 -> {
                        alignment = Pos.CENTER_LEFT
                        translateX += horizontalPosition + 72
                    }
                }

                respectBacking = rectangle {
                    fill = c("#39322b")
                    width = 210.0
                    height = 10.0
                    translateY -= 44
                    when(teamColor) {
                        0 -> translateX -= 384.0
                        else -> translateX += 384.0
                    }
                }

                respectProgress = rectangle {
                    fill = c("#feffe4")
                    width = 100.0
                    height = 10.0
                    translateY -= 44
                    when(teamColor) {
                        0 -> translateX -= 384.0
                        else -> translateX += 384.0
                    }
                }

                atensionBacking = rectangle {
                    fill = c("#2d2d23")
                    width = maximumWidth
                    height = genericHeight
                    translateY += 16
                }

                atensionProgress = rectangle {
                    when(teamColor) {
                        0 -> { // RED CREST
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
                    width = getPercentage(maximumWidth)
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

                imageview(getRes("atlas.png").toString()) { // HAMMER
                    translateY -= 36
                    viewport = Rectangle2D(640.0, 320.0, 192.0, 192.0)
                    when(teamColor) {
                        0 -> translateX += 74.0
                        else -> {
                            translateX -= 74.0
                            scaleX = -1.0
                        }
                    }
                }

                munityProgress = rectangle {
                    fill = c("#2d2d23")
                    width = 116.36 //124.0 (7.64 px/unit)
                    height = 15.0
                    translateY -= 25
                    when(teamColor) {
                        0 -> translateX -= 23.0
                        else -> translateX += 23.0
                    }
                }

            }
        }
    }

    fun applyData(m: MatchData) = Platform.runLater {
        atensionVal = if (teamColor == 0) m.stunProgress.first else if (teamColor == 1) m.stunProgress.second else 0
        atensionMax = if (teamColor == 0) m.stunMaximum.first else if (teamColor == 1) m.stunMaximum.second else 1
        atensionProgress.width = getPercentage(maximumWidth)
        when(teamColor) {
            0 -> { // RED CREST
                atensionProgress.fill = when {
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
                atensionProgress.fill = when {
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
        container.isVisible = true
    }

    private fun getPercentage(modifier:Double = 100.0) = (atensionVal.toDouble() / atensionMax) * modifier

}