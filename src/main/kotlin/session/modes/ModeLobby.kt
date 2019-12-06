package session.modes

import MyApp.Companion.WD
import application.log
import events.*
import session.Session
import twitch.ViewerBet
import utils.addCommas

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

    override fun runCommandBet(e: ViewerBetEvent) { logMode(this, "ViewerBetEvent")
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

    override fun runMatchUpdate(e: XrdMatchUpdateEvent) { }

}