package events

import memscan.MemHandler
import memscan.XrdApi
import session.Fighter
import session.Session
import utils.SessionMode.Mode.*

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
            xrdApi.getFighterData().map { if (it.isValid() && s.update(it)) s.fire(FighterJoinedEvent(s.getFighter(it.steamId))) }
            s.update(xrdApi.getMatchData())
            if (s.fighters().isNotEmpty() && s.isMode(NULL)) s.update(LOBBY)
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
        if (s.stage().isMatchConcluded()) s.fire(MatchConcludedEvent(s.stage().match()))
    }

    private fun getEventsFighterMoved() {
        s.getFighters().filter { !(it.oldData().seatingId == it.getData().seatingId && it.oldData().cabinetId == it.getData().cabinetId)
        }.forEach { movedFighter -> s.fire(FighterMovedEvent(movedFighter)) }
    }

    private fun getEventsMatchLoading() {
        val fighters = s.getFighters().filter { it.isLoading() }
        if (fighters.size == 2) s.fire(MatchLoadingEvent(s.stage().match()))
    }

    private fun getEventsMatchResolved() {
        var fighter0 = Fighter()
        var fighter1 = Fighter()
        s.getFighters().forEach {
            if (it.isSeated(0) && it.justPlayed()) fighter0 = it
            if (it.isSeated(1) && it.justPlayed()) fighter1 = it
        }
        if (fighter0.isValid() && fighter1.isValid()) s.fire(MatchResolvedEvent(s.stage().match()))
    }

    private fun getEventsRoundStarted() {
        if(s.stage().match().getHealth(0) == 420 && s.stage().match().getHealth(1) == 420 && s.isMode(LOADING, SLASH)) {
            if(!s.isMode(MATCH, VICTORY)) s.fire(RoundStartedEvent(s.stage().match()))
        }
    }

    private fun getEventsRoundResolved() {
        if (s.isMode(MATCH)) {
            val m = s.stage().match()
            if (m.getHealth(1) == 0 || m.getHealth(0) == 0) s.fire(RoundResolvedEvent(m))
        }
    }

}