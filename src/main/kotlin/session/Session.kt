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
    fun update(matchSnap: MatchSnap) = stage.addSnap(matchSnap)

    // MODE STUFF
    fun isMode(vararg mode: SessionMode.Mode) = this.mode.isMode(*mode)
    fun update(mode: SessionMode.Mode) = this.mode.update(mode)

    // FIGHTER STUFF
    fun fighters() = fighters.values
    private fun addFighter(fighter: Fighter) { fighters[fighter.getId()] = fighter }
    fun getFighter(id: Long) = fighters().firstOrNull{ it.getId() == id } ?: Fighter()
    fun getFighters(): List<Fighter> = fighters().filter { it.isValid() }
    fun update(fd: FighterData):Boolean {
        val fighter = fighters().firstOrNull{ it.getId() == fd.steamId } ?: Fighter(fd)
        val flag = getFighter(fighter.getId()).isValid() // Map contains Fighter?
        if (flag) fighter.update(fd)
        else addFighter(fighter)
        return flag
    }

    // VIEWER STUFF
    fun viewers() = viewers.values
    private fun addViewer(viewer: Viewer) { viewers[viewer.getId()] = viewer }
    private fun getViewer(id:Long) = viewers().firstOrNull{ it.getId() == id } ?: Viewer()
    fun update(vd: ViewerData):Boolean {
        val viewer = viewers().filter { it.isValid() }.firstOrNull { it.getId() == vd.twitchId } ?: Viewer(vd)
        val flag = getViewer(viewer.getId()).isValid() // Map contains Viewer?
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
        if (e.connected) log(L("Xrd", YLW), L(" has "), L("CONNECTED", GRN))
        else log(L("Xrd", YLW), L(" has "), L("DISCONNECTED", RED))
    }

    fun generateEvents() {
        logUpdateToGUI()
        xrd.generateFighterEvents() // PROCESS FighterEvents
        bot.generateViewerEvents() // PROCESS ViewerEvents
    }

    private fun runViewerMessage(e: ViewerMessageEvent) {
        update(e.viewer.getData())
        log(L(e.viewer.getName(), GRN),
            L(" said ", LOW),
            L(e.text))
    }

    private fun runViewerJoined(e: ViewerJoinedEvent) {
        addViewer(e.viewer)
        log(L("NEW Viewer "),
            L(e.viewer.getName(), GRN),
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
        log(L("Fighter "),
            L(e.fighter.getName(false), YLW),
            L(" added to fighters map"),
            L(" [${e.fighter.getIdString()}]", LOW))
    }

    private fun runFighterMoved(e: FighterMovedEvent) {
        // TODO: Make this use GRN for generic moves, YLW for seat 2, and RED/BLU for seats 0 and 1
        log(L("Fighter "),
            L(e.fighter.getName(false), YLW),
            L(" moved "),
            L(if (e.fighter.getCabinet() > 3) "off cabinet" else "to ${e.fighter.getSeatString()}, "),
            L(e.fighter.getCabinetString(), YLW))
        if (e.fighter.oldData().seatingId == 0 || e.fighter.oldData().seatingId == 1) stage.finalizeMatch()
    }

    private fun runMatchLoading(e: MatchLoadingEvent) {
        if (mode.get() != LOADING) {
            log(L("Match loading ...   "), L(e.match.getFighter(0).getName(), RED),
                L(" vs "), L(e.match.getFighter(1).getName(), BLU))
        }
        update(LOADING)
    }

    private fun runRoundStarted(e: RoundStartedEvent) {
        update(MATCH)
        log(L("Round started ...   "), L(e.match.getFighter(0).getName(), RED),
            L(" vs "), L(e.match.getFighter(1).getName(), BLU))
    }

    private fun runRoundResolved(e: RoundResolvedEvent) {
        update(SLASH)
        var winner = Fighter()
        if (e.match.getFighter(0).getDelta() == 0) winner = e.match.getFighter(1)
        if (e.match.getFighter(1).getDelta() == 0) winner = e.match.getFighter(0)
        when {
            winner.getSeat() == 0 -> log(L("Round resolved, "), L(e.match.getFighter(0).getName(), RED), L(" wins"))
            winner.getSeat() == 1 -> log(L("Round resolved, "), L(e.match.getFighter(1).getName(), BLU), L(" wins"))
            else -> log(L("Round resolved as a "), L("DRAW", YLW))
        }
    }

    private fun runMatchResolved(e: MatchResolvedEvent) {
        if (isMode(LOADING)) update(LOBBY)
        else {

            stage.finalizeMatch()
        }
        var winner = Fighter()
        var betBanner: Pair<String, String> = Pair("","")
        if (e.match.getFighter(0).getDelta() == 1) { winner = e.match.getFighter(0); betBanner = Pair("Red", RED_CHIP) }
        if (e.match.getFighter(1).getDelta() == 1) { winner = e.match.getFighter(1); betBanner = Pair("Blue", BLU_CHIP) }
        bot.sendMessage("${betBanner.first} ${winner.getName()} WINS!")
        log("Match resolved, ${betBanner.second} Fighter ${winner.getName()} is the winner.")
    }

    private fun runMatchConcluded(e: MatchConcludedEvent) {
        update(LOBBY)
    }


    private fun logUpdateToGUI() {
//        log("---- SESSION ----", "--------")
//        log("Session Mode", logic.getMode().name)
//        log("Total Fighters","${logic.getFighters().size}")
//        log("Total Viewers","${logic.getViewers().size}")
//        log("Total Matches","${logic.getStage().getMatches().size}")
//        log("---- MATCH ----", "--------")
//        log("Match Snaps", logic.getMatch().getSnaps().size)
//        log("Match Timer", "${logic.getMatch().getTimer()}")
//        log("Red Rounds", "${logic.getMatch().getRounds(0)}")
//        log("Red Health", "${logic.getMatch().getHealth(0)}")
//        log("Blu Rounds", "${logic.getMatch().getRounds(1)}")
//        log("Blu Health", "${logic.getMatch().getHealth(1)}")
//        log("---- VIEWERS ----", "--------")
//        log("Total Bets", logic.getMatch().getViewerBets().size)
//        log("Red Chips", logic.getMatch().getChips(0))
//        log("Blu Chips", logic.getMatch().getChips(1))
//        log("Red Wagers", logic.getMatch().getWagers(0))
//        log("Blu Wagers", logic.getMatch().getWagers(0))
//        log("---- FIGHTERS ----", "--------")
//        log("R Tension", "${logic.getMatch().getTension(0)}")
//        log("R Guard", "${logic.getMatch().getGuardGauge(0)}")
//        log("R Stunned", "${logic.getMatch().getStrikeStun(0)}")
//        log("R Burst", "${logic.getMatch().getCanBurst(0)}")
//        log("B Tension", "${logic.getMatch().getTension(1)}")
//        log("B Guard", "${logic.getMatch().getGuardGauge(1)}")
//        log("B Stunned", "${logic.getMatch().getStrikeStun(1)}")
//        log("B Burst", "${logic.getMatch().getCanBurst(1)}")
    }



}



