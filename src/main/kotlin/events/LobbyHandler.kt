package events

import models.Fighter
import models.Lobby
import models.Match
import utils.Duo

class LobbyHandler {
    private val lobby: Duo<Lobby> = Duo(Lobby(), Lobby())

    fun update(fighters: List<Fighter>, clientMatch: Match) {
        lobby.f1 = lobby.f2
        lobby.f2 = Lobby(fighters, clientMatch)
    }

    // Match
    fun getOldMatch() = lobby.f1.getMatch(0)
    fun getMatch() = lobby.f2.getMatch(0)

    // Fighters
    fun getOldFighters() = lobby.f1.getFighters()
    fun getNewFighters() = lobby.f2.getFighters()
    fun getFighterPairs(): List<Pair<Fighter, Fighter>> {
        val pairs: MutableList<Pair<Fighter, Fighter>> = mutableListOf()
        val fighters1 = lobby.f1.getFighters()
        val fighters2 = lobby.f2.getFighters()
        fighters1.forEach { fs1 -> fighters2.forEach { fs2 ->
            if (fs1.getId() ==  fs2.getId()) pairs.add(Pair(fs1, fs2))
        } }
        return pairs
    }

}