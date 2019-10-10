package session

import MyApp.Companion.WD
import application.LogText
import application.LogText.Effect.*
import application.log
import events.*
import tornadofx.Controller
import twitch.BLU_CHIP
import twitch.BotHandler
import twitch.RED_CHIP
import twitch.ViewerBet
import utils.addCommas

typealias L = LogText


class Session : Controller() {

    private val state = SessionState()
    private val xrd = XrdHandler(this)
    private val bot = BotHandler(this)

    init {
        subscribe<ViewerMessageEvent> { runViewerMessage(it) }
        subscribe<ViewerJoinedEvent> { runViewerJoined(it) }
        subscribe<CommandBetEvent> { runCommandBet(it) }
        subscribe<FighterJoinedEvent> { runFighterJoined(it) }
        subscribe<FighterMovedEvent> { runFighterMoved(it) }
        subscribe<MatchLoadingEvent> { runMatchLoading(it) }
        subscribe<RoundStartedEvent> { runRoundStarted(it) }
        subscribe<RoundResolvedEvent> { runRoundResolved(it) }
        subscribe<MatchResolvedEvent> { runMatchResolved(it) }
        subscribe<MatchConcludedEvent> { runMatchConcluded(it) }
    }

    fun generateEvents() {
        logUpdateToGUI()
        // PROCESS FighterEvents
        xrd.generateFighterEvents()

        // PROCESS ViewerEvents
        bot.generateViewerEvents()

    }

    private fun runViewerMessage(e: ViewerMessageEvent) {
        state.update(e.viewer.getData())
        log(L(e.viewer.getName(), GRN), L(" said ", LOW), L(e.text))
    }

    private fun runViewerJoined(e: ViewerJoinedEvent) {
        state.putViewer(e.viewer)
        log(L("NEW Viewer "), L(e.viewer.getName(), GRN), L(" added to viewers map"))
    }

    private fun runCommandBet(e: CommandBetEvent) {
        if (state.getMatch().isValid()) {
            val bet = ViewerBet(e.viewer)
            val sb = StringBuilder("Viewer ${e.viewer.getName()} bet ")
            if (bet.isValid()) {
                if (bet.getChips(0)>0) sb.append("${bet.getChips(0)}0% (${addCommas(bet.getWager(0))} $WD) on Red")
                if (bet.getChips(0)>0 && bet.getChips(1)>0) sb.append(" & ")
                if (bet.getChips(1)>0) sb.append("${bet.getChips(1)}0% (${addCommas(bet.getWager(1))} $WD) on Blue")
                log(sb.toString())
                state.addBet(bet)
            }
        } else log("Viewer ${e.viewer.getName()} bet fizzled, betting is locked")
    }

    private fun runFighterJoined(e: FighterJoinedEvent) {
        log(L("Fighter "), L(e.fighter.getName(false), BLU), L(" added to fighters map"), L(" [${e.fighter.getIdString()}]", LOW))
    }

    private fun runFighterMoved(e: FighterMovedEvent) {
        log(L("Fighter "), L(e.fighter.getName(false), BLU), L(" moved "), L(if (e.fighter.getCabinet() > 3) "off cabinet" else "to ${e.fighter.getSeatString()}, "), L(e.fighter.getCabinetString(), YLW))
        if (e.fighter.oldData().seatingId == 0 || e.fighter.oldData().seatingId == 1) state.getStage().finalizeMatch(state)
    }

    private fun runMatchLoading(e: MatchLoadingEvent) {
//        if (state.getMode() != Mode.LOADING) log("NEW Match loading... ${e.match.getFighter(0).getName()} as Red, and ${e.match.getFighter(1).getName()} as Blue")
        state.update(Mode.LOADING)
    }

    private fun runRoundStarted(e: RoundStartedEvent) {
        state.update(Mode.MATCH)
        log(L("Round started with "), L(e.match.getFighter(0).getName(), RED), L(" as Red, and "), L(e.match.getFighter(1).getName()), L(" as Blue", BLU))
    }

    private fun runRoundResolved(e: RoundResolvedEvent) {
        state.update(Mode.SLASH)
        var winner = Fighter()
        if (e.match.getFighter(0).getDelta() == 0) winner = e.match.getFighter(1)
        if (e.match.getFighter(1).getDelta() == 0) winner = e.match.getFighter(0)
        when {
            winner.getSeat() == 0 -> log(L("Round resolved with "), L(e.match.getFighter(0).getName(), RED))
            winner.getSeat() == 1 -> log(L("Round resolved with "), L(e.match.getFighter(1).getName(), BLU))
            else -> log(L("Round resolved with no winner", RED))
        }
    }

    private fun runMatchResolved(e: MatchResolvedEvent) {
        if (state.isMode(Mode.LOADING)) state.update(Mode.LOBBY)
        else {
            state.update(Mode.VICTORY)
            state.getStage().finalizeMatch(state)
        }
        var winner = Fighter()
        var betBanner: Pair<String, String> = Pair("","")
        if (e.match.getFighter(0).getDelta() == 1) { winner = e.match.getFighter(0); betBanner = Pair("Red", RED_CHIP) }
        if (e.match.getFighter(1).getDelta() == 1) { winner = e.match.getFighter(1); betBanner = Pair("Blue", BLU_CHIP) }
        bot.sendMessage("${betBanner.first} ${winner.getName()} WINS!")
        log("Match resolved, ${betBanner.second} Fighter ${winner.getName()} is the winner.")
    }

    private fun runMatchConcluded(e: MatchConcludedEvent) {
        state.update(Mode.LOBBY)
    }

    fun state() = state

    enum class Mode {
        NULL,
        LOBBY,
        LOADING,
        MATCH,
        SLASH,
        VICTORY
    }

    private fun logUpdateToGUI() {
//        log("---- SESSION ----", "--------")
//        log("Session Mode", state.getMode().name)
//        log("Total Fighters","${state.getFighters().size}")
//        log("Total Viewers","${state.getViewers().size}")
//        log("Total Matches","${state.getStage().getMatches().size}")
//        log("---- MATCH ----", "--------")
//        log("Match Snaps", state.getMatch().getSnaps().size)
//        log("Match Timer", "${state.getMatch().getTimer()}")
//        log("Red Rounds", "${state.getMatch().getRounds(0)}")
//        log("Red Health", "${state.getMatch().getHealth(0)}")
//        log("Blu Rounds", "${state.getMatch().getRounds(1)}")
//        log("Blu Health", "${state.getMatch().getHealth(1)}")
//        log("---- VIEWERS ----", "--------")
//        log("Total Bets", state.getMatch().getViewerBets().size)
//        log("Red Chips", state.getMatch().getChips(0))
//        log("Blu Chips", state.getMatch().getChips(1))
//        log("Red Wagers", state.getMatch().getWagers(0))
//        log("Blu Wagers", state.getMatch().getWagers(0))
//        log("---- FIGHTERS ----", "--------")
//        log("R Tension", "${state.getMatch().getTension(0)}")
//        log("R Guard", "${state.getMatch().getGuardGauge(0)}")
//        log("R Stunned", "${state.getMatch().getStrikeStun(0)}")
//        log("R Burst", "${state.getMatch().getCanBurst(0)}")
//        log("B Tension", "${state.getMatch().getTension(1)}")
//        log("B Guard", "${state.getMatch().getGuardGauge(1)}")
//        log("B Stunned", "${state.getMatch().getStrikeStun(1)}")
//        log("B Burst", "${state.getMatch().getCanBurst(1)}")
    }

}



