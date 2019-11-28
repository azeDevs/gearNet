package session

import MyApp.Companion.WD
import application.LogText
import application.LogText.Effect.*
import application.log
import events.*
import memscan.FighterData
import memscan.MatchSnap
import tornadofx.Controller
import twitch.BotHandler
import twitch.Viewer
import twitch.ViewerBet
import twitch.ViewerData
import utils.SessionMode
import utils.SessionMode.Mode.*
import utils.addCommas
import utils.getIdStr
import utils.getSeatLog

typealias L = LogText


class Session : Controller() {

    private val xrd = XrdHandler(this)
    private val bot = BotHandler(this)

    private val mode: SessionMode = SessionMode()
    private val stage: MatchStage = MatchStage(this)
    private val viewers: HashMap<Long, Viewer> = HashMap()
//    private val fighters: ObservableMap<Long, Fighter> = FXCollections.emptyObservableMap()
    private val fighters: HashMap<Long, Fighter> = HashMap()


    fun getStagedFighters(): Pair<Fighter, Fighter> = Pair(stage.match().getFighter(0), stage.match().getFighter(1))


    // MATCH STUFF
    fun stage() = stage
    fun updateMatch(matchSnap: MatchSnap) = stage.addSnap(matchSnap)

    // MODE STUFF
    fun getMode(): SessionMode = mode
    fun isMode(vararg mode: SessionMode.Mode) = this.mode.isMode(*mode)
    fun updateMode(mode: SessionMode.Mode) = this.mode.update(mode)

    // FIGHTER STUFF
    private fun addFighter(fighter: Fighter) { fighters[fighter.getId()] = fighter }
    fun fighters() = fighters.values.filter { it.isValid() }
    fun getFighter(id: Long) = fighters().firstOrNull{ it.getId() == id } ?: Fighter()
    fun updateFighter(fd: FighterData):Boolean {
        // Check if Fighter already exists, if not, create a new Fighter
        val fighter = fighters().firstOrNull{ it.getId() == fd.steamId() } ?: Fighter(fd)
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
    private fun update(vd: ViewerData):Boolean {
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
        if (e.connected) log(L("Xrd", GRN), L(" has ", LOW), L("CONNECTED", GRN))
        else log(L("Xrd", GRN), L(" has ", LOW), L("DISCONNECTED", RED))
    }

    fun generateEvents() {
        xrd.generateFighterEvents()
        bot.generateViewerEvents()
        stage.stageMatch()
    }

    private fun runViewerMessage(e: ViewerMessageEvent) {
        update(e.viewer.getData())
        log(L(e.viewer.getName(), PUR_SNAP),
            L(" said: ", LOW),
            L("“${e.text}”"))
    }

    private fun runViewerJoined(e: ViewerJoinedEvent) {
        addViewer(e.viewer)
        log(L(e.viewer.getName(), PUR_SNAP),
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
    }

    private fun runMatchLoading(e: MatchLoadingEvent) {
        if (mode.get() != LOADING) {
            log(L("Match ${getIdStr(e.match.getId())}", TOX), L(" loading ... "), L(e.match.getFighter(0).getName(), RED),
                L(" vs ", MED), L(e.match.getFighter(1).getName(), BLU))
        }
        updateMode(LOADING)
    }

    private fun runRoundStarted(e: RoundStartedEvent) {
        updateMode(MATCH)
        val round = "Round ${e.match.getRoundNumber()}"
        log(L("Match ${getIdStr(e.match.getId())} ", TOX), L(round, YLW), L(" started ... ", CYA))
    }

    private fun runRoundResolved(e: RoundResolvedEvent) {
        updateMode(SLASH)
        var winner = Fighter()
        val round = "Round ${e.match.getRoundNumber()}"
        if (e.match.getHealth(0) == 0 && e.match.getHealth(1) == 0) log(L(round, YLW), L(" goes to "), L("DRAW", YLW))
        else {
            if (e.match.tookTheRound(0)) winner = e.match.getFighter(0)
            else if (e.match.tookTheRound(1)) winner = e.match.getFighter(1)
            when {
                winner.getSeat() == 0 -> log(L(round, YLW), L(" goes to "), L(e.match.getFighter(0).getName(), RED))
                winner.getSeat() == 1 -> log(L(round, YLW), L(" goes to "), L(e.match.getFighter(1).getName(), BLU))
                else -> log(L(round, YLW), L(" goes to "), L("ERROR", RED))
            }
        }
    }

    private fun runMatchResolved(e: MatchResolvedEvent) {
        if (isMode(LOADING)) updateMode(LOBBY)
        else if (!isMode(VICTORY) && e.match.isResolved()) {
            stage.finalizeMatch()
            val winner = e.match.getWinningFighter()
            bot.sendMessage("${winner.getName()} WINS!")
            log(L(" FINALIZED: ", GRN), L("${e.match.getSnapCount()}"), L(" snaps, ", YLW), L(e.match.getFighter(0).getName(), RED), L(" wins"))
            updateMode(LOBBY)
        }
    }

    private fun runMatchConcluded(e: MatchConcludedEvent) = updateMode(LOBBY)

}



