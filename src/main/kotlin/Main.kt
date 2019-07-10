import application.ApplicationStyle
import application.ApplicationView
import application.stream.BigScoreStyle
import application.stream.InMatchStyle
import application.tools.ToolsMatchStyle
import application.tools.ToolsPlayerStyle
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch

fun main(args: Array<String>) {
    launch<MyApp>(args)
}

class MyApp : App(ApplicationView::class, ApplicationStyle::class, ToolsMatchStyle::class, ToolsPlayerStyle::class, BigScoreStyle::class, InMatchStyle::class) {

    companion object {
        const val SIMULATE_MODE = true
        const val TRACE_BORDERS = false
        const val GHOST_OPACITY = 0.64
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        view.title = "ＧｅａｒＮｅｔ  //  0.6.1"
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width  = 1296.0 // 1280
        stage.height = 759.0  // 720
        stage.isResizable = false
    }

}