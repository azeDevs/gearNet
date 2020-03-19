package application.stream

import MyApp.Companion.SIMULATION_MODE
import application.debug.ArcadeView
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import models.Player
import session.Character.getCharacterTrademark
import tornadofx.*
import utils.addCommas
import utils.getRandomName
import utils.getRes
import kotlin.random.Random

class FighterScoreView(override val root: Parent, private val scaleIndex:Int) : Fragment(), ArcadeView {

    private var wholeThing: StackPane
    private lateinit var character: ImageView
    private lateinit var spirit: ImageView
    private lateinit var handle1: Label
    private lateinit var handle2: Label
    private lateinit var status: ImageView

    private lateinit var rating: ImageView
    private lateinit var bounty1: Label
    private lateinit var bounty2: Label

    private lateinit var change: Label


    init {
        with(root) {
            wholeThing = stackpane { isVisible = false
                addClass(ScoreStyle.bountyContainer)
                translateX += 444
                translateY -= 300

                scaleX -= (scaleIndex*0.056)
                scaleY -= (scaleIndex*0.056)
                translateY += (scaleIndex*(120-scaleIndex*3.3))
                translateX -= (scaleIndex*9.6)


                minWidth = 1024.0
                maxWidth = 1024.0
                character = imageview(getRes("atlas.png").toString()) {
                    viewport = getCharacterTrademark()
                    translateX -= 209
                    translateY += 18
                    fitWidth = 64.0
                    fitHeight = 64.0
                }

                imageview(getRes("atlas.png").toString()) {
                    viewport = Rectangle2D(1280.0, 0.0, 768.0, 192.0)
                    translateX += 25
                    fitWidth = 592.0
                    fitHeight = 148.0
                }

                change = label {
                    translateX += 252
                    translateY -= 38
                    addClass(ScoreStyle.bountyChangeText)
                }

                spirit = imageview(getRes("cb_spirit_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 252
                    translateY += 8
                    fitWidth = 80.0
                    fitHeight = 80.0
                    opacity = 0.96
                    rotate += 9.6
                    blendMode = BlendMode.LIGHTEN
                }

                rating = imageview(getRes("atlas.png").toString()) {
                    viewport = Player().getRatingImage()
                    translateX += 252
                    translateY += 16
                    fitWidth = 52.0
                    fitHeight = 52.0
                    opacity = 0.88
                    blendMode = BlendMode.HARD_LIGHT
                }

                stackpane {
                    translateX -= 44
                    translateY -= 38
                    scaleX *= 0.9
                    opacity = 0.96

                    handle2 = label { addClass(ScoreStyle.bountyHandleShadow)
                        translateY += 3
                        translateX += 2
                        blendMode = BlendMode.HARD_LIGHT
                        opacity = 0.01
                    }
                    handle1 = label { addClass(ScoreStyle.bountyHandleText) }
                }

                stackpane {
                    translateX -= 32.0
                    translateY += 12.0
                    bounty2 = label {
                        addClass(ScoreStyle.bountyBountyShadow)
                        scaleX += 0.04
                        scaleY += 0.18
                        blendMode = BlendMode.ADD
                    }
                    bounty1 = label {
                        addClass(ScoreStyle.bountyBountyText)
                        translateY += 1.0
                    }
                }

                status = imageview(getRes("atlas.png").toString()) {
                    viewport = Rectangle2D(0.0, 256.0, 128.0, 64.0)
                    translateX += 134
                    translateY += 16
                    fitWidth = 80.0
                    fitHeight = 40.0
                }

            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater {
        wholeThing.isVisible = flag
    }

    override fun applyData(p: Player) = Platform.runLater {
        when {
            SIMULATION_MODE -> applyRandomData(p)
            p.getPlayerId() > 0L -> applyFighterData(p)
            else -> applyEmptyData()
        }
    }

    private fun applyEmptyData() {
        character.viewport = getCharacterTrademark()
        handle1.text = ""; handle1.isVisible = false
        handle2.text = ""; handle2.isVisible = false
        status.isVisible = false
        bounty1.text = ""
        bounty2.text = ""
        change.text = ""
        rating.isVisible = false
        spirit.isVisible = false
        wholeThing.isVisible = false
    }

    private fun applyFighterData(p: Player) {
        character.viewport = getCharacterTrademark(p.getFighterData().characterId)
        handle1.text = p.getUserName(); handle1.isVisible = true
        handle2.text = p.getUserName(); handle2.isVisible = true
        status.viewport = p.getStatusImage(); status.isVisible = true
        rating.viewport = p.getRatingImage(p.getPlaySide()); rating.isVisible = true
        bounty1.text = p.getScoreTotalString()
        bounty2.text = p.getScoreTotalString()
        change.text = p.getScoreDeltaString()
        setChangeTextColor(p.getScoreDelta())
        rating.fitWidth = 57.0 * (1 + p.getRating() * 0.033)
        rating.fitHeight = 57.0 * (1 + p.getRating() * 0.033)
        spirit.isVisible = p.getRating() > 0
        spirit.fitWidth = 77.0 * (1 + p.getRating() * 0.33)
        spirit.fitHeight = 77.0 * (1 + p.getRating() * 0.33)

        wholeThing.isVisible = true
    }

    private fun applyRandomData(p: Player) {
        val chainInt = Random.nextInt(9)
        val bountyStr = addCommas(Random.nextInt(1222333).toString())
        val changeInt = Random.nextInt(-444555, 666777)
        val dispName = getRandomName()
        character.viewport = Rectangle2D(Random.nextInt(8) * 64.0, Random.nextInt(4) * 64.0, 64.0, 64.0)
        handle1.text = dispName
        handle2.text = dispName
        bounty1.text = "$bountyStr W$"
        bounty2.text = "$bountyStr W$"
        setChangeTextColor(changeInt)
        change.text = p.getScoreDeltaString(changeInt)
        status.viewport = p.getStatusImage(Random.nextInt(100), Random.nextDouble(2.0).toFloat())
        rating.viewport = p.getRatingImage(p.getPlaySide())
        spirit.isVisible = chainInt > 0
        spirit.fitWidth = 44.0 + ((8+chainInt) * chainInt)
        spirit.fitHeight = 44.0 + ((8+chainInt) * chainInt)
        wholeThing.isVisible = true
    }

    private fun setChangeTextColor(changeInt: Int) = when {
        changeInt > 0 -> change.textFill = LinearGradient(0.0, -20.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.2, 1.0, 0.6)), Stop(0.48, c(0.2, 1.0, 0.6)), Stop(0.58, c(0.0, 0.8, 0.4)), Stop(1.0, c(0.0, 0.8, 0.4))) //c("#17e07f")
        changeInt < 0 -> change.textFill = LinearGradient(0.0, -20.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(1.0, 0.4, 0.5)), Stop(0.48, c(1.0, 0.4, 0.5)), Stop(0.58, c(0.9, 0.1, 0.0)), Stop(1.0, c(0.9, 0.1, 0.0))) // c("#fd2832")
        else -> change.textFill = c("#52463f")
    }


}