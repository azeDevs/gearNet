package session

import memscan.MatchData
import twitch.ViewerBet

/**
 *
 * [Match]
 * contains fighting Players and Match data
 *
 */
class Match (
    private val fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter()),
    private val matchData: MatchData = MatchData()
) {

    // TODO: CONSTRUCT & STAGE A NEW Match WHEN BOTH SEATS ARE OCCUPIED
    // TODO: ADD Match TO Matches map WHEN SessionState REACHES MODE_MATCH
    private val matchId = -1L
    private val viewerBets: MutableList<ViewerBet> = arrayListOf()


    // Getters
    fun getTimer() = matchData.timer
    fun getRounds(seatId:Int) = if (seatId == 0) matchData.rounds.first else matchData.rounds.second
    fun getHealth(seatId:Int) = if (seatId == 0) matchData.health.first else matchData.health.second
    fun getTension(seatId:Int) = if (seatId == 0) matchData.tension.first else matchData.tension.second
    fun getCanBurst(seatId:Int) = if (seatId == 0) matchData.canBurst.first else matchData.canBurst.second
    fun getStrikeStun(seatId:Int) = if (seatId == 0) matchData.strikeStun.first else matchData.strikeStun.second
    fun getGuardGauge(seatId:Int) = if (seatId == 0) matchData.guardGauge.first else matchData.guardGauge.second

    fun getFighter(seatId:Int) = if (seatId == 0) fighters.first else fighters.second
    fun getFighters() = fighters

    fun equals(match: Match):Boolean = match.matchData.equals(this.matchData)
}