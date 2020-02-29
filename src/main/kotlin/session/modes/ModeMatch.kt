package session.modes

import events.*
import session.L
import session.Session
import views.logging.LogText.Effect.RED
import views.logging.LogText.Effect.YLW_FIGHT
import views.logging.log

class ModeMatch(override val s: Session) : Mode(s) {

    override fun toString(): String = "MATCH${super.toString()}"

    override fun runMatchConcluded(e: MatchConcludedEvent) { s.mode().update(ModeLobby(s)) }

    override fun runMatchResolved(e: MatchResolvedEvent) {
        if (e.match.isResolved() && e.match.getTimer() > -1)  s.stage().finalizeMatch()
    }

    override fun runRoundDraw(e: RoundDrawEvent) {
        s.stage().match().incrementRounds()
        log(
            e.match.getRoundLog(),
            L(" resolved as a "),
            L("DRAW", YLW_FIGHT)
        )
        s.mode().update(ModeSlash(s))
    }

    override fun runRoundResolved(e: RoundResolvedEvent) {
        val round = e.match.getRoundLog(false, -1)
        when(s.stage().match().tookTheRound()) {
            0 -> {
                log(
                    e.match.getIdLog(),
                    L(" "),
                    round,
                    L(" goes to "),
                    e.match.fighter(0).getLog(),
                    e.match.getMatchLog()
                )
                s.stage().match().incrementRounds(0) }
            1 -> {
                log(
                    e.match.getIdLog(),
                    L(" "),
                    round,
                    L(" goes to "),
                    e.match.fighter(1).getLog(),
                    e.match.getMatchLog()
                )
                s.stage().match().incrementRounds(1) }
            else -> log(
                e.match.getIdLog(),
                L(" "),
                round,
                L(" goes to "),
                L("ERROR", RED),
                e.match.getMatchLog()
            )
        }
        s.mode().update(ModeSlash(s))
    }

    override fun runRoundStarted(e: RoundStartedEvent) { }

    override fun runMatchLoading(e: MatchLoadingEvent) { logMode(this, "MatchLoadingEvent") }

    override fun runFighterJoined(e: FighterJoinedEvent) { runFighterJoinedCommons(e) }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

    override fun runMatchUpdate(e: XrdMatchUpdateEvent) { s.updateMatch(e.matchSnap) }

}