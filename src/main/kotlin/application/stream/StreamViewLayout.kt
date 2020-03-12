package application.stream

import application.ApplicationStyle
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import session.Player
import session.Session
import session.Session.Companion.LOADING_MODE
import session.Session.Companion.LOBBY_MODE
import session.Session.Companion.MATCH_MODE
import session.Session.Companion.SLASH_MODE
import session.Session.Companion.VICTORY_MODE
import tornadofx.*
import utils.getRes

class StreamViewLayout(override val root: Parent) : Fragment() {

    var showHud = true
    var lockHud = -1

    var streamView: StackPane
    private val bountiesGui: MutableList<BigScoreView> = ArrayList()
    private lateinit var lobbyView: StackPane
    private lateinit var matchView: StackPane
    private lateinit var slashView: StackPane
    private lateinit var loadingView: StackPane

    private lateinit var bountyR: Label
    private lateinit var healthR: Label
    private lateinit var statusR: ImageView
    private lateinit var spiritR: ImageView
    private lateinit var ratingR: ImageView
    private lateinit var round1R: ImageView
    private lateinit var round2R: ImageView

    private lateinit var bountyB: Label
    private lateinit var healthB: Label
    private lateinit var statusB: ImageView
    private lateinit var spiritB: ImageView
    private lateinit var ratingB: ImageView
    private lateinit var round1B: ImageView
    private lateinit var round2B: ImageView

    fun animateTargets() {
        bountiesGui.forEach { it.approachTarget() }
    }

    fun updateStreamLeaderboard(allPlayers: List<Player>, s: Session) {
        val players = allPlayers
        if (s.sessionMode == lockHud) {
            lobbyView.isVisible = showHud
            matchView.isVisible = !showHud
        }
        else {
            lockHud = -1
            when (s.sessionMode) {

                VICTORY_MODE -> {
//                    bountiesGui.forEach { it.setTarget(0.0) }
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                    slashView.isVisible = false
                    loadingView.isVisible = false
                    showHud = true
                }
                LOBBY_MODE -> {
//                    bountiesGui.forEach { it.setTarget(0.0) }
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                    slashView.isVisible = false
                    loadingView.isVisible = false
                    showHud = true
                }
                LOADING_MODE -> {
//                    bountiesGui.forEach { it.setTarget(-256.0) }
                    lobbyView.isVisible = false
                    matchView.isVisible = false
                    slashView.isVisible = false
                    loadingView.isVisible = true
                    showHud = true
                }
                MATCH_MODE -> {
//                    bountiesGui.forEach { it.setTarget(2048.0) }
                    lobbyView.isVisible = false
                    matchView.isVisible = true
                    slashView.isVisible = false
                    loadingView.isVisible = false
                    showHud = false
                }
                SLASH_MODE -> {
//                    bountiesGui.forEach { it.setTarget(-256.0) }
                    lobbyView.isVisible = false
                    matchView.isVisible = false
                    slashView.isVisible = true
                    loadingView.isVisible = false
                    showHud = false
                }

            }
        }

        val p1 = players.firstOrNull { it.getPlaySide().toInt() == 0 } ?: Player()
        val p2 = players.firstOrNull { it.getPlaySide().toInt() == 1 } ?: Player()
        applyData(p1, p2, s)
        for (i in 0..3) {
            if (players.size > i) {
                bountiesGui[i].applyData(players[i], s)
                bountiesGui[i].setVisibility(showHud)
            }
        }
    }

    fun applyData(p1: Player, p2: Player, s: Session) = Platform.runLater {
        if (p1.getSteamId() > 0L) {
            bountyR.text = p1.getBountyString()
            if (s.sessionMode.equals(MATCH_MODE) && s.matchHandler.clientMatch.getHealth(0) > 0) healthR.text = s.matchHandler.clientMatch.getHealth(0).toString()
            else healthR.text = ""
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
            statusR.isVisible = false
            ratingR.isVisible = false
            spiritR.isVisible = false
            round1R.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            round2R.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
        }
        if (p2.getSteamId() > 0L) {
            bountyB.text = p2.getBountyString()
            if (s.sessionMode.equals(MATCH_MODE) && s.matchHandler.clientMatch.getHealth(1) > 0) healthB.text = s.matchHandler.clientMatch.getHealth(1).toString()
            else healthB.text = ""
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

    fun toggleScoreboardMode(session: Session) {
        lockHud = session.sessionMode
        showHud = !showHud
        updateStreamLeaderboard(session.getPlayersList(), session)
    }

    fun toggleStreamerMode(session: Session) {
        if (streamView.opacity.equals(0.64)) streamView.opacity = 1.0
        else if (streamView.opacity.equals(1.0)) streamView.opacity = 0.64
        updateStreamLeaderboard(session.getPlayersList(), session)
    }

    init {
        with(root) {
            streamView = stackpane {
                translateY -= 10
                addClass(ApplicationStyle.streamContainer)

                lobbyView = stackpane {
                    maxWidth = 1920.0
                    minWidth = 1920.0
                    maxHeight = 1080.0
                    minHeight = 1080.0
                    imageview(getRes("barc_lobby.png").toString()) {
                        viewport = Rectangle2D(0.0, 0.0, 1920.0, 1080.0)
                        fitWidth = 1920.0
                        fitHeight = 1080.0
                    }
                    for (i in 0..3) {
                        bountiesGui.add(BigScoreView(parent, i))
                    }
                }

                matchView = stackpane {
                    maxWidth = 1920.0
                    minWidth = 1920.0
                    maxHeight = 1080.0
                    minHeight = 1080.0
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

                slashView = stackpane {
                    imageview(getRes("barc_slash.png").toString()) {
                        viewport = Rectangle2D(0.0, 0.0, 1920.0, 1080.0)
                        fitWidth = 1920.0
                        fitHeight = 1080.0
                    }
                }

                loadingView = stackpane {
                    imageview(getRes("barc_loading.png").toString()) {
                        viewport = Rectangle2D(0.0, 0.0, 1920.0, 1080.0)
                        fitWidth = 1920.0
                        fitHeight = 1080.0
                    }
                }

            }
        }
    }
}