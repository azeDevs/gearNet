package application.stream

import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import memscan.GearNetShifter.Shift.GEAR_MATCH
import models.Player
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.*
import utils.getRes

class InMatchView(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private val container: StackPane
    private lateinit var stunGaugeR: StunGaugeView
    private lateinit var bountyR: Label
    private lateinit var healthR: Label
    private lateinit var statusR: ImageView
    private lateinit var spiritR: ImageView
    private lateinit var ratingR: ImageView

    private lateinit var stunGaugeB: StunGaugeView
    private lateinit var bountyB: Label
    private lateinit var healthB: Label
    private lateinit var statusB: ImageView
    private lateinit var spiritB: ImageView
    private lateinit var ratingB: ImageView

    init {
        with(root) {
            container = stackpane { // MatchView CONTAINER

                stunGaugeR = StunGaugeView(parent, 0) // STUN GAUGE RED
                stunGaugeB = StunGaugeView(parent, 1) // STUN GAUGE BLUE

                imageview(getRes("atlas.png").toString()) { // BACKING RED
                    viewport = Rectangle2D(1344.0, 196.0, 704.0, 128.0)
                    fitWidth = 704.0
                    fitHeight = 128.0
                    translateX -= 620
                    translateY -= 410
                    scaleX *= 0.50
                    scaleY *= 0.50
                }
                imageview(getRes("atlas.png").toString()) { // BACKING BLUE
                    viewport = Rectangle2D(1344.0, 196.0, 704.0, 128.0)
                    fitWidth = 704.0
                    fitHeight = 128.0
                    translateX += 620
                    translateY -= 410
                    scaleX *= -0.50
                    scaleY *= 0.50
                }

                bountyR = label("FREE") { // BOUNTY RED
                    alignment = Pos.CENTER_LEFT
                    addClass(InMatchStyle.matchBountyText)
                    translateX -= 636
                    translateY -= 417
                }
                bountyB = label("FREE") { // BOUNTY BLUE
                    alignment = Pos.CENTER_RIGHT
                    addClass(InMatchStyle.matchBountyText)
                    translateX += 636
                    translateY -= 417
                }

                healthR = label("") { // HEALTH RED
                    isVisible = false
                    alignment = Pos.CENTER_LEFT
                    addClass(InMatchStyle.matchHealthText)
                    translateX -= 524
                    translateY -= 362
                    scaleX = 0.7
                }
                healthB = label("") { // HEALTH BLUE
                    isVisible = false
                    alignment = Pos.CENTER_RIGHT
                    addClass(InMatchStyle.matchHealthText)
                    translateX += 518
                    translateY -= 362
                    scaleX = 0.7
                }

                statusR = imageview(getRes("atlas.png").toString()) { // BOSS STATUS RED
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 64.0)
                    translateX -= 548
                    translateY -= 413
                    fitWidth = 64.0
                    fitHeight = 32.0
                }
                statusB = imageview(getRes("atlas.png").toString()) { // BOSS STATUS BLUE
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 64.0)
                    translateX += 548
                    translateY -= 413
                    fitWidth = 64.0
                    fitHeight = 32.0
                }

                spiritR = imageview(getRes("cb_spirit_red.gif").toString()) { // SPIRIT RED
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX -= 548
                    translateY -= 413
                    fitWidth = 77.0
                    fitHeight = 77.0
                    opacity = 0.96
                    blendMode = BlendMode.ADD
                }
                spiritB = imageview(getRes("cb_spirit_blue.gif").toString()) { // SPIRIT BLUE
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 548
                    translateY -= 413
                    fitWidth = 77.0
                    fitHeight = 77.0
                    opacity = 0.96
                    blendMode = BlendMode.ADD
                }

                ratingR = imageview(getRes("atlas.png").toString()) { // RISK RATING RED
                    viewport = Rectangle2D(832.0, 704.0, 256.0, 64.0)
                    translateX -= 548
                    translateY -= 313
                    fitWidth = 256.0
                    fitHeight = 64.0
                }
                ratingB = imageview(getRes("atlas.png").toString()) { // RISK RATING BLUE
                    viewport = Rectangle2D(1088.0, 704.0, 256.0, 64.0)
                    translateX += 548
                    translateY -= 313
                    fitWidth = 256.0
                    fitHeight = 64.0
                }

            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

    override fun applyData() = Platform.runLater {
        val p1 = a.getPlayers().firstOrNull { it.getPlaySide() == PLAYER_1 } ?: Player()
        val p2 = a.getPlayers().firstOrNull { it.getPlaySide() == PLAYER_2 } ?: Player()
        if (p1.getPlayerId() > 0L) {
            bountyR.text = p1.getScoreTotalString()
            if (a.isShift(GEAR_MATCH)) {
                if (a.getPlayersStaged().p1.getHealth() > 0) healthR.text = a.getPlayersStaged().p1.getHealth().toString()
                else healthR.text = ""
                stunGaugeR.setVisibility(true)
                stunGaugeR.applyData() //stunGaugeR.applyData(s.getClientMatch().getData())
            } else stunGaugeR.setVisibility(false)
            statusR.viewport = Rectangle2D(p1.getStatusImage().minX, p1.getStatusImage().minY, p1.getStatusImage().width, p1.getStatusImage().height)
            statusR.isVisible = true
            ratingR.viewport = p1.getRatingImage(PLAYER_1)
            ratingR.isVisible = p1.getRating() > 0
            spiritR.isVisible = p1.getRating() > 0
        } else {
            bountyR.text = "FREE"
            stunGaugeR.setVisibility(false)
            statusR.isVisible = false
            ratingR.isVisible = false
            spiritR.isVisible = false
        }
        if (p2.getPlayerId() > 0L) {
            bountyB.text = p2.getScoreTotalString()
            if (a.isShift(GEAR_MATCH)) {
                if (a.getPlayersStaged().p2.getHealth() > 0) healthR.text = a.getPlayersStaged().p2.getHealth().toString()
                else healthB.text = ""
                stunGaugeB.setVisibility(true)
                stunGaugeB.applyData() //stunGaugeB.applyData(s.getClientMatch().getData())
            } else stunGaugeB.setVisibility(false)
            statusB.viewport = Rectangle2D(p2.getStatusImage().minX, p2.getStatusImage().minY, p2.getStatusImage().width, p2.getStatusImage().height)
            statusB.isVisible = true
            ratingB.viewport = p2.getRatingImage(PLAYER_2)
            ratingB.isVisible = p2.getRating() > 0
            spiritB.isVisible = p2.getRating() > 0
        } else {
            bountyB.text = "FREE"
            stunGaugeB.setVisibility(false)
            statusB.isVisible = false
            ratingB.isVisible = false
            spiritB.isVisible = false
        }
    }

}