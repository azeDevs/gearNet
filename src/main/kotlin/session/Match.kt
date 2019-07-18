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

    private val P1 = 0
    private val P2 = 1

    private var winner = -1
    private var roundOngoing = false

    // Gotten from MatchData, else gotten from LobbyData (LOBBY QUALITY DATA)
    private var character = Pair(fighters.first.getCharacterId(), fighters.second.getCharacterId())
    private var handle = Pair(fighters.first.getName(), fighters.second.getName())
    private var rounds = Pair(matchData.rounds.first, matchData.rounds.second)
    private var health = Pair(matchData.health.first, matchData.health.second)

    // Gotten from MatchData, else considered useless (MATCH QUALITY DATA)
    private var matchTimer = matchData.timer
    private var tension = Pair(matchData.tension.first, matchData.tension.second)
    private var canBurst = Pair(matchData.canBurst.first, matchData.canBurst.second)
    private var strikeStun = Pair(matchData.strikeStun.first, matchData.strikeStun.second)
    private var guardGauge = Pair(matchData.guardGauge.first, matchData.guardGauge.second)

    fun getData() = matchData

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