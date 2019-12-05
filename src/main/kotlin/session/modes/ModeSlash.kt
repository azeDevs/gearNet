package session.modes

import application.LogText.Effect.CYA
import application.LogText.Effect.YLW
import application.log
import events.*
import session.L
import session.Session

class ModeSlash(override val s: Session) : Mode(s) {

    override fun toString(): String = "${super.toString()}SLASH"

    override fun runMatchConcluded(e: MatchConcludedEvent) { logMode(this, "MatchConcludedEvent")
        log(e.match.getIdLog(false), L(" CONCLUDED", YLW))
        s.updateMode(ModeLobby(s))
    }

    override fun runMatchResolved(e: MatchResolvedEvent) { logMode(this, "MatchResolvedEvent") }

    override fun runRoundDraw(e: RoundDrawEvent) { logMode(this, "RoundDrawEvent") }

    override fun runRoundResolved(e: RoundResolvedEvent) { }

    override fun runRoundStarted(e: RoundStartedEvent) { logMode(this, "RoundStartedEvent")
        s.updateMode(ModeMatch(s))
        val round = "Round ${e.match.getRoundNumber()}"
        log(e.match.getIdLog(), L(round, YLW), L(" started ... ", CYA))
    }

    override fun runMatchLoading(e: MatchLoadingEvent) { logMode(this, "MatchLoadingEvent") }

    override fun runCommandBet(e: ViewerBetEvent) { logMode(this, "ViewerBetEvent") }

    override fun runFighterJoined(e: FighterJoinedEvent) { runFighterJoinedCommons(e) }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

}