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

class AtensionGaugeView(override val root: Parent, private val teamColor:Int) : Fragment() {

    private var wholeThing: StackPane
    private lateinit var backing: Rectangle
    private lateinit var progress: Rectangle
    private var atensionProgress: Int = 0 //Random.nextInt(0, 10000)
    private var atensionMaximum: Int = 10000

    private val genericHeight = 50.0
    private val maximumWidth = 610.0
    private val verticalPosition = 468.0
    private val horizontalPosition = 1100.0

    init {
        with(root) {
            wholeThing = stackpane {
                when(teamColor) {
                    0 -> {
                        alignment = Pos.CENTER_RIGHT
                        translateX -= horizontalPosition
                    }
                    1 -> {
                        alignment = Pos.CENTER_LEFT
                        translateX += horizontalPosition
                    }
                }

                backing = rectangle {
                    fill = c("#2d2d23")
                    width = maximumWidth
                    height = genericHeight
                    translateY += verticalPosition
                }

                progress = rectangle {
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
                    translateY += verticalPosition
                }

                imageview(getRes("barc_atlas.png").toString()) { // HAMMER
                    translateY += verticalPosition - 24.0
                    viewport = Rectangle2D(1152.0, 704.0, 192.0, 192.0)
                    when(teamColor) {
                        0 -> translateX += 96.0
                        else -> {
                            translateX -= 96.0
                            scaleX = -1.0
                        }
                    }
                }

                imageview(getRes("barc_atlas.png").toString()) { // ROTAR
                    translateY += verticalPosition - 10.0
                    viewport = Rectangle2D(1344.0, 704.0, 192.0, 192.0)
                    when(teamColor) {
                        0 -> translateX += 128.0
                        else -> {
                            translateX -= 128.0
                            scaleX = -1.0
                        }
                    }
                }

                imageview(getRes("barc_atlas.png").toString()) { // GAUGE
                    translateY += verticalPosition
                    viewport = Rectangle2D(896.0, 576.0, 640.0, 128.0)
                    when(teamColor) {
                        0 -> translateX += 20.0
                        else -> {
                            translateX -= 20.0
                            scaleX = -1.0
                        }
                    }
                }

                imageview(getRes("barc_atlas.png").toString()) { // RISK
                    translateY += verticalPosition - 46
                    viewport = Rectangle2D(896.0, 704.0, 256.0, 64.0)
                    when(teamColor) {
                        0 -> translateX += 64.0
                        else -> {
                            translateX -= 64.0
                            scaleX = -1.0
                        }
                    }
                }

                imageview(getRes("barc_atlas.png").toString()) { // BOLT
                    translateY += verticalPosition + 24
                    viewport = Rectangle2D(960.0, 768.0, 192.0, 64.0)
                    when(teamColor) {
                        0 -> translateX += 80.0
                        else -> {
                            translateX -= 80.0
                            scaleX = -1.0
                        }
                    }
                }

                imageview(getRes("barc_atlas.png").toString()) { // NUT
                    translateY += verticalPosition - 4.0
                    viewport = Rectangle2D(1024.0, 832.0, 128.0, 64.0)
                    when(teamColor) {
                        0 -> translateX += 100.0
                        else -> {
                            translateX -= 100.0
                            scaleX = -1.0
                        }
                    }
                }

            }
        }
    }

    fun applyData(m: MatchData) = Platform.runLater {
        atensionProgress = if (teamColor == 0) m.stunProgress.first else if (teamColor == 1) m.stunProgress.second else 0
        atensionMaximum = if (teamColor == 0) m.stunMaximum.first else if (teamColor == 1) m.stunMaximum.second else 1
        progress.width = getPercentage(maximumWidth)
        when(teamColor) {
            0 -> { // RED CREST
                progress.fill = when {
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
                progress.fill = when {
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
        wholeThing.isVisible = true
    }

    private fun getPercentage(modifier:Double = 100.0) = (atensionProgress.toDouble() / atensionMaximum) * modifier

}