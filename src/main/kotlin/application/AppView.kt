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
    private lateinit var debugBox: VBox
    private lateinit var console: TextFlow
    private lateinit var redFighter: Label
    private lateinit var bluFighter: Label
    private lateinit var xrdMode: Label
    private lateinit var xrdTime: Label

    private fun cycleGameLoop() {
        GlobalScope.launch {
            // 000: Beginning of GameLoop
            session.generateEvents()
            delay(4); cycleGameLoop()
        }
    }

    private fun cycleUILoop() {
        GlobalScope.launch {
            updateConsole()
            delay(32); cycleUILoop()
        }
    }

    private fun updateConsole() = Platform.runLater {
        redFighter.text = session.getStagedFighters().first.getName()
        bluFighter.text = session.getStagedFighters().second.getName()
        xrdMode.text = "${session.getMode()} MODE"
        xrdTime.text = "TIMER: ${session.stage().match().getTimer()}"
        updateLogs(console)
    }

    override val root = vbox { addClass(AppStyle.appContainer) }

    init {
        val TOP_GOALS_HEIGHT = 140.0
        val BATTLE_STAGE_WIDTH = 1480.0
        val FIGHTER_STAT_HEIGHT = 140.0

        val OVERLAY_MARGIN_WIDTH = 220.0
        val OVERLAY_MARGIN_HEIGHT = 790.0
        
        val BET_CONTAINER_HEIGHT = 100.0
        val BET_TABLE_HEIGHT = 60.0
        val BET_TABLE_WIDTH = 390.0
        val BET_SCALE_WIDTH = 180.0
        
        val TICKER_HEIGHT = 50.0

        with(root) {

            vbox { addClass(AppStyle.wireFrame)
                alignment = Pos.TOP_CENTER
                label("OBS MODE: 1920x1080 MAX / KEY") { addClass(AppStyle.wireText) }
                label("$ARTIFACT_NAME $BUILD_VERSION / OBS Fullscreen Overlay") {
                    addClass(AppStyle.wireText)
                    scaleX = 1.6; scaleY = 1.6
                    translateY += 400.0
                }
                minHeight = TOP_GOALS_HEIGHT
            }

            hbox {
                // KNIGHT WING
                vbox { addClass(AppStyle.wireFrame); alignment = Pos.TOP_RIGHT
                    minHeight = OVERLAY_MARGIN_HEIGHT; maxHeight = OVERLAY_MARGIN_HEIGHT
                    minWidth = OVERLAY_MARGIN_WIDTH; maxWidth = OVERLAY_MARGIN_WIDTH
                    label("FIGHTER QUEUE") { addClass(AppStyle.wirePane)
                        minHeight = 320.0; maxHeight = 320.0
                        minWidth = OVERLAY_MARGIN_WIDTH; maxWidth = OVERLAY_MARGIN_WIDTH
                    }
                }

                // BATTLE THEATRE
                vbox { addClass(AppStyle.wireFrame)
                    minHeight = OVERLAY_MARGIN_HEIGHT
                    minWidth = BATTLE_STAGE_WIDTH/2; maxWidth = BATTLE_STAGE_WIDTH/2
                    label("RED FIGHTER") { addClass(AppStyle.wirePane)
                        minHeight = FIGHTER_STAT_HEIGHT; maxHeight = FIGHTER_STAT_HEIGHT
                        minWidth = BATTLE_STAGE_WIDTH/2; maxWidth = BATTLE_STAGE_WIDTH/2
                    }
                }
                vbox { addClass(AppStyle.wireFrame)
                    minHeight = OVERLAY_MARGIN_HEIGHT
                    minWidth = BATTLE_STAGE_WIDTH/2; maxWidth = BATTLE_STAGE_WIDTH/2
                    label("BLU FIGHTER") { addClass(AppStyle.wirePane)
                        minHeight = FIGHTER_STAT_HEIGHT; maxHeight = FIGHTER_STAT_HEIGHT
                        minWidth = BATTLE_STAGE_WIDTH/2; maxWidth = BATTLE_STAGE_WIDTH/2
                    }

                }

                // SHADE WING
                vbox { addClass(AppStyle.wireFrame)
                    minHeight = OVERLAY_MARGIN_HEIGHT; maxHeight = OVERLAY_MARGIN_HEIGHT
                    minWidth = OVERLAY_MARGIN_WIDTH; maxWidth = OVERLAY_MARGIN_WIDTH
                    label("BOUNTY RANKINGS") { addClass(AppStyle.wirePane)
                        minHeight = OVERLAY_MARGIN_HEIGHT/2; maxHeight = OVERLAY_MARGIN_HEIGHT/2
                        minWidth = OVERLAY_MARGIN_WIDTH; maxWidth = OVERLAY_MARGIN_WIDTH
                    }
                    label("WALLET RANKINGS") { addClass(AppStyle.wirePane)
                        minHeight = OVERLAY_MARGIN_HEIGHT/2; maxHeight = OVERLAY_MARGIN_HEIGHT/2
                        minWidth = OVERLAY_MARGIN_WIDTH; maxWidth = OVERLAY_MARGIN_WIDTH
                    }
                }
            }



            hbox { addClass(AppStyle.wireFrame)
                minHeight = BET_CONTAINER_HEIGHT; maxHeight = BET_CONTAINER_HEIGHT
                minWidth = OVERLAY_MARGIN_WIDTH; maxWidth = OVERLAY_MARGIN_WIDTH
                hbox { minWidth = OVERLAY_MARGIN_WIDTH; maxWidth = OVERLAY_MARGIN_WIDTH; addClass(AppStyle.wireFrame) }
                hbox { minWidth = BATTLE_STAGE_WIDTH/2; maxWidth = BATTLE_STAGE_WIDTH/2; addClass(AppStyle.wireFrame)
                    alignment = Pos.BOTTOM_LEFT
                    label("RED BET TABLE") { addClass(AppStyle.wirePane)
                        minHeight = BET_TABLE_HEIGHT; maxHeight = BET_TABLE_HEIGHT
                        minWidth = BET_TABLE_WIDTH; maxWidth = BET_TABLE_WIDTH
                    }
                    label("RED BET SCALE") { addClass(AppStyle.wirePane)
                        minHeight = BET_CONTAINER_HEIGHT; maxHeight = BET_CONTAINER_HEIGHT
                        minWidth = BET_SCALE_WIDTH; maxWidth = BET_SCALE_WIDTH
                    }
                }
                hbox { minWidth = BATTLE_STAGE_WIDTH/2; maxWidth = BATTLE_STAGE_WIDTH/2; addClass(AppStyle.wireFrame)
                    alignment = Pos.BOTTOM_RIGHT
                    label("BLU BET SCALE") { addClass(AppStyle.wirePane)
                        minHeight = BET_CONTAINER_HEIGHT; maxHeight = BET_CONTAINER_HEIGHT
                        minWidth = BET_SCALE_WIDTH; maxWidth = BET_SCALE_WIDTH
                    }
                    label("BLU BET TABLE") { addClass(AppStyle.wirePane)
                        minHeight = BET_TABLE_HEIGHT; maxHeight = BET_TABLE_HEIGHT
                        minWidth = BET_TABLE_WIDTH; maxWidth = BET_TABLE_WIDTH
                    }
                }
                hbox { minWidth = OVERLAY_MARGIN_WIDTH; maxWidth = OVERLAY_MARGIN_WIDTH; addClass(AppStyle.wireFrame) }
            }


            vbox { addClass(AppStyle.wireFrame)
                minHeight = TICKER_HEIGHT
                label("NEWS TICKER") { addClass(AppStyle.wireText) }
            }

            // DEBUG CONSOLE STUFF
            vbox { alignment = Pos.TOP_CENTER; isVisible = false
                debugBox = vbox { addClass(AppStyle.debugContainer); console = textflow { addClass(AppStyle.debugConsole); translateY += 16 } }
                hbox { addClass(AppStyle.stageContainer); translateY += 16
                    redFighter = label("RedName") { addClass(AppStyle.stageConsole); textFill = c("#FF0000") }
                    bluFighter = label("BluName") { addClass(AppStyle.stageConsole); textFill = c("#40AFFF") }
                    xrdMode = label("nullMode") { addClass(AppStyle.stageConsole); textFill = c("#FF8F40"); translateX += 120 }
                    xrdTime = label("nullTime") { addClass(AppStyle.stageConsole); textFill = c("#8080FF"); translateX += 120 }
                }
                translateY -= 880.0
            }

        }

        log(L("Starting "), L("$ARTIFACT_NAME ", GRN), L(BUILD_VERSION, LOW))
        cycleGameLoop()
        cycleUILoop()
    }
}



