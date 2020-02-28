package views.logging

import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.*


//        with(root.fieldset("Person Info")) {
//            fieldset("Bounty Property") { textfield(bountyProp, IntegerStringConverter()) }
//            fieldset("Delta Property") { textfield(deltaProp, IntegerStringConverter()) }
//            button("Save") { setOnAction { controller.save(fighter) } }
//            button("Update delta from POJO") {
//                setOnAction {
//                    // Set a new value behind the UI's back
//                    fighter.setDelta(42)
//                    // Force an update to the UI
//                    deltaProp.refresh()
//                }
//            }
//        }

// ---------------------------------------------- TESTING STUFF

//class MyView : View() {
//    val controller: MyController by inject()
//
//    override val root = vbox {
//        label("My items")
//        listview(controller.values)
//    }
//}
//
//class MyController: Controller() {
//    var invalid: BooleanBinding = someOtherTextField.textProperty().isEmpty()
//        .or(
//            Bindings.createBooleanBinding(
//                { !validationField.validate() },
//                validationField.textProperty()
//            )
//        )
//
//    val values = FXCollections.observableArrayList("Alpha","Beta","Gamma","Delta")
//}



class CounterView : View() { override val root = BorderPane()
    private val counter = SimpleIntegerProperty()
    init { title = "Counter"
        with(root) { style { padding = box(20.px) }
            center { vbox(10.0) {
                alignment = Pos.CENTER
                label { bind(counter); style { fontSize = 25.px } }
                button("Click to increment").setOnAction { increment() }
            }
            }
        }
    }
    private fun increment() { counter.value += 1 }
}

class CounterApp : App(CounterView::class) {
    override fun onBeforeShow(view: UIComponent) { super.onBeforeShow(view); view.title = "Test App" }
    override fun start(stage: Stage) {
        stage.isResizable = false
        stage.width  = 320.0 + 16
        stage.height = 240.0 + 39
        super.start(stage)
    }
}