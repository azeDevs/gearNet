package views

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.TextFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Fighter
import session.Session
import tornadofx.*
import views.fighters.DebugFighterView
import views.generic.DebugLabelView
import views.generic.DebugStyle
import views.logging.LogText
import views.logging.LogText.Effect.GRN
import views.logging.log
import views.logging.updateLogs

const val atifactName = "GearNet // Atension impulse"
typealias L = LogText

class Application : View() {

    private val session: Session by inject()
    private val fighterQueue: MutableList<DebugFighterView> = ArrayList()
    private val debugLabels: MutableMap<String, DebugLabelView> = HashMap()

    private var debugBox: VBox by singleAssign()
    private var console: TextFlow by singleAssign()
    private var redFighter: Label by singleAssign()
    private var bluFighter: Label by singleAssign()


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

    private fun updateConsole() = runLater {

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

        debugLabels.forEach {
            when(it.key) {
                "Mode" -> it.value.update(it.key, "${session.mode()}")
                "Match" -> it.value.update(it.key, "${session.stage().match().getId()}")
                "Timer" -> it.value.update(it.key, "${session.stage().match().getTimer()}")
                "Snaps" -> it.value.update(it.key, "${session.stage().match().getTimer()}")
            }
        }

        for (i in 0..7) fighterQueue[i].updateFighter(session.getFighters().firstOrNull {
            it.isSeated(i)
        } ?: Fighter())

        updateLogs(console)
    }

    override val root = vbox { addClass(AppStyle.appContainer) }

    init {
        with(root) {
            vbox { addClass(DebugStyle.wireFrame)
                alignment = Pos.TOP_RIGHT
                translateX -= AppStyle.OVERLAY_MARGIN_WIDTH
                label("$atifactName / OBS Fullscreen Overlay") {
                    addClass(DebugStyle.wireText)
                    scaleX = 1.6; scaleY = 1.6
                }
                vbox { addClass(DebugStyle.wireFrame)
                    minWidth = AppStyle.BATTLE_STAGE_WIDTH*0.5; maxWidth = AppStyle.BATTLE_STAGE_WIDTH*0.5
                    alignment = Pos.TOP_RIGHT
                    translateY += 260

                    vbox {
                        for (i in 0..7) hbox {
                            fighterQueue.add(DebugFighterView(parent))
                        }
                    }

                    vbox {
                        debugLabels["Mode"] = DebugLabelView(parent)
                        debugLabels["Match"] = DebugLabelView(parent)
                        debugLabels["Timer"] = DebugLabelView(parent)
                        debugLabels["Snaps"] = DebugLabelView(parent)
                    }

                }
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
                vbox { addClass(DebugStyle.wireFrame); isVisible = false
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

            hbox { addClass(DebugStyle.wireFrame); isVisible = false
                minHeight = AppStyle.BET_CONTAINER_HEIGHT; maxHeight = AppStyle.BET_CONTAINER_HEIGHT
                minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH
                hbox { minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH; addClass(
                    DebugStyle.wireFrame) }
                hbox { minWidth = AppStyle.BATTLE_STAGE_WIDTH/2; maxWidth = AppStyle.BATTLE_STAGE_WIDTH/2; addClass(
                    DebugStyle.wireFrame)
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
                hbox { minWidth = AppStyle.BATTLE_STAGE_WIDTH/2; maxWidth = AppStyle.BATTLE_STAGE_WIDTH/2; addClass(
                    DebugStyle.wireFrame)
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
                hbox { minWidth = AppStyle.OVERLAY_MARGIN_WIDTH; maxWidth = AppStyle.OVERLAY_MARGIN_WIDTH; addClass(
                    DebugStyle.wireFrame) }
            }

            vbox { addClass(DebugStyle.wireFrame); alignment = Pos.CENTER
                minHeight = AppStyle.TICKER_HEIGHT
//                label("NEWS TICKER") { addClass(DebugStyle.wireText) }
            }

            // DEBUG CONSOLE STUFF
            vbox { alignment = Pos.TOP_CENTER
                debugBox = vbox { addClass(DebugStyle.debugContainer)
                    console = textflow { addClass(DebugStyle.debugText); translateY += 16 }
                }
                translateY -= 810.0
            }
        }

        log(
            L("Beginning.. "), L(atifactName, GRN) /*.to.see.further.*/
        )
        cycleGameLoop()
        cycleUILoop()
    }
}



