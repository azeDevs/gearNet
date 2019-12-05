package session.modes

import events.*
import session.Session

class ModeNull(override val s: Session) : Mode(s) {

    override fun toString(): String = "${super.toString()}NULL"

    override fun runMatchConcluded(e: MatchConcludedEvent) { logMode(this, "MatchConcludedEvent") }

    override fun runMatchResolved(e: MatchResolvedEvent) { logMode(this, "MatchResolvedEvent") }

    override fun runRoundDraw(e: RoundDrawEvent) { logMode(this, "RoundDrawEvent") }

    override fun runRoundResolved(e: RoundResolvedEvent) { logMode(this, "RoundResolvedEvent") }

    override fun runRoundStarted(e: RoundStartedEvent) { logMode(this, "RoundStartedEvent") }

    override fun runMatchLoading(e: MatchLoadingEvent) { logMode(this, "MatchLoadingEvent") }

    override fun runCommandBet(e: ViewerBetEvent) { logMode(this, "ViewerBetEvent") }

    override fun runFighterJoined(e: FighterJoinedEvent) {
        if (s.getFighters().isNotEmpty()) s.updateMode(ModeLobby(s))
        runFighterJoinedCommons(e)
    }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

}