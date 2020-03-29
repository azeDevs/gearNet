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
import models.Player
import models.Player.Companion.PLAYER_2
import tornadofx.*
import utils.ArcForm.getValueColor
import utils.XrdCharacter.getCharacterTrademark
import utils.getRes

class FighterScoreView(override val root: Parent, private val index:Int) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private var container: StackPane
    private lateinit var character: ImageView
    private lateinit var handle: Label
    private lateinit var status: ImageView

    private lateinit var rating: ImageView
    private lateinit var bounty: Label
    private lateinit var signs: Label
    private lateinit var change: Label


    init {
        with(root) {
            container = stackpane {
                addClass(ScoreStyle.bountyContainer)
                translateX += 400
                translateY -= 280

                scaleX -= (index*0.042)
                scaleY -= (index*0.042)
                translateY += (index*(130-index*4))
                translateX -= (index*9.6)


                minWidth = 1024.0
                maxWidth = 1024.0
                character = imageview(getRes("atlas.png").toString()) {
                    viewport = getCharacterTrademark()
                    translateX -= 230
                    translateY += 20
                    fitWidth = 69.0
                    fitHeight = 69.0
                }

                imageview(getRes("atlas.png").toString()) {
                    viewport = Rectangle2D(1280.0, 0.0, 768.0, 192.0)
                    translateX += 25
                    fitWidth = 640.0
                    fitHeight = 160.0
                }

                rating = imageview(getRes("atlas.png").toString()) {
                    viewport = Player().getRatingImage()
                    translateX += 253
                    translateY -= 40
                    scaleX *= 0.77
                    scaleY *= 0.77
                }

                handle = label {
                        addClass(ScoreStyle.bountyHandleText)
                        translateX -= 64
                        translateY -= 41
                        scaleX *= 0.9
                        opacity = 0.96
                }

                bounty = label {
                    addClass(ScoreStyle.bountyBountyText)
                    translateX -= 48.0
                    translateY += 13
                }

                status = imageview(getRes("atlas.png").toString()) {
                    viewport = Rectangle2D(0.0, 256.0, 128.0, 64.0)
                    translateX += 271
                    translateY += 18
                    fitWidth = 128.0
                    fitHeight = 64.0
                }


                signs = label {
                    addClass(ScoreStyle.signsTurnedText)
                    alignment = Pos.CENTER
                    translateX += 270.0
                    translateY += 18.0
                }

                change = label("9999") {
                    translateX += 160
                    translateY += 32
                    addClass(ScoreStyle.bountyChangeText)
                }

            }
        }
    }

    override fun update() = Platform.runLater {
        val fighters = a.getPlayersList()
        if (fighters.size-1 >= index) {
            applyFighterData(fighters[index])
        } else applyEmptyData()
    }

    override fun animate() {
        TODO("not implemented")
    }

    private fun applyFighterData(p: Player) {
        character.viewport = getCharacterTrademark(p.getPlayerData().characterId)
        handle.text = p.getUserName()
        status.viewport = p.getStatusImage()
        status.isVisible = false //!p.isWatcher()
        rating.viewport = p.getRatingImage(PLAYER_2)
        rating.isVisible = true
        bounty.text = p.getScoreTotalString()
        if (p.getScoreTotal() == 0) bounty.opacity = 0.77
        else bounty.opacity = 1.0
        change.text = p.getScoreDeltaString()
        change.textFill = getValueColor(p.getScoreDelta())
        signs.text = p.getSigns().toString()
        if (p.getSigns() == 0) signs.opacity = 0.77
        else signs.opacity = 1.0
        container.isVisible = true
    }

    private fun applyEmptyData() {
        character.viewport = getCharacterTrademark()
        handle.text = ""
        status.isVisible = false
        rating.isVisible = false
        bounty.text = ""
        change.text = ""
        signs.text = ""
        container.isVisible = false
    }


}