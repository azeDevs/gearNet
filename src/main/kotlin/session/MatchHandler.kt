package session

import memscan.MatchData
import memscan.PlayerData
import kotlin.math.abs

class MatchHandler {

    val archiveMatches: HashMap<Long, Match> = HashMap()
    var lobbyMatches = arrayListOf(Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()))
    var clientMatch = Match()

    var loser = PlayerData()
    var winner = PlayerData()

    fun updateClientMatch(matchData: MatchData, s: Session): Boolean {
        val updatedMatchSnap = clientMatch.updateMatchSnap(matchData, s)
        for (i in 0..3) lobbyMatches.set(i, Pair(-1L, Match()))
        if (clientMatch.matchId != -1L) lobbyMatches.set(clientMatch.getCabinet().toInt(), Pair(clientMatch.matchId, clientMatch))
        return updatedMatchSnap
    }

    fun resolveEveryone(players: HashMap<Long, Player>, s: Session, data: PlayerData): Boolean {
        val loserPlayer = players.values.filter { it.getSteamId() == data.steamUserId && it.hasLost() }.firstOrNull() ?: Player()
        val winnerPlayer = players.values.filter { it.getSteamId() == data.steamUserId && it.hasWon() }.firstOrNull() ?: Player()

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

            println("loserChain = ${players.get(loser.steamUserId)!!.getChain()} // winnerChain = ${players.get(winner.steamUserId)!!.getChain()}")

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
        val loserBounty = players.get(loser.steamUserId)!!.getBounty()
        val winnerBounty = players.get(winner.steamUserId)!!.getBounty()

        println("loserBounty = ${loserBounty} // winnerBounty = ${winnerBounty}")

        val bonusLoserPayout = (players.get(loser.steamUserId)!!.getChain() * players.get(loser.steamUserId)!!.getMatchesWon()) + players.get(loser.steamUserId)!!.getMatchesPlayed() + (players.get(loser.steamUserId)!!.getChain() * 10)
        val bonusWinnerPayout = (players.get(winner.steamUserId)!!.getChain() * players.get(winner.steamUserId)!!.getMatchesWon()) + players.get(winner.steamUserId)!!.getMatchesPlayed() + (players.get(winner.steamUserId)!!.getChain() * 1000)

        println("bonusLoserPayout = ${bonusLoserPayout} // bonusWinnerPayout = ${bonusWinnerPayout}")

        val payout = ((loserBounty * 0.32)).toInt()

        println("payout = ${payout}")

        players.get(loser.steamUserId)!!.changeBounty(bonusLoserPayout - payout)
        players.get(loser.steamUserId)!!.changeChain(-1)
        players.get(winner.steamUserId)!!.changeBounty(bonusWinnerPayout + payout)
        players.get(winner.steamUserId)!!.changeChain(1)

    }

    private fun resolveClientMatchResults(players: HashMap<Long, Player>) {
        println("/// CLIENT MATCH ///")
        val loserSide = loser.playerSide.toInt()
        val loserRounds = clientMatch.getRounds(loserSide)
        val winnerRounds = clientMatch.getRounds(abs(loserSide - 1))

        println("loserRounds = ${loserRounds} // winnerRounds = ${winnerRounds}")

        val loserBounty = players.get(loser.steamUserId)!!.getBounty()
        val winnerBounty = players.get(winner.steamUserId)!!.getBounty()

        println("loserBounty = ${loserBounty} // winnerBounty = ${winnerBounty}")

        val bonusLoserPayout = (players.get(loser.steamUserId)!!.getChain() * players.get(loser.steamUserId)!!.getMatchesWon()) + players.get(loser.steamUserId)!!.getMatchesPlayed() + (players.get(loser.steamUserId)!!.getChain() * 10)
        val bonusWinnerPayout = (players.get(winner.steamUserId)!!.getChain() * players.get(winner.steamUserId)!!.getMatchesWon()) + players.get(winner.steamUserId)!!.getMatchesPlayed() + (players.get(winner.steamUserId)!!.getChain() * 10)

        println("bonusLoserPayout = ${bonusLoserPayout} // bonusWinnerPayout = ${bonusWinnerPayout}")

        val loserPayout = (((winnerBounty + bonusLoserPayout) * 0.25) * loserRounds).toInt()
        val winnerPayout = (((loserBounty + bonusWinnerPayout) * 0.25) * winnerRounds).toInt()

        println("loserPayout = ${loserPayout} // winnerPayout = ${winnerPayout}")

        players.get(loser.steamUserId)!!.changeBounty(loserPayout - winnerPayout)
        players.get(loser.steamUserId)!!.changeChain(-1)
        players.get(winner.steamUserId)!!.changeBounty(winnerPayout - loserPayout)
        players.get(winner.steamUserId)!!.changeChain(1)
    }

}