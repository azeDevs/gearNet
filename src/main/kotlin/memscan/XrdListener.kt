package memscan

import MyApp.Companion.SIMULATE_MODE
import session.Event
import session.EventType.PLAYER_JOINED
import session.Lobby
import session.Match
import session.Player
import utils.Duo
import utils.log

/**
 *
 * XrdListener                  updates and archives Lobby data
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Player>     contains Player bounty and chains data
 *
 * [XrdListener]
 * updates and archives Lobby data
 *
 */
class XrdListener {

    private val xrdApi: XrdApi = if (SIMULATE_MODE) MemRandomizer() else MemHandler()
    private val lobby: Duo<Lobby> = Duo(Lobby(), Lobby())
    val events: MutableList<Event> = mutableListOf()

    fun generateUpdate(): List<Event> {
        events.clear()
        if (xrdApi.isConnected()) {
            // 1. Generate a Match and Players
            val players: List<Player> = xrdApi.getPlayerData().map { Player(it) }
            val client: Player = players.firstOrNull { it.getSteamId() == xrdApi.getClientSteamId() } ?: Player()
            val matchP0 = players.firstOrNull { it.getCabinet() == client.getCabinet() && it.getSeat() == 0 } ?: Player()
            val matchP1 = players.firstOrNull { it.getCabinet() == client.getCabinet() && it.getSeat() == 1 } ?: Player()
            val clientMatch = Match(Duo(matchP0, matchP1), client.getCabinet(), xrdApi.getMatchData())

            // 2. Archive old Lobby and generate a new one
            lobby.p1 = lobby.p2
            lobby.p2 = Lobby(players, clientMatch)

            // 3. Generate Lobby Events
            generateLobbyEvents()

            // 4. Generate Match Events
            generateMatchEvents()

            // 4. Generate Client Events
            generateClientEvents()
        }
        val out = events
        return out
    }

    private fun getPlayersJoining(): List<Player> {
        val p1 = lobby.p1.getPlayers()
        val p2 = lobby.p2.getPlayers()
        log("p1.size","${p1.size}")
        log("p2.size","${p2.size}")

        return p2.filter { np -> var flag = true
            p1.forEach { op -> if (op.getSteamId() == np.getSteamId()) flag = false }
            flag
        }
    }

    private fun generateLobbyEvents() {
        // PLAYER_JOINED
        getPlayersJoining().forEach { events.add(Event(PLAYER_JOINED, Duo(it), Duo(0))) }

        // LOBBY_PLAYER_MOVED
        // if the cabinetId == 4, then the player is roaming

        // LOBBY_MATCH_ENDED
    }

    private fun generateMatchEvents(): List<Event> {
        val matchEvents: MutableList<Event> = mutableListOf()
        //MATCH_LOADING,
        //MATCH_BURST_ENABLED,
        //MATCH_STRIKE_STUNNED,
        //MATCH_DAMAGE_DEALT,
        //MATCH_ROUND_ENDED,
        return matchEvents
    }

    private fun generateClientEvents(): List<Event> {
        val clientEvents: MutableList<Event> = mutableListOf()
        //CLIENT_DISPLAYED_LOBBY,
        //CLIENT_DISPLAYED_MATCH
        return clientEvents
    }

}

typealias LookupEntity = (op:Player, np:Player) -> Boolean