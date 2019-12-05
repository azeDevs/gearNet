package session.modes

import application.LogText.Effect.RED
import application.LogText.Effect.YLW
import application.log
import events.*
import session.L
import session.Session

class ModeMatch(override val s: Session) : Mode(s) {

    override fun toString(): String = "${super.toString()}MATCH"

    override fun runMatchConcluded(e: MatchConcludedEvent) { runMatchConcludedCommons(e)
        s.updateMode(ModeLobby(s))
    }

    override fun runMatchResolved(e: MatchResolvedEvent) { logMode(this, "MatchResolvedEvent")
        if (e.match.isResolved() && e.match.getTimer() > -1) {
            s.stage().finalizeMatch()
        }
    }

    override fun runRoundDraw(e: RoundDrawEvent) { logMode(this, "RoundDrawEvent")
        s.updateMode(ModeSlash(s))
        s.stage().match().incrementRounds()
        log(e.match.getRoundLog(), L(" resolved as a "), L("DRAW", YLW))
    }

    override fun runRoundResolved(e: RoundResolvedEvent) { logMode(this, "RoundResolvedEvent")
        s.updateMode(ModeSlash(s))
        val round = e.match.getRoundLog(false, -1)
        when(s.stage().match().tookTheRound()) {
            0 -> { log(e.match.getIdLog(), round, L(" goes to "), e.match.getFighterLog(0))
                s.stage().match().incrementRounds(0) }
            1 -> { log(e.match.getIdLog(), round, L(" goes to "), e.match.getFighterLog(1))
                s.stage().match().incrementRounds(1) }
            else -> log(e.match.getIdLog(), round, L(" goes to "), L("ERROR", RED))
        }
    }

    override fun runRoundStarted(e: RoundStartedEvent) { logMode(this, "RoundStartedEvent") }

    override fun runMatchLoading(e: MatchLoadingEvent) { logMode(this, "MatchLoadingEvent") }

    override fun runCommandBet(e: ViewerBetEvent) { logMode(this, "ViewerBetEvent") }

    override fun runFighterJoined(e: FighterJoinedEvent) { runFighterJoinedCommons(e) }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

}