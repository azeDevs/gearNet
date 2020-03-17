package session

import memscan.FighterData
import models.Fighter
import models.Match
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import models.Watcher
import tornadofx.Controller
import utils.Duo
import utils.getIdString
import kotlin.math.max


class Session : Controller() {

    companion object {
        const val LOBBY_MODE = 0
        const val LOADING_MODE = 1
        const val MATCH_MODE = 2
        const val SLASH_MODE = 3
        const val VICTORY_MODE = 4
    }

    val api = ApiHandler(this)
    val matchHandler = MatchHandler(this)
    val fighters: HashMap<Long, Fighter> = HashMap()
    val watchers: HashMap<Long, Watcher> = HashMap()
    var randomValues = false


    fun getClientFighter(): Fighter = fighters.values.firstOrNull { it.getPlayerId() == api.getClientId() } ?: Fighter()
    fun getStagedFighers(): Pair<Fighter, Fighter> {
        val f1 = fighters.values.firstOrNull { it.getPlaySide() == PLAYER_1 && it.getCabinet() == getClientFighter().getCabinet() } ?: Fighter()
        val f2 = fighters.values.firstOrNull { it.getPlaySide() == PLAYER_2 && it.getCabinet() == getClientFighter().getCabinet() } ?: Fighter()
        return Pair(f1, f2)
    }

    fun updateFighters(): Boolean {
        var somethingChanged = false

        // Define the GearNet client player
        api.defineClientId(this)

        api.getFightersInLobby().forEach { data ->

            // Add player if they aren't already stored
            if (!fighters.containsKey(data.steamUserId)) {
                fighters[data.steamUserId] = Fighter(data)
                somethingChanged = true
                log("New player ${getIdString(data.steamUserId)} found ... (${data.displayName})")
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
                log("Generated Match ${getIdString(matchHandler.archiveMatches.size.toLong())}")
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

    var sessionMode: Int = 0

    fun setMode(mode: Int) {
        sessionMode = mode
        when (mode) {
            LOBBY_MODE -> log("Mode = LOBBY")
            LOADING_MODE -> log("Mode = LOADING")
            MATCH_MODE -> log("Mode = MATCH")
            SLASH_MODE -> log("Mode = SLASH")
            VICTORY_MODE -> log("Mode = VICTORY")
        }
    }

    fun getPlayersList(): List<Fighter> = fighters.values.toList()
        .sortedByDescending { item -> item.getStatusFloat() }
        .sortedByDescending { item -> item.getScoreTotal() }
        .sortedByDescending { item -> if (!item.isAbsent()) 1 else 0 }

    private fun getClient(): Fighter = if (fighters.isEmpty()) Fighter() else fighters.values.first { it.getPlayerId() == api.getClientId() }

}

fun log(text: String) = println(text)

