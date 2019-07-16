package session

/**
 *
 * XrdListener                  updates and archives Lobby data.
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Player>     contains Player bounty and chains data
 *
 * [Lobby]
 * contains past and present Lobby data
 *
 */
class Lobby (
    private val players: List<Player> = emptyList(),
    match: Match = Match()
) {
    val cabinets: List<Cabinet> = listOf(generateCab(0, players, match), generateCab(1, players, match), generateCab(2, players, match), generateCab(3, players, match))

    private fun generateCab(cabinetId:Int, players: List<Player>, match: Match): Cabinet =
        Cabinet(players.filter { it.getCabinet() == cabinetId && it.getSeat() > 1 },
            if (match.cabinetId == cabinetId) match else Match())

    fun getPlayers(): List<Player> {
//        val allPlayers: MutableList<Player> = mutableListOf()
//        cabinets.forEach { allPlayers.addAll(it.getPlayers().filter { it.getSteamId() > 0 }.toList()) }
        return players.filter { it.getSteamId() > 0 }.toList()
    }

}



