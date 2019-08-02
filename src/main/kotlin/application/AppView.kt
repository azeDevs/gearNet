package application

import MyApp.Companion.ARTIFACT_NAME
import MyApp.Companion.BUILD_VERSION
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Session
import tornadofx.*
import utils.consoleLog
import utils.log
import utils.watchedLog

class AppView : View() {

    val logEntryView = find(LogEntryView::class)

    private var debugView: VBox
    private val session: Session by inject()
    private lateinit var consoleView: Label
    private lateinit var watchKeyView: Label
    private lateinit var watchValView: Label
    override val root: Form = Form()

    private fun cycleGameLoop() { GlobalScope.launch {
        session.generateEvents()
        delay(4); cycleGameLoop() }
    }

    private fun cycleUILoop() { GlobalScope.launch {
        updateConsole()
        delay(20); cycleUILoop() }
    }

    private fun updateConsole() = Platform.runLater { val sb = StringBuilder()
        consoleLog.forEach { sb.append("\n$it") }; consoleView.text = sb.toString(); sb.clear()
        watchedLog.forEach { sb.append("\n${it.key}") }; watchKeyView.text = sb.toString(); sb.clear()
        watchedLog.forEach { sb.append("\n${it.value}") }; watchValView.text = sb.toString(); sb.clear()
    }

    init { log("Starting $ARTIFACT_NAME $BUILD_VERSION")
        with(root) { addClass(AppStyle.appContainer)
                debugView = vbox { padding = Insets(64.0)
                    hbox {
                        consoleView = label("consoleView")
                        watchKeyView = label("watchKeyView")
                        watchValView = label("watchValView")
                    }
                }
            }

            cycleGameLoop()
            cycleUILoop()

    }
}



