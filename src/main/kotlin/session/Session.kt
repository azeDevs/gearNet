package session

import MyApp.Companion.SIMULATION_MODE
import javafx.collections.ObservableMap
import memscan.*
import models.Match
import models.Player
import models.Player.Companion.MAX_ATENSION
import models.Player.Companion.MAX_RESPECT
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.Controller
import tornadofx.observableMapOf
import twitch.TwitchHandler
import twitch.WatcherData
import utils.Duo
import utils.getRandomName
import kotlin.math.max
import kotlin.random.Random


class Session : Controller() {

    companion object {
        const val OFFLINE_MODE = -1
        const val LOBBY_MODE = 0
        const val MATCH_MODE = 1
        const val SLASH_MODE = 2
        const val VICTORY_MODE = 3
        const val LOADING_MODE = 4
    }

    val gn = GearNet()
    fun startGearNet() = gn.start()

    private var sessionMode: Int = OFFLINE_MODE
    private val xrdApi: XrdApi = if (SIMULATION_MODE) MemRandomizer() else MemHandler()
    private val matchHandler = MatchHandler(this)
    private val twitchHandler = TwitchHandler(this)
    private val players: ObservableMap<Long, Player> = observableMapOf()

    fun getTwitchHandler() = twitchHandler
    fun isXrdApiConnected() = xrdApi.isConnected()

    fun getPlayersMap() = players

    fun getFightersLoading() = xrdApi.getFighterData().filter { it.loadPercent in 1..99 }
    fun getMatchData() = xrdApi.getMatchData()
    fun getClientMatch() = matchHandler.clientMatch
    fun getMatchHandler() = matchHandler

    fun updatePlayerAtension() {
        val f1 = getPlayersMap()[getClientMatch().getFighterData(PLAYER_1).steamId] ?: Player()
        val f2 = getPlayersMap()[getClientMatch().getFighterData(PLAYER_2).steamId] ?: Player()

        if (f1.isValid() && f2.isValid()) {
            // Apply Munity
            f1.setMunity(getPlayersMap().values.filter { item -> item.isTeam(PLAYER_1) }.size)
            f2.setMunity(getPlayersMap().values.filter { item -> item.isTeam(PLAYER_2) }.size)

            // Boost Respect when in strike-stun & taking no damage
            if (getClientMatch().getStrikeStun(PLAYER_1) && !getClientMatch().isBeingDamaged(PLAYER_1)) {
                if (f1.getRespect() >= MAX_RESPECT) f1.addAtension(-2)
                else f1.addRespect(16+f1.getMunity()) }
            if (getClientMatch().getStrikeStun(PLAYER_2) && !getClientMatch().isBeingDamaged(PLAYER_2)) {
                if (f2.getRespect() >= MAX_RESPECT) f2.addAtension(-2)
                else f2.addRespect(16+f2.getMunity()) }

            // Boost Atension when putting opponent into strike-stun
            if (getClientMatch().getStrikeStun(PLAYER_1)) f2.addAtension(f2.getRespect() * (f2.getMunity()+1))
            if (getClientMatch().getStrikeStun(PLAYER_2)) f1.addAtension(f1.getRespect() * (f1.getMunity()+1))

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

    fun getStagedFighers(): Duo<Player> {
        val stagingCabinet = gn.getClientCabinet()
        val f1 = getPlayersMap().values.firstOrNull { it.isOnPlaySide(PLAYER_1) && it.isOnCabinet(stagingCabinet) } ?: Player()
        val f2 = getPlayersMap().values.firstOrNull { it.isOnPlaySide(PLAYER_2) && it.isOnCabinet(stagingCabinet) } ?: Player()
        return Duo(f1, f2)
    }

    fun updateWatchers() {
        getTwitchHandler().generateWatcherEvents()
        if (SIMULATION_MODE) when (Random.nextInt(2560)) {
            0 -> getTwitchHandler().addWatcherData(WatcherData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngRC"))
            1 -> getTwitchHandler().addWatcherData(WatcherData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngBC"))
            2 -> getTwitchHandler().addWatcherData(WatcherData(Random.nextLong(1000000000, 9999999999), getRandomName(), getRandomName()))
        }
    }

    fun updateFighters(): Boolean {
        var somethingChanged = false

        // Define the GearNet client player
//        getFightersInLobby().forEach { data ->
//
//            // Add player if they aren't already stored
//            if (!getPlayersMap().containsKey(data.steamId)) {
//                getPlayersMap()[data.steamId] = Player(data)
//                somethingChanged = true
//                println("Fighter ❝${data.userName}❞ added to Players (${getIdString(data.steamId)})")
//            }
//
//            // The present is now the past, and the future is now the present
//            val player = getPlayersMap()[data.steamId] ?: Player()
//            if (!player.getPlayerData().equals(data)) somethingChanged = true
//            player.updateFighterData(data, getActivePlayerCount())
//            if (player.isStaged()) player.updateMatchData(getClientMatch().getData())
//
//            // Resolve if a game occured and what the reward will be
//            if (getMatchHandler().resolveEveryone(getPlayersMap(), data)) somethingChanged = true
//
//        }

        // New match underway?
        val lobbyMatchPlayers = Duo(FighterData(), FighterData())
        val clientMatchPlayers = Duo(FighterData(), FighterData())

        getFightersLoading().forEach { data ->

            // XrdLobby Match stuff --------
            if (data.seatingId.toInt() == 0) lobbyMatchPlayers.p1 = data
            else lobbyMatchPlayers.p1 = FighterData()
            if (data.seatingId.toInt() == 1) lobbyMatchPlayers.p2 = data
            else lobbyMatchPlayers.p2 = FighterData()

            if (lobbyMatchPlayers.p1.isValid()
                && lobbyMatchPlayers.p2.isValid()
                && lobbyMatchPlayers.p1.cabinetId == lobbyMatchPlayers.p2.cabinetId) {
                val newMatch = Match(
                    getMatchHandler().archiveMatches.size.toLong(),
                    lobbyMatchPlayers.p1.cabinetId,
                    lobbyMatchPlayers
                )
                println("Lobby Match created for ❝${lobbyMatchPlayers.p1.userName}❞ vs ❝${lobbyMatchPlayers.p2.userName}❞")
                getMatchHandler().lobbyMatches[newMatch.getCabinet().toInt()] = Pair(newMatch.matchId, newMatch)
            }

            // Client Match stuff --------
            if (data.cabinetId.toInt() == gn.getClientCabinet()
                && data.seatingId.toInt() == 0) clientMatchPlayers.p1 =
                data else clientMatchPlayers.p1 = FighterData()
            if (data.cabinetId.toInt() == gn.getClientCabinet()
                && data.seatingId.toInt() == 1) clientMatchPlayers.p2 =
                data else clientMatchPlayers.p2 = FighterData()

//            if (isMode(MATCH_MODE)
//                && clientMatchPlayers.p1.steamId == -1L
//                && clientMatchPlayers.p2.steamId == -1L) {
//                getPlayersMap().values.forEach {
//                    if (it.getCabinet() == gn.getClientCabinet()
//                        && it.getPlaySide() == PLAYER_1)
//                        clientMatchPlayers.p1 = it.getPlayerData()
//                    if (it.getCabinet() == gn.getClientCabinet()
//                        && it.getPlaySide() == PLAYER_2)
//                        clientMatchPlayers.p2 = it.getPlayerData()
//                }
//            }
            // Set sessionMode to LOADING_MODE?
            if (!getClientMatch().isValid() && getStagedFighers().p1.isValid() && getStagedFighers().p2.isValid()) {
                getMatchHandler().clientMatch =
                    Match(
                        getMatchHandler().archiveMatches.size.toLong(),
                        gn.getClientCabinet().toByte(),
                        clientMatchPlayers
                    )
                println("Client Match created for ❝${lobbyMatchPlayers.p1.userName}❞ vs ❝${lobbyMatchPlayers.p2.userName}❞")
                somethingChanged = true
                setMode(LOADING_MODE)
            }
            // Set sessionMode to LOBBY_MODE?
            if (!getClientMatch().isValid() && !isMode(LOBBY_MODE) && !isMode(LOADING_MODE)) {
                getMatchHandler().clientMatch = Match()
                somethingChanged = true
                setMode(LOBBY_MODE)
            }
            // Set sessionMode to SLEEP_MODE?
            if (!isXrdApiConnected()) setMode(OFFLINE_MODE)

        }

        // updateMatchInProgress
        getMatchHandler().updateClientMatch(getMatchData())
        updatePlayerAtension()
        return somethingChanged
    }

    fun getActivePlayerCount() = max(getPlayersMap().values.filter { !it.isAbsent() }.size, 1)


    fun isMode(vararg mode: Int) = mode.any { it == sessionMode }
    fun getMode() = gn.getShift()
    fun setMode(mode: Int) {
        sessionMode = mode
        when (mode) {
            OFFLINE_MODE -> println("Mode = SLEEP")
            LOBBY_MODE -> println("Mode = LOBBY")
            MATCH_MODE -> println("Mode = MATCH")
            SLASH_MODE -> println("Mode = SLASH")
            VICTORY_MODE -> println("Mode = VICTORY")
            LOADING_MODE -> println("Mode = LOADING")
        }
    }

    fun getPlayersList(): List<Player> = getPlayersMap().values.toList()
        .sortedByDescending { item -> item.getStatusFloat() }
        .sortedByDescending { item -> item.getScoreTotal() }
        .sortedByDescending { item -> if (!item.isAbsent()) 1 else 0 }

}

