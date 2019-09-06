package session

import MyApp.Companion.WD
import application.log
import memscan.MatchData
import twitch.ViewerBet
import utils.addCommas
import utils.isInRange
import utils.plural
import kotlin.math.abs

/**
 *  [MatchStage]
 *
 */
class MatchStage {
    private val archivedMatches: HashMap<Long, Match> = HashMap()
    private var match: Match = Match()

    fun getMatches(): List<Match> = archivedMatches.values.filter { it.isValid() }
    fun getLastMatch() = if (getMatches().isNotEmpty()) getMatches()[getMatches().lastIndex] else Match()
    fun getMatch() = match
    fun update(md: MatchData):Boolean = getMatch().update(md)
    fun addBet(vb: ViewerBet):Boolean = getMatch().addViewerBet(vb)
    fun getBets() = match.getViewerBets()

    /**
     *  [FINALIZING]
     *  For the current match to be finalized means to payout on all of its ViewerBets,
     *  and to archive it with a winner defined.
     */
    fun finalizeMatch(state: SessionState) {
        if (!isInRange(match.getWinner(), 0, 1)) {
            if (getBets().size > 0) log("Match invalidated ${getBets().size} ${plural("bet", getBets().size)}")
        } else {
            match.getViewerBets().forEach {
                it.getViewer().changeScore(it.getWager(match.getWinner()), it.getWager(abs(match.getWinner()-1)))
                logViewerBetResolution(it)
            }
            log("Match finalized")
            archiveMatch()
        }
        stageMatch(state)
    }

    private fun logViewerBetResolution(it: ViewerBet) {
        val sb = StringBuilder("Viewer ${it.getViewer().getName()} ")
        if (it.getViewer().getScoreDelta() > 0) sb.append("WON ${addCommas(it.getViewer().getScoreDelta())} $WD, ")
        else if (it.getViewer().getScoreDelta() < 0) sb.append("LOST ${addCommas(it.getViewer().getScoreDelta())} $WD, ")
        else sb.append("BROKE EVEN, ")
        sb.append("wallet total now ${addCommas(it.getViewer().getScoreTotal())} $WD")
        log(sb.toString())
    }

    /**
     *  [ARCHIVING]
     *  For the current match to be archived means to add it to archivedMatches,
     *  and to replace match by calling stageNewMatch.
     */
    private fun archiveMatch() {
        if (archivedMatches.containsKey(match.getId())) {
            log("Match ${match.getId()} has failed to archive due to duplicate IDs")
        } else {
            archivedMatches.put(match.getId(), match)
            log("Match ${match.getId()} has been archived.")
        }
    }

    /**
     *  [STAGING]
     *  If the new Match will NOT have the same Fighters as current Seat 0 and 1
     *  With the Seated Winner and Seat 2, create a new Match.
     */
    private fun stageMatch(state:SessionState, newId:Boolean = true) {
        val stageId = if (newId) match.getId() + 1 else match.getId()
        var prospect = state.getFighters().firstOrNull { it.getSeat() == 2 } ?: Fighter()
        val twoFighters = !prospect.isValid()
        if (twoFighters) prospect = getLastMatch().getLosingFighter()
        when (getLastMatch().getWinner()) {
            0 -> match = Match(stageId, Pair(getLastMatch().getWinningFighter(), prospect))
            1 -> match = Match(stageId, Pair(prospect, getLastMatch().getWinningFighter()))
            else -> log("Match stage attempted")
        }
    }

}