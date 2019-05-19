import application.MainStyle
import application.MainView
import application.match.MatchStyle
import application.player.BountyStyle
import application.player.PlayerStyle
import application.player.StreamStyle
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch

val RANDOM_VALUES = false
val BOUNTIES_MODE = false
val TRACE_BORDERS = false

fun main(args: Array<String>) { launch<MyApp>(args) }

class MyApp : App(MainView::class, MainStyle::class, MatchStyle::class, PlayerStyle::class, BountyStyle::class, StreamStyle::class) {

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        view.title = "ＧｅａｒＮｅｔ  //  0.5.6"
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width  = 1296.0 // 1280
        stage.height = 759.0 // 720
        stage.isResizable = false
    }

}