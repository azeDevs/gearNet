import application.ApplicationStyle
import application.ApplicationView
import application.debug.DebugStyle
import application.stream.InMatchStyle
import application.stream.ScoreStyle
import javafx.stage.Stage
import tornadofx.App
import tornadofx.UIComponent
import tornadofx.launch

fun main(args: Array<String>) {
    launch<MyApp>(args)
}

class MyApp : App(ApplicationView::class, ApplicationStyle::class, ScoreStyle::class, InMatchStyle::class, DebugStyle::class) {

    companion object {
        const val VERSION = "0.7.1"
        const val GEARNET_ENABLED = true
        const val SIMULATION_MODE = false
        const val BORDER_TRACINGS = false
        const val TWITCH_CHAT_BOT = true
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        when(GEARNET_ENABLED) {
            true -> view.title = "ＧｅａｒＮｅｔ // $VERSION"
            false -> view.title = "Gᴜɪʟᴛy Åʀᴄᴀᴅɪᴀ // $VERSION"
        }
    }

    override fun start(stage: Stage) {
        when(GEARNET_ENABLED) {
            true -> {
                stage.width  = 1600.0 + 16 // 1600.0 + 16
                stage.height = 900.0 + 39 // 900.0 + 39
                stage.isFullScreen = false
            }
            false -> {
                stage.width  = 1904.0 + 16 // 1600.0 + 16
                stage.height = 1041.0 + 39 // 900.0 + 39
                stage.isFullScreen = true
            }
        }
        stage.isResizable = false
        super.start(stage)
        stage.toBack()
        stage.apply { }
    }

}