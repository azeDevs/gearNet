package memscan

import session.Event
import session.EventType.*
import session.Fighter
import session.LobbyHandler
import session.Match
import utils.Duo
import utils.log

/**
 *
 * XrdListener                  updates and archives Lobby data
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [XrdListener]
 * updates and archives Lobby data
 *
 */
class XrdListener {

    var connected = false
    private val xrdApi: XrdApi = MemHandler()
    private val lobby: LobbyHandler = LobbyHandler()
    val events: MutableList<Event> = mutableListOf()

    fun generateUpdate(): List<Event> {
        events.clear()
        if (xrdApi.isConnected()) { if (!connected) { events.add(Event(XRD_CONNECTED)); connected = true }

            // 1. Generate a Match, Fighters, and update the containing Lobby
            val fighters: List<Fighter> = xrdApi.getFighterData().map { fighterData ->
                val oldFighter: Fighter = lobby.getNewFighters().firstOrNull { it.getId() == fighterData.steamId } ?: Fighter()
                Fighter(oldFighter.getData(), fighterData)
            }
            val client: Fighter = fighters.firstOrNull { it.getId() == xrdApi.getClientSteamId() } ?: Fighter()
            val matchP0 = fighters.firstOrNull { it.getCabinet() == client.getCabinet() && it.getSeat() == 0 } ?: Fighter()
            val matchP1 = fighters.firstOrNull { it.getCabinet() == client.getCabinet() && it.getSeat() == 1 } ?: Fighter()
            val clientMatch = Match(Pair(matchP0, matchP1), client.getCabinet(), xrdApi.getMatchData())
            lobby.update(fighters, clientMatch)

            // 3. Generate Events
            getEventsPlayerJoined()
            getEventsPlayerMoved()
            getEventsMatchLoading()
            getEventsMatchEnded()
            getEventsDamageDealt()
            getEventsRoundStarted()
            getEventsRoundEnded()
            getEventsLobbyDisplayed()
            getEventsMatchDisplayed()

        } else if (connected)  { events.add(Event(XRD_DISCONNECT)); connected = false }
        return events
    }

    private fun getEventsPlayerJoined() {
        val p1 = lobby.getOldFighters()
        val p2 = lobby.getNewFighters()
        log("totalPlayers","${p2.size}")
        p2.filter { np -> var flag = true
            p1.forEach { op -> if (op.getId() == np.getId()) flag = false }
            flag
        }.forEach { events.add(Event(PLAYER_JOINED, it)) }
    }

    private fun getEventsPlayerMoved() {
        val p1 = lobby.getOldFighters()
        val p2 = lobby.getNewFighters()
        p2.filter { np -> var flag = true
            p1.forEach { op -> if (
                op.getSeat() == np.getSeat()
                && op.getCabinet() == np.getCabinet()
            ) flag = false }
            flag
        }.forEach {
            events.add(Event(PLAYER_MOVED, it, it.getSeat()))
        }
    }

    private fun getEventsMatchLoading() {
        // FIXME: THIS NEEDS TO ONLY FIRE ONCE
        val fighters = lobby.getFighterPairs().filter { it.second.isLoading() && it.second.getLoadPercent() < it.first.getLoadPercent() }
        if (fighters.size == 2) {
            log("loadingP1", fighters[0].second.getLoadPercent().toString())
            log("loadingP2", fighters[1].second.getLoadPercent().toString())
            events.add(Event(MATCH_LOADING, Pair(fighters[0].second, fighters[1].second)))
        }
    }

    private fun getEventsMatchEnded() {
        val fightsOut = Duo(Fighter(), Fighter())
        var winningSide = Duo(-1, -1)
        lobby.getFighterPairs().forEach {
            val of = it.first
            val nf = it.second
            if (nf.getMatchesWon() > of.getMatchesWon() && nf.getMatchesPlayed() > of.getMatchesPlayed()) {
                when (nf.getSeat()) {
                    0 -> { fightsOut.p1 = nf; winningSide = Duo(1, 0) }
                    1 -> { fightsOut.p2 = nf; winningSide = Duo(0, 1) } } }
            if (nf.getMatchesWon() == of.getMatchesWon() && nf.getMatchesPlayed() > of.getMatchesPlayed()) {
                when (nf.getSeat()) {
                    0 -> { fightsOut.p1 = nf; winningSide = Duo(0, 1)}
                    1 -> { fightsOut.p2 = nf; winningSide = Duo(1, 0)} } }
        }
        if (fightsOut.p1.isValid() && fightsOut.p2.isValid()) events.add(Event(MATCH_ENDED, Pair(fightsOut.p1, fightsOut.p2), Pair(winningSide.p1, winningSide.p2)))
    }

    private fun getEventsRoundStarted() {
        val oldMatch = lobby.getMatch(getClient().getCabinet()).first
        val newMatch = lobby.getMatch(getClient().getCabinet()).second
        log("Player 1 HP","${newMatch.getHealth(0)}")
        log("Player 2 HP","${newMatch.getHealth(1)}")
        log("TEST", "${newMatch.getHealth(0) == 420 && newMatch.getHealth(1) == 420}")
        if(oldMatch.getHealth(0) != 420 || oldMatch.getHealth(1) != 420)
            if(newMatch.getHealth(0) == 420 && newMatch.getHealth(1) == 420)
                events.add(Event(ROUND_STARTED))
    }

    private fun getEventsRoundEnded() {
        val oldMatch = lobby.getMatch(getClient().getCabinet()).first
        val newMatch = lobby.getMatch(getClient().getCabinet()).second
        if((newMatch.getHealth(0) == 0 && oldMatch.getHealth(0) != 0) || (newMatch.getHealth(1) == 0 && oldMatch.getHealth(1) != 0))
            events.add(Event(ROUND_ENDED, newMatch.fighters, newMatch.getHealth()))

//            // Has the round ended, and did player 1 win?
//            if (roundOngoing && getWinner() == -1 && getHealth(P2) == 0 && getHealth(P1) != getHealth(P2) ) {
//                roundOngoing = false
//                session.setMode(SLASH_MODE)
//                utils.log("[MATC] ID$matchId P1 wins Duel ${getRounds(P1) + getRounds(P2) + 1}")
//            }
//
//            // Has the round ended, and did player 2 win?
//            if (roundOngoing && getWinner() == -1 && getHealth(P1) == 0 && getHealth(P2) != getHealth(P1)) {
//                roundOngoing = false
//                session.setMode(SLASH_MODE)
//                utils.log("[MATC] ID$matchId P2 wins Duel ${getRounds(P1) + getRounds(P2) + 1}")
//            }
        //events.add(Event(ROUND_ENDED))
    }

    private fun getEventsDamageDealt() {
        //events.add(Event(DAMAGE_DEALT))
    }

    private fun getEventsLobbyDisplayed() {
        //events.add(Event(LOBBY_DISPLAYED))
    }

    private fun getEventsMatchDisplayed() {
        //events.add(Event(MATCH_DISPLAYED))
    }

    fun getClient(): Fighter {
        if (lobby.getFighterPairs().size == 1) {
            val clientId = xrdApi.getClientSteamId()
            return lobby.getNewFighters().first { it.getId() == clientId }
        } else return Fighter()
    }

}