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
        with(root) {
            vbox { alignment = Pos.TOP_CENTER
                debugBox = vbox { addClass(AppStyle.debugContainer)
                    console = textflow { addClass(AppStyle.debugConsole)
                        translateY += 16
                    }
                }
                hbox { addClass(AppStyle.stageContainer)
                    translateY += 16
                    redFighter = label("RedName") { addClass(AppStyle.stageConsole); textFill = c("#FF0000") }
                    bluFighter = label("BluName") { addClass(AppStyle.stageConsole); textFill = c("#40AFFF") }
                    xrdMode = label("nullMode") { addClass(AppStyle.stageConsole); textFill = c("#FF8F40"); translateX += 120 }
                    xrdTime = label("nullTime") { addClass(AppStyle.stageConsole); textFill = c("#8080FF"); translateX += 120 }
                }
            }
        }

        log(L("Starting "), L("$ARTIFACT_NAME ", GRN), L(BUILD_VERSION, LOW))
        cycleGameLoop()
        cycleUILoop()
    }
}



