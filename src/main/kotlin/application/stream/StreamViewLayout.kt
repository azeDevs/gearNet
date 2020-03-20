package application.stream

import application.ApplicationStyle
import application.arcade.ArcadeView
import application.debug.DebugStyle
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import memscan.GearNetShifter.Shift.*
import session.Session
import tornadofx.*
import utils.getRes

class StreamViewLayout(override val root: Parent) : Fragment(), ArcadeView {

    private val s: Session by inject()
    private var showHud = true
    var streamView: StackPane

    private lateinit var lobbyView: LobbyView
    private lateinit var inMatchView: InMatchView
    private lateinit var viewersView: ViewersView
    private lateinit var gearNetLogs: Label

    override fun updateAnimation() {
        viewersView.updateAnimation()
    }

    override fun applyData() = Platform.runLater {
        when (s.getMode()) {
            GEAR_OFFLINE -> {
                lobbyView.setVisibility(showHud)
                inMatchView.setVisibility(!showHud)
            }
            GEAR_VICTORY -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
                showHud = true
            }
            GEAR_LOBBY -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
                showHud = true
            }
            GEAR_LOADING -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(false)
                showHud = true
            }
            GEAR_MATCH -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
                showHud = false
            }
            GEAR_SLASH -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(false)
                showHud = false
            }
            GEAR_TRAINER -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
                showHud = false
            }
            GEAR_DRAWN -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
                showHud = false
            }
        }

        inMatchView.applyData()
        lobbyView.applyData()
        viewersView.applyData()
        gearNetLogs.text = s.gn.getUpdateString()
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

                gearNetLogs = label("GearNet.UpdateLogs") {
                    addClass(DebugStyle.tempListYellow)
                    alignment = Pos.TOP_LEFT
                    translateX += 64
                    translateY += 160
                }
            }
        }
    }
}