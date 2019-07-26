package models

import memscan.MatchData

/**
 *
 * XrdHandler                  updates and archives Lobby data.
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [Match]
 * contains fighting Players and Match data
 *
 */
data class Match (
    val fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter()),
    val cabinetId: Int = -1,
    val matchData: MatchData = MatchData()
) {

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
    fun getRounds(seatId:Int) = if (seatId == 0) rounds.first else rounds.second
    fun getHealth(seatId:Int) = if (seatId == 0) health.first else health.second
    fun getHealth() = health
    fun getMatchTimer() = matchTimer
    fun getTension(seatId:Int) = if (seatId == 0) tension.first else tension.second
    fun getCanBurst(seatId:Int) = if (seatId == 0) canBurst.first else canBurst.second
    fun getStrikeStun(seatId:Int) = if (seatId == 0) strikeStun.first else strikeStun.second
    fun getGuardGauge(seatId:Int) = if (seatId == 0) guardGauge.first else guardGauge.second

}