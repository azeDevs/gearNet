package session

import MyApp.Companion.SIMULATE_MODE
import memscan.FighterData
import models.Fighter
import models.Match
import models.Viewer
import tornadofx.Controller
import twitch.BotEventHandler
import twitch.ViewerData
import utils.Duo
import utils.getIdString
import utils.getRandomName
import kotlin.math.max
import kotlin.random.Random


class Session : Controller() {

    companion object {
        const val LOBBY_MODE = 0
        const val LOADING_MODE = 1
        const val MATCH_MODE = 2
        const val SLASH_MODE = 3
        const val VICTORY_MODE = 4
    }

    val api = ApiHandler()
    val twitchHandler = BotEventHandler(this)
    val matchHandler = MatchHandler()
    val players: HashMap<Long, Fighter> = HashMap()
    val viewers: HashMap<Long, Viewer> = HashMap()

    var randomValues = false

    fun updateViewers() {
        twitchHandler.generateViewerEvents()
        if (SIMULATE_MODE) when (Random.nextInt(333)) {
            0 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngRC"))
            1 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngBC"))
            2 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), getRandomName()))
        }
    }

    fun updatePlayers(): Boolean {
        var somethingChanged = false

        // Define the GearNet client player
        api.defineClientId(this)

        val snap = api.getSnap()
        snap.getLobbyPlayers().forEach { data ->

            // Add player if they aren't already stored
            if (!players.containsKey(data.steamUserId)) {
                players[data.steamUserId] = Fighter(data)
                somethingChanged = true
                log("S: New player ${getIdString(data.steamUserId)} found ... (${data.displayName})")
            }

            // The present is now the past, and the future is now the present
            val player = players[data.steamUserId] ?: Fighter()
            if (!player.getData().equals(data)) somethingChanged = true
            player.updatePlayerData(data, getActivePlayerCount())

            // Resolve if a game occured and what the reward will be
            if (matchHandler.resolveEveryone(players, this, data)) somethingChanged = true

        }

        // New match underway?
        // TODO: MAKE CABINETS TO HOUSE THESE
        // NOTE: THIS IS WEHRE YOU LEFT OFF
        val lobbyMatchPlayers = Duo(FighterData(), FighterData())
        val clientMatchPlayers = Duo(FighterData(), FighterData())

        snap.getLoadingPlayers().forEach { data ->

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
                players.values.forEach {
                    if (it.getCabinet() == getClient().getCabinet()
                        && it.getPlaySide().toInt() == 0)
                        clientMatchPlayers.p1 = it.getData()
                    if (it.getCabinet() == getClient().getCabinet()
                        && it.getPlaySide().toInt() == 1)
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
                log("S: Generated Match ${getIdString(matchHandler.archiveMatches.size.toLong())}")
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

    fun updateClientMatch(): Boolean {
        return matchHandler.updateClientMatch(api.getMatchData(), this)
    }

    fun getActivePlayerCount() = max(players.values.filter { !it.isAbsent() }.size, 1)

    var sessionMode: Int = 0

    fun setMode(mode: Int) {
        sessionMode = mode
        when (mode) {
            LOBBY_MODE -> log("S: sessionMode = LOBBY_MODE")
            LOADING_MODE -> log("S: sessionMode = LOADING_MODE")
            MATCH_MODE -> log("S: sessionMode = MATCH_MODE")
            SLASH_MODE -> log("S: sessionMode = SLASH_MODE")
            VICTORY_MODE -> log("S: sessionMode = VICTORY_MODE")
        }
    }

    fun getPlayersList(): List<Fighter> = players.values.toList()
        .sortedByDescending { item -> item.getStatusFloat() }
        .sortedByDescending { item -> item.getScoreTotal() }
        .sortedByDescending { item -> if (!item.isAbsent()) 1 else 0 }

    fun getClient(): Fighter {
        if (players.isEmpty()) return Fighter()
        return players.values.first { it.getId() == api.getClientId() }
    }

}

//var consoleLog = arrayListOf("C: GearNet started")
fun log(text: String) {
//    if (consoleLog.size > 50) consoleLog.removeAt(0)
//    consoleLog.add(text)
    println(text)
}

