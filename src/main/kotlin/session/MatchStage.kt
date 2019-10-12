package session

import MyApp.Companion.WD
import application.LogText.Effect.*
import application.log
import memscan.MatchSnap
import twitch.ViewerBet
import utils.SessionMode.Mode.LOBBY
import utils.SessionMode.Mode.VICTORY
import utils.addCommas
import utils.getIdStr
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
        if (match.getWinningFighter().isValid()) {
            log(L("Match ${getIdStr(match.getId())}: ", YLW), L("Finalizing Match"))
            // Do stuff if there is a winner
            match.getBets().forEach {
                it.getViewer().changeScore(it.getWager(match.getWinner()), it.getWager(abs(match.getWinner()-1)))
                logViewerBetResolution(it)
            }
            log(L("Match ${getIdStr(match.getId())}: ", YLW), L("FINALIZED", GRN))
            archiveMatch()
            s.update(VICTORY)
        } else {
            // Invalidate stuff if there wasn't a winner
            log(L("Match ${getIdStr(match.getId())}: ", YLW), L("INVALIDATED", RED))
            if (match.getBets().isNotEmpty()) log(L("Match ${getIdStr(match.getId())}: ", YLW),
                    L("${match.getBets().size} ${plural("bet", match.getBets().size)} INVALIDATED", RED))
            match = Match()
            s.update(LOBBY)
        }

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
            log(L("Match ${getIdStr(match.getId())}: ", YLW), L("Failed to archive due to duplicate IDs", RED))
        } else {
            archivedMatches[match.getId()] = match
            log(L("Match ${getIdStr(match.getId())}: ", YLW), L("Successfully Archived", GRN))
        }
        stageMatch()
    }

    /**
     *  [STAGING]
     *  If the new Match will NOT have the same Fighters as current Seat 0 and 1
     *  With the Seated Winner and Seat 2, create a new Match.
     */
    fun stageMatch() {
        var matchId = -1L
        // If the last Match is valid, then new matchID is +1
        if (getLastMatch().isValid()) {
            matchId = getLastMatch().getId() + 1
        } else matchId = 0

        // Is there more than 1 fighter on the cabinet?
        if (isFighterSeatedAt(0) && isFighterSeatedAt(1)) {
            log(L("Match ${getIdStr(matchId)}: ", YLW), L("Staging..."))
            if (!isFighterSeatedAt(2)) {
                log(L("Match ${getIdStr(matchId)}: ", YLW), L("There are 2 Fighters on the cabinet"))

                /*
                 DO STUFF THAT WORKS WITH 2 FIGHTERS
                 WHEN THE LOBBY LOADS, THE SEATS WILL NOT CHANGE
                 TODO: CONFIRM THAT A MATCH ABOUT TO BE STAGED IS IDENTICAL TO THE CURRENT MATCH
                */
                val redFighter = s.fighters().firstOrNull { it.getSeat() == 0 } ?: Fighter()
                val bluFighter = s.fighters().firstOrNull { it.getSeat() == 1 } ?: Fighter()
                match = Match(matchId, Pair(redFighter, bluFighter))
            } else {
                log(L("Match ${getIdStr(matchId)}: ", YLW), L("There are 3+ Fighters on the cabinet"))

                /*
                 DO STUFF THAT WORKS WITH 3+ FIGHTERS
                 WHEN THE LOBBY LOADS, THE SEATS WILL CHANGE AFTER THE MATCH HAS ALREADY BEEN STAGED
                 TODO: CONFIRM THAT A MATCH ABOUT TO BE STAGED ISN'T IDENTICAL TO THE CURRENT MATCH
                */

                val prospect = s.fighters().firstOrNull() { it.isSeated(2) } ?: Fighter()
                if (prospect.isValid()) {
                    when (getLastMatch().getWinner()) {
                        0 -> match = Match(matchId, Pair(getLastMatch().getWinningFighter(), prospect))
                        1 -> match = Match(matchId, Pair(prospect, getLastMatch().getWinningFighter()))
                        else -> {
                            log(L("Match ${getIdStr(matchId)}: ", YLW), L("Stage FAILED, Last Match had no winner", RED))
                        }
                    }
                }
            }

            // Log the resulting Staged Match, failed or not
            log(L("Match ${getIdStr(matchId)}: ", YLW), L("Staged Fighters "),
                L(" ${match.getFighter(0).getName()}", RED), L(" vs "),
                L(" ${match.getFighter(1).getName()}", BLU))
        }

    }

    private fun isFighterSeatedAt(seatId: Int): Boolean {
        val seatCheck = s.fighters().firstOrNull { it.getSeat() == seatId } ?: Fighter()
        if (seatCheck.getCabinet() != 0) return false
        return seatCheck.isValid()
    }

    /**
     *  [EVENT_CONDITIONS]
     *  ...
     */
    fun isMatchConcluded() = match.getTimer() == -1 && s.isMode(VICTORY)


}