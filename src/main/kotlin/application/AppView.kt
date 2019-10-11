package application

import MyApp.Companion.ARTIFACT_NAME
import MyApp.Companion.BUILD_VERSION
import application.LogText.Effect.GRN
import application.LogText.Effect.LOW
import javafx.application.Platform
import javafx.scene.layout.VBox
import javafx.scene.text.TextFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Session
import tornadofx.View
import tornadofx.addClass
import tornadofx.textflow
import tornadofx.vbox

typealias L = LogText

class AppView : View() {

    private val session: Session by inject()
    private lateinit var debugBox: VBox
    private lateinit var console: TextFlow

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
            delay(16); cycleUILoop()
        }
    }

    private fun updateConsole() = Platform.runLater {
        updateLogs(console)
    }

    override val root = vbox { addClass(AppStyle.appContainer) }

    init {
        with(root) {
            debugBox = vbox {
                addClass(AppStyle.debugContainer)
                translateY -= 220
                translateX += 240
                console = textflow {
                    addClass(AppStyle.debugConsole)
                    translateY += 16
                }
            }
        }

        log(L("Starting "), L("$ARTIFACT_NAME ", GRN), L(BUILD_VERSION, LOW))
        cycleGameLoop()
        cycleUILoop()
    }
}



