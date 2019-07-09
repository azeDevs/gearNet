package session

import session.Event.Companion.GearNetEvent.*
import utils.Duo
import utils.keepInRange

class XrdLobby {
    private val roamingPlayers = emptyList<Player>()
    private val cabinets = listOf(Cabinet(),Cabinet(),Cabinet(),Cabinet())

    fun getXrdEvents(): List<Event> {
        val e: MutableList<Event> = emptyList<Event>().toMutableList()

        getPlayers().filter { it.isWinner() }.forEach { e.add(Event(PLAYER_WINS_MATCH, Duo(0, 0), Duo(it, Player()))) }
        getPlayers().filter { it.isLoser() }.forEach { e.add(Event(PLAYER_LOST_MATCH, Duo(0, 0), Duo(it, Player()))) }
        getPlayers().filter { it.isLoading() }.forEach { e.add(Event(PLAYER_LOADING_P1)) }

        return e
    }

    private fun getPlayers():List<Player> {
        val allPlayers = roamingPlayers.toMutableSet()
        for (i in 0..3) allPlayers.addAll(cabinets[i].getPlayers())
        return allPlayers.toList()
    }

    fun getCabinet(index:Int = 0) = cabinets[keepInRange(index, 0, 3)]

}



