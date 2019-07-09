package application.tools

import application.ApplicationStyle
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import session.Session
import tornadofx.*

class ToolsViewLayout(override val root: Parent) : Fragment() {

    private val modeGui: MutableList<ToolsModuleView> = ArrayList()
    private val modulesGui: MutableList<ToolsModuleView> = ArrayList()
    private lateinit var matchesPlayedLabel: Label
    private lateinit var playersActiveLabel: Label

    init { with(root) {
        stackpane {
            translateY += 10
            translateX -= 10
            alignment = Pos.TOP_RIGHT
            hbox {
                addClass(ApplicationStyle.utilsContainer); padding = Insets(10.0,10.0,10.0,15.0)
                minWidth = 920.0
                maxWidth = 920.0
                minHeight = 100.0
                maxHeight = 100.0
                vbox { alignment = Pos.BOTTOM_LEFT
                    hbox {
                        alignment = Pos.BOTTOM_LEFT
                        minWidth = 384.0
                        maxWidth = 384.0
                        hbox { modeGui.add(ToolsModuleView(parent, "XrdLobby")) }
                        hbox { modeGui.add(ToolsModuleView(parent, "Loading")) }
                        hbox { modeGui.add(ToolsModuleView(parent, "Match")) }
                        hbox { modeGui.add(ToolsModuleView(parent, "Slash")) }
                        hbox { modeGui.add(ToolsModuleView(parent, "Victory")) }
                    }
                    hbox {
                        alignment = Pos.BOTTOM_LEFT
                        minWidth = 384.0
                        maxWidth = 384.0
                        hbox { modulesGui.add(ToolsModuleView(parent, "Guilty Gear Xrd")) }
                        hbox { modulesGui.add(ToolsModuleView(parent, "GearNet Client")) }
                        hbox { modulesGui.add(ToolsModuleView(parent, "Stats Database")) }
                        hbox {
//                            translateY -= 16
//                            translateX += 4
                            matchesPlayedLabel = label("Matches:  -") { setPadding(Insets(10.0)) }
                            playersActiveLabel = label("Players:  - / -") { setPadding(Insets(10.0)) }
                        }
                    }
                }
            }
        }
        }
    }

    fun applyData(session: Session) = Platform.runLater {
        matchesPlayedLabel.minWidth = 125.0
        playersActiveLabel.minWidth = 125.0
        matchesPlayedLabel.text = "Matches: 1 / ${session.matchHandler.archiveMatches.size}"
        playersActiveLabel.text = "Players: ${session.getActivePlayerCount()} / ${session.players.size}"
        for (i in 0..4) if (i == session.sessionMode) modeGui[i].reset(true) else modeGui[i].reset(false)
        modeGui.forEach { it.nextFrame() }
        modulesGui.forEach { it.nextFrame() }
    }

    fun blinkGuiltyGearIndicator(session: Session) {
        modulesGui[0].reset(session.api.isXrdApiConnected())
    }

    fun blinkGearNetIndicator(session: Session) {
        modulesGui[1].reset(session.api.isXrdApiConnected())
    }

    fun blinkDatabaseIndicator(session: Session) {
        modulesGui[2].reset(session.api.isXrdApiConnected())
    }

}