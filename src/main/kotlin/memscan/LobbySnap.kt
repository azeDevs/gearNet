package memscan

class LobbySnap(private val players: List<PlayerData> = emptyList(), lobbyData: LobbyData = LobbyData()) {

    private val name: String = lobbyData.lobbyName
    private val wins: Int = lobbyData.roundWins
    private val cabs: MutableList<List<PlayerData>> = ArrayList()

    init { for (i in 0..lobbyData.openCabinets) cabs.add(players.filter { it.cabinetLoc.toInt() == 0 }.sortedBy { it.playerSide }.toList()) }

    fun getLobbyName() = name
    fun getLobbyWins() = wins
    fun getLobbyPlayers() = players.filter { it.steamUserId > 0L }
    fun getCabinetPlayers(i:Int) = cabs[i]
    fun getLoadingPlayers() = getLobbyPlayers().filter { it.loadingPct in 1..99 }

}