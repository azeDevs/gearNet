package application.arcade

import memscan.GearNet
import memscan.GearNetShifter.Shift
import memscan.MemHandler
import memscan.XrdApi
import models.Player
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.Controller
import twitch.RoboHandler
import utils.Duo
import utils.getIdString
import utils.isInRange
import kotlin.math.max

class Arcadia : Controller() {

    private val gn = GearNet()
    private val players: MutableMap<Long, Player> = mutableMapOf()
    private val roboApi = RoboHandler(this)
    private val xrdApi: XrdApi = MemHandler()


    /**
     *
     */
    fun startArcadia() = gn.start()
    fun isXrdApiConnected() = xrdApi.isConnected()
    fun isRoboApiConnected() = roboApi.isConnected()
    fun isShift(vararg mode: Shift) = mode.any { it == gn.getShift() }
    fun getShift() = gn.getShift()
    fun getGNLogsString() = gn.getUpdateString()


    /**
     *
     */
    fun getPlayer(player: Player) = players[player.getPlayerId()] ?: Player()
    fun getPlayer(playerId: Long) = players[playerId] ?: Player()
    fun getPlayers() = getPlayersMap().values
    fun getWatchers() = getPlayers().filter { it.isWatcher() }.toList()
    fun getFighters() = getPlayers().filter { !it.isWatcher() }.toList()
    fun getTeam(colorId: Int) = getPlayers().filter { it.isTeam(colorId) }.toList()
    fun getPlayersMap() = players
    fun getPlayersLoading() = gn.getFrame().playerData.filter { it.loadPercent in 1..99 }
    fun getPlayersActive() = getPlayers().filter { !it.isAbsent() }.toList()
    fun getPlayersStaged(): Duo<Player> {
        val p1 = getPlayersMap().values.firstOrNull { it.getPlayerId() == gn.getRedPlayer().steamId } ?: Player()
        val p2 = getPlayersMap().values.firstOrNull { it.getPlayerId() == gn.getBluePlayer().steamId } ?: Player()
        return Duo(p1, p2)
    }


    /**
     *
     */
    fun getClientPlayer() = getPlayer(gn.getClientPlayer().steamId)
    fun getClientMatch() = gn.getClientMatchup()


    /**
     *
     */
    fun updatePlayers(): Boolean {
        var somethingChanged = false
        roboApi.generateWatcherEvents()
        gn.getFrame().playerData.forEach { data ->

            // Add player if they aren't already stored
            if (!getPlayersMap().containsKey(data.steamId)) {
                getPlayersMap()[data.steamId] = Player(data)
                somethingChanged = true
                println("Fighter ❝${data.userName}❞ added to Players (${getIdString(data.steamId)})")
            }

            // The present is now the past, and the future is now the present
            val player = getPlayersMap()[data.steamId] ?: Player()
            if (!player.getPlayerData().equals(data)) somethingChanged = true
            player.addPlayerData(data, max(getPlayersActive().size, 1))
            if (player.isStaged()) player.addMatchupData(gn.getClientMatchup())

            // Resolve if a game occured and what the reward will be
            if (resolveEveryone(player)) somethingChanged = true

        }

        updatePlayerAtension()
        return somethingChanged
    }


    /**
     *
     */
    private fun updatePlayerAtension() {
        val cm = gn.getClientMatchup()
        val p1 = getPlayer(cm.player1.steamId)
        val p2 = getPlayer(cm.player2.steamId)

        if (p1.isValid() && p2.isValid()) {
            processAtension(p1, p2)
            processAtension(p2, p1)
        }
    }

    companion object {
        const val MAX_MUNITY = 16
        const val MAX_RESPECT = 160
        const val MAX_ATENSION = 1600
    }

    private fun processAtension(player: Player, opponent: Player) {

        player.setAmunity(getTeam(player.getTeamSeat()).size+1)

        when(gn.getShift()) {
            Shift.GEAR_MATCH -> {

                // Boost Respect when in strike-stun & taking no damage
                when {
                    player.isStunLocked() && player.getTensionDelta() < 0 -> player.addRespect(7)
                    player.isStunLocked() -> player.addRespect(3)
                }


                // Boost Atension when putting opponent into strike-stun
                when {
                    opponent.isStunLocked() -> {
                        player.addAtension(player.getAmunity()+player.getRespect())
                        player.addRespect(-2)
                    }
//                    player.getAmunity() > 0 -> player.addAtension(player.getAmunity())
                }

                // Resolve RESPECT
                when {
                    player.getRespect() >= MAX_RESPECT -> player.setRespect(MAX_RESPECT)
                    player.getRespect() <= 0 -> player.setRespect(0)
                }

                // Resolve ATENSION
                when {
                    player.getAtension() >= MAX_ATENSION -> {
                        player.setSignal(true)
                        player.setAtension(0)
                        player.addSigns(2)
                        getTeam(player.getTeamSeat()).forEach { it.addSigns(1) }
                    }
                    player.getAtension() <= 0 -> player.setAtension(0)
                }

            }
            Shift.GEAR_SLASH -> {  }
            Shift.GEAR_VICTORY -> { getPlayers().forEach { it.setTeam() } }
            else -> {
                player.setAtension(0)
                player.setRespect(0)
            }
        }


    }


    /**
     *
     */
    private var loser = Player()
    private var winner = Player()
    fun resolveEveryone(data: Player): Boolean {

        getPlayersStaged().p1.isLoser()

        val loserPlayer = if(getPlayer(data).isLoser()) data else Player()
        val winnerPlayer = if(getPlayer(data).isLoser()) data else Player()

        if (loserPlayer.isValid()) loser = loserPlayer
        if (winnerPlayer.isValid()) winner = winnerPlayer

        if (loser.isValid() && winner.isValid()) {
            println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ ᴍᴀᴛᴄʜ ʀᴇᴄᴏʀᴅ ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")
            println("WINNER = ${winner.getUserName()} / LOSER = ${loser.getUserName()}")
            println("WINNER Chain: ${getPlayer(winner).getRating()}")
            println(" LOSER Chain: ${getPlayer(loser).getRating()}")
            resolveLobbyMatchResults()


            val activePlayerCount = max(getPlayers().filter { !it.isAbsent() }.size, 1)
            getPlayers().forEach { p -> if (!p.hasPlayed()) p.incrementBystanding(activePlayerCount, this) }
            println("Idle increment on ${getPlayers().filter { !it.hasPlayed() }.size} players")
            println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")

            loser = Player()
            winner = Player()
            return true
        }
        return false
    }


    /**
     *
     */
    private fun resolveLobbyMatchResults() {
        val winnerSide = winner.getTeamSeat()
        val loserBounty = getPlayer(loser).getScoreTotal()
        val winnerBounty = getPlayer(winner).getScoreTotal()

        println("WINNER Bounty: $winnerBounty")
        println(" LOSER Bounty: $loserBounty")

        val bonusLoserPayout = (getPlayer(loser).getRating() * getPlayer(loser).getMatchesWon()) + getPlayer(loser).getMatchesSum() + (getPlayer(loser).getRating() * 100)
        val bonusWinnerPayout = ((getPlayer(winner).getRating()+1) * getPlayer(winner).getMatchesWon()) + getPlayer(winner).getMatchesSum() + ((getPlayer(winner).getRating()+1) * 1000)

        println("WINNER Signing Bonus: $bonusWinnerPayout")
        println(" LOSER Signing Bonus: $bonusLoserPayout")

        val riskModifier = 0.32 + (0.02 * getPlayer(loser).getRating()) - (0.01 * getPlayer(winner).getRating())
        val payout = (loserBounty * riskModifier).toInt()

        println("RISK = $riskModifier / PAYOUT = $payout")

        if (!isInRange(bonusLoserPayout - payout, 0, 10)) {
            getPlayer(loser).changeScore(bonusLoserPayout - payout)
            getPlayer(loser).changeRating(-2, this)
        }
        getPlayer(winner).changeScore(bonusWinnerPayout + payout)
        getPlayer(winner).changeRating(1, this)

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


        getPlayers().filter { it.isWatcher() }.forEach {
            var scoreChange = it.getSigns() * 8
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
        }

        getPlayers().forEach { it.setTeam() }


    }

}