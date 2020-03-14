package application.stream

import application.ApplicationStyle
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import models.Fighter
import session.Session
import session.Session.Companion.LOADING_MODE
import session.Session.Companion.LOBBY_MODE
import session.Session.Companion.MATCH_MODE
import session.Session.Companion.SLASH_MODE
import session.Session.Companion.VICTORY_MODE
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class StreamViewLayout(override val root: Parent) : Fragment() {

    private var showHud = true
    var lockHud = -1

    var streamView: StackPane

    private lateinit var lobbyView: LobbyView
    private lateinit var matchView: MatchView
    private lateinit var viewersView: ViewersView
    private lateinit var atensionMeters: AtensionMetersView

    fun updateStreamLeaderboard(fighters: List<Fighter>, s: Session) {
        if (s.sessionMode == lockHud) {
            lobbyView.setVisibility(showHud)
            matchView.setVisibility(!showHud)
        }
        else {
            lockHud = -1
            when (s.sessionMode) {
                VICTORY_MODE -> {
                    lobbyView.setVisibility(true)
                    matchView.setVisibility(false)
                    showHud = true
                }
                LOBBY_MODE -> {
                    lobbyView.setVisibility(true)
                    matchView.setVisibility(false)
                    showHud = true
                }
                LOADING_MODE -> {
                    lobbyView.setVisibility(false)
                    matchView.setVisibility(false)
                    showHud = true
                }
                MATCH_MODE -> {
                    lobbyView.setVisibility(false)
                    matchView.setVisibility(true)
                    showHud = false
                }
                SLASH_MODE -> {
                    lobbyView.setVisibility(false)
                    matchView.setVisibility(false)
                    showHud = false
                }
            }
        }

        matchView.applyData(fighters, s)
        lobbyView.applyData(fighters, s, showHud)
        viewersView.applyData(s)
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
            streamView = stackpane { addClass(ApplicationStyle.streamContainer)
                translateY -= 10

                lobbyView = LobbyView(parent)
                matchView = MatchView(parent)
                viewersView = ViewersView(parent)
//                atensionMeters = AtensionMetersView(parent)

                imageview(getRes("barc_atlas.png").toString()) { // BARC TITLE
                    viewport = Rectangle2D(1536.0, 448.0, 448.0, 128.0)
                    fitWidth = 448.0
                    fitHeight = 128.0
                    translateY -= 488
                }
                imageview(getRes("barc_atlas.png").toString()) { // RED BANNER
                    viewport = Rectangle2D(1536.0, 704.0, 256.0, 320.0)
                    fitWidth = 256.0
                    fitHeight = 320.0
                    translateX -= 890
                    translateY -= 380
                }
                imageview(getRes("barc_atlas.png").toString()) { // BLUE BANNER
                    viewport = Rectangle2D(1792.0, 704.0, 256.0, 320.0)
                    fitWidth = 256.0
                    fitHeight = 320.0
                    translateX += 890
                    translateY -= 380
                }



            }
        }
    }
}