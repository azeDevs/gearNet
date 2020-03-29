package application.arcadiaViews

import arcadia.ArcadeView
import arcadia.Arcadia
import arcadia.Arcadia.Companion.MAX_ATENSION
import gearnet.GearNetShifter
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class AtensionWheelView(override val root: Parent) : Fragment(), ArcadeView {

    private var targetRotation = 0.0

    private val a: Arcadia by inject()
    private val container: StackPane
    private lateinit var atensionWheel: ImageView

    init {
        with(root) {
            container = stackpane {
                atensionWheel = imageview(getRes("atlas.png").toString()) { // WHEEL
                    viewport = Rectangle2D(832.0, 768.0, 256.0, 256.0)
                    fitWidth = 200.0
                    fitHeight = 200.0
                    translateY += 444
                }
            }
        }
    }

    override fun animate() = Platform.runLater {
        when{
            atensionWheel.rotate > targetRotation -> atensionWheel.rotate -= 1.0
            atensionWheel.rotate < targetRotation -> atensionWheel.rotate += 1.0
        }
    }

    override fun update() = Platform.runLater {

        val p1 = a.getPlayersStaged().p1
        val p2 = a.getPlayersStaged().p2

        if(p1.isStunLocked()) {
            targetRotation = (p1.getAtension().toDouble() / MAX_ATENSION) * 90.0
        }

        if(p2.isStunLocked()) {
            targetRotation = (p2.getAtension().toDouble() / MAX_ATENSION) * -90.0
        }

        if(a.isShift(GearNetShifter.Shift.GEAR_LOBBY)) {
            targetRotation = 0.0
        }

    }

}