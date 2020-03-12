package session

import memscan.MatchData
import memscan.PlayerData
import utils.isInRange

class MatchHandler {

    val archiveMatches: HashMap<Long, Match> = HashMap()
    var lobbyMatches = arrayListOf(Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()))
    var clientMatch = Match()

    private var loser = PlayerData()
    private var winner = PlayerData()

    fun getLoser() = loser
    fun getWinner() = winner

    fun updateClientMatch(matchData: MatchData, s: Session): Boolean {
        val updatedMatchSnap = clientMatch.updateMatchSnap(matchData, s)
        for (i in 0..3) lobbyMatches[i] = Pair(-1L, Match())
        if (clientMatch.matchId != -1L) lobbyMatches[clientMatch.getCabinet().toInt()] = Pair(clientMatch.matchId, clientMatch)
        return updatedMatchSnap
    }

    fun resolveEveryone(players: HashMap<Long, Player>, s: Session, data: PlayerData): Boolean {
        val loserPlayer = players.values.firstOrNull { it.getSteamId() == data.steamUserId && it.isLoser() } ?: Player()
        val winnerPlayer = players.values.firstOrNull { it.getSteamId() == data.steamUserId && it.isWinner() } ?: Player()

        if (loserPlayer.getSteamId() != -1L) loser = loserPlayer.getData()
        if (winnerPlayer.getSteamId() != -1L) winner = winnerPlayer.getData()

        if (loser.steamUserId != -1L && winner.steamUserId != -1L) {
            log("----------------------------------------- WE HAVE A WINNER")
            log("loserPlayer = ${loser.displayName} // winnerPlayer = ${winner.displayName}")

            resolveLobbyMatchResults(players, s)

            log("loserChain = ${players[loser.steamUserId]!!.getRating()} // winnerChain = ${players[winner.steamUserId]!!.getRating()}")
            players.values.forEach { p -> if (!p.hasPlayed()) p.incrementIdle(s) }
            log("Idle increment on ${players.values.filter { !it.hasPlayed() }.size} players")
            log("-----------------------------------------")

            loser = PlayerData()
            winner = PlayerData()
            return true
        }
        return false
    }

    private fun resolveLobbyMatchResults(players: HashMap<Long, Player>, s: Session) {
        val winnerSide = winner.playerSide.toInt()
        val loserBounty = players[loser.steamUserId]!!.getBounty()
        val winnerBounty = players[winner.steamUserId]!!.getBounty()

        log("loserBounty = $loserBounty // winnerBounty = $winnerBounty")

        val bonusLoserPayout = (players[loser.steamUserId]!!.getRating() * players[loser.steamUserId]!!.getMatchesWon()) + players[loser.steamUserId]!!.getMatchesPlayed() + (players[loser.steamUserId]!!.getRating() * 100)
        val bonusWinnerPayout = (players[winner.steamUserId]!!.getRating(1) * players[winner.steamUserId]!!.getMatchesWon()) + players[winner.steamUserId]!!.getMatchesPlayed() + (players[winner.steamUserId]!!.getRating(1) * 1000)

        log("bonusLoserPayout = $bonusLoserPayout // bonusWinnerPayout = $bonusWinnerPayout")

        val riskModifier = 0.32 + (0.02 * players[loser.steamUserId]!!.getRating()) - (0.01 * players[winner.steamUserId]!!.getRating())
        val payout = (loserBounty * riskModifier).toInt()

        log("payout = $payout")

        if (!isInRange(bonusLoserPayout - payout, 0, 10)) {
            players[loser.steamUserId]!!.changeBounty(bonusLoserPayout - payout)
            players[loser.steamUserId]!!.changeRating(-2)
        }
        players[winner.steamUserId]!!.changeBounty(bonusWinnerPayout + payout)
        players[winner.steamUserId]!!.changeRating(1)

        s.viewers.forEach {
            var scoreChange = 0
            when(winnerSide) {
                0 -> {
                    if(it.value.isTeamR()) scoreChange += (bonusWinnerPayout + payout)
                    if(it.value.isTeamB()) scoreChange += (bonusLoserPayout - payout)
                }
                1 -> {
                    if(it.value.isTeamR()) scoreChange += (bonusLoserPayout - payout)
                    if(it.value.isTeamB()) scoreChange += (bonusWinnerPayout + payout)
                }
            }
            it.value.changeScore(scoreChange)
        }


    }

}