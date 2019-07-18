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
        val playersLoading = lobby.getNewFighters().filter { it.isLoading() }
        if (playersLoading.size == 2) {
            log("loadingP1", playersLoading[0].getLoadPercent().toString())
            log("loadingP2", playersLoading[1].getLoadPercent().toString())
            events.add(Event(MATCH_LOADING, Pair(playersLoading[0], playersLoading[1])))
        }
    }

    private fun getEventsMatchEnded() {
        val fightsOut = Duo(Fighter(), Fighter())
        var winningSide = Duo(-1, -1)
        lobby.getNewFighters().forEach { nf ->
            val of = lobby.getOldFighters().firstOrNull { it.getId() == nf.getId() } ?: Fighter()
            // Find the Winner
            if (nf.getMatchesWon() > of.getMatchesWon() && nf.getMatchesPlayed() > of.getMatchesPlayed()) {
                when (nf.getSeat()) {
                    0 -> { fightsOut.p1 = nf; winningSide = Duo(1, 0) }
                    1 -> { fightsOut.p2 = nf; winningSide = Duo(0, 1) }
                }
            }
            // Find the Loser
            if (nf.getMatchesWon() == of.getMatchesWon() && nf.getMatchesPlayed() > of.getMatchesPlayed()) {
                when (nf.getSeat()) {
                    0 -> { fightsOut.p1 = nf}
                    1 -> { fightsOut.p2 = nf}
                }
            }
        }
        if (fightsOut.p1.isValid() && fightsOut.p2.isValid()) events.add(Event(MATCH_LOADING, Pair(fightsOut.p1, fightsOut.p2), Pair(winningSide.p1, winningSide.p2)))
    }

    private fun getEventsRoundStarted() {
        //            // Has the round started?
//            if (!roundOngoing && getHealth(P1) == 420 && getHealth(P2) == 420 && getWinner() == -1) {
//                roundOngoing = true
//                session.setMode(MATCH_MODE)
//                utils.log("[MATC] ID$matchId Duel ${getRounds(P1) + getRounds(P2) + 1} ... LET'S ROCK!")
//            }
    }

    private fun getEventsRoundEnded() {

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

}