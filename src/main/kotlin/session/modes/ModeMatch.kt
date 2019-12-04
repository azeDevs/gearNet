package session.modes

import application.LogText.Effect.*
import application.log
import events.*
import session.Fighter
import session.L
import session.Session

class ModeMatch(override val s: Session) : Mode(s) {

    override fun toString(): String = "${super.toString()}MATCH"

    override fun runMatchConcluded(e: MatchConcludedEvent) {
        logMode(this, "MatchConcludedEvent")
        log(L("CONCLUDED ", YLW), e.match.getIdLog(false))
        s.updateMode(ModeLobby(s))
    }

    override fun runMatchResolved(e: MatchResolvedEvent) {
        logMode(this, "MatchResolvedEvent")
        if (e.match.isResolved() && e.match.getTimer() > -1) {
            s.stage().finalizeMatch()
            val winner = e.match.getWinningFighter()
            s.sendMessage("${winner.getName()} WINS!")
            log(e.match.getIdLog(), L(" FINALIZED: ", GRN), L("${e.match.getSnapCount()}"), L(" snaps, ", YLW), L(e.match.getFighter(0).getName(), RED), L(" wins"))
        }
    }

    override fun runRoundDraw(e: RoundDrawEvent) {
        logMode(this, "RoundDrawEvent")
        s.updateMode(ModeSlash(s))
        val round = "Round ${e.match.getRoundNumber()-1}"
        log(L(round, YLW), L(" resolved as a "), L("DRAW", YLW))
    }

    override fun runRoundResolved(e: RoundResolvedEvent) {
        logMode(this, "RoundResolvedEvent")
        s.updateMode(ModeSlash(s))
        var winner = Fighter()
        val round = "Round ${e.match.getRoundNumber()-1}"
        if (e.match.tookTheRound(0)) winner = e.match.getFighter(0)
        else if (e.match.tookTheRound(1)) winner = e.match.getFighter(1)
        when {
            winner.getSeat() == 0 -> log(e.match.getIdLog(), L(round, YLW), L(" goes to "), L(e.match.getFighter(0).getName(), RED))
            winner.getSeat() == 1 -> log(e.match.getIdLog(), L(round, YLW), L(" goes to "), L(e.match.getFighter(1).getName(), BLU))
            else -> log(e.match.getIdLog(), L(round, YLW), L(" goes to "), L("ERROR", RED))
        }
    }

    override fun runRoundStarted(e: RoundStartedEvent) {
        logMode(this, "RoundStartedEvent")
    }

    override fun runMatchLoading(e: MatchLoadingEvent) {
        logMode(this, "MatchLoadingEvent")
    }

    override fun runCommandBet(e: ViewerBetEvent) {
        logMode(this, "ViewerBetEvent")
    }

}