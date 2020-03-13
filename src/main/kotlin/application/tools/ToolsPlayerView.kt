package application.tools

import MyApp.Companion.GHOST_OPACITY
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import models.Fighter
import session.Character.getCharacterTrademark
import session.Session
import tornadofx.*
import utils.addCommas
import utils.getRandomName
import utils.getRes
import kotlin.random.Random

class ToolsPlayerView(override val root: Parent) : Fragment() {

    private var wholeThing: HBox

    private lateinit var character: ImageView
    private lateinit var statusBar: HBox
    private lateinit var handle: Label
    private lateinit var status: Label

    private lateinit var bounty1: Label
    private lateinit var bounty2: Label

    private lateinit var chain1: Label
    private lateinit var chain2: Label

    private lateinit var change: Label

    private lateinit var record: Label
    private lateinit var cabinet: Label
    private lateinit var location: Label

    init { with(root) {
        wholeThing = hbox { addClass(ToolsPlayerStyle.playerContainer)
            minWidth = 400.0
            maxWidth = 400.0
            opacity = GHOST_OPACITY
            character = imageview(getRes("gn_atlas.png").toString()) {
                viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                translateY -= 2.0
                translateX -= 2.0
                fitWidth = 64.0
                fitHeight = 64.0
            }
            vbox {
                translateX -= 4.0
                minWidth = 340.0
                maxWidth = 340.0
                stackpane { alignment = Pos.CENTER_LEFT
                    statusBar = hbox { addClass(ToolsPlayerStyle.playerStatusBar)
                        translateY += 1.0
                        maxWidth = 0.0
                    }
                    hbox {
                        handle = label {
                            addClass(ToolsPlayerStyle.playerHandleText)
                            translateY += 1.0
                        }
                        status = label {
                            addClass(ToolsPlayerStyle.playerStatusText)
                            translateX -= 160.0
                            translateY += 4.0
                            maxWidth = 160.0
                            minWidth = 160.0
                        }
                    }
                }

                hbox {
                    vbox { addClass(ToolsPlayerStyle.playerBountyBackdrop)
                        translateY += 2.0
                        stackpane {
                            translateX += 10.0
                            translateY -= 5.0
                            bounty2 = label { addClass(ToolsPlayerStyle.playerBountyShadow)
                                scaleX += 0.05
                                scaleY += 0.20
                            }
                            bounty1 = label { addClass(ToolsPlayerStyle.playerBountyText) }
                        }
                    }
                    vbox {
                        stackpane {
                            translateX -= 64.0
                            chain2 = label { addClass(ToolsPlayerStyle.playerChainShadow)
                                translateY += 6.0
                                scaleY += 0.20
                            }
                            chain1 = label { addClass(ToolsPlayerStyle.playerChainText)
                                translateY += 6.0
                                scaleX -= 0.20
                            }
                        }
                    }
                    vbox { addClass(ToolsPlayerStyle.playerStatsBackdrop)
                        translateX -= 142.0
                        translateY += 2.0
                        record = label { addClass(ToolsPlayerStyle.playerRecordText) }
                        cabinet = label { addClass(ToolsPlayerStyle.playerRecordText) }
                        location = label { addClass(ToolsPlayerStyle.playerRecordText) }
                    }
                    vbox { padding = Insets(0.0,0.0,0.0,8.0)
                        translateX -= 525.0
                        translateY += 8.0
                        change = label { addClass(ToolsPlayerStyle.playerChangeText) }
                    }
                }
            }
        }
    } }

    fun applyData(p: Fighter, session: Session) = Platform.runLater {
        if (session.randomValues) applyRandomData(p) else
            if (p.getId() > 0L) {
                wholeThing.opacity = 1.0
                character.viewport = getCharacterTrademark(p.getData().characterId)

                handle.text = p.getName()
                if (p.isAbsent()) handle.textFill = c("#3befaa88")
                else handle.textFill = c("#3befaa")

                if (p.isAbsent()) statusBar.maxWidth = 0.0
                else statusBar.maxWidth = 335.0 * (p.getLoadPercent()*0.01)

                status.text = p.getBystandingString()

                bounty1.text = p.getScoreTotalString()
                if (p.isAbsent()) bounty1.textFill = c("#a95d29")
                else bounty1.textFill = c("#ffcc33")
                bounty2.text = p.getScoreTotalString()

                chain1.text = p.getRatingString()
                chain2.text = p.getRatingString()

                if (p.getScoreDelta() > 0) change.textFill = c("#84c928")
                else if (p.getScoreDelta() < 0) change.textFill = c("#d22e44")
                else change.textFill = c("#521833")
                change.text = p.getScoreDeltaString()

                record.text = p.getRecordString()
                cabinet.text = p.getCabinetString()
                location.text = p.getPlaySideString()

            } else {
                wholeThing.opacity = GHOST_OPACITY
                character.viewport = Rectangle2D(576.0, 192.0, 64.0, 64.0)
                handle.text = ""
                statusBar.maxWidth = 0.0
                status.text = ""
                bounty1.text = ""
                bounty2.text = ""
                chain1.text = ""
                chain2.text = ""
                change.text = ""
                record.text = ""
                cabinet.text = ""
                location.text = ""
            }
    }

    private fun applyRandomData(p: Fighter) {
        val bountyStr = addCommas(Random.nextInt(1222333).toString())
        val loadingInt = Random.nextInt(100)
        val changeInt = Random.nextInt(-444555, 666777)
        val chainInt = Random.nextInt(0, 9)
        val winsInt = Random.nextInt(44)
        val cabId = Random.nextInt(5)
        wholeThing.opacity = 1.0
        character.viewport = Rectangle2D(Random.nextInt(8) * 128.0, 512 + Random.nextInt(4) * 128.0, 128.0, 128.0)
//        character.viewport = Rectangle2D(Random.nextInt(8) * 64.0, Random.nextInt(4) * 64.0, 64.0, 64.0)
        handle.text = getRandomName()
        statusBar.maxWidth = 335.0 * (loadingInt * 0.01)
        bounty1.text = "$bountyStr W$"
        bounty2.text = "$bountyStr W$"
        chain1.text = if (chainInt >= 8) "★" else if (chainInt > 0) chainInt.toString() else ""
        chain2.text = if (chainInt >= 8) "★" else if (chainInt > 0) chainInt.toString() else ""
        if (changeInt > 0) change.textFill = c("#84c928")
        else if (changeInt < 0) change.textFill = c("#d22e44")
        else change.textFill = c("#e0af1a")
        change.text = p.getScoreDeltaString(1f, changeInt)
        record.text = "W:$winsInt  /  M:${winsInt + Random.nextInt(44)}"
        location.text = p.getPlaySideString(cabId, Random.nextInt(8))
        cabinet.text = p.getCabinetString(cabId)
        status.text = "Standby: ${Random.nextInt(1, 8)} [$loadingInt%]"
    }

}