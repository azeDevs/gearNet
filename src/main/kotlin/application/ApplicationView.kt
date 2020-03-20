package application

import MyApp.Companion.GEARNET_ENABLED
import application.arcade.ArcadeView
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

    private val s: Session by inject()
    override val root: StackPane = StackPane()
    private lateinit var viewLayout: ArcadeView

    init {
        stackpane {
            viewLayout = if(GEARNET_ENABLED) DebugViewLayout(parent) else StreamViewLayout(parent)
            cycleGameLoop()
        }
    }

    fun cycleGameLoop() {
        GlobalScope.launch {
            delay(48)
            viewLayout.applyData()
            viewLayout.updateAnimation()
            cycleGameLoop()
        }
    }

}



