package application.stream

import application.ApplicationStyle
import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import memscan.GearNetShifter.Shift.*
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.stackpane

class StreamViewLayout(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
//    private var showHud = true
    private var streamView: StackPane

    private lateinit var lobbyView: LobbyView
    private lateinit var inMatchView: InMatchView
    private lateinit var viewersView: ViewersView

    override fun updateAnimation() {
        viewersView.updateAnimation()
    }

    override fun applyData() = Platform.runLater {
        when (a.getShift()) {
            GEAR_LOBBY -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
//                showHud = true
            }
            GEAR_LOADING -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
//                showHud = true
            }
            GEAR_MATCH -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
//                showHud = false
            }
            GEAR_SLASH -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
//                showHud = false
            }
            GEAR_DRAWN -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
//                showHud = false
            }
            GEAR_VICTORY -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
//                showHud = true
            }
            GEAR_TRAINER -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
//                showHud = false
            }
            GEAR_INTRO -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
//                showHud = false
            }
            else -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
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

            }
        }
    }
}