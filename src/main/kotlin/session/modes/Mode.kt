package session.modes

import application.LogText.Effect.*
import application.log
import events.*
import session.L
import session.Session
import tornadofx.Controller
import utils.getSeatLog

abstract class Mode(open val s: Session) : Controller() {

    fun logMode(mode:Mode, text:String) {
        log(L(mode.toString(), MED), L(text, CYA))
    }
    override fun toString(): String = "MODE_"

    abstract fun runMatchConcluded(e: MatchConcludedEvent)
    abstract fun runMatchResolved(e: MatchResolvedEvent)
    abstract fun runRoundDraw(e: RoundDrawEvent)
    abstract fun runRoundResolved(e: RoundResolvedEvent)
    abstract fun runRoundStarted(e: RoundStartedEvent)
    abstract fun runMatchLoading(e: MatchLoadingEvent)
    abstract fun runCommandBet(e: ViewerBetEvent)

    fun runViewerJoined(e: ViewerJoinedEvent) {
        log(L("MODE_*", MED), L("ViewerJoinedEvent", CYA))
        s.addViewer(e.viewer)
        log(L(e.viewer.getName(), PUR_SNAP),
            L(" added to viewers map"))
    }

    fun runViewerMessage(e: ViewerMessageEvent) {
        log(L("MODE_*", MED), L("ViewerMessageEvent", CYA))
        s.updateViewer(e.viewer.getData())
        log(L(e.viewer.getName(), PUR_SNAP),
            L(" said: ", LOW),
            L("“${e.text}”"))
    }

    fun runFighterJoined(e: FighterJoinedEvent) {
        log(L("MODE_*", MED), L("FighterJoinedEvent", CYA))
        log(L(e.fighter.getName(), YLW),
            L(" added to fighters map"),
            L(" [${e.fighter.getIdString()}]", LOW))
    }

    fun runFighterMoved(e: FighterMovedEvent) {
        log(L("MODE_*", MED), L("FighterMovedEvent", CYA))
        // FIXME: DOES NOT TRIGGER WHEN MOVING FROM SPECTATOR
        val destination = if (e.fighter.getCabinet() > 3) L( "off cabinet") else getSeatLog(e.fighter.getSeat())
        log(L(e.fighter.getName(), YLW), L(" moved to ", MED), destination)
        if (s.stage().isMatchValid() && e.fighter.justExitedStage()) s.stage().finalizeMatch()
    }

    fun runXrdConnection(e: XrdConnectionEvent) {
        log(L("MODE_*", MED), L("XrdConnectionEvent", CYA))
        if (e.connected) log(L("Xrd", GRN), L(" has ", LOW), L("CONNECTED", GRN))
        else log(L("Xrd", GRN), L(" has ", LOW), L("DISCONNECTED", RED))
    }

}