package events

import events.EventType.XRD_CONNECTED
import events.EventType.XRD_DISCONNECT
import memscan.MemHandler
import memscan.XrdApi
import models.Fighter
import models.Match
import utils.Duo
import utils.log

/**
 *
 * XrdEventListener                  updates and archives Lobby data
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [XrdEventListener]
 * updates and archives Lobby data
 *
 */
class XrdEventListener {

    private val xrdApi: XrdApi = MemHandler()
    private val events: MutableList<FighterEvent> = arrayListOf()
    private val lobbyHandler: LobbyHandler = LobbyHandler()

    private var connected = false
    private var clientFighter = Fighter()

    fun getClient(): Fighter {
        if (lobbyHandler.getFighterPairs().size >= 1 && !clientFighter.isValid()) {
            clientFighter = lobbyHandler.getNewFighters().first { it.getId() == xrdApi.getClientSteamId() }
            log("XrdApi defined “${clientFighter.getName()}” as client source") }
        log("client", "${clientFighter.isValid()}")
        return clientFighter
    }

    fun generateFighterEvents(): List<FighterEvent> {
        events.clear()
        if (xrdApi.isConnected()) { if (!connected) { events.add(FighterEvent(XRD_CONNECTED)); connected = true }

            // 1. Generate a Match, Fighters, and update the containing Lobby
            val fighters: List<Fighter> = xrdApi.getFighterData().map { fighterData ->
                val oldFighter: Fighter = lobbyHandler.getNewFighters().firstOrNull { it.getId() == fighterData.steamId } ?: Fighter()
                Fighter(oldFighter.getData(), fighterData)
            }
            val client: Fighter = fighters.firstOrNull { it.getId() == xrdApi.getClientSteamId() } ?: Fighter()
            val matchP0 = fighters.firstOrNull { it.getCabinet() == client.getCabinet() && it.getSeat() == 0 } ?: Fighter()
            val matchP1 = fighters.firstOrNull { it.getCabinet() == client.getCabinet() && it.getSeat() == 1 } ?: Fighter()
            val clientMatch = Match(Pair(matchP0, matchP1), client.getCabinet(), xrdApi.getMatchData())
            lobbyHandler.update(fighters, clientMatch)

            // 3. Generate Events
            if (getClient().isValid()) {
                logUpdateToGUI()
                getEventsPlayerJoined()
                getEventsPlayerMoved()
                getEventsMatchLoading()
                getEventsMatchEnded()
                getEventsDamageDealt()
                getEventsRoundStarted()
                getEventsRoundEnded()
            }

        } else if (connected)  { events.add(FighterEvent(XRD_DISCONNECT)); connected = false }
        return events
    }

    fun logUpdateToGUI() {
        log("totalPlayers","${lobbyHandler.getNewFighters().size}")
    }

    fun getEventsPlayerJoined() {
        val p1 = lobbyHandler.getOldFighters()
        val p2 = lobbyHandler.getNewFighters()
        p2.filter { np -> var flag = true
            p1.forEach { op -> if (op.getId() == np.getId()) flag = false }
            flag
        }.forEach { events.add(FighterEvent(EventType.FIGHTER_JOINED, it)) }
    }

    fun getEventsPlayerMoved() {
        // FIXME: THIS SHOULD NOT FIRE IMMEDIATELY AFTER FIGHTER_JOINED EVENT
        val p1 = lobbyHandler.getOldFighters()
        val p2 = lobbyHandler.getNewFighters()
        p2.filter { np -> var flag = true
            p1.forEach { op -> if (
                op.getSeat() == np.getSeat()
                && op.getCabinet() == np.getCabinet()
            ) flag = false }
            flag
        }.forEach {
            events.add(FighterEvent(EventType.FIGHTER_MOVED, it, it.getSeat()))
        }
    }

    fun getEventsMatchLoading() {
        // FIXME: THIS NEEDS TO ONLY FIRE ONCE / CURRENTLY DOESN'T FIRE AT ALL
        val fighters = lobbyHandler.getFighterPairs().filter { it.second.isLoading() }
        if (fighters.size == 2) {
            log("loadingP1", fighters[0].second.getLoadPercent().toString())
            log("loadingP2", fighters[1].second.getLoadPercent().toString())
            events.add(FighterEvent(EventType.MATCH_LOADING, Pair(fighters[0].second, fighters[1].second)))
        }
    }

    fun getEventsMatchEnded() {
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
                    0 -> { fightsOut.f1 = nf; winningSide = Duo(0, 1)
                    }
                    1 -> { fightsOut.f2 = nf; winningSide = Duo(1, 0)
                    } } }
        }
        if (fightsOut.f1.isValid() && fightsOut.f2.isValid()) events.add(
            FighterEvent(
                EventType.MATCH_ENDED,
                Pair(fightsOut.f1, fightsOut.f2),
                Pair(winningSide.f1, winningSide.f2)
            )
        )
    }

    fun getEventsRoundStarted() {
        // FIXME: TRIGGERS AFTER MATCH ENDING + ROUND ENDING
        val oldMatch = lobbyHandler.getMatch(getClient().getCabinet()).first
        val newMatch = lobbyHandler.getMatch(getClient().getCabinet()).second
        log("Player 1 HP","${newMatch.getHealth(0)}")
        log("Player 2 HP","${newMatch.getHealth(1)}")
        if(oldMatch.getHealth(0) != 420 || oldMatch.getHealth(1) != 420)
            if(newMatch.getHealth(0) == 420 && newMatch.getHealth(1) == 420)
                events.add(FighterEvent(EventType.ROUND_STARTED))
    }

    fun getEventsRoundEnded() {
        // FIXME: TRIGGERS AFTER MATCH ENDING
        val oldMatch = lobbyHandler.getMatch(getClient().getCabinet()).first
        val newMatch = lobbyHandler.getMatch(getClient().getCabinet()).second
        if((newMatch.getHealth(0) == 0 && oldMatch.getHealth(0) != 0) || (newMatch.getHealth(1) == 0 && oldMatch.getHealth(1) != 0))
            events.add(FighterEvent(EventType.ROUND_ENDED, newMatch.fighters, newMatch.getHealth()))

//            // Has the round ended, and did player 1 win?
//            if (roundOngoing && getWinner() == -1 && getHealth(P2) == 0 && getHealth(P1) != getHealth(P2) ) {
//                roundOngoing = false
//                session.setMode(MODE_SLASH)
//                utils.log("[MATC] ID$matchId P1 wins Duel ${getRounds(P1) + getRounds(P2) + 1}")
//            }
//
//            // Has the round ended, and did player 2 win?
//            if (roundOngoing && getWinner() == -1 && getHealth(P1) == 0 && getHealth(P2) != getHealth(P1)) {
//                roundOngoing = false
//                session.setMode(MODE_SLASH)
//                utils.log("[MATC] ID$matchId P2 wins Duel ${getRounds(P1) + getRounds(P2) + 1}")
//            }
        //add(FighterEvent(ROUND_ENDED))
    }

    fun getEventsDamageDealt() {
        // TODO: ACCUMULATE HP DIFFERENCE WHILE inStun IS true, RESET WITH isStun
        //add(FighterEvent(DAMAGE_DEALT))
    }

}