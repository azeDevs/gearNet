package application

import MyApp.Companion.DEBUGGER_MODE
import application.debug.ArcadeView
import application.debug.DebugViewLayout
import application.stream.StreamViewLayout
import javafx.scene.layout.StackPane
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import session.Session
import tornadofx.View
import tornadofx.stackpane

class ApplicationView : View() {

    override val root: StackPane = StackPane()
    private val session: Session by inject()
    private lateinit var viewLayout: ArcadeView

    init {
        stackpane {
            viewLayout = if(DEBUGGER_MODE) DebugViewLayout(parent) else StreamViewLayout(parent)
        }
        cycleGameloop()
        cycleDatabase()
        cycleAnimations()
    }

    private fun cycleDatabase() {
        GlobalScope.launch {
            // TODO: Add database functionality
            delay(2048)
            cycleDatabase()
        }
    }

    private fun cycleAnimations() {
        GlobalScope.launch {
            viewLayout.updateAnimation(session)
            delay(48)
            cycleAnimations()
        }
    }

    private fun cycleGameloop() {
        GlobalScope.launch {
            if (session.api.isXrdApiConnected()) {
                session.updateFighters()
                session.updateMatchInProgress()
            }
            session.api.updateWatchers()
            session.updatePlayerAtension()
            viewLayout.applyData(session)
            delay(8)
            cycleGameloop()
        }
    }

}



