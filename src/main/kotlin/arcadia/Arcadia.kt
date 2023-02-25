package arcadia

import MyApp.Companion.SIMULATION_MODE
import gearnet.GearNet
import gearnet.GearNetShifter.Shift
import gearnet.MemHandler
import gearnet.MemRandomizer
import gearnet.XrdApi
import models.Player
import tornadofx.Controller
import twitch.RoboHandler
import utils.Duo
import utils.NameGen
import kotlin.math.max

class Arcadia : Controller() {

    private val gn = GearNet()
    private val players: MutableMap<Long, Player> = mutableMapOf()
    private val roboApi = RoboHandler(this)
    private val xrdApi: XrdApi = if (SIMULATION_MODE) MemRandomizer() else MemHandler()


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

    fun getPlayersList(): List<Player> = getPlayers().toList()
        .sortedByDescending { item -> item.getStatusFloat() }
        .sortedByDescending { item -> item.getScoreTotal() }
        .sortedByDescending { item -> if (!item.isAbsent()) 1 else 0 }

    fun getWatchers() = getPlayers().filter { it.isWatcher() }.toList()
    fun getFighters() = getPlayers().filter { !it.isWatcher() }.toList()
    fun getTeam(colorId: Int) = getPlayers().filter { it.isTeam(colorId) }.toList()
    fun getPlayersMap() = players
    fun getPlayersActive() = getFighters().filter { !it.isAbsent() }.toList()
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
                println("Fighter ❝${data.userName}❞ added to Players (${NameGen.getIdString(data.steamId)})")
            }

            // The present is now the past, and the future is now the present
            val player = getPlayersMap()[data.steamId] ?: Player()
            if (!player.getPlayerData().equals(data)) somethingChanged = true
            player.addPlayerData(data, max(getPlayersActive().size, 1))
            if (player.isStaged()) player.addMatchupData(gn.getClientMatchup())

            // Resolve if a game occured and what the reward will be
            if (resolveEveryone()) somethingChanged = true

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
        const val MAX_RESPECT = 333
        const val MAX_ATENSION = 3333
    }

    private fun processAtension(player: Player, opponent: Player) {

        player.setAmunity(getTeam(player.getTeamSeat()).size+1)

        when(gn.getShift()) {
            Shift.GEAR_MATCH -> {

                // Boost Respect when in strike-stun & taking no damage
                when {
                    player.isStunLocked() && player.getTensionDelta() < 0 -> player.addRespect(7)
//                    player.isStunLocked() && player.getHealthDelta() == 0 -> player.addRespect(1)
                }


                // Boost Atension when putting opponent into strike-stun
                when {
                    opponent.isStunLocked() -> {
                        player.addAtension(player.getAmunity()+player.getRespect())
                        player.addRespect(-3)
                    }
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
                        player.addSigns(1)
                        getTeam(player.getTeamSeat()).forEach { it.addSigns(1) }
                    }
                    player.getAtension() <= 0 -> player.setAtension(0)
                }

            }
//            Shift.GEAR_SLASH -> {  }
//            Shift.GEAR_VICTORY -> { getPlayers().forEach { it.setTeam() } }
//            else -> {
//                player.setAtension(0)
//                player.setRespect(0)
//            }
        }


    }


    /**
     *
     */
    private fun resolveEveryone(): Boolean {

        val winner = when {
            getPlayersStaged().p1.isWinner() -> getPlayersStaged().p1
            getPlayersStaged().p2.isWinner() -> getPlayersStaged().p2
            else -> Player()
        }
        val loser = when {
            getPlayersStaged().p1.isLoser() -> getPlayersStaged().p1
            getPlayersStaged().p2.isLoser() -> getPlayersStaged().p2
            else -> Player()
        }

        if (winner.isValid() && loser.isValid()) {

            resolveLobbyMatchResults(winner, loser)

            val activePlayerCount = max(getPlayers().filter { !it.isAbsent() }.size, 1)
            getPlayers().forEach { p -> if (!p.hasPlayed()) p.incrementBystanding(activePlayerCount, this) }
            println("Idle increment on ${getPlayers().filter { !it.hasPlayed() }.size} players")
            println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")

            return true
        }
        return false
    }


    /**
     *
     */
    private fun resolveLobbyMatchResults(winner:Player, loser:Player) {

        println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ ᴍᴀᴛᴄʜ ʀᴇᴄᴏʀᴅ ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")
        println("WINNER = ${winner.getUserName()} / LOSER = ${loser.getUserName()}")

        val loserScore = getPlayer(loser).getScoreTotal()

        val loserRating = loser.getRating()
        val winnerRating = winner.getRating()

        val winnerModifier = if(winnerRating>0) {
            -(0.03 * winnerRating) // 3% less if winner was high Risk
        } else -(0.04 * winnerRating) // 4% more if winner was high Fury

        val loserModifier = if(loserRating>0) {
            -(0.02 * loserRating) // 2% more if loser was high Risk
        } else (0.01 * loserRating) // 1% less if loser was high Fury

        val riskyModifier = 0.32 + loserModifier + winnerModifier
        val payout = (loserScore * riskyModifier).toInt()

        payday(winner, winner, payout, winnerRating)
        payday(loser, winner, payout, winnerRating)

        getPlayers().forEach {
            payday(it, winner, payout, winnerRating)
        }



    }

    private fun payday(it: Player, winner: Player, payout: Int, winnerRating: Int) {
        it.setBystanding(max(getPlayersActive().size, 1))

        val rateInflater = when (it.getRating()) {
            8 -> 51.2
            7 -> 25.6
            6 -> 12.8
            5 -> 6.4
            4 -> 3.2
            3 -> 1.6
            2 -> 0.8
            1 -> 0.4
            -1 -> 0.04
            -2 -> 0.08
            -3 -> 0.16
            -4 -> 0.32
            -5 -> 0.64
            -6 -> 1.28
            -7 -> 2.56
            -8 -> 5.12
            else -> 0.0
        }

        val signingBonus = ((it.getSigns() * 8) * (rateInflater + 1.0)).toInt()
        if (it.isTeam(winner.getTeamSeat())) {
            it.changeRating(1, this)
            it.changeScore(signingBonus + payout)
        } else {
            if (winnerRating > 0) it.changeRating(-2, this)
            else it.changeRating(-1, this)
            it.changeScore(signingBonus - payout)
        }


    }

}