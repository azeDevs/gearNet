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

fun main(args: Array<String>) = launch<MyApp>(args)

val ARTIFACT_NAME = "GearNet // Bounty Bets"
val BUILD_VERSION = "0.7.1"

val WD = "\uD835\uDE86\$"
val RED_BANNER = Pair("\uD83D\uDD34","Red")
val BLU_BANNER = Pair("\uD83D\uDD35","Blue")

class MyApp : App(ApplicationView::class, ApplicationStyle::class, ToolsMatchStyle::class, ToolsPlayerStyle::class, BigScoreStyle::class, InMatchStyle::class) {

    companion object {
        const val SIMULATE_MODE = false
        const val TRACE_BORDERS = false
        const val GHOST_OPACITY = 0.64
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        view.title = "$ARTIFACT_NAME $BUILD_VERSION"
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width  = 1296.0 // 1280
        stage.height = 759.0  // 720
        stage.isResizable = false
    }

}