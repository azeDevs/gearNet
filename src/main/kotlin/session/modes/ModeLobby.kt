package session.modes

import events.*
import session.Session

class ModeLobby(override val s: Session) : Mode(s) {

    override fun toString(): String = "LOBBY${super.toString()}"

    override fun runMatchConcluded(e: MatchConcludedEvent) { }
    override fun runMatchResolved(e: MatchResolvedEvent) { }
    override fun runRoundDraw(e: RoundDrawEvent) { }
    override fun runRoundResolved(e: RoundResolvedEvent) { }
    override fun runRoundStarted(e: RoundStartedEvent) { runRoundStartedCommons(e) }

    override fun runMatchLoading(e: MatchLoadingEvent) {
        if (s.stage().isMatchValid()) s.mode().update(ModeLoading(s))
    }

    override fun runFighterJoined(e: FighterJoinedEvent) { runFighterJoinedCommons(e) }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

    override fun runMatchUpdate(e: XrdMatchUpdateEvent) { }

}