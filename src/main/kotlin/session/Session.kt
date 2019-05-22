package session

import database.DatabaseHandler
import database.SqlApi
import memscan.MemHandler
import memscan.PlayerData
import memscan.XrdApi
import tornadofx.Controller
import utils.Duo
import utils.getIdString
import kotlin.math.max


class Session: Controller() {

    val xrdApi: XrdApi = MemHandler()
    val dataApi: SqlApi = DatabaseHandler()

    var clientId: Long = -1
    val players: HashMap<Long, Player> = HashMap()
    val archiveMatches: HashMap<Long, Match> = HashMap()
    var lobbyMatches = arrayListOf(Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()),Pair(-1L, Match()))
    var clientMatch = Match()

    var consoleLog = arrayListOf("C: GearNet started")
    var randomValues = false

    fun updatePlayers(): Boolean {
        var somethingChanged = false
        var bountyReward = 0
        val playerData = xrdApi.getPlayerData().filter { it.steamUserId != 0L }

        playerData.forEach { data ->
            if (data.steamUserId != 0L) {
                // Add player if they aren't already stored
                if (!players.containsKey(data.steamUserId)) {
                    players[data.steamUserId] = Player(data); somethingChanged = true
                    log("S: New player ${getIdString(data.steamUserId)} found ... (${data.displayName})")
                }

                // The present is now the past, and the future is now the present
                val player = players[data.steamUserId] ?: Player()
                if (!player.getData().equals(data)) { somethingChanged = true }
                player.updatePlayerData(data, getActivePlayerCount())

                // Resolve if a game occured and what the reward will be
                val bountyLost = resolveEveryoneElse(data)
                if (bountyLost > 0) { bountyReward = bountyLost; somethingChanged = true }
            }
        }

        // Define the GearNet client player
        if (clientId == -1L && playerData.size > 0) {
            clientId = xrdApi.getClientSteamId()
            log("C: GearNet client defined ${getIdString(clientId)} ... (${getClient().getNameString()})")
        }

        // Pay the winner
        playerData.forEach { resolveTheWinner(it, bountyReward) }

        // New match underway?
        val matchPlayersPending = Duo(PlayerData(), PlayerData())
        playerData.forEach { data ->
            if (data.loadingPct in 1..99) {
                if (data.cabinetLoc == getClient().getCabinet() && data.playerSide.toInt() == 0) matchPlayersPending.p1 = data
                if (data.cabinetLoc == getClient().getCabinet() && data.playerSide.toInt() == 1) matchPlayersPending.p2 = data
            }
        }
        if (sessionMode == MATCH_MODE && matchPlayersPending.p1.steamUserId == -1L && matchPlayersPending.p2.steamUserId == -1L) {
            players.values.forEach {
                if (it.getCabinet() == getClient().getCabinet() && it.getPlaySide().toInt() == 0) matchPlayersPending.p1 = it.getData()
                if (it.getCabinet() == getClient().getCabinet() && it.getPlaySide().toInt() == 1) matchPlayersPending.p2 = it.getData()
            }
        }
        if (clientMatch.matchId == -1L && matchPlayersPending.p1.steamUserId > 0L && matchPlayersPending.p2.steamUserId > 0L) {
            clientMatch = Match(archiveMatches.size.toLong(), getClient().getCabinet(), matchPlayersPending)
            log("S: Generated Match ${getIdString(archiveMatches.size.toLong())}")
            somethingChanged = true
            setMode(LOADING_MODE)
        }
        if (sessionMode != LOBBY_MODE && sessionMode != LOADING_MODE && clientMatch.getHealth(0)<0 && clientMatch.getHealth(1)<0 && clientMatch.getRisc(0)<0 && clientMatch.getRisc(1)<0 && clientMatch.getTension(0)<0 && clientMatch.getTension(1)<0) {
            clientMatch = Match()
            somethingChanged = true
            setMode(LOBBY_MODE)
        }

        return somethingChanged
    }

    fun updateClientMatch(): Boolean {
        val matchData = xrdApi.getMatchData()
        val updatedMatchSnap = clientMatch.updateMatchSnap(matchData, this)
        for (i in 0..3) lobbyMatches.set(i, Pair(-1L, Match()))
        if (clientMatch.matchId != -1L) lobbyMatches.set(clientMatch.getCabinet().toInt(), Pair(clientMatch.matchId, clientMatch))
        return updatedMatchSnap
    }

    private fun resolveTheWinner(data: PlayerData, loserChange: Int) {
        players.values.filter { it.getSteamId() == data.steamUserId && it.hasWon() }.forEach { w ->
            // Archive the completed match
            if (clientMatch.getWinner() > -1 ) {
                archiveMatches[clientMatch.matchId] = clientMatch
                log("S: Archived Match ${getIdString(clientMatch.matchId)} ... Snapshots ${clientMatch.allData().size}")
            }

            w.changeChain(1)
            val payout = (w.getChain() * w.getMatchesWon()) + w.getMatchesPlayed() + loserChange + (w.getChain() * 1000)
            w.changeBounty(payout)
            log("S: Player ${getIdString(w.getSteamId())} won ${w.getChangeString()} with ${w.getBountyFormatted()} total ... (${data.displayName})")

        }
    }

    private fun resolveEveryoneElse(data: PlayerData): Int {
        var loserChange = 0
        players.values.filter { it.getSteamId() == data.steamUserId && it.hasLost() }.forEach { l ->
            players.values.forEach { p -> if (!p.hasPlayed()) p.incrementIdle(this) }
            l.changeChain(-1)
            if (l.getBounty() > 0) loserChange = l.getBounty().div(3)
            l.changeBounty(-loserChange)
            if (l.getChange() != 0) log("S: Player ${getIdString(l.getSteamId())} lost ${l.getChangeString()} with ${l.getBountyFormatted()} remaining ... (${data.displayName})")
            return loserChange
        }
        return 0
    }

    fun getActivePlayerCount() = max(players.values.filter { !it.isIdle() }.size, 1)

    fun getClient(): Player {
        if (clientId == -1L) return Player()
        else return players[clientId] ?: Player()
    }

    val LOBBY_MODE = 0
    val LOADING_MODE = 1
    val MATCH_MODE = 2
    val SLASH_MODE = 3
    val VICTORY_MODE = 4
    var sessionMode: Int = 0

    fun setMode(mode:Int) {
        sessionMode = mode
        when (mode) {
            0 -> log("S: sessionMode = LOBBY_MODE")
            1 -> log("S: sessionMode = LOADING_MODE")
            2 -> log("S: sessionMode = MATCH_MODE")
            3 -> log("S: sessionMode = SLASH_MODE")
            4 -> log("S: sessionMode = VICTORY_MODE")
        }
    }

    fun getPlayersList():List<Player> = players.values.toList()
        .sortedByDescending { item -> item.getRating() }
        .sortedByDescending { item -> item.getBounty() }
        .sortedByDescending { item -> if (!item.isIdle()) 1 else 0 }


    fun log(text:String) {
        if (consoleLog.size>50) consoleLog.removeAt(0)
        consoleLog.add(text)
        println(text)
    }

}
