package application.debug

import javafx.geometry.Pos
import javafx.scene.Parent
import models.Player
import session.Session
import tornadofx.*


class PlayerViewModel(playerModel: Player) : ItemViewModel<Player>(playerModel) {
    val playerObjectProperty = bind(Player::playerObjectProperty)
    val isPlayerValid = bind(Player::isValidProperty)
    val playerName = bind(Player::nameProperty)
    val scoreTotal = bind(Player::scoreTotalProperty)
}

class GearnetPlayerListView(override val root: Parent) : Fragment() {

//    private lateinit var spotR: GearnetPlayerView
//    private lateinit var spotB: GearnetPlayerView
    private val session: Session by inject()


    init {
        with(root) {
            vbox {
                translateY += 128.0
                translateX += 64.0
                session.getPlayersMap().forEach {
                    hbox { GearnetPlayerView(parent, it.value) }
                }
//                hbox { spotR = GearnetPlayerView(parent, playersList[0]!!) }
//                hbox { spotB = GearnetPlayerView(parent, playersList[1]!!) }
            }
        }
    }

}

class GearnetPlayerView(override val root: Parent, val player: Player = Player()) : Fragment() {
    private val model = PlayerViewModel(player)

    init {
        with(root) {
            hbox {
                alignment = Pos.CENTER_LEFT
                label(model.playerName) {addClass(DebugStyle.debugListYellow)
                    model.rebindOnChange(model.playerName)
                }
                label(model.scoreTotal) {addClass(DebugStyle.debugListYellow)
                    model.rebindOnChange(model.scoreTotal)
                }
            }
        }
    }

}