package session.modes

import events.*
import session.L
import session.Session
import tornadofx.Controller
import utils.getSeatLog
import views.logging.LogText.Effect.*
import views.logging.log

abstract class Mode(open val s: Session) : Controller() {

    private var stepNumber = 0
    fun step(): Int = stepNumber
    fun nextStep() { stepNumber++ }

    fun logMode(mode:Mode, text:String) = log(
        L(mode.toString(), CYA),
        L(" [ ", LOW),
        L(text, CYA),
        L(" ] ", LOW)
    )
    override fun toString(): String = "$stepNumber"

    abstract fun runMatchConcluded(e: MatchConcludedEvent)
    abstract fun runMatchResolved(e: MatchResolvedEvent)
    abstract fun runRoundDraw(e: RoundDrawEvent)
    abstract fun runRoundResolved(e: RoundResolvedEvent)
    abstract fun runRoundStarted(e: RoundStartedEvent)
    abstract fun runMatchLoading(e: MatchLoadingEvent)
    abstract fun runCommandBet(e: ViewerBetEvent)
    abstract fun runFighterJoined(e: FighterJoinedEvent)
    abstract fun runViewerJoined(e: ViewerJoinedEvent)
    abstract fun runViewerMessage(e: ViewerMessageEvent)
    abstract fun runFighterMoved(e: FighterMovedEvent)
    abstract fun runMatchUpdate(e: XrdMatchUpdateEvent)

    // TODO: THIS RUNS FOREVER AND SHOULD ONLY RUN ONCE Æ
    fun runRoundStartedCommons(e: RoundStartedEvent) {
        s.mode().update(ModeMatch(s))
        val round = "Round ${e.match.getRoundNumber()}"
        log(
            e.match.getIdLog(),
            L(" "),
            L(round, YLW_FIGHT),
            L(" started ...", CYA),
            e.match.getMatchLog()
        )
         //NOTE: IS THIS GETTING RESET SOMEWHERE DURING THE INTROS?
    }

    fun runFighterJoinedCommons(e: FighterJoinedEvent) {
        log(
            L(e.fighter.getName(), YLW_FIGHT),
            L(" added to Fighter map"),
            L(" ${e.fighter.getIdString()}", LOW)
        )
    }

    fun runViewerJoinedCommons(e: ViewerJoinedEvent) {
        s.addViewer(e.viewer)
        log(
            L(e.viewer.getName(), PUR_SNAP),
            L(" added to Viewer map")
        )
    }

    fun runViewerMessageCommons(e: ViewerMessageEvent) {
        s.updateViewer(e.viewer.getData())
        log(
            L(e.viewer.getName(), PUR_SNAP),
            L(" said: ", LOW),
            L("“${e.text}”")
        )
    }

    fun runFighterMovedCommons(e: FighterMovedEvent) {
        // FIXME: DOES NOT TRIGGER WHEN MOVING FROM SPECTATOR
        val destination = if (e.fighter.getCabinet() > 3) L( "off cabinet") else getSeatLog(e.fighter.getSeat())
        log(
            L(e.fighter.getName(), YLW_FIGHT),
            L(" moved to ", MED),
            destination
        )
        if (s.stage().isMatchValid() && e.fighter.justExitedStage()) s.stage().finalizeMatch()
    }

    fun runXrdConnection(e: XrdConnectionEvent) {
//        log(L("MODE_*", MED), L("XrdConnectionEvent", CYA))
        if (e.connected) log(
            L("Xrd", GRN),
            L(" has ", LOW),
            L("CONNECTED", GRN)
        )
        else log(L("Xrd", GRN), L(" has ", LOW), L("DISCONNECTED", RED))
    }

}