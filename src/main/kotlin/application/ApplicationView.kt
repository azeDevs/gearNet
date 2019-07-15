package application

import application.stream.StreamViewLayout
import application.tools.ToolsMatchView
import application.tools.ToolsPlayerView
import application.tools.ToolsViewLayout
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Label
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Session
import session.consoleLog
import tornadofx.*

class ApplicationView : View() {

    fun updateConsole() = Platform.runLater {
        val sb = StringBuilder()
        consoleLog.forEach { sb.append("\n${it}") }
        consoleView.setText(sb.toString())
        consoleViewShade.setText(sb.toString())
    }

    override val root: Form = Form()
    private val playersGui: MutableList<ToolsPlayerView> = ArrayList()
    private val matchesGui: MutableList<ToolsMatchView> = ArrayList()
    private val session: Session by inject()
    lateinit private var utilsGui: ToolsViewLayout
    lateinit private var streamViewLayout: StreamViewLayout
    lateinit private var consoleViewShade: Label
    lateinit private var consoleView: Label
    lateinit private var lobbyTitle: Label

    private fun cycleDatabase() {
        GlobalScope.launch {
                utilsGui.blinkDatabaseIndicator(session)
            delay(2048); cycleDatabase()
        }
    }

    private fun cycleGameLoop() {
        GlobalScope.launch {
                utilsGui.blinkGuiltyGearIndicator(session)
//                if (session.api.isXrdApiConnected()) {
//                    session.updatePlayers()
//                    session.updateClientMatch()
//                }
            delay(32); cycleGameLoop()
        }
    }

    private fun cycleUi() {
        GlobalScope.launch {
//            streamViewLayout.animateTargets()
//            utilsGui.applyData(session)
//            updateConsole()
//            // redrawAppUi
//            utilsGui.blinkGearNetIndicator(session)
//            val uiUpdate: List<Player> = session.getPlayersList()
//            for (i in 0..3) matchesGui[i].applyMatch(session.matchHandler.lobbyMatches[i].second, session)
//            for (i in 0..7) if (uiUpdate.size > i) playersGui[i].applyData(uiUpdate[i], session)
//            else playersGui[i].applyData(Player(), session)
//            streamViewLayout.updateStreamLeaderboard(uiUpdate, session)
//            updateTitle()
            delay(32); cycleUi()
        }
    }

    init {
        with(root) {
            addClass(ApplicationStyle.appContainer)
            stackpane {
                vbox {
                    translateX -= 10
                    hbox {
                        alignment = Pos.TOP_RIGHT

                        // ======== MIDDLE COLUMN ========
                        vbox {
                            alignment = Pos.TOP_CENTER; spacing = 2.0
                            minWidth = 520.0
                            maxWidth = 520.0

                            // MATCH INFO
                            label("MATCH MONITORS") { addClass(ApplicationStyle.lobbyName) }
                            // MATCH VIEWS
                            hbox { matchesGui.add(ToolsMatchView(parent)) }
                            hbox { matchesGui.add(ToolsMatchView(parent)) }
                            hbox { matchesGui.add(ToolsMatchView(parent)) }
                            hbox { matchesGui.add(ToolsMatchView(parent)) }
                        }

                        // ======== RIGHT SIDE COLUMN ========
                        vbox {
                            alignment = Pos.TOP_CENTER; spacing = 2.0
                            minWidth = 420.0
                            maxWidth = 420.0

                            // LOBBY NAME
                            lobbyTitle = label("LOBBY_TITLE_FULL") { addClass(ApplicationStyle.lobbyName) }
                            // PLAYER VIEWS
                            for (i in 0..7) hbox { playersGui.add(ToolsPlayerView(parent)) }
                        }

                    }

                    // ======== BOTTOM UTILS ========
                    hbox { utilsGui = ToolsViewLayout(parent) }

                }

                vbox { streamViewLayout = StreamViewLayout(parent) }

                consoleViewShade = label {
                    addClass(ApplicationStyle.consoleFieldShade)
                    minWidth = 1250.0
                    maxWidth = 1250.0
                    minHeight = 700.0
                    maxHeight = 700.0
                    translateY -= 214
                    translateX += 5
                    isVisible = true
                }

                consoleView = label {
                    addClass(ApplicationStyle.consoleField)
                    minWidth = 1250.0
                    maxWidth = 1250.0
                    minHeight = 700.0
                    maxHeight = 700.0
                    translateY -= 216
                    translateX += 4
                    isVisible = true
                }

                button {
                    addClass(ApplicationStyle.toggleStreamButton)
                    translateY -= 15
                    minWidth = 1240.0
                    maxWidth = 1240.0
                    minHeight = 680.0
                    maxHeight = 680.0
                    shortpress {
                        if (streamViewLayout.streamView.isVisible) streamViewLayout.toggleScoreboardMode(session)
                    }
                    longpress {
                        if (consoleView.isVisible.equals(false)) {
                            consoleView.isVisible = true
                            consoleViewShade.isVisible = true
                        } else {
                            consoleView.isVisible = false
                            consoleViewShade.isVisible = false
                        }
                    }
                }

            }

            cycleDatabase()
            cycleGameLoop()
            cycleUi()
        }
    }
}



