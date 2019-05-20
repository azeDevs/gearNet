package application.stream

import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import session.Player
import session.Session
import tornadofx.*
import utils.getRes

class StreamView(override val root: Parent) : Fragment() {

    var showHud = true
    var lockHud = false
    private val bountiesGui: MutableList<BountyView> = ArrayList()
    lateinit var lobbyView: StackPane
    var streamView: VBox

    fun updateStreamLeaderboard(players: List<Player>, s: Session) {
        if (lockHud) lobbyView.isVisible = showHud
        else {
            if (s.sessionMode == s.LOBBY_MODE || s.sessionMode == s.LOADING_MODE || s.sessionMode == s.VICTORY_MODE) lobbyView.isVisible = true
            else if (s.sessionMode == s.MATCH_MODE || s.sessionMode == s.SLASH_MODE) lobbyView.isVisible = false
        }

        for (i in 0..3) {
            if (players.size > i) {
                bountiesGui[i].applyData(players[i])
                bountiesGui[i].setVisibility(showHud)
            } else {
                bountiesGui[i].applyData(Player())
                bountiesGui[i].setVisibility(false)
            }
        }
    }

    fun toggleStreamerMode(session:Session) {
        if (lockHud) {
            showHud = !showHud
            session.log("CONSOLE: Scoreboard Toggle = $showHud")
            updateStreamLeaderboard(session.getPlayersList(), session)
        } else {
            streamView.isVisible = !streamView.isVisible
            session.log("CONSOLE: Streaming Toggle = ${streamView.isVisible}")
            updateStreamLeaderboard(session.getPlayersList(), session)
        }
    }

    init {
        with(root) {
            streamView = vbox { addClass(StreamStyle.streamContainer)
                translateY -= 8
                lobbyView = stackpane {
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
                    vbox {
                        // BOUNTY VIEWS
                        for (i in 0..3) {
                            hbox {
                                bountiesGui.add(BountyView(parent, i))
                            }
                        }
                    }
                }

            }
        }
    }
}