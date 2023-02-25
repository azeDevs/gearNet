package application.gearnetViews

import MyApp.Companion.GEARNET_ENABLED
import arcadia.ArcadeView
import arcadia.Arcadia
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.*

class GearNetLayout(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private lateinit var container: StackPane
    private lateinit var gearNetBacking: Button
    private lateinit var gearNetLogs: Label
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
    private lateinit var debugList: Label



    init {
        with(root) {
            gearNetBacking = button { addClass(GearNetStyle.toggleViewButton)
                minWidth = 1920.0
                maxWidth = 1920.0
                minHeight = 1080.0
                maxHeight = 1080.0
                shortpress { container.isVisible = !container.isVisible }

                container = stackpane {
//                    if (GEARNET_ENABLED) addClass(GearNetStyle.debugContainer)

                    gearNetLogs = label("GearNet.UpdateLogs") {
                        isVisible = false //TODO: REMOVE ME
                        addClass(GearNetStyle.tempListYellow)
                        alignment = Pos.TOP_LEFT
                        translateX += 64
                        translateY += 240
                    }

                    modeLabel = label("OFFLINE_MODE") {
                        isVisible = false //TODO: REMOVE ME
                        addClass(GearNetStyle.debugTextYellow)
                        alignment = Pos.CENTER
                        translateY += 388
                    }

                    apiConnections = label("GuiltyGear NA / RoboTwitch NA") {
                        addClass(GearNetStyle.debugListGeneric)
                        textAlignment = TextAlignment.RIGHT
                        alignment = Pos.CENTER_RIGHT
                        translateY -= 425
                        translateX -= 420
                    }

                    clientFighter = label("Host Client: NA") {
                        addClass(GearNetStyle.debugListGeneric)
                        textAlignment = TextAlignment.LEFT
                        alignment = Pos.CENTER_LEFT
                        translateY -= 425
                        translateX += 420
                    }

                    timeLabel = label("TIMER\nNA") {
                        addClass(GearNetStyle.tempListGeneric)
                        textAlignment = TextAlignment.CENTER
                        alignment = Pos.CENTER
                        translateY += 360
                    }

                    fighterRidentification = label("RED STAGE...") {
                        isVisible = false //TODO: REMOVE ME
                        addClass(GearNetStyle.debugTextRed)
                        textAlignment = TextAlignment.LEFT
                        alignment = Pos.CENTER_LEFT
                        translateY += 388
                        translateX -= 400
                    }

                    fighterBidentification = label("BLUE STAGE...") {
                        isVisible = false //TODO: REMOVE ME
                        addClass(GearNetStyle.debugTextBlue)
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
                            addClass(GearNetStyle.debugListRed)
                            alignment = Pos.BOTTOM_LEFT
                            textAlignment = TextAlignment.LEFT
                        }
                    }

                    vbox {
                        alignment = Pos.BOTTOM_RIGHT
                        translateY -= 38
                        translateX -= 8
                        fighterBstats = label("BLUE FIGHTER RECORD...") {
                            addClass(GearNetStyle.debugListBlue)
                            alignment = Pos.BOTTOM_RIGHT
                            textAlignment = TextAlignment.RIGHT
                        }
                    }

                    vbox {
                        alignment = Pos.BOTTOM_LEFT
                        translateY -= 256
                        translateX += 2
                        matchRstats = label("RED MATCH STATS...") {
                            addClass(GearNetStyle.tempListRed)
                            alignment = Pos.BOTTOM_LEFT
                            textAlignment = TextAlignment.LEFT
                        }
                    }

                    vbox {
                        alignment = Pos.BOTTOM_RIGHT
                        translateY -= 256
                        translateX -= 2
                        matchBstats = label("BLUE MATCH STATS...") {
                            addClass(GearNetStyle.tempListBlue)
                            alignment = Pos.BOTTOM_RIGHT
                            textAlignment = TextAlignment.RIGHT
                        }
                    }

                    vbox {
                        alignment = Pos.TOP_LEFT
                        translateY += 8
                        translateX += 8
                        fightersList = label("ALL FIGHTERS LIST...") {
                            addClass(GearNetStyle.debugListGeneric)
                            alignment = Pos.TOP_LEFT
                            textAlignment = TextAlignment.LEFT
                        }
                    }

                    vbox {
                        alignment = Pos.TOP_RIGHT
                        translateY += 8
                        translateX -= 8
                        watchersList = label("ALL WATCHERS LIST...") {
                            addClass(GearNetStyle.debugListGeneric)
                            alignment = Pos.TOP_RIGHT
                            textAlignment = TextAlignment.RIGHT
                        }
                    }

                    vbox {
                        alignment = Pos.BOTTOM_LEFT
                        translateY -= 170
                        translateX += 8
                        teamRPlayers = label("RED AMUNITY LIST...") {
                            addClass(GearNetStyle.tempListRed)
                            alignment = Pos.BOTTOM_LEFT
                            textAlignment = TextAlignment.LEFT
                        }
                    }

                    vbox {
                        alignment = Pos.BOTTOM_RIGHT
                        translateY -= 170
                        translateX -= 8
                        teamBPlayers = label("BLUE AMUNITY LIST...") {
                            addClass(GearNetStyle.tempListBlue)
                            alignment = Pos.BOTTOM_RIGHT
                            textAlignment = TextAlignment.RIGHT
                        }
                    }

                    vbox {
                        alignment = Pos.CENTER_RIGHT
                        translateY -= 0
                        translateX -= 0
                        debugList = label("DEBUG OUTPUT") {
                            addClass(GearNetStyle.debugListYellow)
                            alignment = Pos.TOP_LEFT
                            textAlignment = TextAlignment.LEFT
                        }
                    }

                }
            }

        }
    }


    override fun update() = Platform.runLater {

        debugList.text = a.getGearnet().getMatchDataString()

        val p1 = a.getPlayersStaged().p1
        val p2 = a.getPlayersStaged().p2

        gearNetLogs.text = a.getGNLogsString()
        modeLabel.text = a.getShift().name


        val timerText = when (a.getClientMatch().winner) {
            PLAYER_1->"<- TIMER [${a.getClientMatch().shift.name}] - "
            PLAYER_2->" - TIMER [${a.getClientMatch().shift.name}] ->"
            else->" - TIMER [${a.getClientMatch().shift.name}] - " }
        timeLabel.text = "$timerText\n${a.getClientMatch().timer}"


        apiConnections.text = "GuiltyGear ${if(a.isXrdApiConnected()) "OK" else "NA"} / RoboTwitch ${if (a.isRoboApiConnected()) "OK" else "NA"}"
        clientFighter.text = "Host Client: ${a.getClientPlayer().getUserName()}"

        fighterRidentification.text = p1.getDebugDataString(2)
        fighterBidentification.text = p2.getDebugDataString(2)

        fighterRstats.isVisible = p1.isValid()
        fighterRstats.text = "Record: ${p1.getRecordString()}" +
                "\nBystanding: ${p1.getBystandingString()}" +
                "\nRating: ${p1.getRatingString()}" +
                "\nS: ${p1.getScoreTotalString()}${if(p1.getScoreDelta()!=0) " (${p1.getScoreDeltaString()})" else ""}" +
                "\nA: ${p1.getAtension()} / R: ${p1.getRespect()} / M: ${p1.getAmunity()}"

        fighterBstats.isVisible = p2.isValid()
        fighterBstats.text = "Record: ${p2.getRecordString()}" +
                "\nBystanding: ${p2.getBystandingString()}" +
                "\nRating: ${p2.getRatingString()}" +
                "\nS: ${p2.getScoreTotalString()}${if(p2.getScoreDelta()!=0) " (${p2.getScoreDeltaString()})" else ""}" +
                "\nA: ${p2.getAtension()} / R: ${p2.getRespect()} / M: ${p2.getAmunity()}"

        matchRstats.isVisible = p1.isValid() && a.getClientMatch().isTimeValid()
        matchRstats.text = "Won ${p1.getRoundsString()}" +
                "\n${p1.getHealthString()}" +
                "\n${p1.getStunString()}" +
                "\n${p1.getTensionString()}" +
                "\n${p1.getGuardGaugeString()}" +
                "\n${p1.getBurstEnabledString()}" +
                "\n${p1.getStunLockedString()}"

        matchBstats.isVisible = p2.isValid() && a.getClientMatch().isTimeValid()
        matchBstats.text = "Won ${p2.getRoundsString()}" +
                "\n${p2.getHealthString()}" +
                "\n${p2.getStunString()}" +
                "\n${p2.getTensionString()}" +
                "\n${p2.getGuardGaugeString()}" +
                "\n${p2.getBurstEnabledString()}" +
                "\n${p2.getStunLockedString()}"

        val fighterNamesText = StringBuilder("FIGHTERS:")
        a.getFighters().forEach { fighterNamesText.append("\n${it.getDebugDataString(2)} ${it.getAtensionString()}") }
        fightersList.text = fighterNamesText.toString()

        val watcherNamesText = StringBuilder("WATCHERS:")
        a.getWatchers().forEach { watcherNamesText.append("\n${it.getDebugDataString(0)} ${it.getAtensionString()}") }
        watchersList.text = watcherNamesText.toString()

        teamRPlayers.isVisible = a.getTeam(PLAYER_1).isNotEmpty()
        val teamRText = StringBuilder("REDS:")
        a.getTeam(PLAYER_1).forEach { teamRText.append("\n${if(it.isWatcher()) "W:" else "F:"} ${it.getDebugDataString(0)} ${it.getAtensionString()}") }
        teamRPlayers.text = teamRText.toString()

        teamBPlayers.isVisible = a.getTeam(PLAYER_2).isNotEmpty()
        val teamBText = StringBuilder("BLUES:")
        a.getTeam(PLAYER_2).forEach { teamBText.append("\n${if(it.isWatcher()) "W:" else "F:"} ${it.getDebugDataString(0)} ${it.getAtensionString()}") }
        teamBPlayers.text = teamBText.toString()

    }

    override fun animate() { }

}