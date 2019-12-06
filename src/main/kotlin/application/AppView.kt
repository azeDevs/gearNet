package application

import MyApp.Companion.ARTIFACT_NAME
import MyApp.Companion.BUILD_VERSION
import application.LogText.Effect.GRN
import application.LogText.Effect.LOW
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.TextFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Session
import tornadofx.*

typealias L = LogText

class AppView : View() {

    private val session: Session by inject()
//    private var fighterView: DebugFighterView by singleAssign()
    private var debugBox: VBox by singleAssign()
    private var console: TextFlow by singleAssign()
    private var redFighter: Label by singleAssign()
    private var bluFighter: Label by singleAssign()
//    private var redHP: Label by singleAssign()
//    private var bluHP: Label by singleAssign()
    private var xrdMode: Label by singleAssign()
    private var xrdTime: Label by singleAssign()


    private fun cycleGameLoop() {
        GlobalScope.launch {
            // 000: Beginning of GameLoop
            session.generateEvents()
            delay(8); cycleGameLoop()
        }
    }

    private fun cycleUILoop() {
        GlobalScope.launch {
            updateConsole()
            delay(16); cycleUILoop()
        }
    }

    private fun updateConsole() = Platform.runLater {

        if (session.getStagedFighters().first.getName().isNotEmpty()) redFighter.text = "${session.getStagedFighters().first.getName()}: ${session.stage().match().getHealth(0)} HP"
        else {
            if (session.stage().match().isInHellFire(0)) redFighter.text = "RED FIGHTER: ${session.stage().match().getHealth(0)} HP!"
            else redFighter.text = "RED FIGHTER: ${session.stage().match().getHealth(0)} HP"
        }

        if (session.getStagedFighters().second.getName().isNotEmpty()) bluFighter.text = "${session.getStagedFighters().second.getName()}: ${session.stage().match().getHealth(1)} HP"
        else {
            if (session.stage().match().isInHellFire(1)) bluFighter.text = "BLU FIGHTER: ${session.stage().match().getHealth(1)} HP!"
            else bluFighter.text = "BLU FIGHTER: ${session.stage().match().getHealth(1)} HP"
        }

        xrdTime.text = "TIMER ${session.stage().match().getTimer()} / ${session.stage().match().getSnapCount()} SNAPS"
        xrdMode.text = "${session.mode()} ID${session.stage().match().getId()}"
        updateLogs(console)
    }

    override val root = vbox { addClass(AppStyle.appContainer) }

    init {
        with(root) {
            vbox { addClass(DebugStyle.wireFrame)
                alignment = Pos.TOP_CENTER
                label("$ARTIFACT_NAME $BUILD_VERSION / OBS Fullscreen Overlay") {
                    addClass(DebugStyle.wireText)
                    scaleX = 1.6; scaleY = 1.6
                }
                xrdTime = label("nullTime") { addClass(DebugStyle.statusText); textFill = c("#8080FF"); translateY += 260 }
                xrdMode = label("nullMode") { addClass(DebugStyle.statusText); textFill = c("#FF8F40"); translateY += 840 }
                minHeight = AppStyle.TOP_GOALS_HEIGHT
            }

            hbox {
                // KNIGHT WING
                vbox { addClass(DebugStyle.wireFrame); alignment = Pos.TOP_RIGHT
                    minHeight = AppStyle.OVERLAY_MARGIN_HEIGHT; maxHeight = AppStyle.OVERLAY_MARGIN_HEIGHT
                    minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH
//                    vbox { addClass(AppStyle.fighterZone)
//                        minHeight = AppStyle.OVERLAY_MARGIN_HEIGHT/2; maxHeight = AppStyle.OVERLAY_MARGIN_HEIGHT/2
//                        minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH
//                        label("FIGHTER QUEUE")
//                        fighterView = DebugFighterView(this)
//                    }
//                    label("VIEWER CHAT") { addClass(AppStyle.viewerZone)
//                        minHeight = AppStyle.OVERLAY_MARGIN_HEIGHT/2; maxHeight = AppStyle.OVERLAY_MARGIN_HEIGHT/2
//                        minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH
//                    }
                }

                // BATTLE THEATRE
                vbox { addClass(DebugStyle.wireFrame)
                    minHeight = AppStyle.OVERLAY_MARGIN_HEIGHT
                    minWidth = AppStyle.BATTLE_STAGE_WIDTH/2; maxWidth = AppStyle.BATTLE_STAGE_WIDTH/2
                    vbox { addClass(AppStyle.redPane)
                        minHeight = AppStyle.FIGHTER_STAT_HEIGHT; maxHeight = AppStyle.FIGHTER_STAT_HEIGHT
                        minWidth = AppStyle.BATTLE_STAGE_WIDTH/2; maxWidth = AppStyle.BATTLE_STAGE_WIDTH/2
                        redFighter = label("RED FIGHTER")
//                        redHP = label("-redHP")
                    }
                }
                vbox { addClass(DebugStyle.wireFrame)
                    minHeight = AppStyle.OVERLAY_MARGIN_HEIGHT
                    minWidth = AppStyle.BATTLE_STAGE_WIDTH/2; maxWidth = AppStyle.BATTLE_STAGE_WIDTH/2
                    vbox { addClass(AppStyle.bluPane)
                        minHeight = AppStyle.FIGHTER_STAT_HEIGHT; maxHeight = AppStyle.FIGHTER_STAT_HEIGHT
                        minWidth = AppStyle.BATTLE_STAGE_WIDTH/2; maxWidth = AppStyle.BATTLE_STAGE_WIDTH/2
                        bluFighter = label("BLU FIGHTER")
//                        bluHP = label("-bluHP")
                    }
                }

                // SHADE WING
                vbox { addClass(DebugStyle.wireFrame)
                    minHeight = AppStyle.OVERLAY_MARGIN_HEIGHT; maxHeight = AppStyle.OVERLAY_MARGIN_HEIGHT
                    minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH
                    vbox { addClass(AppStyle.fighterZone)
                        minHeight = AppStyle.OVERLAY_MARGIN_HEIGHT/2; maxHeight = AppStyle.OVERLAY_MARGIN_HEIGHT/2
                        minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH
                        label("FIGHTER RANKINGS")
                    }
                    vbox { addClass(AppStyle.viewerZone)
                        minHeight = AppStyle.OVERLAY_MARGIN_HEIGHT/2; maxHeight = AppStyle.OVERLAY_MARGIN_HEIGHT/2
                        minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH
                        label("VIEWER RANKINGS")
                    }
                }
            }

            hbox { addClass(DebugStyle.wireFrame)
                minHeight = AppStyle.BET_CONTAINER_HEIGHT; maxHeight = AppStyle.BET_CONTAINER_HEIGHT
                minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH
                hbox { minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH; addClass(DebugStyle.wireFrame) }
                hbox { minWidth = AppStyle.BATTLE_STAGE_WIDTH/2; maxWidth = AppStyle.BATTLE_STAGE_WIDTH/2; addClass(DebugStyle.wireFrame)
                    alignment = Pos.BOTTOM_LEFT
                    vbox {
                        minHeight = AppStyle.BET_CONTAINER_HEIGHT; maxHeight = AppStyle.BET_CONTAINER_HEIGHT
                        minWidth = AppStyle.BET_TABLE_WIDTH; maxWidth = AppStyle.BET_TABLE_WIDTH
                        vbox { addClass(AppStyle.redPane)
                            minHeight = AppStyle.TENSION_PULSE_HEIGHT; maxHeight = AppStyle.TENSION_PULSE_HEIGHT
                            minWidth = AppStyle.BET_TABLE_WIDTH; maxWidth = AppStyle.BET_TABLE_WIDTH
                            label("RED TENSION")
                        }
                        vbox { addClass(AppStyle.viewerZone)
                            minHeight = AppStyle.BET_TABLE_HEIGHT; maxHeight = AppStyle.BET_TABLE_HEIGHT
                            minWidth = AppStyle.BET_TABLE_WIDTH; maxWidth = AppStyle.BET_TABLE_WIDTH
                            label("TOTAL AMOUNT WAGERED (RED)")
                        }
                    }
                    vbox { addClass(AppStyle.viewerZone)
                        minHeight = AppStyle.BET_CONTAINER_HEIGHT; maxHeight = AppStyle.BET_CONTAINER_HEIGHT
                        minWidth = AppStyle.BET_SCALE_WIDTH; maxWidth = AppStyle.BET_SCALE_WIDTH
                        label("PAYOUT SCALE")
                    }
                }
                hbox { minWidth = AppStyle.BATTLE_STAGE_WIDTH/2; maxWidth = AppStyle.BATTLE_STAGE_WIDTH/2; addClass(DebugStyle.wireFrame)
                    alignment = Pos.BOTTOM_RIGHT
                    vbox { addClass(AppStyle.viewerZone)
                        minHeight = AppStyle.BET_CONTAINER_HEIGHT; maxHeight = AppStyle.BET_CONTAINER_HEIGHT
                        minWidth = AppStyle.BET_SCALE_WIDTH; maxWidth = AppStyle.BET_SCALE_WIDTH
                        label("PAYOUT SCALE")
                    }
                    vbox {
                        minHeight = AppStyle.BET_CONTAINER_HEIGHT; maxHeight = AppStyle.BET_CONTAINER_HEIGHT
                        minWidth = AppStyle.BET_TABLE_WIDTH; maxWidth = AppStyle.BET_TABLE_WIDTH
                        vbox { addClass(AppStyle.bluPane)
                            minHeight = AppStyle.TENSION_PULSE_HEIGHT; maxHeight = AppStyle.TENSION_PULSE_HEIGHT
                            minWidth = AppStyle.BET_TABLE_WIDTH; maxWidth = AppStyle.BET_TABLE_WIDTH
                            label("BLU TENSION")
                        }
                        vbox { addClass(AppStyle.viewerZone)
                            minHeight = AppStyle.BET_TABLE_HEIGHT; maxHeight = AppStyle.BET_TABLE_HEIGHT
                            minWidth = AppStyle.BET_TABLE_WIDTH; maxWidth = AppStyle.BET_TABLE_WIDTH
                            label("TOTAL AMOUNT WAGERED (BLU)")
                        }
                    }
                }
                hbox { minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH; addClass(DebugStyle.wireFrame) }
            }

            vbox { addClass(DebugStyle.wireFrame); alignment = Pos.CENTER
                minHeight = AppStyle.TICKER_HEIGHT
                label("NEWS TICKER") { addClass(DebugStyle.wireText) }
            }

            // DEBUG CONSOLE STUFF
            vbox { alignment = Pos.TOP_CENTER
                debugBox = vbox { addClass(DebugStyle.debugContainer)
                    console = textflow { addClass(DebugStyle.debugText); translateY += 16 }
                }
                translateY -= 810.0
            }
        }

        log(L("Starting "), L("$ARTIFACT_NAME ", GRN), L(BUILD_VERSION, LOW))
        cycleGameLoop()
        cycleUILoop()
    }
}



