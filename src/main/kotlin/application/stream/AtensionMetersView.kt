package application.stream

import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import memscan.GearNetShifter
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class AtensionMetersView(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private val container: StackPane
    private lateinit var atensionWheel: ImageView
    private lateinit var atensionGaugeR: AtensionGaugeView
    private lateinit var atensionGaugeB: AtensionGaugeView

    init {
        with(root) {
            container = stackpane {
                atensionWheel = imageview(getRes("atlas.png").toString()) { // WHEEL
                    viewport = Rectangle2D(832.0, 768.0, 256.0, 256.0)
                    fitWidth = 200.0
                    fitHeight = 200.0
                    translateY += 444
                }
                atensionGaugeR = AtensionGaugeView(parent, PLAYER_1)
                atensionGaugeB = AtensionGaugeView(parent, PLAYER_2)
            }
        }
    }

    override fun updateAnimation() {
        atensionGaugeR.updateAnimation()
        atensionGaugeB.updateAnimation()
    }

    override fun applyData() = Platform.runLater {
        if (a.getPlayersStaged().p1.getSignal()) {
            atensionWheel.rotate -= 3.33
        }
        if (a.getPlayersStaged().p2.getSignal()) {
            atensionWheel.rotate += 3.33
        }
        if (a.isShift(GearNetShifter.Shift.GEAR_LOBBY)) atensionWheel.rotate = 0.0
        atensionGaugeR.applyData()
        atensionGaugeB.applyData()
    }

}