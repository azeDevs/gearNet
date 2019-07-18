package session

import memscan.MatchData

/**
 *
 * XrdListener                  updates and archives Lobby data.
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [Match]
 * contains fighting Players and Match data
 *
 */
class Match (
    val fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter()),
    val cabinetId: Int = -1,
    val matchData: MatchData = MatchData()
) {
    val winner = -1

    // Gotten from MatchData, else gotten from LobbyData (LOBBY QUALITY DATA)
    private val character = Pair(fighters.first.getCharacterId(), fighters.second.getCharacterId())
    private val handle = Pair(fighters.first.getName(), fighters.second.getName())
    private val rounds = Pair(matchData.rounds.first, matchData.rounds.second)
    private val health = Pair(matchData.health.first, matchData.health.second)
    // Gotten from MatchData, else considered useless (MATCH QUALITY DATA)
    private val matchTimer = matchData.timer
    private val tension = Pair(matchData.tension.first, matchData.tension.second)
    private val canBurst = Pair(matchData.canBurst.first, matchData.canBurst.second)
    private val strikeStun = Pair(matchData.strikeStun.first, matchData.strikeStun.second)
    private val guardGauge = Pair(matchData.guardGauge.first, matchData.guardGauge.second)

    // Getters
    fun getHealth() = health
    fun getHealth(seatId:Int) = if (seatId == 0) health.first else health.second
    fun geRounds(seatId:Int) = if (seatId == 0) rounds.first else rounds.second

    fun getCabinetString(cabId:Int = cabinetId): String {
        return when(cabId) {
            0 -> "CABINET A"
            1 -> "CABINET B"
            2 -> "CABINET C"
            3 -> "CABINET D"
            else -> "$cabId"
        }
    }

}