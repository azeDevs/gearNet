import application.AppStyle
import application.AppView
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch

fun main(args: Array<String>) = launch<MyApp>(args)
class MyApp : App(AppView::class, AppStyle::class) {

    companion object {
        const val ARTIFACT_NAME = "GearNet // Bounty Bets"
        const val BUILD_VERSION = "0.7.2"
        const val WD = "\uD835\uDE86\$"
        const val SILENCE_BOT = true
    }

    override fun onBeforeShow(view: UIComponent) { super.onBeforeShow(view); view.title = "$ARTIFACT_NAME $BUILD_VERSION" }
    override fun start(stage: Stage) { super.start(stage); stage.toBack(); stage.isResizable = false
        stage.width  = 1920.0 + 16
        stage.height = 1080.0 + 39
    }

}

/*
     f1 - 7 chain  [ VS ]  chain 1 - f2
               bet 64% payout  |  p2chain*4 percent payout reduction
                                  p1chain*8 percent payout bonus
     p1bet = 100             p2bet = 1000
*/