package views.generic

import javafx.scene.Parent
import javafx.scene.control.Label
import tornadofx.*
import utils.The

class DebugLabelView(override val root: Parent) : Fragment() {

    private var tagText: Label by singleAssign()
    private var valueText: Label by singleAssign()

    init {
        with(root) {
            hbox {
                tagText = label("-") { addClass(DebugStyle.debugText) }
                valueText = label("-") { addClass(DebugStyle.debugText) }
            }
        }
    }

    fun update(tag: String, value: String) = runLater {
        tagText.text = tag
        valueText.text = value
        if (The(value).isInt() && The(value).toInt() > 0) valueText.textFill = c("#00FF00")
        else if (The(value).isInt() && The(value).toInt() < 0) valueText.textFill = c("#FF0000")
        else valueText.textFill = c("#AACCFF")
    }

}
