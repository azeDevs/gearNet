package session

import javafx.collections.ObservableMap
import memscan.FighterData
import memscan.MatchData
import models.Match
import models.Player
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import utils.isInRange
import kotlin.math.max

class MatchHandler(val s: Session) {

    val archiveMatches: HashMap<Long, Match> = HashMap()
    val lobbyMatches = arrayListOf(Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()))
    var clientMatch = Match()

    private var loser = FighterData()
    private var winner = FighterData()

    fun updateClientMatch(matchData: MatchData): Boolean {
        val updatedMatchSnap = clientMatch.updateMatchSnap(matchData)
        for (i in 0..3) lobbyMatches[i] = Pair(-1L, Match())
        if (clientMatch.matchId != -1L) lobbyMatches[clientMatch.getCabinet().toInt()] = Pair(clientMatch.matchId, clientMatch)
        return updatedMatchSnap
    }

    fun resolveEveryone(players: ObservableMap<Long, Player>, data: FighterData): Boolean {
        val loserPlayer = players.values.firstOrNull { it.getPlayerId() == data.steamId && it.isLoser() } ?: Player()
        val winnerPlayer = players.values.firstOrNull { it.getPlayerId() == data.steamId && it.isWinner() } ?: Player()

//        if (loserPlayer.getPlayerId() != -1L) loser = loserPlayer.getPlayerData()
//        if (winnerPlayer.getPlayerId() != -1L) winner = winnerPlayer.getPlayerData()

        if (loser.steamId != -1L && winner.steamId != -1L) {
            println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ ᴍᴀᴛᴄʜ ʀᴇᴄᴏʀᴅ ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")
            println("WINNER = ${winner.userName} / LOSER = ${loser.userName}")
            println("WINNER Chain: ${players[winner.steamId]!!.getRating()}")
            println(" LOSER Chain: ${players[loser.steamId]!!.getRating()}")
            resolveLobbyMatchResults(players)


            val activePlayerCount = max(players.values.filter { !it.isAbsent() }.size, 1)
            players.values.forEach { p -> if (!p.hasPlayed()) p.incrementBystanding(activePlayerCount) }
            println("Idle increment on ${players.values.filter { !it.hasPlayed() }.size} players")
            println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")

            loser = FighterData()
            winner = FighterData()
            return true
        }
        return false
    }

    private fun resolveLobbyMatchResults(players: ObservableMap<Long, Player>) {
        val winnerSide = winner.seatingId.toInt()
        val loserBounty = players[loser.steamId]!!.getScoreTotal()
        val winnerBounty = players[winner.steamId]!!.getScoreTotal()

        println("WINNER Bounty: $winnerBounty")
        println(" LOSER Bounty: $loserBounty")

        val bonusLoserPayout = (players[loser.steamId]!!.getRating() * players[loser.steamId]!!.getMatchesWon()) + players[loser.steamId]!!.getMatchesSum() + (players[loser.steamId]!!.getRating() * 100)
        val bonusWinnerPayout = ((players[winner.steamId]!!.getRating()+1) * players[winner.steamId]!!.getMatchesWon()) + players[winner.steamId]!!.getMatchesSum() + ((players[winner.steamId]!!.getRating()+1) * 1000)

        println("WINNER Signing Bonus: $bonusWinnerPayout")
        println(" LOSER Signing Bonus: $bonusLoserPayout")

        val riskModifier = 0.32 + (0.02 * players[loser.steamId]!!.getRating()) - (0.01 * players[winner.steamId]!!.getRating())
        val payout = (loserBounty * riskModifier).toInt()

        println("RISK = $riskModifier / PAYOUT = $payout")

        if (!isInRange(bonusLoserPayout - payout, 0, 10)) {
            players[loser.steamId]!!.changeScore(bonusLoserPayout - payout)
            players[loser.steamId]!!.changeRating(-2)
        }
        players[winner.steamId]!!.changeScore(bonusWinnerPayout + payout)
        players[winner.steamId]!!.changeRating(1)

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


        players.values.filter { it.isWatcher() }.forEach {
            var scoreChange = 0
            when(winnerSide) {
                0 -> {
                    if(it.isTeam(PLAYER_1) && !it.isTeam(PLAYER_2)) scoreChange += ((100*riskModifier).toInt() + payout)
                    if(!it.isTeam(PLAYER_1) && it.isTeam(PLAYER_2)) scoreChange -= ((100*riskModifier).toInt() + payout)
                    if(it.isTeam(PLAYER_1) && it.isTeam(PLAYER_2)) scoreChange -= ((100*riskModifier).toInt() + (payout*riskModifier).toInt())
                }
                1 -> {
                    if(it.isTeam(PLAYER_1) && !it.isTeam(PLAYER_2)) scoreChange -= ((100*riskModifier).toInt() + payout)
                    if(!it.isTeam(PLAYER_1) && it.isTeam(PLAYER_2)) scoreChange += ((100*riskModifier).toInt() + payout)
                    if(it.isTeam(PLAYER_1) && it.isTeam(PLAYER_2)) scoreChange -= ((100*riskModifier).toInt() + (payout*riskModifier).toInt())
                }
            }
            it.changeScore(scoreChange)
            it.setTeam()
        }


    }

}