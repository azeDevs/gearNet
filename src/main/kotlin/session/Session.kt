package session

import BLU_BANNER
import RED_BANNER
import WD
import events.EventType.*
import events.FighterEvent
import events.ViewerEvent
import events.XrdHandler
import models.Fighter
import session.SessionMode.*
import tornadofx.Controller
import twitch.BotHandler
import utils.addCommas
import utils.log


class Session : Controller() {

    private val state = SessionState()
    private val xrd = XrdHandler()
    private val bot = BotHandler()

    fun logUpdateToGUI() {
        log("sessionMode", state.getMode().name)
        log("totalFighters","${state.getFighters().size}")
        log("totalViewers","${state.getViewers().size}")
    }

    fun generateEvents() {
        logUpdateToGUI()
        // PROCESS FighterEvents
        xrd.generateFighterEvents(state).forEach { state.update(it)
            when (it.getType()) {
                NULL_EVENT -> false
                XRD_CONNECTED -> log("XrdApi connected")
                XRD_DISCONNECT -> log("XrdApi disconnected")
                FIGHTER_JOINED -> runFighterJoined(it)
                FIGHTER_MOVED -> runFighterMoved(it)

                MATCH_LOADING -> runMatchLoading(it)
                MATCH_RESOLVED -> runMatchResolved(it)
                ROUND_STARTED -> runRoundStarted(it)
                ROUND_RESOLVED -> runRoundResolved(it)
                MATCH_CONCLUDED -> runMatchConcluded(it)

                BURST_ENABLED -> runBurstEnabled(it)
                STRIKE_STUNNED -> runStrikeStunned(it)
                DAMAGE_DEALT -> runDamageDealt(it)
            }
        }

        // PROCESS ViewerEvents
        bot.generateViewerEvents(state).forEach { state.update(it)
            when (it.getType()) {
                NULL_EVENT -> false
                VIEWER_JOINED -> runViewerJoined(it)
                VIEWER_MESSAGE -> runViewerMessage(it)

                COMMAND_BET -> runCommandBet(it)
                COMMAND_HELP -> runCommandHelp(it)
                COMMAND_WALLET -> runCommandWallet(it)
            }
        }

    }

    private fun runViewerMessage(it: ViewerEvent) {
        log("${it.getName()} said “${it.getMessage()}”")
    }

    private fun runViewerJoined(it: ViewerEvent) {
        log("NEW Viewer ${it.getName()} added to viewers map")
    }

    private fun runCommandWallet(it: ViewerEvent) {
        log("!WALLET command from ${it.getName()} initiated")
        bot.sendMessage("${it.getName()} initiated !WALLET")
    }

    private fun runCommandHelp(it: ViewerEvent) {
        log("!HELP command from ${it.getName()} initiated")
        bot.sendMessage("${it.getName()} initiated !HELP")
    }

    private fun runCommandBet(it: ViewerEvent) {
        if (it.getBetAmount() > 0) {
            bot.sendMessage("${addCommas(it.getBetAmount())} \uD835\uDE86\$ ${it.getBetBanner().first} ${it.getName()}")
            log("!BET command from ${it.getName()}, ${addCommas(it.getBetAmount())} $WD on ${it.getBetBanner().second}, initiated")
        } else log("!BET command from ${it.getName()} failed to initiate, invalid amount")
    }

    private fun runFighterJoined(it: FighterEvent) {
        log("NEW Fighter ${it.getName()} added to fighters map")
    }

    private fun runFighterMoved(it: FighterEvent) {
        log("Fighter ${it.getName()} moved ${
        if (it.get().getCabinet() > 3) "off cabinet" 
        else "to ${it.get().getSeatString()
        }, ${it.get().getCabinetString()}"}")
    }

    private fun runDamageDealt(it: FighterEvent) {
        log(it.getType().name)
    }

    private fun runStrikeStunned(it: FighterEvent) {
        log(it.getType().name)
    }

    private fun runBurstEnabled(it: FighterEvent) {
        log(it.getType().name)
    }

    private fun runMatchLoading(it: FighterEvent) {
        if (state.getMode() != MODE_LOADING) log("NEW Match loading... ${it.get(0).getName()} as Red, and ${it.get(1).getName()} as Blue")
        state.updateMode(MODE_LOADING)
    }

    private fun runRoundStarted(it: FighterEvent) {
        state.updateMode(MODE_MATCH)
        log("Round started with ${it.get(0).getName()} as Red, and ${it.get(1).getName()} as Blue")
    }

    private fun runRoundResolved(it: FighterEvent) {
        state.updateMode(MODE_SLASH)
        var winner = Fighter()
        if (it.getDelta(0) == 0) winner = it.get(1)
        if (it.getDelta(1) == 0) winner = it.get(0)
        log("Round resolved with ${winner.getName()} as the winner.")
    }

    private fun runMatchResolved(it: FighterEvent) {
        if (state.isMode(MODE_LOADING)) state.updateMode(MODE_LOBBY)
        else state.updateMode(MODE_VICTORY)
        var winner = Fighter()
        var betBanner: Pair<String, String> = Pair("","")
        if (it.getDelta(0) == 1) { winner = it.get(0); betBanner = RED_BANNER }
        if (it.getDelta(1) == 1) { winner = it.get(1); betBanner = BLU_BANNER }
        bot.sendMessage("${betBanner.first} ${winner.getName()} WINS!")
        log("Match resolved, ${betBanner.second} Fighter ${winner.getName()} is the winner.")
    }

    private fun runMatchConcluded(it: FighterEvent) {
        state.updateMode(MODE_LOBBY)
    }

}



