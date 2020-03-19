package application.debug

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import session.Session
import session.Session.Companion.LOADING_MODE
import session.Session.Companion.LOBBY_MODE
import session.Session.Companion.MATCH_MODE
import session.Session.Companion.OFFLINE_MODE
import session.Session.Companion.SLASH_MODE
import session.Session.Companion.VICTORY_MODE
import tornadofx.*

class DebugViewLayout(override val root: Parent) : Fragment(), ArcadeView {

    private var container: StackPane
    private val session: Session by inject()

    private lateinit var modeLabel: Label
    private lateinit var timeLabel: Label
    private lateinit var apiConnections: Label
    private lateinit var clientFighter: Label
    private lateinit var fighterRidentification: Label
    private lateinit var fighterBidentification: Label
    private lateinit var fighterRstats: Label
    private lateinit var fighterBstats: Label
    private lateinit var matchRstats: Label
    private lateinit var matchBstats: Label
    private lateinit var teamRPlayers: Label
    private lateinit var teamBPlayers: Label
    private lateinit var watchersList: Label
    private lateinit var fightersList: Label

    private lateinit var gearnetPlayerListView: GearnetPlayerListView
//    private lateinit var playerList: PlayerList
//    private lateinit var playerEditor: PlayerEditor

    init {
        with(root) {
            container = stackpane {
                addClass(DebugStyle.debugContainer)

                modeLabel = label("OFFLINE_MODE") {
                    addClass(DebugStyle.debugTextYellow)
                    alignment = Pos.CENTER
                    translateY += 388
                }

                apiConnections = label("GuiltyGear NA / RoboTwitch NA") {
                    addClass(DebugStyle.debugListGeneric)
                    textAlignment = TextAlignment.RIGHT
                    alignment = Pos.CENTER_RIGHT
                    translateY -= 425
                    translateX -= 420
                }

                clientFighter = label("Host Client: NA") {
                    addClass(DebugStyle.debugListGeneric)
                    textAlignment = TextAlignment.LEFT
                    alignment = Pos.CENTER_LEFT
                    translateY -= 425
                    translateX += 420
                }

                timeLabel = label("TIMER\nNA") {
                    addClass(DebugStyle.tempListGeneric)
                    textAlignment = TextAlignment.CENTER
                    alignment = Pos.CENTER
                    translateY += 320
                }

                fighterRidentification = label("RED STAGE...") {
                    addClass(DebugStyle.debugTextRed)
                    textAlignment = TextAlignment.LEFT
                    alignment = Pos.CENTER_LEFT
                    translateY += 388
                    translateX -= 400
                }

                fighterBidentification = label("BLUE STAGE...") {
                    addClass(DebugStyle.debugTextBlue)
                    textAlignment = TextAlignment.RIGHT
                    alignment = Pos.CENTER_RIGHT
                    translateY += 388
                    translateX += 400
                }

                vbox {
                    alignment = Pos.BOTTOM_LEFT
                    translateY -= 38
                    translateX += 8
                    fighterRstats = label("RED FIGHTER RECORD...") {
                        addClass(DebugStyle.debugListRed)
                        alignment = Pos.BOTTOM_LEFT
                        textAlignment = TextAlignment.LEFT
                    }
                }

                vbox {
                    alignment = Pos.BOTTOM_RIGHT
                    translateY -= 38
                    translateX -= 8
                    fighterBstats = label("BLUE FIGHTER RECORD...") {
                        addClass(DebugStyle.debugListBlue)
                        alignment = Pos.BOTTOM_RIGHT
                        textAlignment = TextAlignment.RIGHT
                    }
                }

                vbox {
                    alignment = Pos.BOTTOM_LEFT
                    translateY -= 170
                    translateX += 508
                    matchRstats = label("RED MATCH STATS...") {
                        addClass(DebugStyle.tempListRed)
                        alignment = Pos.BOTTOM_LEFT
                        textAlignment = TextAlignment.LEFT
                    }
                }

                vbox {
                    alignment = Pos.BOTTOM_RIGHT
                    translateY -= 170
                    translateX -= 508
                    matchBstats = label("BLUE MATCH STATS...") {
                        addClass(DebugStyle.tempListBlue)
                        alignment = Pos.BOTTOM_RIGHT
                        textAlignment = TextAlignment.RIGHT
                    }
                }

                vbox {
                    alignment = Pos.TOP_LEFT
                    translateY += 8
                    translateX += 8
                    fightersList = label("ALL FIGHTERS LIST...") {
                        addClass(DebugStyle.debugListGeneric)
                        alignment = Pos.TOP_LEFT
                        textAlignment = TextAlignment.LEFT
                    }
                }

                vbox {
                    alignment = Pos.TOP_RIGHT
                    translateY += 8
                    translateX -= 8
                    watchersList = label("ALL WATCHERS LIST...") {
                        addClass(DebugStyle.debugListGeneric)
                        alignment = Pos.TOP_RIGHT
                        textAlignment = TextAlignment.RIGHT
                    }
                }

                vbox {
                    alignment = Pos.BOTTOM_LEFT
                    translateY -= 170
                    translateX += 8
                    teamRPlayers = label("RED AMUNITY LIST...") {
                        addClass(DebugStyle.tempListRed)
                        alignment = Pos.BOTTOM_LEFT
                        textAlignment = TextAlignment.LEFT
                    }
                }

                vbox {
                    alignment = Pos.BOTTOM_RIGHT
                    translateY -= 170
                    translateX -= 8
                    teamBPlayers = label("BLUE AMUNITY LIST...") {
                        addClass(DebugStyle.tempListBlue)
                        alignment = Pos.BOTTOM_RIGHT
                        textAlignment = TextAlignment.RIGHT
                    }
                }

                gearnetPlayerListView = GearnetPlayerListView(parent)

            }
        }
    }

    // TODO: MAKE ANIMATIONS RUN ON THEIR OWN COROUTINES? (OR JUST LEARN THE ViewModel PATTERN)
    // FIXME: LOADING_MODE SWITCHES INCONSISTENTLY
    // FIXME: VICTORY_MODE DOESN'T SWITCH BACK TO LOBBY_MODE AFTER A MATCH HAS ENDED
    // FIXME: AUTO-TEAM ASSIGNMENT FOR FIGHTERS IS ADVERSELY AFFECTING BOUNTY RESULTS
    // FIXME: SLEEP_MODE DOESN'T TRIGGER WHEN Xrd IS CLOSED

    override fun applyData(s: Session) = Platform.runLater {

        when (s.getMode()) {
            OFFLINE_MODE -> modeLabel.text = "OFFLINE_MODE"
            LOBBY_MODE -> modeLabel.text = "LOBBY_MODE"
            MATCH_MODE -> modeLabel.text = "MATCH_MODE"
            SLASH_MODE -> modeLabel.text = "SLASH_MODE"
            LOADING_MODE -> modeLabel.text = "LOADING_MODE"
            VICTORY_MODE -> modeLabel.text = "VICTORY_MODE"
        }

        timeLabel.isVisible = s.getClientMatch().isValid()
        timeLabel.text = "TIMER\n${s.getClientMatch().getTimer()}"

        apiConnections.text = "GuiltyGear ${if(s.isXrdApiConnected()) "OK" else "NA"} / RoboTwitch ${if (s.getTwitchHandler().isConnected()) "OK" else "NA"}"
        clientFighter.text = "Host Client: ${s.getClientFighter().getDebugDataString(0)}"
        fighterRidentification.text = s.getStagedFighers().p1.getDebugDataString(2)
        fighterBidentification.text = s.getStagedFighers().p2.getDebugDataString(2)

        fighterRstats.isVisible = s.getStagedFighers().p1.isValid()
        fighterRstats.text = "Record: ${s.getStagedFighers().p1.getRecordString()}" +
                "\nBystanding: ${s.getStagedFighers().p1.getBystandingString()}" +
                "\nRating: ${s.getStagedFighers().p1.getRatingString()}" +
                "\nS: ${s.getStagedFighers().p1.getScoreTotalString()}${if(s.getStagedFighers().p2.getScoreDelta()!=0) " (${s.getStagedFighers().p2.getScoreDeltaString()})" else ""}" +
                "\nA: ${s.getStagedFighers().p1.getAtension()} / R: ${s.getStagedFighers().p1.getRespect()} / M: ${s.getStagedFighers().p1.getMunity()}"

        fighterBstats.isVisible = s.getStagedFighers().p2.isValid()
        fighterBstats.text = "Record: ${s.getStagedFighers().p2.getRecordString()}" +
                "\nBystanding: ${s.getStagedFighers().p2.getBystandingString()}" +
                "\nRating: ${s.getStagedFighers().p2.getRatingString()}" +
                "\nS: ${s.getStagedFighers().p2.getScoreTotalString()}${if(s.getStagedFighers().p2.getScoreDelta()!=0) " (${s.getStagedFighers().p2.getScoreDeltaString()})" else ""}" +
                "\nA: ${s.getStagedFighers().p2.getAtension()} / R: ${s.getStagedFighers().p2.getRespect()} / M: ${s.getStagedFighers().p2.getMunity()}"

        matchRstats.isVisible = s.getStagedFighers().p1.isValid() && s.getClientMatch().isValid()
        matchRstats.text = "Won ${s.getStagedFighers().p1.getRoundsString()}" +
                "\n${s.getStagedFighers().p1.getHealthString()}" +
                "\n${s.getStagedFighers().p1.getStunString()}" +
                "\n${s.getStagedFighers().p1.getTensionString()}" +
                "\n${s.getStagedFighers().p1.getRiscString()}" +
                "\n${s.getStagedFighers().p1.getBurstString()}" +
                "\n${s.getStagedFighers().p1.getStrikeStunString()}"

        matchBstats.isVisible = s.getStagedFighers().p2.isValid() && s.getClientMatch().isValid()
        matchBstats.text = "Won ${s.getStagedFighers().p2.getRoundsString()}" +
                "\n${s.getStagedFighers().p2.getHealthString()}" +
                "\n${s.getStagedFighers().p2.getStunString()}" +
                "\n${s.getStagedFighers().p2.getTensionString()}" +
                "\n${s.getStagedFighers().p2.getRiscString()}" +
                "\n${s.getStagedFighers().p2.getBurstString()}" +
                "\n${s.getStagedFighers().p2.getStrikeStunString()}"

        val fighterNamesText = StringBuilder("FIGHTERS:")
        s.getFighters().forEach { fighterNamesText.append("\n${it.getDebugDataString(2)} ${it.getAtensionString()}") }
        fightersList.text = fighterNamesText.toString()

        val watcherNamesText = StringBuilder("WATCHERS:")
        s.getWatchers().forEach { watcherNamesText.append("\n${it.getDebugDataString(0)} ${it.getAtensionString()}") }
        watchersList.text = watcherNamesText.toString()

        teamRPlayers.isVisible = s.getTeamRed().isNotEmpty()
        val teamRText = StringBuilder("REDS:")
        s.getTeamRed().forEach { teamRText.append("\n${if(it.isWatcher()) "W:" else "F:"} ${it.getDebugDataString(0)} ${it.getAtensionString()}") }
        teamRPlayers.text = teamRText.toString()

        teamBPlayers.isVisible = s.getTeamBlue().isNotEmpty()
        val teamBText = StringBuilder("BLUES:")
        s.getTeamBlue().forEach { teamBText.append("\n${if(it.isWatcher()) "W:" else "F:"} ${it.getDebugDataString(0)} ${it.getAtensionString()}") }
        teamBPlayers.text = teamBText.toString()

    }

}