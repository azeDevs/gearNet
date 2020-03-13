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
import models.Fighter
import session.Session
import session.Session.Companion.LOADING_MODE
import session.Session.Companion.LOBBY_MODE
import session.Session.Companion.MATCH_MODE
import session.Session.Companion.SLASH_MODE
import session.Session.Companion.VICTORY_MODE
import tornadofx.*

class ApplicationView : View() {

    fun updateConsole() = Platform.runLater {
//        val sb = StringBuilder()
//        consoleLog.forEach { sb.append("\n${it}") }
//        consoleView.setText(sb.toString())
    }

    override val root: Form = Form()
    private val playersGui: MutableList<ToolsPlayerView> = ArrayList()
    private val matchesGui: MutableList<ToolsMatchView> = ArrayList()
    private val session: Session by inject()
    private lateinit var utilsGui: ToolsViewLayout
    private lateinit var streamViewLayout: StreamViewLayout
    private lateinit var consoleView: Label
    private lateinit var lobbyTitle: Label

    private fun cycleDatabase() {
        GlobalScope.launch {
            utilsGui.blinkDatabaseIndicator(session)
            delay(2048); cycleDatabase()
        }
    }

    private fun cycleMemScan() {
        GlobalScope.launch {
            utilsGui.blinkGuiltyGearIndicator(session)
            utilsGui.applyData(session)
            updateConsole()
            if (session.api.isXrdApiConnected()) {
                session.updatePlayers()
                session.updateClientMatch()
            }
            session.updateViewers()
            utilsGui.blinkGearNetIndicator(session)
            val uiUpdate: List<Fighter> = session.getPlayersList()
            for (i in 0..3) matchesGui[i].applyMatch(session.matchHandler.lobbyMatches[i].second, session)
            for (i in 0..7) if (uiUpdate.size > i) playersGui[i].applyData(uiUpdate[i], session)
            else playersGui[i].applyData(Fighter(), session)
            streamViewLayout.updateStreamLeaderboard(uiUpdate, session)
            updateTitle()
            delay(8)
            cycleMemScan()
        }
    }

    private fun updateTitle() = Platform.runLater {
        when (session.sessionMode) {
            LOBBY_MODE -> lobbyTitle.text = "LOBBY_MODE"
            LOADING_MODE -> lobbyTitle.text = "LOADING_MODE"
            MATCH_MODE -> lobbyTitle.text = "MATCH_MODE"
            SLASH_MODE -> lobbyTitle.text = "SLASH_MODE"
            VICTORY_MODE -> lobbyTitle.text = "VICTORY_MODE"
        }
    }


    init {
        with(root) {
            addClass(ApplicationStyle.appContainer)
            stackpane {
                consoleView = label {
                    addClass(ApplicationStyle.consoleField)
                    minWidth = 1250.0
                    maxWidth = 1250.0
                    minHeight = 700.0
                    maxHeight = 700.0
                    translateY -= 26
                    translateX += 6
                }
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

                vbox {
                    translateY -= 15
                    streamViewLayout = StreamViewLayout(parent)
                }

                button {
                    addClass(ApplicationStyle.toggleStreamButton)
                    translateY -= 15
                    minWidth = 1920.0
                    maxWidth = 1920.0
                    minHeight = 1080.0
                    maxHeight = 1080.0
                    shortpress {
                        if (streamViewLayout.streamView.isVisible) streamViewLayout.toggleScoreboardMode(session)
                    }
                    longpress {
                        streamViewLayout.toggleStreamerMode(session)
                        streamViewLayout.lockHud = -1
                    }
                }

            }

            cycleDatabase()
            cycleMemScan()
        }
    }
}



