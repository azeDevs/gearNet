package application.debug

import application.ApplicationStyle
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import session.Session
import tornadofx.*

class DebugViewLayout(override val root: Parent) : Fragment(), ArcadeView {

    private var container: StackPane

    private lateinit var modeLabel: Label
    private lateinit var clientFighter: Label
    private lateinit var fighter1: Label
    private lateinit var fighter2: Label
    private lateinit var watchersList: Label
    private lateinit var fightersList: Label

    init {
        with(root) {
            container = stackpane {
                addClass(ApplicationStyle.streamContainer)

                modeLabel = label("MODE") {
                    addClass(ApplicationStyle.debugLabel)
                    alignment = Pos.CENTER
                    translateY += 465
                }

                clientFighter = label("Client") {
                    addClass(ApplicationStyle.debugList)
                    textAlignment = TextAlignment.LEFT
                    alignment = Pos.CENTER_LEFT
                    translateY -= 514
                    translateX -= 520
                }

                fighter1 = label("Red") {
                    addClass(ApplicationStyle.debugLabel)
                    textAlignment = TextAlignment.LEFT
                    alignment = Pos.CENTER_LEFT
                    translateY += 465
                    translateX -= 480
                }

                fighter2 = label("Blue") {
                    addClass(ApplicationStyle.debugLabel)
                    textAlignment = TextAlignment.RIGHT
                    alignment = Pos.CENTER_RIGHT
                    translateY += 465
                    translateX += 480
                }

                vbox {
                    alignment = Pos.TOP_LEFT
                    translateY += 8
                    translateX += 8
                    fightersList = label("FIGHTERS") {
                        addClass(ApplicationStyle.debugList)
                        alignment = Pos.TOP_LEFT
                        textAlignment = TextAlignment.LEFT
                    }
                }

                vbox {
                    alignment = Pos.TOP_RIGHT
                    translateY += 8
                    translateX -= 8
                    watchersList = label("WATCHERS") {
                        addClass(ApplicationStyle.debugList)
                        alignment = Pos.TOP_RIGHT
                        textAlignment = TextAlignment.RIGHT
                    }
                }

            }
        }
    }

    // FIXME: LOADING_MODE DOESN'T TRIGGER

    override fun applyData(s: Session) = Platform.runLater {
        when (s.sessionMode) {
            Session.SLEEP_MODE -> modeLabel.text = "SLEEP_MODE"
            Session.LOBBY_MODE -> modeLabel.text = "LOBBY_MODE"
            Session.MATCH_MODE -> modeLabel.text = "MATCH_MODE"
            Session.SLASH_MODE -> modeLabel.text = "SLASH_MODE"
            Session.LOADING_MODE -> modeLabel.text = "LOADING_MODE"
            Session.VICTORY_MODE -> modeLabel.text = "VICTORY_MODE"
        }

        if (s.getClientFighter().isValid()) clientFighter.text = "Client: [${s.getClientFighter().getLoadPercent()}] ${s.getClientFighter().getUserName()}"
        else clientFighter.text = "Client: []"
        if (s.getStagedFighers().first.isValid()) fighter1.text = "Red: [${s.getStagedFighers().first.getLoadPercent()}] ${s.getStagedFighers().first.getUserName()}"
        else fighter1.text = "Red: []"
        if (s.getStagedFighers().second.isValid()) fighter2.text = "Blue: [${s.getStagedFighers().first.getLoadPercent()}] ${s.getStagedFighers().second.getUserName()}"
        else fighter2.text = "Blue: []"

        val fighterNames = StringBuilder("FIGHTERS:")
        s.fighters.forEach { fighterNames.append("\n[${it.value.getLoadPercent()}] ${it.value.getUserName()}") }
        fightersList.text = fighterNames.toString()

        val watcherNames = StringBuilder("WATCHERS:")
        s.watchers.forEach { watcherNames.append("\n${it.value.getUserName()}") }
        watchersList.text = watcherNames.toString()

    }

}