package session

import utils.Duo

class Cabinet {
    private val match = Match()
    private val fightingPlayers = Duo(Player(), Player())
    private val queuedUpPlayers = emptyList<Player>()
    private val spectatingPlayers = emptyList<Player>()

    fun getPlayers():List<Player> {
        val allPlayers = emptySet<Player>().toMutableSet()
        allPlayers.add(fightingPlayers.p1)
        allPlayers.add(fightingPlayers.p2)
        allPlayers.addAll(queuedUpPlayers)
        allPlayers.addAll(spectatingPlayers)
        return allPlayers.toList()
    }
}