package application.stream

import application.ApplicationStyle
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
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
    private lateinit var lobbyView: Group
    private lateinit var statsView: Group
    private lateinit var matchView: HBox

    private lateinit var bounty0: Label
    private lateinit var health0: Label
    private lateinit var rating0: ImageView
    private lateinit var spirit0: ImageView
    private lateinit var chains0: ImageView
    private lateinit var round10: ImageView
    private lateinit var round20: ImageView

    private lateinit var bounty1: Label
    private lateinit var health1: Label
    private lateinit var rating1: ImageView
    private lateinit var spirit1: ImageView
    private lateinit var chains1: ImageView
    private lateinit var round11: ImageView
    private lateinit var round21: ImageView

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
                    showHud = true
                }
                LOBBY_MODE -> {
//                    bountiesGui.forEach { it.setTarget(0.0) }
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                    showHud = true
                }
                LOADING_MODE -> {
//                    bountiesGui.forEach { it.setTarget(-256.0) }
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                    showHud = true
                }
                MATCH_MODE -> {
//                    bountiesGui.forEach { it.setTarget(2048.0) }
                    lobbyView.isVisible = false
                    matchView.isVisible = true
                    showHud = false
                }
                SLASH_MODE -> {
//                    bountiesGui.forEach { it.setTarget(-256.0) }
                    lobbyView.isVisible = false
                    matchView.isVisible = true
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
            bounty0.text = p1.getBountyString()
            if (s.sessionMode.equals(MATCH_MODE) && s.matchHandler.clientMatch.getHealth(0) > 0) health0.text = s.matchHandler.clientMatch.getHealth(0).toString()
            else health0.text = ""
            rating0.viewport = Rectangle2D(p1.getRatingImage().minX, p1.getRatingImage().minY + 20, p1.getRatingImage().width, p1.getRatingImage().height - 20)
            rating0.isVisible = true
            chains0.viewport = p1.getChainImage()
            chains0.isVisible = p1.getChain() > 0
            spirit0.isVisible = p1.getChain() > 0
            if (s.matchHandler.clientMatch.getRounds(0) > 0) round10.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
            else round10.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            if (s.matchHandler.clientMatch.getRounds(0) > 1) round20.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
            else round20.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
        } else {
            bounty0.text = "FREE"
            rating0.isVisible = false
            chains0.isVisible = false
            spirit0.isVisible = false
            round10.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            round20.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
        }
        if (p2.getSteamId() > 0L) {
            bounty1.text = p2.getBountyString()
            if (s.sessionMode.equals(MATCH_MODE) && s.matchHandler.clientMatch.getHealth(1) > 0) health1.text = s.matchHandler.clientMatch.getHealth(1).toString()
            else health1.text = ""
            rating1.viewport = Rectangle2D(p2.getRatingImage().minX, p2.getRatingImage().minY + 20, p2.getRatingImage().width, p2.getRatingImage().height - 20)
            rating1.isVisible = true
            chains1.viewport = p2.getChainImage()
            chains1.isVisible = p2.getChain() > 0
            spirit1.isVisible = p2.getChain() > 0

            if (s.matchHandler.clientMatch.getRounds(1) > 0) round11.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
            else round11.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            if (s.matchHandler.clientMatch.getRounds(1) > 1) round21.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
            else round21.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
        } else {
            bounty1.text = "FREE"
            rating1.isVisible = false
            chains1.isVisible = false
            spirit1.isVisible = false
            round11.viewport = Rectangle2D(128.0, 512.0, 64.0, 64.0)
            round21.viewport = Rectangle2D(128.0, 576.0, 64.0, 64.0)
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
                addClass(ApplicationStyle.streamContainer)
                translateY -= 8
                lobbyView = group {
                    maxWidth = 1280.0
                    minWidth = 1280.0
                    maxHeight = 720.0
                    minHeight = 720.0
                    imageview(getRes("gn_stream.png").toString()) {
                        viewport = Rectangle2D(0.0, 832.0, 1024.0, 192.0)
                        translateY += 256
                        fitWidth = 1280.0
                        fitHeight = 240.0
                    }
                    imageview(getRes("gn_stream.png").toString()) {
                        viewport = Rectangle2D(0.0, 832.0, 1024.0, 192.0)
                        rotate += 180
                        translateY -= 256
                        fitWidth = 1280.0
                        fitHeight = 240.0
                    }
                    for (i in 0..3) {
                        bountiesGui.add(BigScoreView(parent, i))
                    }
                }

                statsView = group {
                    opacity = 0.0
                    maxWidth = 1280.0
                    minWidth = 1280.0
                    maxHeight = 720.0
                    minHeight = 720.0
                    imageview(getRes("gn_stream.png").toString()) {
                        viewport = Rectangle2D(0.0, 832.0, 1024.0, 192.0)
                        translateY += 380
                        fitWidth = 1280.0
                        fitHeight = 240.0
                    }
                    imageview(getRes("gn_stream.png").toString()) {
                        viewport = Rectangle2D(0.0, 832.0, 1024.0, 192.0)
                        rotate += 180
                        translateY -= 410
                        fitWidth = 1280.0
                        fitHeight = 240.0
                    }
                }

                matchView = hbox { alignment = Pos.TOP_CENTER
                        isVisible = false

                            stackpane { alignment = Pos.TOP_LEFT
                                imageview(getRes("gn_stream.png").toString()) { alignment = Pos.TOP_LEFT
                                    viewport = Rectangle2D(384.0, 256.0, 832.0, 128.0)
                                    fitWidth = 410.0
                                    fitHeight = 60.0
                                    translateX -= 94
                                    translateY += 61
                                }
                                bounty0 = label("FREE") { alignment = Pos.CENTER_LEFT
                                    addClass(InMatchStyle.matchBountyText)
                                    translateX -= 50
                                    translateY += 71.5
//                                    blendMode = BlendMode.ADD
                                }
                                health0 = label("") { alignment = Pos.CENTER_LEFT
                                    addClass(InMatchStyle.matchHealthText)
                                    translateX -= 102
                                    translateY += 40.5
                                    scaleX = 0.7
                                }
                                rating0 = imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(0.0, 256.0 + 20, 128.0, 64.0 - 20)
                                    translateX += 101
                                    translateY += 83
                                    fitWidth = 50.0
                                    fitHeight = 30.0 - 10
                                }
                                spirit0 = imageview(getRes("cb_chain_red.gif").toString()) {
                                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                                    translateX += 166
                                    translateY += 63
                                    fitWidth = 50.0
                                    fitHeight = 50.0
                                    opacity = 0.96
                                    blendMode = BlendMode.ADD
                                }
                                chains0 = imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(128.0, 448.0, 64.0, 64.0)
                                    translateX += 172.5
                                    translateY += 73
                                    fitWidth = 36.0
                                    fitHeight = 36.0
                                    opacity = 0.96
                                    blendMode = BlendMode.HARD_LIGHT
                                }
                                round10 = imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(320.0, 192.0, 64.0, 64.0)
                                    translateX += 212
                                    translateY += 86
                                    fitWidth = 48.0
                                    fitHeight = 48.0
                                }
                                round20 = imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(320.0, 192.0, 64.0, 64.0)
                                    translateX += 248
                                    translateY += 86
                                    fitWidth = 48.0
                                    fitHeight = 48.0
                                }
                            }

                            stackpane { alignment = Pos.TOP_RIGHT
                                imageview(getRes("gn_stream.png").toString()) { alignment = Pos.TOP_RIGHT
                                    viewport = Rectangle2D(384.0, 384.0, 832.0, 128.0)
                                    fitWidth = 410.0
                                    fitHeight = 60.0
                                    translateX += 192
                                    translateY += 61
                                }
                                bounty1 = label("FREE") { alignment = Pos.CENTER_RIGHT
                                    addClass(InMatchStyle.matchBountyText)
                                    translateX += 52
                                    translateY += 71.5
//                                    blendMode = BlendMode.ADD
                                }
                                health1 = label("") { alignment = Pos.CENTER_RIGHT
                                    addClass(InMatchStyle.matchHealthText)
                                    translateX += 101
                                    translateY += 40.5
                                    scaleX = 0.7
                                }
                                rating1 = imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(0.0, 256.0 + 20, 128.0, 64.0 - 20)
                                    translateX -= 95
                                    translateY += 83
                                    fitWidth = 50.0
                                    fitHeight = 30.0 - 10
                                }
                                spirit1 = imageview(getRes("cb_chain_red.gif").toString()) {
                                    viewport = Rectangle2D(0.0, 0.0, 128.0, 128.0)
                                    translateX -= 164
                                    translateY += 63
                                    fitWidth = 50.0
                                    fitHeight = 50.0
                                    opacity = 0.96
                                    blendMode = BlendMode.ADD
                                }
                                chains1 = imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(128.0, 448.0, 64.0, 64.0)
                                    translateX -= 170
                                    translateY += 73
                                    fitWidth = 36.0
                                    fitHeight = 36.0
                                    opacity = 0.96
                                    blendMode = BlendMode.HARD_LIGHT
                                }
                                round11 = imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(320.0, 192.0, 64.0, 64.0)
                                    translateX -= 209
                                    translateY += 86
                                    fitWidth = 48.0
                                    fitHeight = 48.0
                                }
                                round21 = imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(320.0, 192.0, 64.0, 64.0)
                                    translateX -= 245
                                    translateY += 86
                                    fitWidth = 48.0
                                    fitHeight = 48.0
                                }
                            }
                }
            }
        }
    }
}