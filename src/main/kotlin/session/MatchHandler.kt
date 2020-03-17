package session

import memscan.FighterData
import memscan.MatchData
import models.Fighter
import models.Match
import utils.isInRange

class MatchHandler(val s: Session) {

    val archiveMatches: HashMap<Long, Match> = HashMap()
    val lobbyMatches = arrayListOf(Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()))
    var clientMatch = Match()

    private var loser = FighterData()
    private var winner = FighterData()

    fun updateClientMatch(matchData: MatchData): Boolean {
        val updatedMatchSnap = clientMatch.updateMatchSnap(matchData, s)
        for (i in 0..3) lobbyMatches[i] = Pair(-1L, Match())
        if (clientMatch.matchId != -1L) lobbyMatches[clientMatch.getCabinet().toInt()] = Pair(clientMatch.matchId, clientMatch)
        return updatedMatchSnap
    }

    fun resolveEveryone(players: HashMap<Long, Fighter>, data: FighterData): Boolean {
        val loserPlayer = players.values.firstOrNull { it.getPlayerId() == data.steamUserId && it.isLoser() } ?: Fighter()
        val winnerPlayer = players.values.firstOrNull { it.getPlayerId() == data.steamUserId && it.isWinner() } ?: Fighter()

        if (loserPlayer.getPlayerId() != -1L) loser = loserPlayer.getData()
        if (winnerPlayer.getPlayerId() != -1L) winner = winnerPlayer.getData()

        if (loser.steamUserId != -1L && winner.steamUserId != -1L) {
            log("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ ᴍᴀᴛᴄʜ ʀᴇᴄᴏʀᴅ ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")
            log("WINNER = ${winner.displayName} / LOSER = ${loser.displayName}")
            log("WINNER Chain: ${players[winner.steamUserId]!!.getRating()}")
            log(" LOSER Chain: ${players[loser.steamUserId]!!.getRating()}")
            resolveLobbyMatchResults(players)
            players.values.forEach { p -> if (!p.hasPlayed()) p.incrementBystanding(s) }
            log("Idle increment on ${players.values.filter { !it.hasPlayed() }.size} players")
            log("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")

            loser = FighterData()
            winner = FighterData()
            return true
        }
        return false
    }

    private fun resolveLobbyMatchResults(players: HashMap<Long, Fighter>) {
        val winnerSide = winner.playerSide.toInt()
        val loserBounty = players[loser.steamUserId]!!.getScoreTotal()
        val winnerBounty = players[winner.steamUserId]!!.getScoreTotal()

        log("WINNER Bounty: $winnerBounty")
        log(" LOSER Bounty: $loserBounty")

        val bonusLoserPayout = (players[loser.steamUserId]!!.getRating() * players[loser.steamUserId]!!.getMatchesWon()) + players[loser.steamUserId]!!.getMatchesSum() + (players[loser.steamUserId]!!.getRating() * 100)
        val bonusWinnerPayout = ((players[winner.steamUserId]!!.getRating()+1) * players[winner.steamUserId]!!.getMatchesWon()) + players[winner.steamUserId]!!.getMatchesSum() + ((players[winner.steamUserId]!!.getRating()+1) * 1000)

        log("WINNER Signing Bonus: $bonusWinnerPayout")
        log(" LOSER Signing Bonus: $bonusLoserPayout")

        val riskModifier = 0.32 + (0.02 * players[loser.steamUserId]!!.getRating()) - (0.01 * players[winner.steamUserId]!!.getRating())
        val payout = (loserBounty * riskModifier).toInt()

        log("RISK = $riskModifier / PAYOUT = $payout")

        if (!isInRange(bonusLoserPayout - payout, 0, 10)) {
            players[loser.steamUserId]!!.changeScore(bonusLoserPayout - payout)
            players[loser.steamUserId]!!.changeRating(-2)
        }
        players[winner.steamUserId]!!.changeScore(bonusWinnerPayout + payout)
        players[winner.steamUserId]!!.changeRating(1)

        /*
            ±0 NEUTRAL   =              (0± bountyInflate %, 0± betOnPayout %, 0± betOffPayout %)
            +1           = C            (+40 bountyInflate %, -8 betOnPayout %, +16 betOffPayout %)
            +2           = C+           (+80 bountyInflate %, -16 betOnPayout %, +32 betOffPayout %)
            +3           = B            (+160 bountyInflate %, -24 betOnPayout %, +64 betOffPayout %)
            +4           = B+           (+320 bountyInflate %, -32 betOnPayout %, +128 betOffPayout %)
            +5           = A            (+640 bountyInflate %, -40 betOnPayout %, +256 betOffPayout %)
            +6           = A+           (+1280 bountyInflate %, -48 betOnPayout %, +512 betOffPayout %)
            +7           = S            (+2560 bountyInflate %, -56 betOnPayout %, +1024 betOffPayout %)
            +8 APEX      = BOSS         (+5120 bountyInflate %, -64 betOnPayout %, +2048 betOffPayout %)
        */

        s.watchers.forEach {
            var scoreChange = 0
            when(winnerSide) {
                0 -> {
                    if(it.value.isTeamR() && !it.value.isTeamB()) scoreChange += ((100*riskModifier).toInt() + payout)
                    if(!it.value.isTeamR() && it.value.isTeamB()) scoreChange -= ((100*riskModifier).toInt() + payout)
                    if(it.value.isTeamR() && it.value.isTeamB()) scoreChange -= ((100*riskModifier).toInt() + (payout*riskModifier).toInt())
                }
                1 -> {
                    if(it.value.isTeamR() && !it.value.isTeamB()) scoreChange -= ((100*riskModifier).toInt() + payout)
                    if(!it.value.isTeamR() && it.value.isTeamB()) scoreChange += ((100*riskModifier).toInt() + payout)
                    if(it.value.isTeamR() && it.value.isTeamB()) scoreChange -= ((100*riskModifier).toInt() + (payout*riskModifier).toInt())
                }
            }
            it.value.changeScore(scoreChange)
            it.value.resetTeam()
        }


    }

}