package application

import MyApp.Companion.GEARNET_ENABLED
import application.arcade.ArcadeView
import application.arcade.Arcadia
import application.debug.DebugViewLayout
import application.stream.StreamViewLayout
import javafx.scene.layout.StackPane
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.View
import tornadofx.stackpane

class ApplicationView : View() {

    private val arcadia: Arcadia by inject()
    override val root: StackPane = StackPane()
    private lateinit var viewLayout: ArcadeView

    init {
        stackpane {
            viewLayout = if(GEARNET_ENABLED) DebugViewLayout(parent) else StreamViewLayout(parent)
            cycleGameLoop()
        }
    }

    private fun cycleGameLoop() {
        GlobalScope.launch {
            delay(48)
            arcadia.updatePlayers()
            viewLayout.applyData()
            viewLayout.updateAnimation()
            cycleGameLoop()
        }
    }

}



