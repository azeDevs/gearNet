package session

import application.log
import memscan.MatchSnap
import twitch.ViewerBet

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
    private var winner = -1

    fun getId() = matchId
    fun getBets(): List<ViewerBet> = viewerBets
    fun getWinner() = winner
    fun getWinningFighter() = getFighter(getWinner())
    fun getTimer() = getSnap().timer
    fun getHealth(seatId: Int) = getSnap().health(seatId)
    fun getFighter(seatId: Int) = if (seatId == 0) fighters.first else if (seatId == 1) fighters.second else Fighter()

    fun isValid() = matchId > -1 && getFighter(0).isValid() && getFighter(1).isValid()
    fun addViewerBet(bet: ViewerBet) = viewerBets.add(bet)
    fun update(matchSnap: MatchSnap): Boolean {
        if (!isResolved() && !matchSnap.isSameAs(getSnap())) {
            snaps.add(matchSnap)
            if (getRounds(0) == 2) prepareMatchForArchive(0)
            else if (getRounds(1) == 2) prepareMatchForArchive(1)
            return true
        }
        return false
    }

    fun tookTheRound(seatId: Int): Boolean {
        if (snaps.size > 1) return getSnap().rounds(seatId) > snaps[snaps.lastIndex-1].rounds(seatId)
        else return false
    }
    private fun getRounds(seatId: Int) = getSnap().rounds(seatId)
    private fun isResolved() = winner > -1
    private fun getSnap(): MatchSnap = if (snaps.isNotEmpty()) snaps[snaps.lastIndex] else MatchSnap()
    private fun prepareMatchForArchive(winner:Int) {
        this.winner = winner
        log("Match Snaps: ", snaps.size)
    }

}