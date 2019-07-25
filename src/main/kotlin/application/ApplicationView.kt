package application

import ARTIFACT_NAME
import BUILD_VERSION
import application.stream.StreamViewLayout
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Session
import tornadofx.*
import utils.consoleLog
import utils.log
import utils.watchedLog

class ApplicationView : View() {

    fun updateConsole() = Platform.runLater {
        val sb = StringBuilder()
        consoleLog.forEach { sb.append("\n${it}") }
        consoleView.setText(sb.toString())
        sb.clear()
        watchedLog.forEach { sb.append("\n${it.key}") }
        watchKeyView.setText(sb.toString())
        sb.clear()
        watchedLog.forEach { sb.append("\n${it.value}") }
        watchValView.setText(sb.toString())
    }

    override val root: Form = Form()
    private val session: Session by inject()
    lateinit private var streamViewLayout: StreamViewLayout
    lateinit private var consoleView: Label
    lateinit private var watchKeyView: Label
    lateinit private var watchValView: Label
    lateinit private var debugView: StackPane


    private fun cycleGameLoop() {
        GlobalScope.launch {
            session.generateEvents()
            updateConsole()
            streamViewLayout.updateStreamLeaderboard(session)
            delay(32); cycleGameLoop()
        }
    }

    init {
        log("Starting $ARTIFACT_NAME $BUILD_VERSION")
        with(root) {
            addClass(ApplicationStyle.appContainer)
            stackpane {

                vbox { streamViewLayout = StreamViewLayout(parent) }

                debugView = stackpane {
                    consoleView = label {
                        addClass(ApplicationStyle.consoleField)
                        minWidth = 1060.0
                        maxWidth = 1060.0
                        translateX -= 84
                        translateY -= 8
                    }
                    watchKeyView = label {
                        addClass(ApplicationStyle.consoleField)
                        minWidth = 160.0
                        maxWidth = 160.0
                        translateX += 530
                        translateY -= 8
                    }
                    watchValView = label {
                        addClass(ApplicationStyle.consoleFieldNoBG)
                        minWidth = 160.0
                        maxWidth = 160.0
                        translateX += 530
                        translateY -= 8
                    }
                }
                button {
                    addClass(ApplicationStyle.toggleStreamButton)
                    translateY -= 15
                    shortpress { streamViewLayout.toggleScoreboard() }
                    longpress { debugView.isVisible = !debugView.isVisible }
                }

            }
            cycleGameLoop()
        }
    }
}



