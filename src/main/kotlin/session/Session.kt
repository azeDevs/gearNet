package session

import events.*
import memscan.FighterData
import memscan.MatchSnap
import session.modes.Mode
import session.modes.ModeVictory
import session.modes.SessionMode
import tornadofx.Controller
import twitch.BotEventHandler
import twitch.Viewer
import twitch.ViewerData
import views.logging.LogText

typealias L = LogText


class Session : Controller() {

    private val xrd = XrdEventHandler(this)
    private val bot = BotEventHandler(this)

    private val mode: SessionMode = SessionMode(this)
    private val stage: MatchStage = MatchStage(this)
    private val viewers: HashMap<Long, Viewer> = HashMap()
    private val fighters: HashMap<Long, Fighter> = HashMap()

    init {
        subscribe<XrdConnectionEvent> { mode.get().runXrdConnection(it) }
        subscribe<XrdMatchUpdateEvent> { mode.get().runMatchUpdate(it) }
        subscribe<ViewerMessageEvent> { mode.get().runViewerMessage(it) }
        subscribe<ViewerJoinedEvent> { mode.get().runViewerJoined(it) }
        subscribe<FighterJoinedEvent> { mode.get().runFighterJoined(it) }
        subscribe<FighterMovedEvent> { mode.get().runFighterMoved(it) }
        subscribe<MatchLoadingEvent> { mode.get().runMatchLoading(it) }
        subscribe<RoundStartedEvent> { mode.get().runRoundStarted(it) }
        subscribe<RoundResolvedEvent> { mode.get().runRoundResolved(it) }
        subscribe<RoundDrawEvent> { mode.get().runRoundDraw(it) }
        subscribe<MatchResolvedEvent> { mode.get().runMatchResolved(it) }
        subscribe<MatchConcludedEvent> { mode.get().runMatchConcluded(it) }
    }

    fun generateEvents() {
        xrd.generateFighterEvents()
        bot.generateViewerEvents()
        stage.stageMatch()
    }

    // MATCH STUFF
    fun stage() = stage
    fun updateMatch(matchSnap: MatchSnap) = if (mode.isMode(ModeVictory(this))) false else stage.addSnap(matchSnap)
    fun getStagedFighters(): Pair<Fighter, Fighter> = Pair(stage.match().fighter(0), stage.match().fighter(1))

    // MODE STUFF
    fun mode(): SessionMode = mode
    fun isMode(vararg mode: Mode) = this.mode.isMode(*mode)

    // FIGHTER STUFF
    fun addFighter(fighter: Fighter) { fighters[fighter.getId()] = fighter }
    fun getFighters() = fighters.values.filter { it.isValid() }
    fun getFighter(id: Long) = getFighters().firstOrNull{ it.getId() == id } ?: Fighter()
    fun updateFighter(fd: FighterData):Boolean {
        // Check if Fighter already exists, if not, create a new Fighter
        val fighter = getFighters().firstOrNull{ it.getId() == fd.steamId() } ?: Fighter(fd)
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
    fun sendMessage(message: String) = bot.sendMessage(message)
    fun addViewer(viewer: Viewer) { viewers[viewer.getId()] = viewer }
    fun getViewers() = viewers.values.filter { it.isValid() }
    fun getViewer(id:Long) = getViewers().firstOrNull{ it.getId() == id } ?: Viewer()
    fun updateViewer(vd: ViewerData):Boolean {
        val viewer = getViewers().firstOrNull { it.getId() == vd.twitchId } ?: Viewer(vd)
        val flag = fighters.containsKey(viewer.getId())
        if (flag) viewer.update(vd)
        else addViewer(viewer)
        return flag
    }

}



