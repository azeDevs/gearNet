import application.MainStyle
import application.MainView
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch
import tornadofx.reloadStylesheetsOnFocus

fun main(args: Array<String>) { launch<MyApp>(args) }

class MyApp : App(MainView::class, MainStyle::class) {

    init {
        reloadStylesheetsOnFocus()
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        view.title = "ＧｅａｒＮｅｔ  //  0.4.81"

    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 960.0
        stage.height = 720.0
        stage.isResizable = false
    }

}