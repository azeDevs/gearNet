package application

import MyApp.Companion.GEARNET_ENABLED
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
            viewLayout = if(GEARNET_ENABLED) DebugViewLayout(parent) else StreamViewLayout(parent)
        }
        cycleGameloop()
//        cycleDatabase()
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
            viewLayout.applyData(session)
            delay(48)
            cycleAnimations()
        }
    }

    private fun cycleGameloop() {
        GlobalScope.launch {
            session.updateFighters()
            session.updateWatchers()
//            if (session.isXrdApiConnected()) session.updateFighters()
//            delay(4)
            cycleGameloop()
        }
    }

}



