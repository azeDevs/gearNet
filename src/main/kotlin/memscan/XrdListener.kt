package memscan

import MyApp.Companion.SIMULATE_MODE
import session.Event
import session.EventType.*
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

    var connected = false
    private val xrdApi: XrdApi = if (SIMULATE_MODE) MemRandomizer() else MemHandler()
    private val lobby: Duo<Lobby> = Duo(Lobby(), Lobby())
    val events: MutableList<Event> = mutableListOf()

    fun generateUpdate(): List<Event> {
        events.clear()
        if (xrdApi.isConnected()) {
            if (!connected) { events.add(Event(XRD_CONNECTED)); connected = true }

            // 1. Generate a Match and Players
            val players: List<Player> = xrdApi.getPlayerData().map { Player(it) }
            val client: Player = players.firstOrNull { it.getSteamId() == xrdApi.getClientSteamId() } ?: Player()
            val matchP0 = players.firstOrNull { it.getCabinet() == client.getCabinet() && it.getSeat() == 0 } ?: Player()
            val matchP1 = players.firstOrNull { it.getCabinet() == client.getCabinet() && it.getSeat() == 1 } ?: Player()
            val clientMatch = Match(Duo(matchP0, matchP1), client.getCabinet(), xrdApi.getMatchData())

            // 2. Archive old Lobby and generate a new one
            lobby.p1 = lobby.p2
            lobby.p2 = Lobby(players, clientMatch)

            // 3. Generate Events
            getEventsPlayerJoined()
            getEventsPlayerMoved()
            getEventsMatchLoading()
            getEventsMatchEnded()
            getEventsBurstEnabled()
            getEventsStrikeStunned()
            getEventsDamageDealt()
            getEventsRoundEnded()
            getEventsLobbyDisplayed()
            getEventsMatchDisplayed()

        } else if (connected)  {
            events.add(Event(XRD_DISCONNECT))
            connected = false
        }
        return events
    }

    private fun getEventsPlayerJoined() {
        val p1 = lobby.p1.getPlayers()
        val p2 = lobby.p2.getPlayers()
        log("totalPlayers","${p2.size}")
        p2.filter { np -> var flag = true
            p1.forEach { op -> if (op.getSteamId() == np.getSteamId()) flag = false }
            flag
        }.forEach { events.add(Event(PLAYER_JOINED, Duo(it), Duo(0))) }
    }

    private fun getEventsPlayerMoved() {
        val p1 = lobby.p1.getPlayers()
        val p2 = lobby.p2.getPlayers()
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
        val psloading = lobby.p2.getPlayers().filter { it.isLoading() }
        if (psloading.size == 2) {
            log("loadingP1", psloading[0].getLoadPercent().toString())
            log("loadingP2", psloading[1].getLoadPercent().toString())
            events.add(Event(MATCH_LOADING, Duo(psloading[0],psloading[1]), Duo(0)))
        }
    }

    private fun getEventsMatchEnded() {
        //events.add(Event(MATCH_ENDED))
    }

    private fun getEventsBurstEnabled() {
        //events.add(Event(BURST_ENABLED))
    }

    private fun getEventsStrikeStunned() {
        //events.add(Event(STRIKE_STUNNED))
    }

    private fun getEventsDamageDealt() {
        //events.add(Event(DAMAGE_DEALT))
    }

    private fun getEventsRoundEnded() {
        //events.add(Event(ROUND_ENDED))
    }

    private fun getEventsLobbyDisplayed() {
        //events.add(Event(LOBBY_DISPLAYED))
    }

    private fun getEventsMatchDisplayed() {
        //events.add(Event(MATCH_DISPLAYED))
    }

}