package session

import MyApp.Companion.WD
import application.LogText.Effect.*
import application.log
import memscan.MatchSnap
import twitch.ViewerBet
import utils.SessionMode.Mode.*
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

    /*
        FIXME: ACCOUNT FOR MOVING INTO STAGED SEATING FROM SPECTATOR
        FIXME: ACCOUNT FOR DRAWS, THE WHOLE SYSTEMJ SHITS ITSELF, FIX THAT TOO

    */

    private val archivedMatches: HashMap<Long, Match> = HashMap()
    private var match: Match = Match()

    private fun getMatches(): List<Match> = archivedMatches.values.filter { it.isValid() }
    private fun getLastMatch() = if (getMatches().isNotEmpty()) getMatches()[getMatches().lastIndex] else Match()
    fun clearStage() { match = Match() }
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
        if (match.getWinningFighter().isValid() && s.isMode(MATCH)) {
            // Do stuff if there is a winner
            s.updateMode(VICTORY)
            finalizePayouts()
            archiveMatch()
        } else if (!match.getWinningFighter().isValid() && !s.isMode(VICTORY)) {
            // Do stuff if there wasn't a winner
            if (!s.isMode(LOBBY)) s.updateMode(LOBBY)
            log(getIdLog(), L("INVALIDATED", RED))
            // Invalidate any Bets
            if (match.getBets().isNotEmpty())
                log(getIdLog(), L("${match.betCount()} ${plural("bet", match.betCount())} INVALIDATED", RED))
        }
        clearStage()
    }

    private fun getIdLog() = L("Match${getIdStr(match.getId())}: ", TOX)

    private fun finalizePayouts() {
        match.getBets().forEach {
            it.getViewer().changeScore(it.getWager(match.getWinner()), it.getWager(abs(match.getWinner()-1)))
            logViewerBetResolution(it) }
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
            log(getIdLog(), L("Archive FAILED due to duplicate IDs", RED))
        } else {
            archivedMatches[match.getId()] = match
            log(getIdLog(), L("ARCHIVED SUCCESSFULLY", GRN))
        }
    }

    /**
     *  [STAGING]
     *  If the new Match will NOT have the same Fighters as current Seat 0 and 1
     *  With the Seated Winner and Seat 2, create a new Match.
     */
    fun stageMatch() {
        val matchId = if (getMatches().isNotEmpty()) {
            if (getLastMatch().isValid()) getLastMatch().getId()+1 else -1
        } else 0


        // Is there more than 1 fighter on the cabinet?
        if (isFighterSeatedAt(0) && isFighterSeatedAt(1) && !match.isValid()) {
            if (!isFighterSeatedAt(2)) {
                log(L("STAGING ", YLW), getIdLog(), L("(2 Fighters on Cabinet)", LOW))

                /*
                 DO STUFF THAT WORKS WITH 2 FIGHTERS
                 WHEN THE LOBBY LOADS, THE SEATS WILL NOT CHANGE
                 TODO: CONFIRM THAT A MATCH ABOUT TO BE STAGED IS IDENTICAL TO THE CURRENT MATCH
                */
                val redFighter = s.fighters().firstOrNull { it.getSeat() == 0 } ?: Fighter()
                val bluFighter = s.fighters().firstOrNull { it.getSeat() == 1 } ?: Fighter()
                match = Match(matchId, Pair(redFighter, bluFighter))
            } else {
                log(L("STAGING ", YLW), getIdLog(), L(" (3+ Fighters on Cabinet)", LOW))

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
                            log(getIdLog(), L("Stage FAILED, Last Match had no winner", RED))
                        }
                    }
                }
            }


            // Log the resulting Staged Match, failed or not
            log(getIdLog(), L("Staged Fighters "),
                L(match.getFighter(0).getName(), RED), L(" vs "),
                L(match.getFighter(1).getName(), BLU))
        }

    }

    private fun isFighterSeatedAt(seatId: Int): Boolean {
        val seatCheck = s.fighters().firstOrNull { it.getSeat() == seatId } ?: Fighter()
        if (seatCheck.getCabinet() != 0) return false
        return seatCheck.isValid()
    }

}