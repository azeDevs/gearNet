package session

import memscan.FighterData
import models.Match
import models.Player
import models.Player.Companion.MAX_ATENSION
import models.Player.Companion.MAX_RESPECT
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.Controller
import utils.Duo
import utils.getIdString
import kotlin.math.max


class Session : Controller() {

    companion object {
        const val SLEEP_MODE = -1
        const val LOBBY_MODE = 0
        const val MATCH_MODE = 1
        const val SLASH_MODE = 2
        const val VICTORY_MODE = 3
        const val LOADING_MODE = 4
    }

    var sessionMode: Int = SLEEP_MODE

    val api = ApiHandler(this)

    fun updatePlayerAtension() {
        val f1 = api.getPlayersMap()[api.getClientMatch().getFighterData(PLAYER_1).steamUserId] ?: Player()
        val f2 = api.getPlayersMap()[api.getClientMatch().getFighterData(PLAYER_2).steamUserId] ?: Player()

        if (f1.isValid() && f2.isValid()) {
            // Apply Munity
            f1.setMunity(api.getPlayersMap().values.filter { item -> item.isTeamR() }.size)
            f2.setMunity(api.getPlayersMap().values.filter { item -> item.isTeamB() }.size)

            // Boost Respect when in strike-stun & taking no damage
            if (api.getClientMatch().getStrikeStun(PLAYER_1) && !api.getClientMatch().isBeingDamaged(PLAYER_1)) {
                if (f1.getRespect() >= MAX_RESPECT) f1.addAtension(-2)
                else f1.addRespect(16+f1.getMunity()) }
            if (api.getClientMatch().getStrikeStun(PLAYER_2) && !api.getClientMatch().isBeingDamaged(PLAYER_2)) {
                if (f2.getRespect() >= MAX_RESPECT) f2.addAtension(-2)
                else f2.addRespect(16+f2.getMunity()) }

            // Boost Atension when putting opponent into strike-stun
            if (api.getClientMatch().getStrikeStun(PLAYER_1)) f2.addAtension(f2.getRespect() * (f2.getMunity()+1))
            if (api.getClientMatch().getStrikeStun(PLAYER_2)) f1.addAtension(f1.getRespect() * (f1.getMunity()+1))

            // Resolve full Atension
            if (f1.getAtension() >= MAX_ATENSION) {
                f1.setAtension(0)
                f1.setRespect(0)
                f1.addSigns(1)
            }
            if (f2.getAtension() >= MAX_ATENSION) {
                f2.setAtension(0)
                f2.setRespect(0)
                f2.addSigns(1)
            }
        }
    }

    fun getClientFighter(): Player = api.getPlayersMap().values.firstOrNull { it.getPlayerId() == api.getClientId() } ?: Player()
    fun getStagedFighers(): Duo<Player> {
        val stagingCabinet = if(getClientFighter().isOnCabinet()) getClientFighter().getCabinet() else 0
        val f1 = api.getPlayersMap().values.firstOrNull { it.isOnPlaySide(PLAYER_1) && it.isOnCabinet(stagingCabinet) } ?: Player()
        val f2 = api.getPlayersMap().values.firstOrNull { it.getPlaySide() == PLAYER_2 && it.getCabinet() == stagingCabinet } ?: Player()
        return Duo(f1, f2)
    }

    fun updateFighters(): Boolean {
        var somethingChanged = false

        // Define the GearNet client player
        api.defineClientId()
        api.getFightersInLobby().forEach { data ->

            // Add player if they aren't already stored
            if (!api.getPlayersMap().containsKey(data.steamUserId)) {
                api.getPlayersMap()[data.steamUserId] = Player(data)
                somethingChanged = true
                println("New player ${getIdString(data.steamUserId)} found ... (${data.displayName})")
            }

            // The present is now the past, and the future is now the present
            val player = api.getPlayersMap()[data.steamUserId] ?: Player()
            if (!player.getData().equals(data)) somethingChanged = true
            player.updatePlayerData(data, getActivePlayerCount())

            // Resolve if a game occured and what the reward will be
            if (api.getMatchHandler().resolveEveryone(api.getPlayersMap(), data)) somethingChanged = true

        }

        // New match underway?
        val lobbyMatchPlayers = Duo(FighterData(), FighterData())
        val clientMatchPlayers = Duo(FighterData(), FighterData())

        api.getFightersLoading().forEach { data ->

            // XrdLobby Match stuff --------
            if (data.playerSide.toInt() == 0) lobbyMatchPlayers.p1 = data
            else lobbyMatchPlayers.p1 = FighterData()
            if (data.playerSide.toInt() == 1) lobbyMatchPlayers.p2 = data
            else lobbyMatchPlayers.p2 = FighterData()

            if (lobbyMatchPlayers.p1.steamUserId != -1L
                && lobbyMatchPlayers.p2.steamUserId != -1L
                && lobbyMatchPlayers.p1.cabinetLoc == lobbyMatchPlayers.p2.cabinetLoc) {
                val newMatch = Match(
                    api.getMatchHandler().archiveMatches.size.toLong(),
                    lobbyMatchPlayers.p1.cabinetLoc,
                    lobbyMatchPlayers
                )
                api.getMatchHandler().lobbyMatches[newMatch.getCabinet().toInt()] = Pair(newMatch.matchId, newMatch)
            }

            // Client Match stuff --------
            if (data.cabinetLoc == getClient().getCabinet().toByte()
                && data.playerSide.toInt() == 0) clientMatchPlayers.p1 =
                data else clientMatchPlayers.p1 = FighterData()
            if (data.cabinetLoc == getClient().getCabinet().toByte()
                && data.playerSide.toInt() == 1) clientMatchPlayers.p2 =
                data else clientMatchPlayers.p2 = FighterData()

            if (sessionMode == MATCH_MODE
                && clientMatchPlayers.p1.steamUserId == -1L
                && clientMatchPlayers.p2.steamUserId == -1L) {
                api.getPlayersMap().values.forEach {
                    if (it.getCabinet() == getClient().getCabinet()
                        && it.getPlaySide() == PLAYER_1)
                        clientMatchPlayers.p1 = it.getData()
                    if (it.getCabinet() == getClient().getCabinet()
                        && it.getPlaySide() == PLAYER_2)
                        clientMatchPlayers.p2 = it.getData()
                }
            }
            // Set sessionMode to LOADING_MODE?
            if (api.getClientMatch().matchId == -1L
                && clientMatchPlayers.p1.steamUserId > 0L
                && clientMatchPlayers.p2.steamUserId > 0L) {
                api.getMatchHandler().clientMatch =
                    Match(
                        api.getMatchHandler().archiveMatches.size.toLong(),
                        getClient().getCabinet().toByte(),
                        clientMatchPlayers
                    )
                println("Generated Match ${getIdString(api.getMatchHandler().archiveMatches.size.toLong())}")
                somethingChanged = true
                setMode(LOADING_MODE)
            }
            // Set sessionMode to LOBBY_MODE?
            if (sessionMode != LOBBY_MODE && sessionMode != LOADING_MODE
                && api.getClientMatch().getHealth(0) < 0
                && api.getClientMatch().getHealth(1) < 0
                && api.getClientMatch().getRisc(0) < 0
                && api.getClientMatch().getRisc(1) < 0
                && api.getClientMatch().getTension(0) < 0
                && api.getClientMatch().getTension(1) < 0
            ) {
                api.getMatchHandler().clientMatch = Match()
                somethingChanged = true
                setMode(LOBBY_MODE)
            }

        }

        return somethingChanged
    }

    fun updateMatchInProgress(): Boolean {
        return api.getMatchHandler().updateClientMatch(api.getMatchData())
    }

    fun getActivePlayerCount() = max(api.getPlayersMap().values.filter { !it.isAbsent() }.size, 1)



    fun setMode(mode: Int) {
        sessionMode = mode
        when (mode) {
            SLEEP_MODE -> println("Mode = SLEEP")
            LOBBY_MODE -> println("Mode = LOBBY")
            MATCH_MODE -> println("Mode = MATCH")
            SLASH_MODE -> println("Mode = SLASH")
            VICTORY_MODE -> println("Mode = VICTORY")
            LOADING_MODE -> println("Mode = LOADING")
        }
    }

    fun getPlayersList(): List<Player> = api.getPlayersMap().values.toList()
        .sortedByDescending { item -> item.getStatusFloat() }
        .sortedByDescending { item -> item.getScoreTotal() }
        .sortedByDescending { item -> if (!item.isAbsent()) 1 else 0 }

    private fun getClient(): Player = if (api.getPlayersMap().isEmpty()) Player() else api.getPlayersMap().values.first { it.getPlayerId() == api.getClientId() }

}

