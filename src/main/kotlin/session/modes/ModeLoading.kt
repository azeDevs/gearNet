package session.modes

import events.*
import session.L
import session.Session
import views.logging.LogText.Effect.*
import views.logging.log

class ModeLoading(override val s: Session) : Mode(s) {

    override fun toString(): String = "LOADING${super.toString()}"

    override fun runMatchConcluded(e: MatchConcludedEvent) { }

    override fun runMatchResolved(e: MatchResolvedEvent) { }

    override fun runRoundDraw(e: RoundDrawEvent) { }

    override fun runRoundResolved(e: RoundResolvedEvent) { }

    override fun runRoundStarted(e: RoundStartedEvent) { runRoundStartedCommons(e) }

    override fun runMatchLoading(e: MatchLoadingEvent) {
        if (step() == 0) { nextStep()
            log(
                e.match.getIdLog(),
                L(" MatchLoadingEvent ... "),
                L(e.match.fighter(0).getName(), RED),
                L(" vs ", MED),
                L(e.match.fighter(1).getName(), BLU)
            )
        }
    }

    override fun runFighterJoined(e: FighterJoinedEvent) { runFighterJoinedCommons(e) }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

    override fun runMatchUpdate(e: XrdMatchUpdateEvent) { s.updateMatch(e.matchSnap) }

}