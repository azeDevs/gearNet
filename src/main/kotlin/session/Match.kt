package session

import application.log
import memscan.MatchData
import twitch.ViewerBet
import java.lang.Math.abs

/**
 *
 * [Match]
 * contains fighting Players and Match data
 *
 */
class Match (
    private val matchId: Long = -1,
    private val fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter()),
    matchData: MatchData = MatchData()
) {

    // TODO: CONSTRUCT & STAGE A NEW Match WHEN BOTH SEATS ARE OCCUPIED
    // TODO: ADD Match TO Matches map WHEN SessionState REACHES MODE_MATCH
    private val snaps: MutableList<MatchData> = arrayListOf(matchData)
    private val viewerBets: MutableList<ViewerBet> = arrayListOf()
    private var winner = -1

    // Getters
    fun getId() = matchId
    fun getSnaps(): List<MatchData> = snaps.filter { it.isValid() }
    fun getSnap(): MatchData = if (snaps.isNotEmpty()) snaps[snaps.lastIndex] else MatchData()
    fun oldSnap(): MatchData = if (snaps.size > 1) snaps[snaps.lastIndex] else getSnap()
    fun getViewerBets(): List<ViewerBet> = viewerBets
    fun getViewerBets(seatId:Int) = if (seatId == 0) viewerBets.filter { it.hasWager(0) } else if (seatId == 1) viewerBets.filter { it.hasWager(1) } else emptyList()
    fun getWagers(seatId:Int): Int { var sum = 0; getViewerBets(seatId).forEach { sum += it.getWager(seatId) }; return sum }
    fun getChips(seatId:Int): Int { var sum = 0; getViewerBets(seatId).forEach { sum += it.getChips(seatId) }; return sum }
    fun addViewerBet(bet:ViewerBet) = viewerBets.add(bet)
    fun getWinner() = winner
    fun getWinningFighter() = getFighter(getWinner())
    fun getLosingFighter() = getFighter(abs(getWinner()-1))

    fun getTimer() = getSnap().timer
    fun getRounds(seatId:Int) = if (seatId == 0) getSnap().rounds.first else if (seatId == 1)  getSnap().rounds.second else MatchData().rounds.second
    fun getHealth(seatId:Int) = if (seatId == 0) getSnap().health.first else if (seatId == 1)  getSnap().health.second else MatchData().health.second
    fun getTension(seatId:Int) = if (seatId == 0) getSnap().tension.first else if (seatId == 1)  getSnap().tension.second else MatchData().tension.second
    fun getCanBurst(seatId:Int) = if (seatId == 0) getSnap().canBurst.first else if (seatId == 1)  getSnap().canBurst.second else MatchData().canBurst.second
    fun getStrikeStun(seatId:Int) = if (seatId == 0) getSnap().strikeStun.first else if (seatId == 1)  getSnap().strikeStun.second else MatchData().strikeStun.second
    fun getGuardGauge(seatId:Int) = if (seatId == 0) getSnap().guardGauge.first else if (seatId == 1)  getSnap().guardGauge.second else MatchData().guardGauge.second

    fun getFighters() = fighters
    fun getFighter(seatId:Int) = if (seatId == 0) fighters.first else if (seatId == 1)  fighters.second else Fighter()
    fun hasSameFighters(match:Match) = match.getFighter(0) == getFighter(0) && match.getFighter(1) == getFighter(1)

    fun equals(match: Match):Boolean = match.getSnap().equals(this.getSnap())
    fun isResolved() = winner > -1
    fun isValid() = matchId > -1 && getFighter(0).isValid() && getFighter(1).isValid()
    fun update(matchData:MatchData):Boolean {
        if (!isResolved() && !matchData.equals(getSnap())) {
            snaps.add(matchData)
            log("matchSnaps", snaps.size)
            if (getRounds(0) == 2) winner = 0
            if (getRounds(1) == 2) winner = 1
            return true
        }
        return false
    }
}