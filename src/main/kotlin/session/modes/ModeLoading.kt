package session.modes

import MyApp.Companion.WD
import application.LogText.Effect.*
import application.log
import events.*
import session.L
import session.Session
import twitch.ViewerBet
import utils.addCommas

class ModeLoading(override val s: Session) : Mode(s) {

    override fun toString(): String = "${super.toString()}LOADING"

    override fun runMatchConcluded(e: MatchConcludedEvent) { logMode(this, "MatchConcludedEvent") }

    override fun runMatchResolved(e: MatchResolvedEvent) { logMode(this, "MatchResolvedEvent")
        s.updateMode(ModeLobby(s))
    }

    override fun runRoundDraw(e: RoundDrawEvent) { logMode(this, "RoundDrawEvent") }

    override fun runRoundResolved(e: RoundResolvedEvent) { logMode(this, "RoundResolvedEvent") }

    override fun runRoundStarted(e: RoundStartedEvent) { logMode(this, "RoundStartedEvent")
        s.updateMode(ModeMatch(s))
    }

    override fun runMatchLoading(e: MatchLoadingEvent) {
        if (step() == 0) { logMode(this, "MatchLoadingEvent")
            log(e.match.getIdLog(), L(" loading ... "), L(e.match.getFighter(0).getName(), RED), L(" vs ", MED), L(e.match.getFighter(1).getName(), BLU))
            nextStep()
        }
    }

    override fun runCommandBet(e: ViewerBetEvent) {
        logMode(this, "ViewerBetEvent")
        if (s.stage().isMatchValid()) {
            val bet = ViewerBet(e.viewer)
            val sb = StringBuilder("Viewer ${e.viewer.getName()} bet ")
            if (bet.isValid()) {
                if (bet.getChips(0)>0) sb.append("${bet.getChips(0)}0% (${addCommas(bet.getWager(0))} $WD) on Red")
                if (bet.getChips(0)>0 && bet.getChips(1)>0) sb.append(" & ")
                if (bet.getChips(1)>0) sb.append("${bet.getChips(1)}0% (${addCommas(bet.getWager(1))} $WD) on Blue")
                log(sb.toString())
                s.stage().addBet(bet)
            }
        } else log("Viewer ${e.viewer.getName()} bet fizzled, betting is locked")
    }

    override fun runFighterJoined(e: FighterJoinedEvent) { runFighterJoinedCommons(e) }

    override fun runViewerJoined(e: ViewerJoinedEvent) { runViewerJoinedCommons(e) }

    override fun runViewerMessage(e: ViewerMessageEvent) { runViewerMessageCommons(e) }

    override fun runFighterMoved(e: FighterMovedEvent) { runFighterMovedCommons(e) }

}