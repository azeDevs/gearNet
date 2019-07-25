package models

/**
 *
 * XrdHandler                  updates and archives Lobby data.
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [Cabinet]
 * contains Match and Players seating data
 *
 */
class Cabinet(
    private val queue: List<Fighter> = emptyList(),
    val match: Match = Match()
) {
    fun getFighters():List<Fighter> {
        val allFighters = emptySet<Fighter>().toMutableSet()
        allFighters.add(match.fighters.first)
        allFighters.add(match.fighters.second)
        allFighters.addAll(queue)
        return allFighters.toList()
    }
}