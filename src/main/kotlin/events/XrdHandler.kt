package events

import memscan.MemHandler
import memscan.XrdApi
import session.Fighter
import session.Session
import session.Session.Mode

/**
 *
 * [XrdHandler]
 * updates and archives Lobby data
 *
 */
class XrdHandler(private val s: Session) {

    private val xrdApi: XrdApi = MemHandler()
    private var connected = false

    fun generateFighterEvents() {
        if (fireConnectionEvent(xrdApi.isConnected())) {
            xrdApi.getFighterData().map { if (it.isValid() && !s.state.update(it)) s.fire(FighterJoinedEvent(s.state.getFighter(it))) }
            s.state.update(xrdApi.getMatchData())
            if (s.state.getFighters().isNotEmpty() && s.state.isMode(Mode.NULL)) s.state.update(Mode.LOBBY)
            getEventsFighterMoved()
            getEventsMatchLoading()
            getEventsMatchResolved()
            getEventsMatchConcluded()
            getEventsRoundResolved()
            getEventsRoundStarted()
        }
    }

    private fun fireConnectionEvent(connect:Boolean): Boolean {
        if (connected != connect) { s.fire(XrdConnectionEvent(connect)); connected = connect }
        return connected
    }

    private fun getEventsMatchConcluded() {
        if (s.state.getMatch().getTimer() == -1 && s.state.isMode(Mode.VICTORY)) s.fire(MatchConcludedEvent(s.state.getMatch()))
    }

    private fun getEventsFighterMoved() {
        s.state.getFighters().filter { !(it.oldData().seatingId == it.getData().seatingId && it.oldData().cabinetId == it.getData().cabinetId)
        }.forEach { movedFighter -> s.fire(FighterMovedEvent(movedFighter, movedFighter.getSeat())) }
    }

    private fun getEventsMatchLoading() {
        val fighters = s.state.getFighters().filter { it.isLoading() }
        if (fighters.size == 2) {
//            log("R Loaded", fighters[0].getLoadPercent().toString())
//            log("B Loaded", fighters[1].getLoadPercent().toString())
            s.fire(MatchLoadingEvent(s.state.getMatch()))
        }
    }

    private fun getEventsMatchResolved() {
        var fighter0 = Fighter()
        var fighter1 = Fighter()
        s.state.getFighters().forEach {
            if (it.isSeated(0) && it.justPlayed()) fighter0 = it
            if (it.isSeated(1) && it.justPlayed()) fighter1 = it
        }
        if (fighter0.isValid() && fighter1.isValid()) s.fire(MatchResolvedEvent(s.state.getMatch(), s.state.getMatch().getWinner()))
    }

    private fun getEventsRoundStarted() {
        if(s.state.getMatch().getHealth(0) == 420 && s.state.getMatch().getHealth(1) == 420 && s.state.isMode(Mode.LOADING, Mode.SLASH)) {
            if(!s.state.isMode(Mode.MATCH, Mode.VICTORY)) s.fire(RoundStartedEvent(s.state.getMatch()))
        }
    }

    private fun getEventsRoundResolved() {
        if (s.state.isMode(Mode.MATCH)) {
            val m = s.state.getMatch()
            if (m.getHealth(0) == 0) s.fire(RoundResolvedEvent(s.state.getMatch(), 1))
            else if (m.getHealth(1) == 0) s.fire(RoundResolvedEvent(s.state.getMatch(), 0))
            else if (m.getHealth(1) == 0 && m.getHealth(0) == 0) s.fire(RoundResolvedEvent(s.state.getMatch(), -1))
        }
    }

}