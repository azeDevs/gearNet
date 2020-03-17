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
    private lateinit var inMatchView: InMatchView
    private lateinit var viewersView: ViewersView


    fun updateStreamLeaderboard(fighters: List<Fighter>, s: Session) {
        if (s.sessionMode == lockHud) {
            lobbyView.setVisibility(showHud)
            inMatchView.setVisibility(!showHud)
        }
        else {
            lockHud = -1
            when (s.sessionMode) {
                VICTORY_MODE -> {
                    lobbyView.setVisibility(true)
                    inMatchView.setVisibility(false)
                    showHud = true
                }
                LOBBY_MODE -> {
                    lobbyView.setVisibility(true)
                    inMatchView.setVisibility(false)
                    showHud = true
                }
                LOADING_MODE -> {
                    lobbyView.setVisibility(false)
                    inMatchView.setVisibility(false)
                    showHud = true
                }
                MATCH_MODE -> {
                    lobbyView.setVisibility(false)
                    inMatchView.setVisibility(true)
                    showHud = false
                }
                SLASH_MODE -> {
                    lobbyView.setVisibility(false)
                    inMatchView.setVisibility(false)
                    showHud = false
                }
            }
        }

        inMatchView.applyData(fighters, s)
        lobbyView.applyData(fighters, s, showHud)
        viewersView.applyData(s)
    }

    fun toggleScoreboardMode(session: Session) {
        lockHud = session.sessionMode
        showHud = !showHud
        updateStreamLeaderboard(session.getPlayersList(), session)
    }

    fun toggleStreamerMode(session: Session) {
        if (streamView.opacity.equals(0.01)) streamView.opacity = 1.0
        else if (streamView.opacity.equals(1.0)) streamView.opacity = 0.01
        updateStreamLeaderboard(session.getPlayersList(), session)
    }

    init {
        with(root) {
            streamView = stackpane { addClass(ApplicationStyle.streamContainer)

                lobbyView = LobbyView(parent)
                inMatchView = InMatchView(parent)
                viewersView = ViewersView(parent)


                imageview(getRes("atlas.png").toString()) { // BARC TITLE
                    viewport = Rectangle2D(1088.0, 768.0, 448.0, 128.0)
                    fitWidth = 448.0
                    fitHeight = 128.0
                    translateY -= 488
                }
                imageview(getRes("atlas.png").toString()) { // RED BANNER
                    viewport = Rectangle2D(1536.0, 704.0, 256.0, 320.0)
                    fitWidth = 256.0
                    fitHeight = 320.0
                    translateX -= 880
                    translateY -= 400
                    scaleX *= 0.88
                    scaleY *= 0.88
                }
                imageview(getRes("atlas.png").toString()) { // BLUE BANNER
                    viewport = Rectangle2D(1792.0, 704.0, 256.0, 320.0)
                    fitWidth = 256.0
                    fitHeight = 320.0
                    translateX += 880
                    translateY -= 400
                    scaleX *= 0.88
                    scaleY *= 0.88
                }

            }
        }
    }
}