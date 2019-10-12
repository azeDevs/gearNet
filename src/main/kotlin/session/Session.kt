package session

import MyApp.Companion.WD
import application.LogText
import application.LogText.Effect.*
import application.log
import events.*
import memscan.FighterData
import memscan.MatchSnap
import tornadofx.Controller
import twitch.*
import utils.SessionMode
import utils.SessionMode.Mode.*
import utils.addCommas
import utils.getSeatLog

typealias L = LogText


class Session : Controller() {

    private val xrd = XrdHandler(this)
    private val bot = BotHandler(this)

    private val mode: SessionMode = SessionMode()
    private val stage: MatchStage = MatchStage(this)
    private val viewers: HashMap<Long, Viewer> = HashMap()
    private val fighters: HashMap<Long, Fighter> = HashMap()


    // MATCH STUFF
    fun stage() = stage
    fun updateMatch(matchSnap: MatchSnap) = stage.addSnap(matchSnap)

    // MODE STUFF
    fun isMode(vararg mode: SessionMode.Mode) = this.mode.isMode(*mode)
    fun update(mode: SessionMode.Mode) = this.mode.update(mode)

    // FIGHTER STUFF
    private fun addFighter(fighter: Fighter) { fighters[fighter.getId()] = fighter }
    fun fighters() = fighters.values.filter { it.isValid() }
    fun getFighter(id: Long) = fighters().firstOrNull{ it.getId() == id } ?: Fighter()
    fun updateFighter(fd: FighterData):Boolean {
        // Check if Fighter already exists, if not, create a new Fighter
        val fighter = fighters().firstOrNull{ it.getId() == fd.steamId } ?: Fighter(fd)
        // Does the Fighter Map already contain the Fighter
        val flag = fighters.containsKey(fighter.getId())
        // If the Fighter did exist, then update them with the new FighterData
        // Else add the new Fighter to the Fighter Map
        if (flag) fighter.update(fd)
        else addFighter(fighter)
        // Return whether or not a Fighter was updated
        return flag
    }

    // VIEWER STUFF
    private fun addViewer(viewer: Viewer) { viewers[viewer.getId()] = viewer }
    fun viewers() = viewers.values.filter { it.isValid() }
    fun getViewer(id:Long) = viewers().firstOrNull{ it.getId() == id } ?: Viewer()
    fun update(vd: ViewerData):Boolean {
        val viewer = viewers().firstOrNull { it.getId() == vd.twitchId } ?: Viewer(vd)
        val flag = fighters.containsKey(viewer.getId())
        if (flag) viewer.update(vd)
        else addViewer(viewer)
        return flag
    }

    init {
        subscribe<XrdConnectionEvent> { runXrdConnection(it) }
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

    private fun runXrdConnection(e: XrdConnectionEvent) {
        if (e.connected) log(L("Xrd", ORN), L(" has ", LOW), L("CONNECTED", GRN))
        else log(L("Xrd", ORN), L(" has ", LOW), L("DISCONNECTED", RED))
    }

    fun generateEvents() {
        // 001: Process FighterEvents
        xrd.generateFighterEvents()
        // ???: PROCESS ViewerEvents
        bot.generateViewerEvents()
    }

    private fun runViewerMessage(e: ViewerMessageEvent) {
        update(e.viewer.getData())
        log(L(e.viewer.getName(), CYA),
            L(" said: ", LOW),
            L("“${e.text}”"))
    }

    private fun runViewerJoined(e: ViewerJoinedEvent) {
        addViewer(e.viewer)
        log(L(e.viewer.getName(), CYA),
            L(" added to viewers map"))
    }

    private fun runCommandBet(e: CommandBetEvent) {
        if (stage.isMatchValid()) {
            val bet = ViewerBet(e.viewer)
            val sb = StringBuilder("Viewer ${e.viewer.getName()} bet ")
            if (bet.isValid()) {
                if (bet.getChips(0)>0) sb.append("${bet.getChips(0)}0% (${addCommas(bet.getWager(0))} $WD) on Red")
                if (bet.getChips(0)>0 && bet.getChips(1)>0) sb.append(" & ")
                if (bet.getChips(1)>0) sb.append("${bet.getChips(1)}0% (${addCommas(bet.getWager(1))} $WD) on Blue")
                log(sb.toString())
                stage.addBet(bet)
            }
        } else log("Viewer ${e.viewer.getName()} bet fizzled, betting is locked")
    }

    private fun runFighterJoined(e: FighterJoinedEvent) {
        log(L(e.fighter.getName(), YLW),
            L(" added to fighters map"),
            L(" [${e.fighter.getIdString()}]", LOW))
    }

    private fun runFighterMoved(e: FighterMovedEvent) {
        // FIXME: DOES NOT TRIGGER WHEN MOVING FROM SPECTATOR
        val destination = if (e.fighter.getCabinet() > 3) L( "off cabinet") else getSeatLog(e.fighter.getSeat())
        log(L(e.fighter.getName(), YLW), L(" moved to ", MED), destination)



        if (stage.isMatchValid() && e.fighter.justExitedStage()) stage.finalizeMatch()
        else if (!stage.isMatchValid()) stage.stageMatch()
    }

    private fun runMatchLoading(e: MatchLoadingEvent) {
        if (mode.get() != LOADING) {
            log(L("Match loading ... "), L(e.match.getFighter(0).getName(), RED),
                L(" vs "), L(e.match.getFighter(1).getName(), BLU))
        }
        update(LOADING)
    }

    private fun runRoundStarted(e: RoundStartedEvent) {
        update(MATCH)
        log(L("Round started ... "), L(e.match.getFighter(0).getName(), RED),
            L(" vs "), L(e.match.getFighter(1).getName(), BLU))
    }

    private fun runRoundResolved(e: RoundResolvedEvent) {
        update(SLASH)
        var winner = Fighter()
        if (e.match.getHealth(0) == 0 && e.match.getHealth(1) == 0) {
            log(L("Round resolved as a "), L("DRAW", YLW))
        } else {
            if (e.match.tookTheRound(0)) winner = e.match.getFighter(0)
            if (e.match.tookTheRound(1)) winner = e.match.getFighter(1)
            when {
                winner.getSeat() == 0 -> log(L("Round resolved, "), L(e.match.getFighter(0).getName(), RED), L(" wins"))
                winner.getSeat() == 1 -> log(L("Round resolved, "), L(e.match.getFighter(1).getName(), BLU), L(" wins"))
                else -> log(L("Round resolved with an "), L("ERROR", RED))
            }
        }
    }

    private fun runMatchResolved(e: MatchResolvedEvent) {
        if (isMode(LOADING)) update(LOBBY)
        else stage.finalizeMatch()
        val winner = e.match.getWinningFighter()
        var betBanner: Pair<String, String> = Pair("","")
        if (winner.isSeated(0)) betBanner = Pair("Red", RED_CHIP)
        if (winner.isSeated(1)) betBanner = Pair("Blue", BLU_CHIP)
        bot.sendMessage("${betBanner.first} ${winner.getName()} WINS!")
        log("Match resolved, ${betBanner.second} Fighter ${winner.getName()} is the winner.")
    }

    private fun runMatchConcluded(e: MatchConcludedEvent) {
        update(LOBBY)
    }

}



