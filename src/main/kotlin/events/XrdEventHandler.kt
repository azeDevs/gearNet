package events

import memscan.MemHandler
import memscan.XrdApi
import session.Session
import session.modes.*

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
            if (s.getFighters().isNotEmpty() && s.isMode(ModeNull(s))) s.updateMode(ModeLobby(s))
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
        if (s.stage().match().getTimer() == -1 && (s.isMode(ModeVictory(s)) || s.isMode(ModeSlash(s)) || s.isMode(ModeMatch(s))))
            s.fire(MatchConcludedEvent(s.stage().getLastMatch()))
    }

    private fun getEventsFighterMoved() {
        s.getFighters().filter { !(it.oldData().seatingId() == it.getData().seatingId() && it.oldData().cabinetId() == it.getData().cabinetId())
        }.forEach { movedFighter -> s.fire(FighterMovedEvent(movedFighter)) }
    }

    private fun getEventsMatchLoading() {
        val fighters = s.getFighters().filter { it.isLoading() }
        if (fighters.size == 2) s.fire(MatchLoadingEvent(s.stage().match()))
    }

    private fun getEventsMatchResolved() {
        if (s.stage().match().getWinningFighter().isValid()) s.fire(MatchResolvedEvent(s.stage().match()))
    }

    private fun getEventsRoundStarted() {
        if(s.stage().match().getHealth(0) == 420 && s.stage().match().getHealth(1) == 420 && s.isMode(ModeLoading(s), ModeSlash(s))) {
            if(!s.isMode(ModeMatch(s), ModeVictory(s))) s.fire(RoundStartedEvent(s.stage().match()))
        }
    }

    private fun getEventsRoundResolved() {
        val m = s.stage().match()
        if (m.getHealth(1) == 0 && m.getHealth(0) == 0) { s.fire(RoundDrawEvent(m)) }
        if (m.getTimer() == 0 && m.getHealth(1) == m.getHealth(0)) s.fire(RoundDrawEvent(m))
        if (m.getHealth(1) == 0 || m.getHealth(0) == 0) s.fire(RoundResolvedEvent(m))
        if (m.getTimer() == 0) s.fire(RoundResolvedEvent(m))
    }

}