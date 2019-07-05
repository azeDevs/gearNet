package session

import memscan.MatchData
import memscan.PlayerData
import kotlin.math.abs

class MatchHandler {

    val archiveMatches: HashMap<Long, Match> = HashMap()
    var lobbyMatches = arrayListOf(Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()))
    var clientMatch = Match()

    private var loser = PlayerData()
    private var winner = PlayerData()

    fun updateClientMatch(matchData: MatchData, s: Session): Boolean {
        val updatedMatchSnap = clientMatch.updateMatchSnap(matchData, s)
        for (i in 0..3) lobbyMatches[i] = Pair(-1L, Match())
        if (clientMatch.matchId != -1L) lobbyMatches[clientMatch.getCabinet().toInt()] = Pair(clientMatch.matchId, clientMatch)
        return updatedMatchSnap
    }

    fun resolveEveryone(players: HashMap<Long, Player>, s: Session, data: PlayerData): Boolean {
        val loserPlayer = players.values.firstOrNull { it.getSteamId() == data.steamUserId && it.isLoser() } ?: Player()
        val winnerPlayer = players.values.firstOrNull { it.getSteamId() == data.steamUserId && it.isWinner() } ?: Player()

        if (loserPlayer.getSteamId() != -1L) {
            println("******** loserPlayer = ${loserPlayer.getNameString()}")
            loser = loserPlayer.getData()
        }
        if (winnerPlayer.getSteamId() != -1L) {
            println("******** winnerPlayer = ${winnerPlayer.getNameString()}")
            winner = winnerPlayer.getData()
        }

        if (loser.steamUserId != -1L && winner.steamUserId != -1L) {
            println("----------------------------------------- WE HAVE A WINNER")
            println("loserPlayer = ${loser.displayName} // winnerPlayer = ${winner.displayName}")

            if ((loser.steamUserId == clientMatch.players.p1.steamUserId || loser.steamUserId == clientMatch.players.p2.steamUserId) && (winner.steamUserId == clientMatch.players.p1.steamUserId || winner.steamUserId == clientMatch.players.p2.steamUserId)) {
                resolveClientMatchResults(players)
            } else resolveLobbyMatchResults(players)

            println("loserChain = ${players[loser.steamUserId]!!.getChain()} // winnerChain = ${players[winner.steamUserId]!!.getChain()}")
            players.values.forEach { p -> if (!p.hasPlayed()) p.incrementIdle(s) }
            println("Idle increment on ${players.values.filter { !it.hasPlayed() }.size} players")
            println("-----------------------------------------")

            loser = PlayerData()
            winner = PlayerData()
            return true
        }
        return false
    }

    private fun resolveLobbyMatchResults(players: HashMap<Long, Player>) {
        println("/// LOBBY MATCH ///")
        val loserBounty = players[loser.steamUserId]!!.getBounty()
        val winnerBounty = players[winner.steamUserId]!!.getBounty()

        println("loserBounty = $loserBounty // winnerBounty = $winnerBounty")

        val bonusLoserPayout = (players[loser.steamUserId]!!.getChain() * players[loser.steamUserId]!!.getMatchesWon()) + players[loser.steamUserId]!!.getMatchesPlayed() + (players[loser.steamUserId]!!.getChain() * 100)
        val bonusWinnerPayout = (players[winner.steamUserId]!!.getChain() * players[winner.steamUserId]!!.getMatchesWon()) + players[winner.steamUserId]!!.getMatchesPlayed() + (players[winner.steamUserId]!!.getChain() * 1000)

        println("bonusLoserPayout = $bonusLoserPayout // bonusWinnerPayout = $bonusWinnerPayout")

        val payout = ((loserBounty * 0.32)).toInt()

        println("payout = $payout")

        players[loser.steamUserId]!!.changeBounty(bonusLoserPayout - payout)
        players[loser.steamUserId]!!.changeChain(-1)
        players[winner.steamUserId]!!.changeBounty(bonusWinnerPayout + payout)
        players[winner.steamUserId]!!.changeChain(1)

    }

    private fun resolveClientMatchResults(players: HashMap<Long, Player>) {
        println("/// CLIENT MATCH ///")
        val loserSide = loser.playerSide.toInt()
        val loserRounds = clientMatch.getRounds(loserSide)
        val winnerRounds = clientMatch.getRounds(abs(loserSide - 1))

        println("loserRounds = $loserRounds // winnerRounds = $winnerRounds")

        val loserBounty = players[loser.steamUserId]!!.getBounty()
        val winnerBounty = players[winner.steamUserId]!!.getBounty()

        println("loserBounty = $loserBounty // winnerBounty = $winnerBounty")

        val bonusLoserPayout = (players[loser.steamUserId]!!.getChain() * players[loser.steamUserId]!!.getMatchesWon()) + players[loser.steamUserId]!!.getMatchesPlayed() + (players[loser.steamUserId]!!.getChain() * 100)
        val bonusWinnerPayout = (players[winner.steamUserId]!!.getChain() * players[winner.steamUserId]!!.getMatchesWon()) + players[winner.steamUserId]!!.getMatchesPlayed() + (players[winner.steamUserId]!!.getChain() * 1000)

        println("bonusLoserPayout = $bonusLoserPayout // bonusWinnerPayout = $bonusWinnerPayout")

        val loserPayout = (((winnerBounty + bonusLoserPayout) * 0.25) * loserRounds).toInt()
        val winnerPayout = (((loserBounty + bonusWinnerPayout) * 0.25) * winnerRounds).toInt()

        println("loserPayout = $loserPayout // winnerPayout = $winnerPayout")

        players[loser.steamUserId]!!.changeBounty(loserPayout - winnerPayout)
        players[loser.steamUserId]!!.changeChain(-2)
        players[winner.steamUserId]!!.changeBounty(winnerPayout - loserPayout)
        players[winner.steamUserId]!!.changeChain(1)
    }

}