package session

import application.LogText.Effect.*
import memscan.MatchSnap
import twitch.ViewerBet
import utils.getIdStr

/**
 *
 * [Match]
 * contains fighting Players and Match data
 *
 */
class Match (
    private val matchId: Long = -1,
    private val fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter()),
    matchSnap: MatchSnap = MatchSnap()
) {
    private val snaps: MutableList<MatchSnap> = arrayListOf(matchSnap)
    private val viewerBets: MutableList<ViewerBet> = arrayListOf()

    private var roundsRed = 0
    private var roundsBlu = 0
    private var roundsDraw = 0

    private var winner = -1

    fun incrementRounds(seatId: Int = -1) = if (seatId == 0) roundsRed++ else if (seatId == 1) roundsBlu++ else roundsDraw++

    fun getId() = matchId
    fun getBets(): List<ViewerBet> = viewerBets
    fun betCount() = getBets().size
    fun getWinner() = winner
    fun getWinningFighter() = getFighter(getWinner())
    fun getTimer() = getSnap().timer()
    fun getHealth(seatId: Int) = getSnap().health(seatId)
    fun getFighter(seatId: Int) = if (seatId == 0) fighters.first else if (seatId == 1) fighters.second else Fighter()

    fun isValid() = matchId > -1 && getFighter(0).isValid() && getFighter(1).isValid()
    fun addViewerBet(bet: ViewerBet) = viewerBets.add(bet)
    fun update(matchSnap: MatchSnap): Boolean {
        if (!isResolved() && !matchSnap.isSameAs(getSnap())) {
            snaps.add(matchSnap)
            if (getRounds(0) == 2) this.winner = 0
            else if (getRounds(1) == 2) this.winner = 1
            return true
        }
        return false
    }
    fun isResolved() = winner > -1
    fun getRoundNumber() = getSnap().rounds(0) + getSnap().rounds(1) + 1
    fun getSnapCount() = snaps.size
    fun tookTheRound(): Int {
        if (snaps.size > 2 ) {
            if (getSnap().rounds(0) > snaps[snaps.lastIndex-2].rounds(0)) return 0
            else if (getSnap().rounds(1) > snaps[snaps.lastIndex-2].rounds(1)) return 1
        }
        return -1
    }

    private fun getRounds(seatId: Int) = getSnap().rounds(seatId)
    private fun getSnap(): MatchSnap = if (snaps.isNotEmpty()) snaps[snaps.lastIndex] else MatchSnap()

    fun getIdLog(colon:Boolean = true, matchId:Long = getId()) = L("Match${getIdStr(matchId)}${if (colon) ": " else ""}", TOX)
    fun getRoundLog(colon:Boolean = false, change:Int = 0) = L("Round ${getRoundNumber()+change}${if (colon) ": " else ""}", YLW)
    fun getFighterLog(seatId: Int) = when (seatId) {
        0 -> L(getFighter(0).getName(), RED)
        1 -> L(getFighter(1).getName(), BLU)
        else -> L("null", LOW)
    }

}