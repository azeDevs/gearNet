package models

import memscan.MatchData

/**
 *
 * [Match]
 * contains fighting Players and Match data
 *
 */
data class Match (
    private val fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter()),
    private val matchData: MatchData = MatchData()
) {

    private val matchTimer = matchData.timer
    private val rounds = Pair(matchData.rounds.first, matchData.rounds.second)
    private val health = Pair(matchData.health.first, matchData.health.second)
    private val tension = Pair(matchData.tension.first, matchData.tension.second)
    private val canBurst = Pair(matchData.canBurst.first, matchData.canBurst.second)
    private val strikeStun = Pair(matchData.strikeStun.first, matchData.strikeStun.second)
    private val guardGauge = Pair(matchData.guardGauge.first, matchData.guardGauge.second)

    // Getters
    fun getMatchTimer() = matchTimer
    fun getRounds(seatId:Int) = if (seatId == 0) rounds.first else rounds.second
    fun getHealth(seatId:Int) = if (seatId == 0) health.first else health.second
    fun getTension(seatId:Int) = if (seatId == 0) tension.first else tension.second
    fun getCanBurst(seatId:Int) = if (seatId == 0) canBurst.first else canBurst.second
    fun getStrikeStun(seatId:Int) = if (seatId == 0) strikeStun.first else strikeStun.second
    fun getGuardGauge(seatId:Int) = if (seatId == 0) guardGauge.first else guardGauge.second

    fun getFighter(seatId:Int) = if (seatId == 0) fighters.first else fighters.second
    fun getFighters() = fighters

    fun equals(match: Match):Boolean = match.matchData.equals(this.matchData)
}