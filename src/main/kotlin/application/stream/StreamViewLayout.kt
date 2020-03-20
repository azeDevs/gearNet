package application.stream

import application.ApplicationStyle
import application.debug.ArcadeView
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import session.Session
import session.Session.Companion.LOADING_MODE
import session.Session.Companion.LOBBY_MODE
import session.Session.Companion.MATCH_MODE
import session.Session.Companion.OFFLINE_MODE
import session.Session.Companion.SLASH_MODE
import session.Session.Companion.VICTORY_MODE
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class StreamViewLayout(override val root: Parent) : Fragment(), ArcadeView {

    private val s: Session by inject()
    private var showHud = true
    var streamView: StackPane

    private lateinit var lobbyView: LobbyView
    private lateinit var inMatchView: InMatchView
    private lateinit var viewersView: ViewersView

    override fun updateAnimation() {
        viewersView.updateAnimation()
    }

    override fun applyData() {
        when (s.getMode()) {
            OFFLINE_MODE -> {
                lobbyView.setVisibility(showHud)
                inMatchView.setVisibility(!showHud)
            }
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

        inMatchView.applyData()
        lobbyView.applyData()
        viewersView.applyData()
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
            }
        }
    }
}