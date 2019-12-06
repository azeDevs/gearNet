package session

import MyApp.Companion.WD
import application.LogText.Effect.*
import application.log
import memscan.MatchSnap
import session.modes.ModeLobby
import session.modes.ModeMatch
import session.modes.ModeVictory
import twitch.ViewerBet
import utils.addCommas
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
        NOTE: SLASH MODE AND MATCH MODE VIOLENTLY ALTERNATE WHEN A DRAW HAPPENS, MITE B USEFUL
    */

    private val archivedMatches: HashMap<Long, Match> = HashMap()
    private var m: Match = Match()

    private fun getMatches(): List<Match> = archivedMatches.values.filter { it.isValid() }
    private fun clearStage() { m = Match() }
    fun getLastMatch() = if (getMatches().isNotEmpty()) getMatches()[getMatches().lastIndex] else Match()
    fun isMatchValid() = m.isValid()
    fun match() = m



    fun addSnap(ms: MatchSnap):Boolean = m.update(ms)
    fun addBet(vb: ViewerBet):Boolean = m.addViewerBet(vb)

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
        if (isFighterSeatedAt(0) && isFighterSeatedAt(1) && !m.isValid()) {
            if (!isFighterSeatedAt(2)) doStuffThatWorksWith2Fighters(matchId)
            else doStuffThatWorkWith3PlusFighters(matchId)

            // Log the resulting Staged Match, failed or not
            log(m.getIdLog(), L(" SUCCESSFULLY STAGED Fighters ", GRN), m.fighter(0).getLog(), L(" vs ", MED), m.fighter(1).getLog())
        }

    }

    private fun doStuffThatWorkWith3PlusFighters(matchId: Long) {
        log(L("STAGING ", YLW_FIGHT), m.getIdLog(false, matchId), L(" (3+ Fighters on Cabinet)", LOW))

        /*
         DO STUFF THAT WORKS WITH 3+ FIGHTERS
         WHEN THE LOBBY LOADS, THE SEATS WILL CHANGE AFTER THE MATCH HAS ALREADY BEEN STAGED
         TODO: CONFIRM THAT A MATCH ABOUT TO BE STAGED ISN'T IDENTICAL TO THE CURRENT MATCH
        */
        val prospect = s.getFighters().firstOrNull { it.isSeated(2) } ?: Fighter()
        if (prospect.isValid()) {
            when (getLastMatch().getWinner()) {
                0 -> m = Match(matchId, Pair(getLastMatch().getWinningFighter(), prospect))
                1 -> m = Match(matchId, Pair(prospect, getLastMatch().getWinningFighter()))
                else -> {
                    log(m.getIdLog(), L(" STAGING FAILED", RED), L(", last Match had no winner", MED))
                }
            }
        }
    }

    private fun doStuffThatWorksWith2Fighters(matchId: Long) {
        log(L("STAGING ", YLW_FIGHT), m.getIdLog(false, matchId), L(" (2 Fighters on Cabinet)", LOW))

        /*
         DO STUFF THAT WORKS WITH 2 FIGHTERS
         WHEN THE LOBBY LOADS, THE SEATS WILL NOT CHANGE
         TODO: CONFIRM THAT A MATCH ABOUT TO BE STAGED IS IDENTICAL TO THE CURRENT MATCH
        */
        val redFighter = s.getFighters().firstOrNull { it.getSeat() == 0 } ?: Fighter()
        val bluFighter = s.getFighters().firstOrNull { it.getSeat() == 1 } ?: Fighter()
        m = Match(matchId, Pair(redFighter, bluFighter))
    }

    /**
     *  [ARCHIVING]
     *  For the current match to be archived means to add it to archivedMatches,
     *  and to replace match by calling stageNewMatch.
     */
    private fun archiveMatch() {
        if (archivedMatches.containsKey(m.getId())) {
            log(m.getIdLog(false), L(" FAILED TO ARCHIVE", RED), L(", duplicate IDs", MED))
        } else {
            archivedMatches[m.getId()] = m
            log(m.getIdLog(false), L(" WAS ARCHIVED SUCCESSFULLY", GRN))
        }
    }

    /**
     *  [FINALIZING]
     *  For the current match to be finalized means to payout on all of its ViewerBets,
     *  and to archive it with a winner defined.
     */
    fun finalizeMatch() {
        if (m.getWinningFighter().isValid() && s.isMode(ModeMatch(s))) {
            // Do stuff if there is a winner
            s.mode().update(ModeVictory(s))
            val winner = m.getWinningFighter()
            s.sendMessage("${winner.getName()} WINS!")
            log(m.getIdLog(false), L(" FINALIZED: ", GRN), L("${m.getSnapCount()}", YLW_FIGHT), L(" snaps, ", GRN), m.fighter(winner.getSeat()).getLog(), L(" wins", GRN), m.getMatchLog())
            finalizePayouts()
            archiveMatch()
        } else if (!m.getWinningFighter().isValid() && !s.isMode(ModeVictory(s))) {
            // Do stuff if there wasn't a winner
            if (!s.isMode(ModeLobby(s))) s.mode().update(ModeLobby(s))
            log(m.getIdLog(false), L(" INVALIDATED", RED), m.getMatchLog())
            // Invalidate any Bets
            if (m.getBets().isNotEmpty())
                log(m.getIdLog(), L(" ${m.betCount()} ${plural("bet", m.betCount())} INVALIDATED", RED), m.getMatchLog())
        }
        clearStage()
    }

    private fun finalizePayouts() {
        m.getBets().forEach {
            it.getViewer().changeScore(it.getWager(m.getWinner()), it.getWager(abs(m.getWinner()-1)))
            logViewerBetResolution(it) }
    }

    private fun isFighterSeatedAt(seatId: Int): Boolean {
        val seatCheck = s.getFighters().firstOrNull { it.getSeat() == seatId } ?: Fighter()
        if (seatCheck.getCabinet() != 0) return false
        return seatCheck.isValid()
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

}