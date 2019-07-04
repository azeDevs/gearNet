package application.stream

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.effect.BlendMode
import javafx.scene.layout.StackPane
import session.*
import tornadofx.*
import utils.getRes

class StreamView(override val root: Parent) : Fragment() {

    var showHud = true
    var lockHud = -1
    private val bountiesGui: MutableList<BountyView> = ArrayList()
    lateinit var lobbyView: StackPane
    lateinit var matchView: StackPane
    lateinit var bounty0: Label
    lateinit var bounty1: Label
    var streamView: StackPane

    fun updateStreamLeaderboard(allPlayers: List<Player>, s: Session) {
        val players = allPlayers//.filter { it.getChain() > 0 }
        if (s.sessionMode == lockHud) {
            lobbyView.isVisible = showHud
            matchView.isVisible = !showHud
        }
        else {
            lockHud = -1
            when (s.sessionMode) {
                LOBBY_MODE -> {
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                }
                LOADING_MODE -> {
                    lobbyView.isVisible = true
                    matchView.isVisible = false
                }
                MATCH_MODE -> {
                    lobbyView.isVisible = false
                    matchView.isVisible = true
                }
                SLASH_MODE -> {
                    lobbyView.isVisible = false
                    matchView.isVisible = true
                }
                VICTORY_MODE -> {
                    lobbyView.isVisible = true
                    matchView.isVisible = false
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
        if (p1.getSteamId() > 0L) bounty0.text = p1.getBountyString()
        if (p2.getSteamId() > 0L) bounty1.text = p2.getBountyString()
    }

    fun toggleScoreboardMode(session: Session) {
        lockHud = session.sessionMode
        showHud = !showHud
        session.log("C: Scoreboard Toggle = $showHud")
        updateStreamLeaderboard(session.getPlayersList(), session)
    }

    fun toggleStreamerMode(session: Session) {
        streamView.isVisible = !streamView.isVisible
        session.log("C: Streaming Toggle = ${streamView.isVisible}")
        updateStreamLeaderboard(session.getPlayersList(), session)
    }

    init {
        with(root) {
            streamView = stackpane {
                addClass(StreamStyle.streamContainer)
                translateY -= 8
                lobbyView = stackpane {
                    maxWidth = 1280.0
                    minWidth = 1280.0
                    maxHeight = 720.0
                    minHeight = 720.0
                    imageview(getRes("gn_stream.png").toString()) {
                        viewport = Rectangle2D(0.0, 704.0, 1024.0, 320.0)
                        translateY += 380
                        fitWidth = 1280.0
                        fitHeight = 400.0
                    }
                    imageview(getRes("gn_stream.png").toString()) {
                        viewport = Rectangle2D(0.0, 704.0, 1024.0, 320.0)
                        rotate += 180
                        translateY -= 410
                        fitWidth = 1280.0
                        fitHeight = 400.0
                    }
                    vbox { translateY += 88
                        // BOUNTY VIEWS
                        for (i in 0..3) {
                            hbox {
                                bountiesGui.add(BountyView(parent, i))
                            }
                        }
                    }
                }
                matchView = stackpane {
                    maxWidth = 1280.0
                    minWidth = 1280.0
                    maxHeight = 720.0
                    minHeight = 720.0
                    isVisible = false
                    hbox { alignment = Pos.TOP_CENTER
                        hbox { alignment = Pos.TOP_CENTER
                            stackpane { alignment = Pos.TOP_CENTER
                                imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(448.0, 192.0, 576.0, 128.0)
                                    fitWidth = 225.0
                                    fitHeight = 50.0
                                    translateY += 58
                                    translateX -= 300
                                }
                                bounty0 = label("FREE") { alignment = Pos.CENTER_LEFT
                                    addClass(BountyStyle.bountyMatchText)
                                    translateY += 66.0
                                    translateX -= 300.0
                                    blendMode = BlendMode.ADD
                                }
                            }
                        }
                        hbox { alignment = Pos.TOP_CENTER
                            stackpane { alignment = Pos.TOP_CENTER
                                imageview(getRes("gn_stream.png").toString()) {
                                    viewport = Rectangle2D(448.0, 192.0, 576.0, 128.0)
                                    fitWidth = 225.0
                                    fitHeight = 50.0
                                    translateY += 58
                                    translateX += 300
                                    rotate += 180.0
                                }
                                bounty1 = label("FREE") { alignment = Pos.CENTER_RIGHT
                                    addClass(BountyStyle.bountyMatchText)
                                    translateY += 66.0
                                    translateX += 300.0
                                    blendMode = BlendMode.ADD

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}