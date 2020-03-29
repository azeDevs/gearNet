package application

import application.arcadiaViews.ArcadiaLayout
import application.gearnetViews.GearNetLayout
import arcadia.ArcadeView
import arcadia.Arcadia
import javafx.scene.layout.StackPane
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.View
import tornadofx.stackpane

class ApplicationView : View() {

    private val arcadia: Arcadia by inject()
    override val root: StackPane = StackPane()
    private lateinit var arcadiaLayout: ArcadeView
    private lateinit var gearNetLayout: ArcadeView

    init {
        stackpane {
            arcadiaLayout = ArcadiaLayout(parent)
            gearNetLayout = GearNetLayout(parent)
            cycleGameLoop()
        }
    }

    private fun cycleGameLoop() {
        GlobalScope.launch {
            delay(48)
            arcadia.updatePlayers()
            gearNetLayout.update()
            arcadiaLayout.update()
            arcadiaLayout.animate()
            cycleGameLoop()
        }
    }

}



