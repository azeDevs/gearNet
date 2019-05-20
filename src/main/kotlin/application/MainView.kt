package application

import application.match.MatchView
import application.player.PlayerView
import application.stream.StreamView
import application.tools.ToolsView
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Label
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Player
import session.Session
import tornadofx.*

class MainView : View() {

    fun updateConsole() = Platform.runLater {
        val sb = StringBuilder()
        session.consoleLog.forEach { sb.append("\n${it}") }
        consoleView.setText(sb.toString())
    }

    override val root: Form = Form()
    private val playersGui: MutableList<PlayerView> = ArrayList()
    private val matchesGui: MutableList<MatchView> = ArrayList()
    private val session: Session by inject()
    lateinit private var utilsGui: ToolsView
    lateinit private var streamView: StreamView
    lateinit private var consoleView: Label

    private fun cycleDatabase() {
        GlobalScope.launch {
            utilsGui.blinkDatabaseIndicator(session)
            delay(2048); cycleDatabase()
        }
    }

    private fun cycleMemScan() {
        GlobalScope.launch {
            utilsGui.blinkGuiltyGearIndicator(session)
            if (session.xrdApi.isConnected() && session.updatePlayers()) redrawAppUi()
            if (session.xrdApi.isConnected() && session.updateMatch()) redrawAppUi()
            delay(128); cycleMemScan()
        }
    }

    private fun cycleUi() {
        GlobalScope.launch {
            utilsGui.applyData(session)
            updateConsole()
            delay(64); cycleUi()
        }
    }

    private fun redrawAppUi() {
        utilsGui.blinkGearNetIndicator(session)
        // Sort and redraw PlayerViews
        val uiUpdate: List<Player> = session.getPlayersList()
        for (i in 0..7) if (uiUpdate.size > i) playersGui[i].applyData(uiUpdate[i], session)
        else playersGui[i].applyData(Player(), session)
        matchesGui[0].applyMatch(session.match)
        streamView.updateStreamLeaderboard(uiUpdate, session)
    }

    init {
        with(root) {
            addClass(MainStyle.appContainer)
            stackpane {
                consoleView = label { addClass(MainStyle.consoleField)
                    minWidth = 1250.0
                    maxWidth = 1250.0
                    minHeight = 700.0
                    maxHeight = 700.0
                    translateY -= 26
                    translateX += 6
                }
                vbox { translateX -= 10
                    hbox {
                        alignment = Pos.TOP_RIGHT

                        // ======== MIDDLE COLUMN ========
                        vbox {
                            alignment = Pos.TOP_CENTER; spacing = 2.0
                            minWidth = 520.0
                            maxWidth = 520.0

                            // MATCH INFO
                            label("MATCH MONITORS") { addClass(MainStyle.lobbyName) }
                            // MATCH VIEWS
                            hbox { matchesGui.add(MatchView(parent)) }
                            hbox { matchesGui.add(MatchView(parent)) }
                            hbox { matchesGui.add(MatchView(parent)) }
                            hbox { matchesGui.add(MatchView(parent)) }
                        }

                        // ======== RIGHT SIDE COLUMN ========
                        vbox {
                            alignment = Pos.TOP_CENTER; spacing = 2.0
                            minWidth = 420.0
                            maxWidth = 420.0

                            // LOBBY NAME
                            label("LOBBY_TITLE_FULL") { addClass(MainStyle.lobbyName) }
                            // PLAYER VIEWS
                            for (i in 0..7) hbox { playersGui.add(PlayerView(parent)) }
                        }

                    }

                    // ======== BOTTOM UTILS ========
                    hbox { utilsGui = ToolsView(parent) }

                }

                vbox { streamView = StreamView(parent) }

                button { addClass(MainStyle.toggleStreamButton)
                    translateY -= 15
                    minWidth = 1240.0
                    maxWidth = 1240.0
                    minHeight = 680.0
                    maxHeight = 680.0
                    shortpress {
                        if (streamView.streamView.isVisible) streamView.toggleScoreboardMode(session)
                        session.log("C: Click!")
                    }
                    longpress {
                        streamView.toggleStreamerMode(session)
                        streamView.lockHud = -1
                    }
                }

            }

            cycleDatabase()
            cycleMemScan()
            cycleUi()
        }
    }
}



