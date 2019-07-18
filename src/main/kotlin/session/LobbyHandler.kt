package session

import utils.Duo
import utils.keepInRange

class LobbyHandler {
    private val lobby: Duo<Lobby> = Duo(Lobby(), Lobby())

    fun getLobby() = lobby
    fun update(fighters: List<Fighter>, clientMatch: Match) {
        lobby.p1 = lobby.p2
        lobby.p2 = Lobby(fighters, clientMatch)
    }

    // Match
    fun getMatch(cabinet: Int) = Pair(lobby.p1.getMatch(keepInRange(cabinet, 0, 3)), lobby.p2.getMatch(keepInRange(cabinet, 0, 3)))

    // Fighters
    fun getOldFighters() = lobby.p1.getFighters()
    fun getNewFighters() = lobby.p2.getFighters()
    fun getFighter(id:Long) = lobby.p2.getFighters().firstOrNull { it.getId() == id }
    fun getFighterPairs(): List<Pair<Fighter, Fighter>> {
        val pairs: MutableList<Pair<Fighter, Fighter>> = mutableListOf()
        val fighters1 = lobby.p1.getFighters()
        val fighters2 = lobby.p2.getFighters()
        fighters1.forEach { fs1 -> fighters2.forEach { fs2 ->
            if (fs1.getId() ==  fs2.getId()) pairs.add(Pair(fs1, fs2))
        } }
        return pairs
    }

}