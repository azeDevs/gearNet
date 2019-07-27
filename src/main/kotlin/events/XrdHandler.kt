package events

import events.EventType.*
import memscan.MemHandler
import memscan.XrdApi
import models.Fighter
import session.SessionMode.*
import session.SessionState
import utils.Duo
import utils.log
typealias FE = FighterEvent

/**
 *
 * [XrdHandler]
 * updates and archives Lobby data
 *
 */
class XrdHandler {

    private val xrdApi: XrdApi = MemHandler()
    private val events: MutableList<FE> = arrayListOf()
    private var connected = false

    fun generateFighterEvents(state: SessionState): List<FE> {
        events.clear()
        if (xrdApi.isConnected()) { if (!connected) { events.add(FE(XRD_CONNECTED)); connected = true }
            xrdApi.getFighterData().map { fighterData -> if (fighterData.isValid() && !state.update(fighterData)) { events.add(FE(FIGHTER_JOINED, state.getFighter(fighterData))) } }
            state.update(xrdApi.getMatchData())
            if (state.getFighters().isNotEmpty() && state.isMode(MODE_NULL)) state.update(MODE_LOBBY)

            getEventsFighterMoved(state)
            getEventsMatchLoading(state)
            getEventsMatchResolved(state)
            getEventsMatchConcluded(state)
            getEventsRoundResolved(state)
            getEventsRoundStarted(state)

        } else if (connected)  { events.add(FE(XRD_DISCONNECT)); connected = false }
        return events
    }

    private fun getEventsMatchConcluded(state: SessionState) {
        if (state.getMatch().getMatchTimer() == -1 && state.isMode(MODE_VICTORY))
            events.add(FE(MATCH_CONCLUDED))
    }

    private fun getEventsFighterMoved(state: SessionState) {
        state.getFighters().filter { !(it.oldData().seatingId == it.getData().seatingId && it.oldData().cabinetId == it.getData().cabinetId)
        }.forEach { movedFighter ->
            events.add(FE(FIGHTER_MOVED, movedFighter, movedFighter.getSeat()))
        }
    }

    private fun getEventsMatchLoading(state: SessionState) {
        val fighters = state.getFighters().filter { it.isLoading() }
        if (fighters.size == 2) {
            log("R Loaded", fighters[0].getLoadPercent().toString())
            log("B Loaded", fighters[1].getLoadPercent().toString())
            events.add(FE(MATCH_LOADING, Pair(fighters[0], fighters[1])))
        }
    }

    private fun getEventsMatchResolved(state: SessionState) {
        val outFighters = Duo(Fighter(), Fighter())
        var winningSide = Duo(-1, -1)
        state.getFighters().forEach {
            if (it.isSeated(0) && it.justPlayed()) outFighters.f1 = it
            if (it.isSeated(1) && it.justPlayed()) outFighters.f2 = it
            if (it.justWon()) {
                if (it.isSeated(0)) winningSide = Duo(1, 0)
                if (it.isSeated(1)) winningSide = Duo(0, 1)
            }
        }
        if (outFighters.f1.isValid() && outFighters.f2.isValid()) events.add(
            FE(MATCH_RESOLVED,
                Pair(outFighters.f1, outFighters.f2),
                Pair(winningSide.f1, winningSide.f2)
            )
        )
    }

    private fun getEventsRoundStarted(state: SessionState) {
        if(state.getMatch().getHealth(0) == 420 && state.getMatch().getHealth(1) == 420 && state.isMode(MODE_LOADING, MODE_SLASH)) {
            if(!state.isMode(MODE_MATCH, MODE_VICTORY)) events.add(FE(ROUND_STARTED, state.getMatch().getFighters()))
        }
    }

    private fun getEventsRoundResolved(state: SessionState) {
        val m = state.getMatch()
        if ((m.getHealth(0) == 0 || m.getHealth(1) == 0) && state.isMode(MODE_MATCH))
            events.add(FE(ROUND_RESOLVED, Pair(m.getFighter(0), m.getFighter(1)), Pair(m.getHealth(0), m.getHealth(1))))
    }

}