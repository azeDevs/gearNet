package memscan

import MyApp.Companion.SIMULATE_MODE
import session.Event
import session.Lobby
import session.Match
import session.Player
import utils.Duo

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

    fun generateUpdate() {
        if (xrdApi.isConnected()) {
            // 1. Generate a Match and Players
            val players: List<Player> = xrdApi.getPlayerData().map { Player(it) }
            val client: Player = players.first { it.getSteamId() == xrdApi.getClientSteamId() }
            val matchP0 = players.first { it.getCabinet() == client.getCabinet() && it.getSeat() == 0 }
            val matchP1 = players.first { it.getCabinet() == client.getCabinet() && it.getSeat() == 1 }
            val clientMatch = Match(Duo(matchP0, matchP1), client.getCabinet(), xrdApi.getMatchData())

            // 2. Generate a new Lobby
            lobby.p1 = lobby.p2
            lobby.p2 = Lobby(players, clientMatch)

            // 4. Compare them and return an Event map
            val events: MutableList<Event> = mutableListOf()
            // TODO: MAKE THIS ASSEMBLE EVENTS LIST
        }
    }

}