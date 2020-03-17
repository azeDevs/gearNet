package application

import application.stream.StreamViewLayout
import javafx.scene.layout.StackPane
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Session
import tornadofx.*

class ApplicationView : View() {

    override val root: StackPane = StackPane()
    private val session: Session by inject()
    private lateinit var streamViewLayout: StreamViewLayout

    private fun cycleDatabase() {
        GlobalScope.launch {
            // TODO: Add database functionality
            delay(2048)
            cycleDatabase()
        }
    }

    private fun cycleMemScan() {
        GlobalScope.launch {
            if (session.api.isXrdApiConnected()) {
                session.updateFighters()
                session.updateMatchInProgress()
            }
            session.api.updateViewers()
            streamViewLayout.updateStreamLeaderboard(session.getPlayersList(), session)
            delay(16)
            cycleMemScan()
        }
    }


    init {
        stackpane {
            streamViewLayout = StreamViewLayout(parent)
            button {
                addClass(ApplicationStyle.toggleStreamButton)
                minWidth = 1920.0
                maxWidth = 1920.0
                minHeight = 1080.0
                maxHeight = 1080.0
                shortpress {
                    if (streamViewLayout.streamView.isVisible) streamViewLayout.toggleScoreboardMode(session)
                }
                longpress {
                    streamViewLayout.toggleStreamerMode(session)
                    streamViewLayout.lockHud = -1
                }
            }

        }
        cycleDatabase()
        cycleMemScan()
    }
}



