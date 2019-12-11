package session.modes

import events.*
import session.Session

class ModeNull(override val s: Session) : Mode(s) {

    override fun toString(): String = "NULL${super.toString()}"

    override fun runMatchConcluded(e: MatchConcludedEvent) { }

    override fun runMatchResolved(e: MatchResolvedEvent) { logMode(this, "MatchResolvedEvent") }

    override fun runRoundDraw(e: RoundDrawEvent) { logMode(this, "RoundDrawEvent") }

    override fun runRoundResolved(e: RoundResolvedEvent) { logMode(this, "RoundResolvedEvent") }

    override fun runRoundStarted(e: RoundStartedEvent) { logMode(this, "RoundStartedEvent") }

    override fun runMatchLoading(e: MatchLoadingEvent) { logMode(this, "MatchLoadingEvent") }

    override fun runCommandBet(e: ViewerBetEvent) { logMode(this, "ViewerBetEvent") }

    override fun runFighterJoined(e: FighterJoinedEvent) {
        if (s.getFighters().isNotEmpty()) s.mode().update(ModeLobby(s))
        runFighterJoinedCommons(e)
    }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

    override fun runMatchUpdate(e: XrdMatchUpdateEvent) { }

}