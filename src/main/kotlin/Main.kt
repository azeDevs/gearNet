import application.MainStyle
import application.MainView
import application.match.MatchStyle
import application.player.PlayerStyle
import application.stream.BountyStyle
import application.stream.StreamStyle
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch

val TRACE_BORDERS = false
val GHOST_OPACITY = 0.64

fun main(args: Array<String>) { launch<MyApp>(args) }

class MyApp : App(MainView::class, MainStyle::class, MatchStyle::class, PlayerStyle::class, BountyStyle::class, StreamStyle::class) {

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        view.title = "ＧｅａｒＮｅｔ  //  0.5.15"
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width  = 1296.0 // 1280
        stage.height = 759.0  // 720
        stage.isResizable = false
    }

}