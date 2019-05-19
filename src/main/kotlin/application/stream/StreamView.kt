package application.stream

import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import session.Match
import session.Player
import tornadofx.*
import utils.getRes

class StreamView(override val root: Parent) : Fragment() {

    var showhud = true
    var forcehud = false
    private val bountiesGui: MutableList<BountyView> = ArrayList()
    lateinit var lobbyView: StackPane

    fun updateStreamLeaderboard(players: List<Player>, match: Match) {
        showhud = true //!(match.isMatchOngoing() || match.isRoundOngoing())
        println("showHud = $showhud | inMatch = ${match.isMatchOngoing()} | inRound = ${match.isRoundOngoing()}")
        lobbyView.isVisible = showhud

        for (i in 0..3) {
            if (players.size > i) bountiesGui[i].applyData(players[i])
            else bountiesGui[i].applyData(Player())

            if (players.size > i /*&& players[i].isScoreboardWorthy()*/) bountiesGui[i].setVisibility(showhud)
            else bountiesGui[i].setVisibility(false)
        }
    }

    init {
        with(root) {
            vbox { addClass(StreamStyle.streamContainer)
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