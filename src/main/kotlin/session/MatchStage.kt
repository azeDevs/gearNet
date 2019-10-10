package session

import MyApp.Companion.WD
import application.LogText.Effect.RED
import application.LogText.Effect.YLW
import application.log
import memscan.MatchSnap
import twitch.ViewerBet
import utils.SessionMode
import utils.SessionMode.Mode.VICTORY
import utils.addCommas
import utils.isInRange
import utils.plural
import kotlin.math.abs

/**
 *  [MatchStage]
 *  MatchStage handles all match creation, identification, and processing.
 *  It also stores state for ViewerBets so that bets are tied to Match lifecycle
 */
class MatchStage(private val s: Session) {
    private val archivedMatches: HashMap<Long, Match> = HashMap()
    private var match: Match = Match()

    private fun getMatches(): List<Match> = archivedMatches.values.filter { it.isValid() }
    private fun getLastMatch() = if (getMatches().isNotEmpty()) getMatches()[getMatches().lastIndex] else Match()
    fun isMatchValid() = match.isValid()
    fun match() = match

    fun addSnap(ms: MatchSnap):Boolean = match.update(ms)
    fun addBet(vb: ViewerBet):Boolean = match.addViewerBet(vb)

    /**
     *  [FINALIZING]
     *  For the current match to be finalized means to payout on all of its ViewerBets,
     *  and to archive it with a winner defined.
     */
    fun finalizeMatch() {
        if (!isInRange(match.getWinner(), 0, 1)) {
            if (match.getBets().isNotEmpty())
                log("Match invalidated ${match.getBets().size} ${plural("bet", match.getBets().size)}")
        } else {
            match.getBets().forEach {
                it.getViewer().changeScore(it.getWager(match.getWinner()), it.getWager(abs(match.getWinner()-1)))
                logViewerBetResolution(it)
            }
            log(L("Match finalized", RED))
            archiveMatch()
        }
        s.update(VICTORY)
        stageMatch()
    }

    private fun logViewerBetResolution(it: ViewerBet) {
        val sb = StringBuilder("Viewer ${it.getViewer().getName()} ")
        when {
            it.getViewer().getScoreDelta() > 0 -> sb.append("WON ${addCommas(it.getViewer().getScoreDelta())} $WD, ")
            it.getViewer().getScoreDelta() < 0 -> sb.append("LOST ${addCommas(it.getViewer().getScoreDelta())} $WD, ")
            else -> sb.append("BROKE EVEN, ") }
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
            log(L("Match ${match.getId()} has failed to archive due to duplicate IDs", RED))
        } else {
            archivedMatches[match.getId()] = match
            log(L("Match ${match.getId()} has been archived.", RED))
        }
    }

    /**
     *  [STAGING]
     *  If the new Match will NOT have the same Fighters as current Seat 0 and 1
     *  With the Seated Winner and Seat 2, create a new Match.
     */
    private fun stageMatch() {
        val stageId = getLastMatch().getId() + 1
//        if (getMatches().isNotEmpty()) stageId = if (newId) { getLastMatch().getId() + 1 } else { match.getId() }

        var prospect = s.fighters().firstOrNull { it.getSeat() == 2 } ?: Fighter()
        val twoFighters = !prospect.isValid()
        if (twoFighters) prospect = getLastMatch().getLosingFighter()
        when (getLastMatch().getWinner()) {
            0 -> match = Match(stageId, Pair(getLastMatch().getWinningFighter(), prospect))
            1 -> match = Match(stageId, Pair(prospect, getLastMatch().getWinningFighter()))
            else -> log(L("Match stage attempted", YLW))
        }
    }

    /**
     *  [EVENT_CONDITIONS]
     *  ...
     */
    fun isMatchConcluded() = match.getTimer() == -1 && s.isMode(SessionMode.Mode.VICTORY)


}