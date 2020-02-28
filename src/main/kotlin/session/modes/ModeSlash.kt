package session.modes

import events.*
import session.Session

class ModeSlash(override val s: Session) : Mode(s) {

    override fun toString(): String = "SLASH${super.toString()}"

    override fun runMatchConcluded(e: MatchConcludedEvent) { s.mode().update(ModeLobby(s)) }

    override fun runMatchResolved(e: MatchResolvedEvent) { logMode(this, "MatchResolvedEvent") }

    override fun runRoundDraw(e: RoundDrawEvent) { logMode(this, "RoundDrawEvent") }

    override fun runRoundResolved(e: RoundResolvedEvent) { }

    override fun runRoundStarted(e: RoundStartedEvent) { runRoundStartedCommons(e) }

    override fun runMatchLoading(e: MatchLoadingEvent) { logMode(this, "MatchLoadingEvent") }

    override fun runCommandBet(e: ViewerBetEvent) { logMode(this, "ViewerBetEvent") }

    override fun runFighterJoined(e: FighterJoinedEvent) { runFighterJoinedCommons(e) }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

    override fun runMatchUpdate(e: XrdMatchUpdateEvent) { s.updateMatch(e.matchSnap) }

}