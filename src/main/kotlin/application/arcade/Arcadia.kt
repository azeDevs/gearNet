package application.arcade

import memscan.GearNet
import memscan.MemHandler
import memscan.XrdApi
import models.Player
import models.Player.Companion.MAX_ATENSION
import models.Player.Companion.MAX_RESPECT
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.Controller
import twitch.RoboHandler
import utils.Duo
import utils.getIdString
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


    /**
     *
     */
    fun getPlayer(playerId: Long) = players[playerId] ?: Player()
    fun getPlayers() = players.values
    fun getPlayersMap() = players
    fun getPlayersLoading() = gn.getFrame().playerData.filter { it.loadPercent in 1..99 }
    fun getPlayersActive() = getPlayers().filter { !it.isAbsent() }
    fun getPlayersStaged(): Duo<Player> {
        val stagingCabinet = gn.getClientCabinet()
        val p1 = getPlayersMap().values.firstOrNull { it.isOnPlaySide(PLAYER_1) && it.isOnCabinet(stagingCabinet) } ?: Player()
        val p2 = getPlayersMap().values.firstOrNull { it.isOnPlaySide(PLAYER_2) && it.isOnCabinet(stagingCabinet) } ?: Player()
        return Duo(p1, p2)
    }


    /**
     *
     */
    fun updatePlayers() {
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
//            if (resolveEveryone(data)) somethingChanged = true

        }

        updatePlayerAtension()

    }


    /**
     *
     */
    private fun updatePlayerAtension() {
        val cm = gn.getClientMatchup()
        val p1 = getPlayer(cm.player1.steamId)
        val p2 = getPlayer(cm.player2.steamId)

        if (p1.isValid() && p2.isValid()) {
            // Apply Munity
            p1.setMunity(getPlayersMap().values.filter { item -> item.isTeam(PLAYER_1) }.size)
            p2.setMunity(getPlayersMap().values.filter { item -> item.isTeam(PLAYER_2) }.size)


            // Boost Respect when in strike-stun & taking no damage
            if (p1.getStrikeStun() && !p1.isBeingDamaged()) {
                if (p1.getRespect() >= MAX_RESPECT) p1.addAtension(-2)
                else p1.addRespect(16+p1.getMunity()) }
            if (p2.getStrikeStun() && !p2.isBeingDamaged()) {
                if (p2.getRespect() >= MAX_RESPECT) p2.addAtension(-2)
                else p2.addRespect(16+p2.getMunity()) }

            // Boost Atension when putting opponent into strike-stun
            if (p1.getStrikeStun()) p2.addAtension(p2.getRespect() * (p2.getMunity()+1))
            if (p2.getStrikeStun()) p1.addAtension(p1.getRespect() * (p1.getMunity()+1))

            // Resolve full Atension
            if (p1.getAtension() >= MAX_ATENSION) {
                p1.setAtension(0)
                p1.setRespect(0)
                p1.addSigns(1)
            }
            if (p2.getAtension() >= MAX_ATENSION) {
                p2.setAtension(0)
                p2.setRespect(0)
                p2.addSigns(1)
            }
        }
    }

}