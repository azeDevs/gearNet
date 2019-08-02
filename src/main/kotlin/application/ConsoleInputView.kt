package application

import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import utils.log


class ConsoleInputController : Controller() {
    fun sendToConsole(value: String?) {
        log("admin: $value")
    }
}

class MyView : View() {

    override val root = VBox()

    init { with(root) { this += button("Press Me").apply {
                textFill = Color.RED
                action { println("Button pressed!") }
            }
        }
    }

}

class ConsoleInputView : Fragment() {
    val controller: ConsoleInputController  by inject()
    val input = SimpleStringProperty()

    override val root = form {
        fieldset {
            field("Input") { textfield(input) }
            button("Run") {
                action {
                    controller.sendToConsole(input.value ?: "")
                    input.value = ""
                }
            }
        }
    }
}