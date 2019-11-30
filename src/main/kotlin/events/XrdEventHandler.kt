package events

import memscan.MemHandler
import memscan.XrdApi
import session.Session
import utils.SessionMode.Mode.*

/**
 *
 * [XrdEventHandler]
 * updates and archives Lobby data
 *
 */
class XrdEventHandler(private val s: Session) {

    private val xrdApi: XrdApi = MemHandler()
    private var connected = false

    fun generateFighterEvents() {
        // 002: Fire an event if Xrd is running and the process is hooked
        if (fireConnectionEvent(xrdApi.isConnected())) {
            xrdApi.getFighterData().map {
                // 003: Verify the incoming data is valid
                if (it.isValid() && !s.updateFighter(it)) {
                    // 004: If it wasn't an update to an existing Fighter, fire a FighterJoinedEvent
                    s.fire(FighterJoinedEvent(s.getFighter(it.steamId())))
                }
            }
            s.updateMatch(xrdApi.getMatchSnap())
            // 005: When the first Fighter appears in memory, a Lobby has been created/joined
            if (s.fighters().isNotEmpty() && s.isMode(NULL)) s.updateMode(LOBBY)
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
        if (s.stage().match().getTimer() == -1 && (s.isMode(VICTORY) || s.isMode(SLASH) || s.isMode(MATCH)))
            s.fire(MatchConcludedEvent(s.stage().match()))
    }

    private fun getEventsFighterMoved() {
        s.fighters().filter { !(it.oldData().seatingId() == it.getData().seatingId() && it.oldData().cabinetId() == it.getData().cabinetId())
        }.forEach { movedFighter -> s.fire(FighterMovedEvent(movedFighter)) }
    }

    private fun getEventsMatchLoading() {
        val fighters = s.fighters().filter { it.isLoading() }
        if (fighters.size == 2) s.fire(MatchLoadingEvent(s.stage().match()))
    }

    private fun getEventsMatchResolved() {
        if (s.stage().match().getWinningFighter().isValid()) s.fire(MatchResolvedEvent(s.stage().match()))
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
            if (m.getTimer() == 0 && s.isMode(MATCH)) s.fire(RoundResolvedEvent(m))
        }
    }

}