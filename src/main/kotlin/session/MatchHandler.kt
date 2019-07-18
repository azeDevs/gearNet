package session

import memscan.FighterData

class MatchHandler {

    val archiveMatches: HashMap<Long, Match> = HashMap()
    var lobbyMatches = arrayListOf(Pair(-1L, Match()), Pair(-1L, Match()), Pair(-1L, Match()), Pair(-1L, Match()))
    var clientMatch = Match()

    private var loser = FighterData()
    private var winner = FighterData()

}
//
//    fun updateClientMatch(matchData: MatchData, s: Session): Boolean {
//        val updatedMatchSnap = clientMatch.updateMatch(matchData, s)
//        for (i in 0..3) lobbyMatches[i] = Pair(-1L, Match())
//        if (clientMatch.matchId != -1L) lobbyMatches[clientMatch.getCabinet().toInt()] = Pair(clientMatch.matchId, clientMatch)
//        return updatedMatchSnap
//    }
//
//    fun resolveEveryone(players: HashMap<Long, Fighter>, s: Session, data: FighterData): Boolean {
//        val loserPlayer = players.values.firstOrNull { it.getId() == data.steamId && it.isLoser() } ?: Fighter()
//        val winnerPlayer = players.values.firstOrNull { it.getId() == data.steamId && it.isWinner() } ?: Fighter()
//
//        if (loserPlayer.getId() != -1L) {
//            utils.log("MATCH LOSER: ${loserPlayer.getName()}")
//            loser = loserPlayer.getData()
//        }
//        if (winnerPlayer.getId() != -1L) {
//            utils.log("MATCH WINNER: ${winnerPlayer.getName()}")
//            winner = winnerPlayer.getData()
//        }
//
//        if (loser.steamId != -1L && winner.steamId != -1L) {
//            utils.log("-------- MATCH RESULTS -------- [ resolveEveryone ]")
//            utils.log("loserPlayer = ${loser.displayName} // winnerPlayer = ${winner.displayName}")
//
//            if ((loser.steamId == clientMatch.players.p1.steamId || loser.steamId == clientMatch.players.p2.steamId) && (winner.steamId == clientMatch.players.p1.steamId || winner.steamId == clientMatch.players.p2.steamId)) {
//                resolveClientMatchResults(players)
//            } else resolveLobbyMatchResults(players)
//
//            utils.log("loserChain = ${players[loser.steamId]!!.getChain()} // winnerChain = ${players[winner.steamId]!!.getChain()}")
//            players.values.forEach { p -> if (!p.hasPlayed()) p.incrementIdle(s) }
//            utils.log("Idle increment on ${players.values.filter { !it.hasPlayed() }.size} players")
//            utils.log("-------------------------------")
//
//            loser = FighterData()
//            winner = FighterData()
//            return true
//        }
//        return false
//    }
//
//
//
//    private fun resolveClientMatchResults(players: HashMap<Long, Fighter>) {
//        utils.log("-------- MATCH RESULTS -------- [ resolveClientMatchResults ]")
//        val loserSide = loser.seatingId.toInt()
//        val loserRounds = clientMatch.getRounds(loserSide)
//        val winnerRounds = clientMatch.getRounds(abs(loserSide - 1))
//
//        utils.log("loserRounds = $loserRounds // winnerRounds = $winnerRounds")
//
//        val loserBounty = players[loser.steamId]!!.getBounty()
//        val winnerBounty = players[winner.steamId]!!.getBounty()
//
//        utils.log("loserBounty = $loserBounty // winnerBounty = $winnerBounty")
//
//        val bonusLoserPayout = (players[loser.steamId]!!.getChain() * players[loser.steamId]!!.getMatchesWon()) + players[loser.steamId]!!.getMatchesPlayed() + (players[loser.steamId]!!.getChain() * 100)
//        val bonusWinnerPayout = (players[winner.steamId]!!.getChain() * players[winner.steamId]!!.getMatchesWon()) + players[winner.steamId]!!.getMatchesPlayed() + (players[winner.steamId]!!.getChain() * 1000)
//
//        utils.log("bonusLoserPayout = $bonusLoserPayout // bonusWinnerPayout = $bonusWinnerPayout")
//
//        val loserPayout = (((winnerBounty + bonusLoserPayout) * 0.25) * loserRounds).toInt()
//        val winnerPayout = (((loserBounty + bonusWinnerPayout) * 0.25) * winnerRounds).toInt()
//
//        utils.log("loserPayout = $loserPayout // winnerPayout = $winnerPayout")
//
//        players[loser.steamId]!!.changeBounty(loserPayout - winnerPayout)
//        players[loser.steamId]!!.changeChain(-2)
//        players[winner.steamId]!!.changeBounty(winnerPayout - loserPayout)
//        players[winner.steamId]!!.changeChain(1)
//        utils.log("-------------------------------")
//    }
//}