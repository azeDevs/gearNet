package application.stream

import application.debug.ArcadeView
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import models.Player
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import session.Session
import session.Session.Companion.MATCH_MODE
import tornadofx.*
import utils.getRes

class InMatchView(override val root: Parent) : Fragment(), ArcadeView {

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

    override fun applyData(s: Session) = Platform.runLater {
        val f1 = s.getPlayersList().firstOrNull { it.getPlaySide() == PLAYER_1 } ?: Player()
        val f2 = s.getPlayersList().firstOrNull { it.getPlaySide() == PLAYER_2 } ?: Player()
        if (f1.getPlayerId() > 0L) {
            bountyR.text = f1.getScoreTotalString()
            if (s.isMode(MATCH_MODE)) {
                if (s.getClientMatch().getHealth(0) > 0) healthR.text = s.getClientMatch().getHealth(0).toString()
                else healthR.text = ""
                stunGaugeR.setVisibility(true)
                stunGaugeR.applyData(s.getClientMatch().getData())
            } else stunGaugeR.setVisibility(false)
            statusR.viewport = Rectangle2D(f1.getStatusImage().minX, f1.getStatusImage().minY, f1.getStatusImage().width, f1.getStatusImage().height)
            statusR.isVisible = true
            ratingR.viewport = f1.getRatingImage(PLAYER_1)
            ratingR.isVisible = f1.getRating() > 0
            spiritR.isVisible = f1.getRating() > 0
        } else {
            bountyR.text = "FREE"
            stunGaugeR.setVisibility(false)
            statusR.isVisible = false
            ratingR.isVisible = false
            spiritR.isVisible = false
        }
        if (f2.getPlayerId() > 0L) {
            bountyB.text = f2.getScoreTotalString()
            if (s.isMode(MATCH_MODE)) {
                if (s.getClientMatch().getHealth(1) > 0) healthB.text = s.getClientMatch().getHealth(1).toString()
                else healthB.text = ""
                stunGaugeB.setVisibility(true)
                stunGaugeB.applyData(s.getClientMatch().getData())
            } else stunGaugeB.setVisibility(false)
            statusB.viewport = Rectangle2D(f2.getStatusImage().minX, f2.getStatusImage().minY, f2.getStatusImage().width, f2.getStatusImage().height)
            statusB.isVisible = true
            ratingB.viewport = f2.getRatingImage(PLAYER_2)
            ratingB.isVisible = f2.getRating() > 0
            spiritB.isVisible = f2.getRating() > 0
        } else {
            bountyB.text = "FREE"
            stunGaugeB.setVisibility(false)
            statusB.isVisible = false
            ratingB.isVisible = false
            spiritB.isVisible = false
        }
    }

}