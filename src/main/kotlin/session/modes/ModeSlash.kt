package session.modes

import application.LogText
import application.log
import events.*
import session.L
import session.Session

class ModeSlash(override val s: Session) : Mode(s) {

    override fun toString(): String = "${super.toString()}SLASH"

    override fun runMatchConcluded(e: MatchConcludedEvent) {
        logMode(this, "MatchConcludedEvent")
        log(L("CONCLUDED ", LogText.Effect.YLW), e.match.getIdLog(false))
        s.updateMode(ModeLobby(s))
    }

    override fun runMatchResolved(e: MatchResolvedEvent) {
        logMode(this, "MatchResolvedEvent")
    }

    override fun runRoundDraw(e: RoundDrawEvent) {
        logMode(this, "RoundDrawEvent")
    }

    override fun runRoundResolved(e: RoundResolvedEvent) {
        logMode(this, "RoundResolvedEvent")
    }

    override fun runRoundStarted(e: RoundStartedEvent) {
        logMode(this, "RoundStartedEvent")
        s.updateMode(ModeMatch(s))
        val round = "Round ${e.match.getRoundNumber()}"
        log(e.match.getIdLog(), L(round, LogText.Effect.YLW), L(" started ... ", LogText.Effect.CYA))
    }

    override fun runMatchLoading(e: MatchLoadingEvent) {
        logMode(this, "MatchLoadingEvent")
    }

    override fun runCommandBet(e: ViewerBetEvent) {
        logMode(this, "ViewerBetEvent")
    }

}