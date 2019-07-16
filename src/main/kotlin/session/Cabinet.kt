package session

/**
 *
 * XrdListener                  updates and archives Lobby data.
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Player>     contains Player bounty and chains data
 *
 * [Cabinet]
 * contains Match and Players seating data
 *
 */
class Cabinet(
    private val queue: List<Player> = emptyList(),
    private val match: Match = Match()
) {
    fun getPlayers():List<Player> {
        val allPlayers = emptySet<Player>().toMutableSet()
        allPlayers.add(match.fighters.p1)
        allPlayers.add(match.fighters.p2)
        allPlayers.addAll(queue)
        return allPlayers.toList()
    }
}









//{
//
//
//    private val match = Match()
//    private val fightingPlayers = Duo(Player(), Player())
//    private val queuedUpPlayers = emptyList<Player>()
//    private val spectatingPlayers = emptyList<Player>()
//
//    fun updateCabinet(matchData: MatchData, playerData: List<PlayerData>, s:Session) {
//        match.updateMatch(matchData, s)
//        // TODO: MAKE PLAYERDATA UPDATE ALL PLAYERS ON THIS CABINET
//    }
//
//    fun getPlayers():List<Player> {
//        val allPlayers = emptySet<Player>().toMutableSet()
//        allPlayers.add(fightingPlayers.p1)
//        allPlayers.add(fightingPlayers.p2)
//        allPlayers.addAll(queuedUpPlayers)
//        allPlayers.addAll(spectatingPlayers)
//        return allPlayers.toList()
//    }
//
//    private fun resolveLobbyMatchResults(players: HashMap<Long, Player>) {
//        utils.log("-------- MATCH RESULTS -------- [ resolveLobbyMatchResults ]")
//        val loserBounty = players[loser.steamId]!!.getBounty()
//        val winnerBounty = players[winner.steamId]!!.getBounty()
//
//        utils.log("loserBounty = $loserBounty // winnerBounty = $winnerBounty")
//
//        val bonusLoserPayout = (players[loser.steamId]!!.getChain() * players[loser.steamId]!!.getMatchesWon()) + players[loser.steamId]!!.getMatchesPlayed() + (players[loser.steamId]!!.getChain() * 100)
//        val bonusWinnerPayout = (players[winner.steamId]!!.getChain(1) * players[winner.steamId]!!.getMatchesWon()) + players[winner.steamId]!!.getMatchesPlayed() + (players[winner.steamId]!!.getChain(1) * 1000)
//
//        utils.log("bonusLoserPayout = $bonusLoserPayout // bonusWinnerPayout = $bonusWinnerPayout")
//
//        val payout = ((loserBounty * 0.32)).toInt()
//
//        utils.log("payout = $payout")
//
//        if (!isInRange(bonusLoserPayout - payout, 0, 10)) {
//            players[loser.steamId]!!.changeBounty(bonusLoserPayout - payout)
//            players[loser.steamId]!!.changeChain(-2)
//        }
//        players[winner.steamId]!!.changeBounty(bonusWinnerPayout + payout)
//        players[winner.steamId]!!.changeChain(1)
//        utils.log("-------------------------------")
//    }
//
//    fun updateClientMatch(matchData: MatchData, s: Session): Boolean {
//        val updatedMatchSnap = clientMatch.updateMatch(matchData, s)
//        for (i in 0..3) lobbyMatches[i] = Pair(-1L, Match())
//        if (clientMatch.matchId != -1L) lobbyMatches[clientMatch.getCabinetId().toInt()] = Pair(clientMatch.matchId, clientMatch)
//        return updatedMatchSnap
//    }
//
//    private fun resolveClientMatchResults(players: HashMap<Long, Player>) {
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
//
//}