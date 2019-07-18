package session

import utils.Duo

class LobbyHandler {
    private val lobby: Duo<Lobby> = Duo(Lobby(), Lobby())

    fun getLobby() = lobby
    fun update(fighters: List<Fighter>, clientMatch: Match) {
        lobby.p1 = lobby.p2
        lobby.p2 = Lobby(fighters, clientMatch)
    }

    fun getOldFighters() = lobby.p1.getFighters()
    fun getNewFighters() = lobby.p2.getFighters()
    fun getFighter(id:Long) = lobby.p2.getFighters().firstOrNull { it.getId() == id }

}