package events

import events.EventType.*
import memscan.MemHandler
import memscan.XrdApi
import models.Fighter
import models.Match
import session.SessionMode.*
import session.SessionState
import utils.Duo
import utils.log

/**
 *
 * XrdHandler                  updates and archives Lobby data
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [XrdHandler]
 * updates and archives Lobby data
 *
 */
class XrdHandler {

    private val xrdApi: XrdApi = MemHandler()
    private val events: MutableList<FighterEvent> = arrayListOf()
    // TODO: THERE'S SOME CONFUSION WITH LobbyHandler AND SessionState
    // As far  where Match data comes from, LobbyHandler seems like overkill right now
    // Maybe move LobbyHandler data into SessionState and remove all multi-cabinet support
    private val lobbyHandler: LobbyHandler = LobbyHandler()

    private var connected = false
    private var clientFighter = Fighter()

    private fun getClientFighter(state: SessionState): Fighter {
        if (lobbyHandler.getFighterPairs().isNotEmpty() && !clientFighter.isValid()) {
            clientFighter = lobbyHandler.getNewFighters().first { it.getId() == xrdApi.getClientSteamId() }
            state.updateMode(MODE_LOBBY)
            log("XrdApi defined ${clientFighter.getName()} as client source") }
        return clientFighter
    }

    fun generateFighterEvents(state: SessionState): List<FighterEvent> {
        events.clear()
        if (xrdApi.isConnected()) { if (!connected) { events.add(FighterEvent(XRD_CONNECTED)); connected = true }

            // 1. Generate a Match, Fighters, and update the containing Lobby
            val fighters: List<Fighter> = xrdApi.getFighterData().map { fighterData ->
                val oldFighter: Fighter = lobbyHandler.getNewFighters().firstOrNull { it.getId() == fighterData.steamId } ?: Fighter()
                Fighter(oldFighter.getData(), fighterData)
            }
            val matchFighter0 = fighters.firstOrNull { it.getCabinet() == clientFighter.getCabinet() && it.getSeat() == 0 } ?: Fighter()
            val matchFighter1 = fighters.firstOrNull { it.getCabinet() == clientFighter.getCabinet() && it.getSeat() == 1 } ?: Fighter()
            val clientMatch = Match(Pair(matchFighter0, matchFighter1), clientFighter.getCabinet(), xrdApi.getMatchData())

            log("Match Timer","${clientMatch.getMatchTimer()}")
            log("R Rounds","${clientMatch.getRounds(0)}")
            log("R Health","${clientMatch.getHealth(0)}")
            log("R Tension","${clientMatch.getTension(0)}")
            log("R Guard","${clientMatch.getGuardGauge(0)}")
            log("R Stunned","${clientMatch.getStrikeStun(0)}")
            log("R Burst","${clientMatch.getCanBurst(0)}")

            log("B Rounds","${clientMatch.getRounds(1)}")
            log("B Health","${clientMatch.getHealth(1)}")
            log("B Tension","${clientMatch.getTension(1)}")
            log("B Guard","${clientMatch.getGuardGauge(1)}")
            log("B Stunned","${clientMatch.getStrikeStun(1)}")
            log("B Burst","${clientMatch.getCanBurst(1)}")

            lobbyHandler.update(fighters, clientMatch)

            // 3. Generate Events
            if (getClientFighter(state).isValid()) {
                getEventsFighterJoined()
                getEventsFighterMoved()
                getEventsMatchLoading()
                getEventsMatchResolved()
                //getEventsDamageDealt()
                getEventsRoundResolved()
                getEventsRoundStarted(state)
                getEventsMatchConcluded(state)
            }

        } else if (connected)  { events.add(FighterEvent(XRD_DISCONNECT)); connected = false }
        return events
    }

    private fun getEventsMatchConcluded(state: SessionState) {
        if (lobbyHandler.getMatch().getMatchTimer() == -1 && state.isMode(MODE_VICTORY))
            events.add(FighterEvent(MATCH_CONCLUDED))
    }

    private fun getEventsFighterJoined() {
        // TODO: THIS DOESN'T WORK, BUT DOESN'T TEHCNICALLY NEED TO. BACKBURNER.
        lobbyHandler.getNewFighters().filter { nf ->
            var flag = true
            lobbyHandler.getFighterPairs().forEach { pair -> if (pair.first.getId() == nf.getId()) flag = false }
            flag
        }.forEach { events.add(FighterEvent(FIGHTER_JOINED, it)) }
    }

    private fun getEventsFighterMoved() {
            val p1 = lobbyHandler.getOldFighters()
            val p2 = lobbyHandler.getNewFighters()
            p2.filter { np -> var flag = true
                p1.forEach { op -> if (op.getSeat() == np.getSeat() && op.getCabinet() == np.getCabinet()) flag = false }
                flag
            }.forEach { movedFighter ->
                if (events.none { fighterEvent -> fighterEvent.getType() == FIGHTER_JOINED && fighterEvent.getId() == movedFighter.getId() }) {
                    events.add(FighterEvent(FIGHTER_MOVED, movedFighter, movedFighter.getSeat())) }
            }
    }

    private fun getEventsMatchLoading() {
        val fighters = lobbyHandler.getFighterPairs().filter { it.second.isLoading() }
        if (fighters.size == 2) {
            log("R Loaded", fighters[0].second.getLoadPercent().toString())
            log("B Loaded", fighters[1].second.getLoadPercent().toString())
            events.add(FighterEvent(MATCH_LOADING, Pair(fighters[0].second, fighters[1].second)))
        }
    }

    private fun getEventsMatchResolved() {
        val fightsOut = Duo(Fighter(), Fighter())
        var winningSide = Duo(-1, -1)
        lobbyHandler.getFighterPairs().forEach {
            val of = it.first
            val nf = it.second
            if (nf.getMatchesWon() > of.getMatchesWon() && nf.getMatchesPlayed() > of.getMatchesPlayed()) {
                when (nf.getSeat()) {
                    0 -> { fightsOut.f1 = nf; winningSide = Duo(1, 0) }
                    1 -> { fightsOut.f2 = nf; winningSide = Duo(0, 1) } } }
            if (nf.getMatchesWon() == of.getMatchesWon() && nf.getMatchesPlayed() > of.getMatchesPlayed()) {
                when (nf.getSeat()) {
                    0 -> { fightsOut.f1 = nf; winningSide = Duo(0, 1) }
                    1 -> { fightsOut.f2 = nf; winningSide = Duo(1, 0) } } }
        }
        if (fightsOut.f1.isValid() && fightsOut.f2.isValid()) events.add(
            FighterEvent(
                MATCH_RESOLVED,
                Pair(fightsOut.f1, fightsOut.f2),
                Pair(winningSide.f1, winningSide.f2)
            )
        )
    }

    private fun getEventsRoundStarted(state: SessionState) {
        if(lobbyHandler.getMatch().getHealth(0) == 420 && lobbyHandler.getMatch().getHealth(1) == 420) {
            if(!state.isMode(MODE_MATCH, MODE_VICTORY)) events.add(FighterEvent(ROUND_STARTED, lobbyHandler.getMatch().fighters))
        }
    }

    private fun getEventsRoundResolved() {
        val oldMatch = lobbyHandler.getOldMatch()
        val newMatch = lobbyHandler.getMatch()
        if((newMatch.getHealth(0) == 0 && oldMatch.getHealth(0) != 0) || (newMatch.getHealth(1) == 0 && oldMatch.getHealth(1) != 0))
            events.add(FighterEvent(ROUND_RESOLVED, newMatch.fighters, newMatch.getHealth()))
    }

    fun getEventsDamageDealt() {
        TODO("ACCUMULATE HP DIFFERENCE WHILE inStun IS true, RESET WITH isStun")
        //add(FighterEvent(DAMAGE_DEALT))
    }

}