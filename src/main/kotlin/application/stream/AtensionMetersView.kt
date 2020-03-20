package application.stream

import application.arcade.ArcadeView
import application.arcade.Arcadia
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.image.ImageView
import tornadofx.Fragment
import tornadofx.imageview
import tornadofx.stackpane
import utils.getRes

class AtensionMetersView(override val root: Parent) : Fragment(), ArcadeView {

    private val a: Arcadia by inject()
    private lateinit var atensionWheel: ImageView
    private lateinit var atensionGaugeR: AtensionGaugeView
    private lateinit var atensionGaugeB: AtensionGaugeView

    init {
        with(root) {
            stackpane {
                atensionWheel = imageview(getRes("atlas.png").toString()) { // WHEEL
                    viewport = Rectangle2D(832.0, 768.0, 256.0, 256.0)
                    fitWidth = 200.0
                    fitHeight = 200.0
                    translateY += 444
                }
                atensionGaugeR = AtensionGaugeView(parent, 0)
                atensionGaugeB = AtensionGaugeView(parent, 1)
            }
        }
    }

    override fun updateAnimation() {
        atensionGaugeR.updateAnimation()
        atensionGaugeB.updateAnimation()
    }

    override fun applyData() = Platform.runLater {
        atensionGaugeR.applyData()
        atensionGaugeB.applyData()
    }

}