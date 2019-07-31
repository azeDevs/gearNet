package application

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*


data class LogEntry (val watchVal:String, val watchKey: String, val sourceId:String, val console: String)

class LogEntryView : View() {

    private val controller: LogEntryViewController by inject()
    lateinit private var watchValView: Label
    lateinit private var watchKeyView: Label
    lateinit private var sourceIdView: Label
    lateinit private var consoleView: Label

    override val root = form {

        textflow {
            text("watchValView") {
                fill = Color.PURPLE
                font = Font(20.0)
            }
            text("watchKeyView") {
                fill = Color.ORANGE
                font = Font(28.0)
            }
            text("watchValView") {
                fill = Color.PURPLE
                font = Font(20.0)
            }
            text("watchKeyView") {
                fill = Color.ORANGE
                font = Font(28.0)
            }
            text("watchValView") {
                fill = Color.PURPLE
                font = Font(20.0)
            }
            text("watchKeyView") {
                fill = Color.ORANGE
                font = Font(28.0)
            }
        }

        watchValView = label { addClass(AppStyle.debugWatchVal) }
        watchKeyView = label { addClass(AppStyle.debugWatchKey) }
        sourceIdView = label { addClass(AppStyle.debugWatchKey) }
        consoleView = label { addClass(AppStyle.debugConsole) }
        listview(controller.values)
    }

}

class LogEntryViewModel : ItemViewModel<LogEntry>() {
    val watchVal: StringProperty = bind { SimpleStringProperty(item?.watchVal ?: "") }
    val watchKey: StringProperty = bind { SimpleStringProperty(item?.watchKey ?: "") }
    val sourceId: StringProperty = bind { SimpleStringProperty(item?.watchVal ?: "") }
    val console: StringProperty = bind { SimpleStringProperty(item?.watchKey ?: "") }
}

class LogEntryViewController : Controller() {
    val values = FXCollections.observableArrayList("Alpha","Beta","Gamma","Delta")
}