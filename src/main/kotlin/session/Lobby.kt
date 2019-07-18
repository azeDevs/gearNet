package session

/**
 *
 * XrdListener                  updates and archives Lobby data.
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [Lobby]
 * contains past and present Lobby data
 *
 */
class Lobby (
    private val fighters: List<Fighter> = emptyList(),
    match: Match = Match()
) {
    val cabinets: List<Cabinet> = listOf(generateCab(0, fighters, match), generateCab(1, fighters, match), generateCab(2, fighters, match), generateCab(3, fighters, match))

    private fun generateCab(cabinetId:Int, fighters: List<Fighter>, match: Match): Cabinet =
        Cabinet(fighters.filter { it.getCabinet() == cabinetId && it.getSeat() > 1 },
            if (match.cabinetId == cabinetId) match else Match())

    fun getFighters(): List<Fighter> = fighters.filter { it.getId() > 0 }.toList()

}



