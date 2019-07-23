package application.tools

import MyApp.Companion.GHOST_OPACITY
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import models.Match
import session.Session
import tornadofx.*
import utils.Duo
import utils.getRes

/*
    data class MatchData(
        val tension: Pair<Int, Int> = Pair(-1,-1),
        val health: Pair<Int, Int> = Pair(-1,-1),
        val burst: Pair<Boolean, Boolean> = Pair(false,false),
        val risc: Pair<Int, Int> = Pair(-1,-1),
        val isHit: Pair<Boolean, Boolean> = Pair(false,false)
    )
*/
class ToolsMatchView(override val root: Parent) : Fragment() {

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
                    addClass(ToolsMatchStyle.matchTitle)
                    translateY -= 2.2
                    scaleX += 8.8
                    scaleY += 8.8
                    opacity = 0.25
                }
                cabinet = label {
                    addClass(ToolsMatchStyle.matchTitle)
                    translateY -= 45.0
                }
                hbox {
                    addClass(ToolsMatchStyle.matchContainer)

                    vbox { addClass(ToolsMatchStyle.sidestatsContainer)
                        translateY += 35.0
                        hbox {
                            character.f1 = imageview(getRes("gn_atlas.png").toString()) {
                                viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                                fitHeight = 32.0
                                fitWidth = 32.0
                                translateX -= 3.0
                                translateY -= 2.0
                            }
                            vbox {
                                handle.f1 = label().addClass(ToolsMatchStyle.matchPlayerTitle)
                                hbox {
                                    health.f1 = label().addClass(ToolsMatchStyle.demoText)
                                    rounds.f1 = label { addClass(ToolsMatchStyle.demoText); translateX -= 20.0 }
                                }
                            }
                        }
                        hbox{ translateY += 6.0
                            risc.f1 = label().addClass(ToolsMatchStyle.demoText)
                            isHit.f1 = label().addClass(ToolsMatchStyle.demoText)
                        }
                        hbox { translateY += 6.0
                            tension.f1 = label().addClass(ToolsMatchStyle.demoText)
                            burst.f1 = label().addClass(ToolsMatchStyle.demoText)
                        }
                    }
                    vbox { addClass(ToolsMatchStyle.sidestatsContainer)
                        translateY += 35.0
                        translateX += 4.0
                        hbox {
                            character.f2 = imageview(getRes("gn_atlas.png").toString()) {
                                viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                                fitHeight = 32.0
                                fitWidth = 32.0
                                translateX -= 3.0
                                translateY -= 2.0
                            }
                            vbox {
                                handle.f2 = label().addClass(ToolsMatchStyle.matchPlayerTitle)
                                hbox { //translateY += 6.0
                                    health.f2 = label().addClass(ToolsMatchStyle.demoText)
                                    rounds.f2 = label { addClass(ToolsMatchStyle.demoText); translateX -= 20.0 }
                                }
                            }
                        }
                        hbox{ translateY += 6.0
                            risc.f2 = label().addClass(ToolsMatchStyle.demoText)
                            isHit.f2 = label().addClass(ToolsMatchStyle.demoText)
                        }
                        hbox { translateY += 6.0
                            tension.f2 = label().addClass(ToolsMatchStyle.demoText)
                            burst.f2 = label().addClass(ToolsMatchStyle.demoText)
                        }
                    }
                }
            }
        }
    }

    fun applyMatch(m: Match, session: Session) = Platform.runLater {
//            if (m.matchId != -1L) {
//                wholeThing.opacity = 1.0
//                timer.text = m.getTimer().toString()
//                cabinet.text = m.getCabinetString()
//
//                character.f1.setViewport(getCharacterTrademark(m.getCharacter(P1)))
//                handle.f1.text = m.getHandleString(P1)
//                tension.f1.text = m.getTensionString(P1)
//                health.f1.text = m.getHealthString(P1)
//                rounds.f1.text = m.getRoundsString(P1)
//                burst.f1.text = m.getBurstString(P1)
//                risc.f1.text = m.getRiscString(P1)
//                isHit.f1.text = m.getHitStunString(P1)
//
//                character.f2.setViewport(getCharacterPortrait(m.getCharacter(P2)))
//                handle.f2.text = m.getHandleString(P2)
//                tension.f2.text = m.getTensionString(P2)
//                health.f2.text = m.getHealthString(P2)
//                rounds.f2.text = m.getRoundsString(P2)
//                burst.f2.text = m.getBurstString(P2)
//                risc.f2.text = m.getRiscString(P2)
//                isHit.f2.text = m.getHitStunString(P2)
//            } else {
//                wholeThing.opacity = GHOST_OPACITY
//                timer.text = ""
//                cabinet.text = ""
//
//                character.f1.setViewport(getCharacterPortrait(NULL))
//                handle.f1.text = ""
//                tension.f1.text = ""
//                health.f1.text = ""
//                rounds.f1.text = ""
//                burst.f1.text = ""
//                risc.f1.text = ""
//                isHit.f1.text = ""
//
//                character.f2.setViewport(getCharacterPortrait(NULL))
//                handle.f2.text = ""
//                tension.f2.text = ""
//                health.f2.text = ""
//                rounds.f2.text = ""
//                burst.f2.text = ""
//                risc.f2.text = ""
//                isHit.f2.text = ""
//            }
//
//        if (isInRange(m.getTension(P1), 0, 10000)) tension.f1.textFill = c("#84c928") else tension.f1.textFill = c("#d22e44")
//        if (isInRange(m.getHealth(P1), 0, 420)) health.f1.textFill = c("#84c928") else health.f1.textFill = c("#d22e44")
//        if (isInRange(m.getRisc(P1), -12800, 12800)) risc.f1.textFill = c("#84c928") else risc.f1.textFill = c("#d22e44")
//
//        if (isInRange(m.getTension(P2), 0, 10000)) tension.f2.textFill = c("#84c928") else tension.f2.textFill = c("#d22e44")
//        if (isInRange(m.getHealth(P2), 0, 420)) health.f2.textFill = c("#84c928") else health.f2.textFill = c("#d22e44")
//        if (isInRange(m.getRisc(P2), -12800, 12800)) risc.f2.textFill = c("#84c928") else risc.f2.textFill = c("#d22e44")
    }

}