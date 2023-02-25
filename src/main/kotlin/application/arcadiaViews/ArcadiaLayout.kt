package application.arcadiaViews

import application.ApplicationStyle
import arcadia.ArcadeView
import arcadia.Arcadia
import gearnet.GearNetShifter.Shift.*
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.stackpane

class ArcadiaLayout(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private var streamView: StackPane

    private lateinit var lobbyView: LobbyView
    private lateinit var inMatchView: InMatchView
    private lateinit var viewersView: ViewersView

    override fun animate() {
        viewersView.animate()
    }

    override fun update() = Platform.runLater {
//        when (a.getShift()) {
        when (GEAR_MATCH) {
            GEAR_LOBBY -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
            }
            GEAR_LOADING -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
            }
            GEAR_MATCH -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
            }
            GEAR_SLASH -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
            }
            GEAR_DRAWN -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
            }
            GEAR_VICTORY -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
            }
            GEAR_TRAINER -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
            }
            GEAR_INTRO -> {
                lobbyView.setVisibility(false)
                inMatchView.setVisibility(true)
            }
            else -> {
                lobbyView.setVisibility(true)
                inMatchView.setVisibility(false)
            }
        }

        inMatchView.update()
        lobbyView.update()
        viewersView.update()
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