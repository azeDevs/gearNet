import application.ApplicationStyle
import application.ApplicationView
import application.stream.InMatchStyle
import application.stream.ScoreStyle
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch

fun main(args: Array<String>) {
    launch<MyApp>(args)
}

class MyApp : App(ApplicationView::class, ApplicationStyle::class, ScoreStyle::class, InMatchStyle::class) {

    companion object {
        const val SILENT_TWITCH = true
        const val SIMULATE_MODE = false
        const val TRACE_BORDERS = false
        const val GHOST_OPACITY = 0.64
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        view.title = "ＧｅａｒＮｅｔ  //  0.6.1"
    }

    override fun start(stage: Stage) {
        stage.width  = 1904.0 + 16 // 1600.0 + 16
        stage.height = 1041.0 + 39 // 900.0 + 39
        stage.isResizable = false
        stage.isFullScreen = true
        super.start(stage)
        stage.toBack()
        stage.apply {

        }
    }

}