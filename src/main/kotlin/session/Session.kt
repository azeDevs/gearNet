package session

import memscan.FighterData
import models.Fighter
import models.Match
import models.Player.Companion.MAX_ATENSION
import models.Player.Companion.MAX_RESPECT
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import models.Watcher
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
    val matchHandler = MatchHandler(this)
    val fighters: HashMap<Long, Fighter> = HashMap()
    val watchers: HashMap<Long, Watcher> = HashMap()
    var randomValues = false

    fun updatePlayerAtension() {
        val f1 = fighters[matchHandler.clientMatch.getFighterData(PLAYER_1).steamUserId] ?: Fighter()
        val f2 = fighters[matchHandler.clientMatch.getFighterData(PLAYER_2).steamUserId] ?: Fighter()

        if (f1.isValid() && f2.isValid()) {
            // Apply Munity
            f1.setMunity(watchers.values.filter { item -> item.isTeamR() }.size)
            f2.setMunity(watchers.values.filter { item -> item.isTeamB() }.size)

            // Boost Respect when in strike-stun & taking no damage
            if (matchHandler.clientMatch.getStrikeStun(PLAYER_1) && !matchHandler.clientMatch.isBeingDamaged(PLAYER_1)) {
                if (f1.getRespect() >= MAX_RESPECT) f1.addAtension(-2)
                else f1.addRespect(16+f1.getMunity()) }
            if (matchHandler.clientMatch.getStrikeStun(PLAYER_2) && !matchHandler.clientMatch.isBeingDamaged(PLAYER_2)) {
                if (f2.getRespect() >= MAX_RESPECT) f2.addAtension(-2)
                else f2.addRespect(16+f2.getMunity()) }

            // Boost Atension when putting opponent into strike-stun
            if (matchHandler.clientMatch.getStrikeStun(PLAYER_1)) f2.addAtension(f2.getRespect() * (f2.getMunity()+1))
            if (matchHandler.clientMatch.getStrikeStun(PLAYER_2)) f1.addAtension(f1.getRespect() * (f1.getMunity()+1))

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

    fun getClientFighter(): Fighter = fighters.values.firstOrNull { it.getPlayerId() == api.getClientId() } ?: Fighter()
    fun getStagedFighers(): Pair<Fighter, Fighter> {
        val f1 = fighters.values.firstOrNull { it.getPlaySide() == PLAYER_1 && it.getCabinet() == getClientFighter().getCabinet() } ?: Fighter()
        val f2 = fighters.values.firstOrNull { it.getPlaySide() == PLAYER_2 && it.getCabinet() == getClientFighter().getCabinet() } ?: Fighter()
        return Pair(f1, f2)
    }

    fun updateFighters(): Boolean {
        var somethingChanged = false

        // Define the GearNet client player
        api.defineClientId()
        api.getFightersInLobby().forEach { data ->

            // Add player if they aren't already stored
            if (!fighters.containsKey(data.steamUserId)) {
                fighters[data.steamUserId] = Fighter(data)
                somethingChanged = true
                println("New player ${getIdString(data.steamUserId)} found ... (${data.displayName})")
            }

            // The present is now the past, and the future is now the present
            val player = fighters[data.steamUserId] ?: Fighter()
            if (!player.getData().equals(data)) somethingChanged = true
            player.updatePlayerData(data, getActivePlayerCount())

            // Resolve if a game occured and what the reward will be
            if (matchHandler.resolveEveryone(fighters, data)) somethingChanged = true

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
                    matchHandler.archiveMatches.size.toLong(),
                    lobbyMatchPlayers.p1.cabinetLoc,
                    lobbyMatchPlayers
                )
                matchHandler.lobbyMatches[newMatch.getCabinet().toInt()] = Pair(newMatch.matchId, newMatch)
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
                fighters.values.forEach {
                    if (it.getCabinet() == getClient().getCabinet()
                        && it.getPlaySide() == PLAYER_1)
                        clientMatchPlayers.p1 = it.getData()
                    if (it.getCabinet() == getClient().getCabinet()
                        && it.getPlaySide() == PLAYER_2)
                        clientMatchPlayers.p2 = it.getData()
                }
            }
            // Set sessionMode to LOADING_MODE?
            if (matchHandler.clientMatch.matchId == -1L
                && clientMatchPlayers.p1.steamUserId > 0L
                && clientMatchPlayers.p2.steamUserId > 0L) {
                matchHandler.clientMatch =
                    Match(
                        matchHandler.archiveMatches.size.toLong(),
                        getClient().getCabinet().toByte(),
                        clientMatchPlayers
                    )
                println("Generated Match ${getIdString(matchHandler.archiveMatches.size.toLong())}")
                somethingChanged = true
                setMode(LOADING_MODE)
            }
            // Set sessionMode to LOBBY_MODE?
            if (sessionMode != LOBBY_MODE && sessionMode != LOADING_MODE
                && matchHandler.clientMatch.getHealth(0) < 0
                && matchHandler.clientMatch.getHealth(1) < 0
                && matchHandler.clientMatch.getRisc(0) < 0
                && matchHandler.clientMatch.getRisc(1) < 0
                && matchHandler.clientMatch.getTension(0) < 0
                && matchHandler.clientMatch.getTension(1) < 0
            ) {
                matchHandler.clientMatch = Match()
                somethingChanged = true
                setMode(LOBBY_MODE)
            }

        }

        return somethingChanged
    }

    fun updateMatchInProgress(): Boolean {
        return matchHandler.updateClientMatch(api.getMatchData())
    }

    fun getActivePlayerCount() = max(fighters.values.filter { !it.isAbsent() }.size, 1)



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

    fun getPlayersList(): List<Fighter> = fighters.values.toList()
        .sortedByDescending { item -> item.getStatusFloat() }
        .sortedByDescending { item -> item.getScoreTotal() }
        .sortedByDescending { item -> if (!item.isAbsent()) 1 else 0 }

    private fun getClient(): Fighter = if (fighters.isEmpty()) Fighter() else fighters.values.first { it.getPlayerId() == api.getClientId() }

}

