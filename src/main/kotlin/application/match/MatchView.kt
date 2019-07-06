package application.match

import GHOST_OPACITY
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import session.Character.NULL
import session.Character.getCharacterPortrait
import session.Character.getCharacterTrademark
import session.Match
import session.Session
import tornadofx.*
import utils.Duo
import utils.getRandomName
import utils.getRes
import utils.isInRange
import kotlin.random.Random

/*
    data class MatchData(
        val tension: Pair<Int, Int> = Pair(-1,-1),
        val health: Pair<Int, Int> = Pair(-1,-1),
        val burst: Pair<Boolean, Boolean> = Pair(false,false),
        val risc: Pair<Int, Int> = Pair(-1,-1),
        val isHit: Pair<Boolean, Boolean> = Pair(false,false)
    )
*/
class MatchView(override val root: Parent) : Fragment() {

    private val P1 = 0
    private val P2 = 1
    var playerSteamId = Duo(-1L, -1L)

    var wholeThing = StackPane()
    var timer = Label()
    var cabinet = Label()
    var character = Duo(ImageView(),ImageView())
    var rounds = Duo(Label(),Label())
    var handle = Duo(Label(),Label())
    var tension = Duo(Label(),Label())
    var health = Duo(Label(),Label())
    var burst = Duo(Label(),Label())
    var risc = Duo(Label(),Label())
    var isHit = Duo(Label(),Label())

    init {
        with(root) {
            wholeThing = stackpane { opacity = GHOST_OPACITY
//                imageview(getRes("gn_atlas.png").toString()) { setViewport(Rectangle2D(6.0, 768.0, 500.0, 128.0)) }
                timer = label {
                    addClass(MatchStyle.matchTitle)
                    translateY -= 2.2
                    scaleX += 8.8
                    scaleY += 8.8
                    opacity = 0.25
                }
                cabinet = label {
                    addClass(MatchStyle.matchTitle)
                    translateY -= 45.0
                }
                hbox {
                    addClass(MatchStyle.matchContainer)

                    vbox { addClass(MatchStyle.sidestatsContainer)
                        translateY += 35.0
                        hbox {
                            character.p1 = imageview(getRes("gn_atlas.png").toString()) {
                                viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                                fitHeight = 32.0
                                fitWidth = 32.0
                                translateX -= 3.0
                                translateY -= 2.0
                            }
                            vbox {
                                handle.p1 = label().addClass(MatchStyle.matchPlayerTitle)
                                hbox {
                                    health.p1 = label().addClass(MatchStyle.demoText)
                                    rounds.p1 = label { addClass(MatchStyle.demoText); translateX -= 20.0 }
                                }
                            }
                        }
                        hbox{ translateY += 6.0
                            risc.p1 = label().addClass(MatchStyle.demoText)
                            isHit.p1 = label().addClass(MatchStyle.demoText)
                        }
                        hbox { translateY += 6.0
                            tension.p1 = label().addClass(MatchStyle.demoText)
                            burst.p1 = label().addClass(MatchStyle.demoText)
                        }
                    }
                    vbox { addClass(MatchStyle.sidestatsContainer)
                        translateY += 35.0
                        translateX += 4.0
                        hbox {
                            character.p2 = imageview(getRes("gn_atlas.png").toString()) {
                                viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                                fitHeight = 32.0
                                fitWidth = 32.0
                                translateX -= 3.0
                                translateY -= 2.0
                            }
                            vbox {
                                handle.p2 = label().addClass(MatchStyle.matchPlayerTitle)
                                hbox { //translateY += 6.0
                                    health.p2 = label().addClass(MatchStyle.demoText)
                                    rounds.p2 = label { addClass(MatchStyle.demoText); translateX -= 20.0 }
                                }
                            }
                        }
                        hbox{ translateY += 6.0
                            risc.p2 = label().addClass(MatchStyle.demoText)
                            isHit.p2 = label().addClass(MatchStyle.demoText)
                        }
                        hbox { translateY += 6.0
                            tension.p2 = label().addClass(MatchStyle.demoText)
                            burst.p2 = label().addClass(MatchStyle.demoText)
                        }
                    }
                }
            }
        }
    }

    fun applyMatch(m: Match, session: Session) = Platform.runLater {
        if (session.randomValues) applyRandomData(m) else
            if (m.matchId != -1L) {
                wholeThing.opacity = 1.0
                timer.text = m.getTimer().toString()
                cabinet.text = m.getCabinetString()

                character.p1.setViewport(getCharacterTrademark(m.getCharacter(P1)))
                handle.p1.text = m.getHandleString(P1)
                tension.p1.text = m.getTensionString(P1)
                health.p1.text = m.getHealthString(P1)
                rounds.p1.text = m.getRoundsString(P1)
                burst.p1.text = m.getBurstString(P1)
                risc.p1.text = m.getRiscString(P1)
                isHit.p1.text = m.getHitStunString(P1)

                character.p2.setViewport(getCharacterPortrait(m.getCharacter(P2)))
                handle.p2.text = m.getHandleString(P2)
                tension.p2.text = m.getTensionString(P2)
                health.p2.text = m.getHealthString(P2)
                rounds.p2.text = m.getRoundsString(P2)
                burst.p2.text = m.getBurstString(P2)
                risc.p2.text = m.getRiscString(P2)
                isHit.p2.text = m.getHitStunString(P2)
            } else {
                wholeThing.opacity = GHOST_OPACITY
                timer.text = ""
                cabinet.text = ""

                character.p1.setViewport(getCharacterPortrait(NULL))
                handle.p1.text = ""
                tension.p1.text = ""
                health.p1.text = ""
                rounds.p1.text = ""
                burst.p1.text = ""
                risc.p1.text = ""
                isHit.p1.text = ""

                character.p2.setViewport(getCharacterPortrait(NULL))
                handle.p2.text = ""
                tension.p2.text = ""
                health.p2.text = ""
                rounds.p2.text = ""
                burst.p2.text = ""
                risc.p2.text = ""
                isHit.p2.text = ""
            }

        if (isInRange(m.getTension(P1), 0, 10000)) tension.p1.textFill = c("#84c928") else tension.p1.textFill = c("#d22e44")
        if (isInRange(m.getHealth(P1), 0, 420)) health.p1.textFill = c("#84c928") else health.p1.textFill = c("#d22e44")
        if (isInRange(m.getRisc(P1), -12800, 12800)) risc.p1.textFill = c("#84c928") else risc.p1.textFill = c("#d22e44")

        if (isInRange(m.getTension(P2), 0, 10000)) tension.p2.textFill = c("#84c928") else tension.p2.textFill = c("#d22e44")
        if (isInRange(m.getHealth(P2), 0, 420)) health.p2.textFill = c("#84c928") else health.p2.textFill = c("#d22e44")
        if (isInRange(m.getRisc(P2), -12800, 12800)) risc.p2.textFill = c("#84c928") else risc.p2.textFill = c("#d22e44")
    }

    private fun applyRandomData(m: Match) {
        wholeThing.opacity = 1.0
        timer.text = Random.nextInt(1, 99).toString()
        cabinet.text = m.getCabinetString(Random.nextInt(3))

        character.p1.setViewport(getCharacterPortrait(Random.nextInt(25)))
        handle.p1.text = getRandomName()
        tension.p1.text = m.getTensionString(P1)
        health.p1.text = m.getHealthString(P1)
        rounds.p1.text = m.getRoundsString(P1)
        burst.p1.text = m.getBurstString(P1)
        risc.p1.text = m.getRiscString(P1)
        isHit.p1.text = m.getHitStunString(P1)

        character.p2.setViewport(getCharacterPortrait(Random.nextInt(25)))
        handle.p2.text = getRandomName()
        tension.p2.text = m.getTensionString(P2)
        health.p2.text = m.getHealthString(P2)
        rounds.p2.text = m.getRoundsString(P2)
        burst.p2.text = m.getBurstString(P2)
        risc.p2.text = m.getRiscString(P2)
        isHit.p2.text = m.getHitStunString(P2)
    }

}