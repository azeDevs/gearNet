package application.arcadiaViews

import arcadia.ArcadeView
import arcadia.Arcadia
import gearnet.GearNetShifter.Shift.GEAR_MATCH
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
import utils.getRes

class InMatchView(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private val container: StackPane
    private lateinit var backingR: ImageView
    private lateinit var stunGaugeR: StunGaugeView
    private lateinit var bountyR: Label
    private lateinit var statusR: ImageView
    private lateinit var ratingR: ImageView

    private lateinit var backingB: ImageView
    private lateinit var stunGaugeB: StunGaugeView
    private lateinit var bountyB: Label
    private lateinit var statusB: ImageView
    private lateinit var ratingB: ImageView

    init {
        with(root) {
            container = stackpane { // MatchView CONTAINER

                stunGaugeR = StunGaugeView(parent, 0) // STUN GAUGE RED
                stunGaugeB = StunGaugeView(parent, 1) // STUN GAUGE BLUE

                backingR = imageview(getRes("atlas.png").toString()) { // BACKING RED
                    viewport = Rectangle2D(1344.0, 196.0, 704.0, 128.0)
                    fitWidth = 704.0
                    fitHeight = 128.0
                    translateX -= 620
                    translateY -= 410
                    scaleX *= 0.50
                    scaleY *= 0.50
                }

                backingB = imageview(getRes("atlas.png").toString()) { // BACKING BLUE
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

    fun setVisibility(flag: Boolean) = Platform.runLater {
        container.isVisible = flag
//        ratingR.isVisible = flag
//        ratingB.isVisible = flag
    }

    override fun update() = Platform.runLater {
        applyStagedFighterData(a.getPlayer(a.getClientMatch().player1.steamId))
        applyStagedFighterData(a.getPlayer(a.getClientMatch().player2.steamId))
    }

    override fun animate() { }

    private fun applyStagedFighterData(p: Player) {
        var backing = backingR
        var bounty = bountyR
        var stunGauge = stunGaugeR
        var rating = ratingR
        var status = statusR
        if (p.getTeamSeat() == PLAYER_2) {
            backing = backingB
            bounty = bountyB
            stunGauge = stunGaugeB
            rating = ratingB
            status = statusB
        }


        if (p.isValid()) {
            backing.isVisible = true
            bounty.text = p.getScoreTotalString()
            bounty.isVisible = true
            if (a.isShift(GEAR_MATCH)) {
                stunGauge.setVisibility(p.isOnCabinet())
                stunGauge.applyData()
                rating.viewport = p.getRatingImage(p.getTeamSeat())
                rating.isVisible = p.isOnCabinet()
            } else {
                stunGauge.setVisibility(false)
                rating.isVisible = false
            }
            status.viewport = Rectangle2D(
                p.getStatusImage().minX,
                p.getStatusImage().minY,
                p.getStatusImage().width,
                p.getStatusImage().height
            )
            status.isVisible = false //p.isOnCabinet()

        } else {
            backing.isVisible = false
            bounty.text = ""
            bounty.isVisible = false
            stunGauge.setVisibility(false)
            status.isVisible = false
            rating.isVisible = false
        }
    }

}