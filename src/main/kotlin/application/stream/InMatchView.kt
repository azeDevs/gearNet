package application.stream

import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
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
    private lateinit var statusR: ImageView
    private lateinit var ratingR: ImageView

    private lateinit var stunGaugeB: StunGaugeView
    private lateinit var bountyB: Label
    private lateinit var statusB: ImageView
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
                    alignment = Pos.CENTER_RIGHT
                    addClass(InMatchStyle.matchBountyText)
                    translateX -= 600
                    translateY -= 419
                }

                bountyB = label("FREE") { // BOUNTY BLUE
                    alignment = Pos.CENTER_LEFT
                    addClass(InMatchStyle.matchBountyText)
                    translateX += 600
                    translateY -= 419
                }

                statusR = imageview(getRes("atlas.png").toString()) { // BOSS STATUS RED
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 64.0)
                    translateX -= 764
                    translateY -= 415
                    fitWidth = 64.0
                    fitHeight = 32.0
                }

                statusB = imageview(getRes("atlas.png").toString()) { // BOSS STATUS BLUE
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 64.0)
                    translateX += 764
                    translateY -= 415
                    fitWidth = 64.0
                    fitHeight = 32.0
                }

                ratingR = imageview(getRes("atlas.png").toString()) { // RISK RATING RED
                    viewport = Rectangle2D(832.0, 704.0, 256.0, 64.0)
                    translateX -= 500
                    translateY -= 320
                    fitWidth = 256.0
                    fitHeight = 64.0
                }

                ratingB = imageview(getRes("atlas.png").toString()) { // RISK RATING BLUE
                    viewport = Rectangle2D(1088.0, 704.0, 256.0, 64.0)
                    translateX += 500
                    translateY -= 320
                    fitWidth = 256.0
                    fitHeight = 64.0
                }

            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

    override fun applyData() = Platform.runLater {
        val p1 = a.getPlayers().firstOrNull { it.getTeamSeat() == PLAYER_1 } ?: Player()
        val p2 = a.getPlayers().firstOrNull { it.getTeamSeat() == PLAYER_2 } ?: Player()

        if (p1.getPlayerId() > 0L) {
            bountyR.text = p1.getScoreTotalString()
            bountyR.isVisible = true
            if (a.isShift(GEAR_MATCH)) {
                stunGaugeR.setVisibility(p1.isOnCabinet())
                stunGaugeR.applyData()
                ratingR.viewport = p1.getRatingImage(PLAYER_1)
                ratingR.isVisible = p1.isOnCabinet()
            } else {
                stunGaugeR.setVisibility(false)
                ratingR.isVisible = false
            }
            statusR.viewport = Rectangle2D(p1.getStatusImage().minX, p1.getStatusImage().minY, p1.getStatusImage().width, p1.getStatusImage().height)
            statusR.isVisible = p1.isOnCabinet()

        } else {
            bountyR.text = ""
            bountyR.isVisible = false
            stunGaugeR.setVisibility(false)
            statusR.isVisible = false
            ratingR.isVisible = false
        }

        if (p2.getPlayerId() > 0L) {
            bountyB.text = p2.getScoreTotalString()
            bountyB.isVisible = true
            if (a.isShift(GEAR_MATCH)) {
                stunGaugeB.setVisibility(p2.isOnCabinet())
                stunGaugeB.applyData()
                ratingB.viewport = p2.getRatingImage(PLAYER_2)
                ratingB.isVisible = p2.isOnCabinet()
            } else {
                stunGaugeB.setVisibility(false)
                ratingB.isVisible = false
            }
            statusB.viewport = Rectangle2D(p2.getStatusImage().minX, p2.getStatusImage().minY, p2.getStatusImage().width, p2.getStatusImage().height)
            statusB.isVisible = p2.isOnCabinet()

        } else {
            bountyB.text = ""
            bountyB.isVisible = false
            stunGaugeB.setVisibility(false)
            statusB.isVisible = false
            ratingB.isVisible = false
        }
    }

}