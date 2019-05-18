package application.player

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import session.Character.getCharacterPortrait
import session.Player
import tornadofx.*
import utils.addCommas
import utils.generateRandomName
import utils.getRes
import kotlin.random.Random

class BountyView(override val root: Parent) : Fragment() {

    private var wholeThing: StackPane
    private lateinit var character: ImageView
    private lateinit var chains: ImageView
    private lateinit var handle: Label
    private lateinit var riskRating: ImageView

    private lateinit var chain: ImageView
    private lateinit var bounty1: Label
    private lateinit var bounty2: Label

    private lateinit var change: Label


    init {
        with(root) {
            wholeThing = stackpane { isVisible = false
                addClass(BountyStyle.bountyContainer)
                translateX += 420
                minWidth = 1024.0
                maxWidth = 1024.0
                character = imageview(getRes("gn_atlas.png").toString()) {
                    viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                    translateX -= 260
                    translateY += 18
                }
                imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(192.0, 0.0, 832.0, 192.0)
                    fitWidth = 640.0
                    fitHeight = 148.0
                }
                chains = imageview(getRes("cb_chain.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 254
                    translateY += 10
                    fitWidth = 120.0
                    fitHeight = 120.0
                    opacity = 0.95
                    rotate += 16
                    blendMode = BlendMode.ADD
                }
                chain = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
                    translateX += 252
                    translateY += 16
                    fitWidth = 52.0
                    fitHeight = 52.0
                    opacity = 0.95
                    blendMode = BlendMode.HARD_LIGHT
                }
                handle = label {
                    addClass(BountyStyle.handleText)
                    translateX -= 65
                    translateY -= 36
                    opacity = 0.8
                    blendMode = BlendMode.HARD_LIGHT
                }


                stackpane {
                    translateX -= 110.0
                    translateY += 12.0
                    bounty2 = label("") {
                        addClass(BountyStyle.bountyShadow)
                        scaleX += 0.05
                        scaleY += 0.25
                        rotate -= 0.5
                        blendMode = BlendMode.ADD
                        text
                    }
                    bounty1 = label("") {
                        addClass(BountyStyle.bountyText)
                        translateY += 1.0
                        rotate += 0.5
                        blendMode = BlendMode.ADD
                    }
                }

                riskRating = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(0.0, 256.0, 128.0, 64.0)
                    translateX += 134
                    translateY += 16
                    fitWidth = 80.0
                    fitHeight = 40.0
//                    blendMode = BlendMode.HARD_LIGHT
                }

                change = label("") {
                    translateX += 252
                    translateY -= 40
                    rotate += 1
                    addClass(BountyStyle.changeText)
                    blendMode = BlendMode.HARD_LIGHT
                }

            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater {
        wholeThing.isVisible = flag
    }

    fun applyData(p: Player) = Platform.runLater {
        if (false) applyRandomData(p) else
            if (p.getSteamId() > 0L) {
                character.viewport = getCharacterPortrait(p.getData().characterId, p.isIdle())
                handle.text = p.getNameString(); handle.isVisible = true
                riskRating.viewport = p.getRatingImage()
                chain.viewport = p.getChainImage()
                bounty1.text = p.getBountyString()
                bounty2.text = p.getBountyString()
                if (p.getChange() > 0) change.textFill = c("#17e07f") else change.textFill = c("#fd2832")
                change.text = p.getChangeString()
                if (p.getChain() < 1) chains.isVisible = false
                else chains.isVisible = true
                wholeThing.isVisible = true
            } else {
                character.viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                handle.text = ""; handle.isVisible = false
                riskRating.isVisible = false
                chain.isVisible = false
                bounty1.text = ""
                bounty2.text = ""
                change.text = ""
                chains.isVisible = false
                wholeThing.isVisible = false
            }
    }

    private fun applyRandomData(p: Player) {
        val chainInt = Random.nextInt(9)
        val bountyStr = addCommas(Random.nextInt(1222333).toString())
        val changeInt = Random.nextInt(-444555, 666777)
        character.viewport = Rectangle2D(Random.nextInt(8) * 64.0, Random.nextInt(4) * 64.0, 64.0, 64.0)
        handle.text = generateRandomName()
        bounty1.text = "$bountyStr W$"
        bounty2.text = "$bountyStr W$"
        if (changeInt > 0) change.textFill = c("#17e07f") else change.textFill = c("#fd2832")
        change.text = p.getChangeString(1f, changeInt)
        riskRating.viewport = p.getRatingImage(Random.nextInt(100), Random.nextDouble(2.0).toFloat())
        chain.viewport = p.getChainImage(chainInt)
        if (chainInt < 1) chains.isVisible = false
        else chains.isVisible = true
        wholeThing.isVisible = true
    }

}