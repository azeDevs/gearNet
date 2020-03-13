package application.stream

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import models.Fighter
import session.Session
import tornadofx.*
import utils.getRes

class MatchView(override val root: Parent) : Fragment() {

    private val container: StackPane
    private lateinit var stunGaugeR: StunGaugeView
    private lateinit var bountyR: Label
    private lateinit var healthR: Label
    private lateinit var statusR: ImageView
    private lateinit var spiritR: ImageView
    private lateinit var ratingR: ImageView
    private lateinit var round1R: ImageView
    private lateinit var round2R: ImageView

    private lateinit var stunGaugeB: StunGaugeView
    private lateinit var bountyB: Label
    private lateinit var healthB: Label
    private lateinit var statusB: ImageView
    private lateinit var spiritB: ImageView
    private lateinit var ratingB: ImageView
    private lateinit var round1B: ImageView
    private lateinit var round2B: ImageView

    init {
        with(root) {
            container = stackpane {
                maxWidth = 1920.0
                minWidth = 1920.0
                maxHeight = 1080.0
                minHeight = 1080.0

                stunGaugeR = StunGaugeView(parent, 0)
                stunGaugeB = StunGaugeView(parent, 1)

                imageview(getRes("barc_match.png").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 1920.0, 1080.0)
                    fitWidth = 1920.0
                    fitHeight = 1080.0
                }

                bountyR = label("FREE") { alignment = Pos.CENTER_LEFT
                    addClass(InMatchStyle.matchBountyText)
                    translateX -= 460
                    translateY -= 316
                }
                healthR = label("") { alignment = Pos.CENTER_LEFT
                    addClass(InMatchStyle.matchHealthText)
                    translateX -= 524
                    translateY -= 362
                    scaleX = 0.7
                }
                statusR = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 64.0)
                    translateX -= 346
                    translateY -= 312
                    fitWidth = 64.0
                    fitHeight = 32.0
                }
                spiritR = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX -= 265
                    translateY -= 316
                    fitWidth = 77.0
                    fitHeight = 77.0
                    opacity = 0.96
                    blendMode = BlendMode.ADD
                }
                ratingR = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(128.0, 448.0, 64.0, 64.0)
                    translateX -= 268
                    translateY -= 312
                    fitWidth = 52.0
                    fitHeight = 52.0
                    opacity = 0.96
                    blendMode = BlendMode.HARD_LIGHT
                }
                round1R = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(320.0, 192.0, 64.0, 64.0)
                    translateX -= 112
                    translateY -= 300
                    fitWidth = 48.0
                    fitHeight = 48.0
                }
                round2R = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(320.0, 192.0, 64.0, 64.0)
                    translateX -= 148
                    translateY -= 300
                    fitWidth = 48.0
                    fitHeight = 48.0
                }

                bountyB = label("FREE") { alignment = Pos.CENTER_RIGHT
                    addClass(InMatchStyle.matchBountyText)
                    translateX += 460
                    translateY -= 316
                }
                healthB = label("") { alignment = Pos.CENTER_RIGHT
                    addClass(InMatchStyle.matchHealthText)
                    translateX += 518
                    translateY -= 362
                    scaleX = 0.7
                }
                statusB = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 64.0)
                    translateX += 346
                    translateY -= 312
                    fitWidth = 64.0
                    fitHeight = 32.0
                }
                spiritB = imageview(getRes("cb_chain_red.gif").toString()) {
                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                    translateX += 265
                    translateY -= 316
                    fitWidth = 77.0
                    fitHeight = 77.0
                    opacity = 0.96
                    blendMode = BlendMode.ADD
                }
                ratingB = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(128.0, 448.0, 64.0, 64.0)
                    translateX += 268
                    translateY -= 312
                    fitWidth = 52.0
                    fitHeight = 52.0
                    opacity = 0.96
                    blendMode = BlendMode.HARD_LIGHT
                }
                round1B = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(320.0, 192.0, 64.0, 64.0)
                    translateX += 109
                    translateY -= 300
                    fitWidth = 48.0
                    fitHeight = 48.0
                }
                round2B = imageview(getRes("gn_stream.png").toString()) {
                    viewport = Rectangle2D(320.0, 192.0, 64.0, 64.0)
                    translateX += 145
                    translateY -= 300
                    fitWidth = 48.0
                    fitHeight = 48.0
                }

            }
        }
    }

    fun setVisibility(flag: Boolean) = Platform.runLater { container.isVisible = flag }

    fun applyData(fighters: List<Fighter>, s: Session) = Platform.runLater {
        val p1 = fighters.firstOrNull { it.getPlaySide().toInt() == 0 } ?: Fighter()
        val p2 = fighters.firstOrNull { it.getPlaySide().toInt() == 1 } ?: Fighter()
        if (p1.getId() > 0L) {
            bountyR.text = p1.getScoreTotalString()
            if (s.sessionMode == Session.MATCH_MODE) {
                if (s.matchHandler.clientMatch.getHealth(0) > 0) healthR.text = s.matchHandler.clientMatch.getHealth(0).toString()
                else healthR.text = ""
                stunGaugeR.setVisibility(true)
                stunGaugeR.applyData(s.matchHandler.clientMatch.getData())
            }
            statusR.viewport = Rectangle2D(p1.getStatusImage().minX, p1.getStatusImage().minY, p1.getStatusImage().width, p1.getStatusImage().height)
            statusR.isVisible = true
            ratingR.viewport = p1.getRatingImage()
            ratingR.isVisible = p1.getRating() > 0
            spiritR.isVisible = p1.getRating() > 0
            if (s.matchHandler.clientMatch.getRounds(0) > 0) round1R.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
            else round1R.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            if (s.matchHandler.clientMatch.getRounds(0) > 1) round2R.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
            else round2R.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
        } else {
            bountyR.text = "FREE"
            stunGaugeR.setVisibility(false)
            statusR.isVisible = false
            ratingR.isVisible = false
            spiritR.isVisible = false
            round1R.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            round2R.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
        }
        if (p2.getId() > 0L) {
            bountyB.text = p2.getScoreTotalString()
            if (s.sessionMode == Session.MATCH_MODE) {
                if (s.matchHandler.clientMatch.getHealth(1) > 0) healthB.text = s.matchHandler.clientMatch.getHealth(1).toString()
                else healthB.text = ""
                stunGaugeB.setVisibility(true)
                stunGaugeB.applyData(s.matchHandler.clientMatch.getData())
            }
            statusB.viewport = Rectangle2D(p2.getStatusImage().minX, p2.getStatusImage().minY, p2.getStatusImage().width, p2.getStatusImage().height)
            statusB.isVisible = true
            ratingB.viewport = p2.getRatingImage()
            ratingB.isVisible = p2.getRating() > 0
            spiritB.isVisible = p2.getRating() > 0

            if (s.matchHandler.clientMatch.getRounds(1) > 0) round1B.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
            else round1B.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            if (s.matchHandler.clientMatch.getRounds(1) > 1) round2B.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
            else round2B.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
        } else {
            bountyB.text = "FREE"
            statusB.isVisible = false
            ratingB.isVisible = false
            spiritB.isVisible = false
            round1B.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            round2B.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
        }
    }

}