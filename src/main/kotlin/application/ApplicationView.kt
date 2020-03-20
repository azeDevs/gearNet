package application

import MyApp.Companion.GEARNET_ENABLED
import application.debug.ArcadeView
import application.debug.DebugViewLayout
import application.stream.StreamViewLayout
import javafx.scene.layout.StackPane
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
        }
    }

}



