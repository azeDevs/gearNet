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
    players: List<Player> = emptyList(),
    match: Match = Match()
) {

    val cabinetA: Cabinet = generateCab(0, players, match)
    val cabinetB: Cabinet = generateCab(1, players, match)
    val cabinetC: Cabinet = generateCab(2, players, match)
    val cabinetD: Cabinet = generateCab(3, players, match)

    private fun generateCab(cabinetId:Int, players: List<Player>, match: Match): Cabinet =
        Cabinet(
            players.filter { it.getCabinet() == cabinetId && it.getSeat() > 1 },
            if (match.cabinetId == cabinetId) match else Match()
        )

}



