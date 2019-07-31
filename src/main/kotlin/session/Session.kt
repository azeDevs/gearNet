package session

import MyApp.Companion.WD
import events.EventType.*
import events.FighterEvent
import events.ViewerEvent
import events.XrdHandler
import tornadofx.Controller
import twitch.BLU_CHIP
import twitch.BotHandler
import twitch.RED_CHIP
import twitch.ViewerBet
import utils.addCommas
import utils.log


class Session : Controller() {

    private val state = SessionState()
    private val xrd = XrdHandler()
    private val bot = BotHandler()

    fun generateEvents() {
        logUpdateToGUI()
        // PROCESS FighterEvents
        xrd.generateFighterEvents(state).forEach {
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
        bot.generateViewerEvents(state).forEach {
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
        state.update(it.getData())
        log("${it.getName()} said “${it.getMessage()}”")
    }

    private fun runViewerJoined(it: ViewerEvent) {
        state.putViewer(it.getViewer())
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
        if (state.getMatch().isValid()) {
            val bet = ViewerBet(it.getViewer())
            val sb = StringBuilder("Viewer ${it.getName()} bet ")
            if (bet.isValid()) {
                if (bet.getChips(0)>0) sb.append("${bet.getChips(0)}0% (${addCommas(bet.getWager(0))} $WD) on Red")
                if (bet.getChips(0)>0 && bet.getChips(1)>0) sb.append(" & ")
                if (bet.getChips(1)>0) sb.append("${bet.getChips(1)}0% (${addCommas(bet.getWager(1))} $WD) on Blue")
                log(sb.toString())
                state.addBet(bet)
            }
        } else log("Viewer ${it.getName()} bet fizzled, betting is locked")
    }

    private fun runFighterJoined(it: FighterEvent) {
        log("NEW Fighter ${it.getName()} added to fighters map with Steam ID ${it.getId()}")
    }

    private fun runFighterMoved(it: FighterEvent) {
        log("Fighter ${it.getName()} moved ${if (it.get().getCabinet() > 3) "off cabinet" else "to ${it.get().getSeatString()}, ${it.get().getCabinetString()}"}")
        if (it.get().oldData().seatingId == 0 || it.get().oldData().seatingId == 1) state.getStage().finalizeMatch(state)
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
        if (state.getMode() != Mode.LOADING) log("NEW Match loading... ${it.get(0).getName()} as Red, and ${it.get(1).getName()} as Blue")
        state.update(Mode.LOADING)
    }

    private fun runRoundStarted(it: FighterEvent) {
        state.update(Mode.MATCH)
        log("Round started with ${it.get(0).getName()} as Red, and ${it.get(1).getName()} as Blue")
    }

    private fun runRoundResolved(it: FighterEvent) {
        state.update(Mode.SLASH)
        var winner = Fighter()
        if (it.getDelta(0) == 0) winner = it.get(1)
        if (it.getDelta(1) == 0) winner = it.get(0)
        log("Round resolved with ${winner.getName()} as the winner.")
    }

    private fun runMatchResolved(it: FighterEvent) {
        if (state.isMode(Mode.LOADING)) state.update(Mode.LOBBY)
        else {
            state.update(Mode.VICTORY)
            state.getStage().finalizeMatch(state)
        }
        var winner = Fighter()
        var betBanner: Pair<String, String> = Pair("","")
        if (it.getDelta(0) == 1) { winner = it.get(0); betBanner = Pair("Red", RED_CHIP) }
        if (it.getDelta(1) == 1) { winner = it.get(1); betBanner = Pair("Blue", BLU_CHIP) }
        bot.sendMessage("${betBanner.first} ${winner.getName()} WINS!")
        log("Match resolved, ${betBanner.second} Fighter ${winner.getName()} is the winner.")
    }

    private fun runMatchConcluded(it: FighterEvent) {
        state.update(Mode.LOBBY)
    }


    fun logUpdateToGUI() {
        log("---- SESSION ----", "--------")
        log("Session Mode", state.getMode().name)
        log("Total Fighters","${state.getFighters().size}")
        log("Total Viewers","${state.getViewers().size}")
        log("Total Matches","${state.getStage().getMatches().size}")
        log("---- MATCH ----", "--------")
        log("Match Snaps", state.getMatch().getSnaps().size)
        log("Match Timer", "${state.getMatch().getTimer()}")
        log("Red Rounds", "${state.getMatch().getRounds(0)}")
        log("Red Health", "${state.getMatch().getHealth(0)}")
        log("Blu Rounds", "${state.getMatch().getRounds(1)}")
        log("Blu Health", "${state.getMatch().getHealth(1)}")
        log("---- VIEWERS ----", "--------")
        log("Total Bets", state.getMatch().getViewerBets().size)
        log("Red Chips", state.getMatch().getChips(0))
        log("Blu Chips", state.getMatch().getChips(1))
        log("Red Wagers", state.getMatch().getWagers(0))
        log("Blu Wagers", state.getMatch().getWagers(0))
        log("---- FIGHTERS ----", "--------")
        log("R Tension", "${state.getMatch().getTension(0)}")
        log("R Guard", "${state.getMatch().getGuardGauge(0)}")
        log("R Stunned", "${state.getMatch().getStrikeStun(0)}")
        log("R Burst", "${state.getMatch().getCanBurst(0)}")
        log("B Tension", "${state.getMatch().getTension(1)}")
        log("B Guard", "${state.getMatch().getGuardGauge(1)}")
        log("B Stunned", "${state.getMatch().getStrikeStun(1)}")
        log("B Burst", "${state.getMatch().getCanBurst(1)}")
    }

    enum class Mode {
        NULL,
        LOBBY,
        LOADING,
        MATCH,
        SLASH,
        VICTORY
    }

}



