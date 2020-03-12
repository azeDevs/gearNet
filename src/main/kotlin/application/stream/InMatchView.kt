package application.stream

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
import session.Character.getCharacterTrademark
import session.Player
import session.Session
import session.log
import tornadofx.*
import utils.addCommas
import utils.getRandomName
import utils.getRes
import kotlin.math.roundToInt
import kotlin.random.Random

class InMatchView(override val root: Parent, val scaleIndex:Int) : Fragment() {

    private var wholeThing: StackPane
    private lateinit var character: ImageView
    private lateinit var chains1: ImageView
    private lateinit var chains2: ImageView
    private lateinit var chains3: ImageView
    private lateinit var chains4: ImageView
    private lateinit var chains5: ImageView
    private lateinit var chains6: ImageView
    private lateinit var chains7: ImageView
    private lateinit var chains8: ImageView
    private lateinit var handle1: Label
    private lateinit var handle2: Label
    private lateinit var riskRating: ImageView

    private lateinit var chain: ImageView
    private lateinit var bounty1: Label
    private lateinit var bounty2: Label

    private lateinit var change: Label


    init {
        with(root) {
            wholeThing = stackpane { isVisible = false
                addClass(BigScoreStyle.bountyContainer)
                translateX += target

                scaleX += (0.16)
                scaleY += (0.16)
                scaleX -= (scaleIndex*0.02)*(scaleIndex*1.16)
                scaleY -= (scaleIndex*0.02)*(scaleIndex*1.16)
                translateX += scaleIndex*scaleIndex*scaleIndex
                translateY += scaleIndex*12
                translateY -= scaleIndex*scaleIndex*scaleIndex

                translateX += 400
                minWidth = 1024.0
                maxWidth = 1024.0
                character = imageview(getRes("gn_atlas.png").toString()) {
                    viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                    translateX -= 209
                    translateY += 18
                    fitWidth = 64.0
                    fitHeight = 64.0
                }
                imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(256.0, 0.0, 768.0, 192.0)
                    translateX += 25
                    fitWidth = 592.0
                    fitHeight = 148.0
                }
                change = label {
                    translateX += 252
                    translateY -= 40
                    rotate += 1
                    addClass(BigScoreStyle.bountyChangeText)
                    blendMode = BlendMode.HARD_LIGHT
                }
                val chainsLightFit = 50.0
                val chainsLightOpacity = 0.8
                val chainsLightRotation = 16.0
                chains2 = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 229
                    translateY -= 9
                    fitWidth = chainsLightFit
                    fitHeight = chainsLightFit
                    opacity = chainsLightOpacity
                    rotate += chainsLightRotation
                    blendMode = BlendMode.ADD
                }
                chains3 = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 218
                    translateY += 13
                    fitWidth = chainsLightFit
                    fitHeight = chainsLightFit
                    opacity = chainsLightOpacity
                    rotate += chainsLightRotation
                    blendMode = BlendMode.ADD

                }
                chains4 = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 229
                    translateY += 37
                    fitWidth = chainsLightFit
                    fitHeight = chainsLightFit
                    opacity = chainsLightOpacity
                    rotate += chainsLightRotation
                    blendMode = BlendMode.ADD

                }
                chains5 = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 253
                    translateY += 47
                    fitWidth = chainsLightFit
                    fitHeight = chainsLightFit
                    opacity = chainsLightOpacity
                    rotate += chainsLightRotation
                    blendMode = BlendMode.ADD

                }
                chains6 = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 276
                    translateY += 37
                    fitWidth = chainsLightFit
                    fitHeight = chainsLightFit
                    opacity = chainsLightOpacity
                    rotate += chainsLightRotation
                    blendMode = BlendMode.ADD

                }
                chains7 = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 286
                    translateY += 13
                    fitWidth = chainsLightFit
                    fitHeight = chainsLightFit
                    opacity = chainsLightOpacity
                    rotate += chainsLightRotation
                    blendMode = BlendMode.ADD

                }
                chains8 = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 276
                    translateY -= 9
                    fitWidth = chainsLightFit
                    fitHeight = chainsLightFit
                    opacity = chainsLightOpacity
                    rotate += chainsLightRotation
                    blendMode = BlendMode.ADD
                }

                chains1 = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 252
                    translateY += 8
                    fitWidth = 80.0
                    fitHeight = 80.0
                    opacity = 0.96
                    rotate += chainsLightRotation
                    blendMode = BlendMode.ADD
                }

                chain = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
                    translateX += 252
                    translateY += 16
                    fitWidth = 52.0
                    fitHeight = 52.0
                    opacity = 0.96
                    blendMode = BlendMode.HARD_LIGHT
                }

                stackpane {
                    translateX -= 44
                    translateY -= 38
                    scaleX *= 0.9
                    opacity = 0.96

                    handle2 = label { addClass(BigScoreStyle.bountyHandleShadow)
                        translateY += 3
                        translateX += 2
                        blendMode = BlendMode.HARD_LIGHT
                    }
                    handle1 = label { addClass(BigScoreStyle.bountyHandleText) }
                }

                stackpane {
                    translateX -= 60.0
                    translateY += 12.0
                    bounty2 = label {
                        addClass(BigScoreStyle.bountyBountyShadow)
                        scaleX += 0.04
                        scaleY += 0.18
                        blendMode = BlendMode.ADD
                    }
                    bounty1 = label {
                        addClass(InMatchStyle.matchBountyText)
                        translateY += 1.0
                    }
                }

                riskRating = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(0.0, 256.0, 128.0, 64.0)
                    translateX += 134
                    translateY += 16
                    fitWidth = 80.0
                    fitHeight = 40.0
                }



            }
        }
    }

    var target = 0.0
    private var current = 0.0

    fun approachTarget() {
        if (current != target) {
            val targetFraction = ((current - target) * 0.5)
            log("target: ${target}  |  current: ${current}  |  FRACTION: ${targetFraction}")
            wholeThing.translateX -= targetFraction
            current -= targetFraction
            if ((current).roundToInt().equals((target).roundToInt())) current = target
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater {
        wholeThing.isVisible = flag
    }

    fun applyData(p: Player, s: Session) = Platform.runLater {
        if (s.randomValues) applyRandomData(p) else
            if (p.getSteamId() > 0L) {
                character.viewport = getCharacterTrademark(p.getData().characterId)
                handle1.text = p.getNameString(); handle1.isVisible = true
                handle2.text = p.getNameString(); handle2.isVisible = true
                riskRating.viewport = p.getStatusImage(); riskRating.isVisible = true
                chain.viewport = p.getRatingImage(); chain.isVisible = true
                bounty1.text = p.getBountyString()
//                if (p.getBounty() > 0) bounty1.addClass(BigScoreStyle.bountyBountyText)
//                else  bounty1.addClass(BigScoreStyle.bountyFreeText)
                bounty2.text = p.getBountyString()
                change.text = p.getChangeString()
                setChangeTextColor(p.getBountyChange())
                chains1.isVisible = p.getRating() > 0
                chains2.isVisible = p.getRating() > 1
                chains3.isVisible = p.getRating() > 2
                chains4.isVisible = p.getRating() > 3
                chains5.isVisible = p.getRating() > 4
                chains6.isVisible = p.getRating() > 5
                chains7.isVisible = p.getRating() > 6
                chains8.isVisible = p.getRating() > 7


                chains1.fitWidth = 57.0 + ((8+p.getRating()) * p.getRating())
                chains1.fitHeight = 57.0 + ((8+p.getRating()) * p.getRating())

                wholeThing.isVisible = true
            } else {
                character.viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                handle1.text = ""; handle1.isVisible = false
                handle2.text = ""; handle2.isVisible = false
                riskRating.isVisible = false
                chain.isVisible = false
                bounty1.text = ""
                bounty2.text = ""
                change.text = ""
                chains1.isVisible = false
                chains2.isVisible = false
                chains3.isVisible = false
                chains4.isVisible = false
                chains5.isVisible = false
                chains6.isVisible = false
                chains7.isVisible = false
                chains8.isVisible = false
                wholeThing.isVisible = false
            }
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
        change.text = p.getChangeString(1f, changeInt)
        riskRating.viewport = p.getStatusImage(Random.nextInt(100), Random.nextDouble(2.0).toFloat())
        chain.viewport = p.getRatingImage(chainInt)
        chains1.isVisible = chainInt > 0
        chains2.isVisible = chainInt > 1
        chains3.isVisible = chainInt > 2
        chains4.isVisible = chainInt > 3
        chains5.isVisible = chainInt > 4
        chains6.isVisible = chainInt > 5
        chains7.isVisible = chainInt > 6
        chains8.isVisible = chainInt > 7
        chains1.fitWidth = 44.0 + ((8+chainInt) * chainInt)
        chains1.fitHeight = 44.0 + ((8+chainInt) * chainInt)
        wholeThing.isVisible = true
    }

    private fun setChangeTextColor(changeInt: Int) {
        if (changeInt > 0) change.textFill = LinearGradient(0.0, -20.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(0.2, 1.0, 0.6)), Stop(0.48, c(0.2, 1.0, 0.6)), Stop(0.58, c(0.0, 0.8, 0.4)), Stop(1.0, c(0.0, 0.8, 0.4))) //c("#17e07f")
        else if (changeInt < 0) change.textFill = LinearGradient(0.0, -20.0, 0.0, 10.0, false, CycleMethod.NO_CYCLE, Stop(0.0, c(1.0, 0.4, 0.5)), Stop(0.48, c(1.0, 0.4, 0.5)), Stop(0.58, c(0.9, 0.1, 0.0)), Stop(1.0, c(0.9, 0.1, 0.0))) // c("#fd2832")
        else change.textFill = c("#e0af1a")
    }

}