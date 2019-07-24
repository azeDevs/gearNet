package session

import WD
import events.EventType.*
import events.FighterEvent
import events.ViewerEvent
import events.XrdEventListener
import session.SessionMode.*
import tornadofx.Controller
import twitch.TwitchBot
import utils.addCommas
import utils.log


class Session : Controller() {

    private val state = SessionState()
    private var sessionMode = MODE_NULL
    private val xrdListener = XrdEventListener()
    private val botApi = TwitchBot()

    fun generateEvents() {
        log("sessionMode", sessionMode.name)
        // PROCESS FighterEvents
        xrdListener.generateFighterEvents(state).forEach { state.update(it)
            when (it.getType()) {
                NULL_EVENT -> false
                XRD_CONNECTED -> log("XrdApi connected")
                XRD_DISCONNECT -> log("XrdApi disconnected")
                FIGHTER_JOINED -> runFighterJoined(it)
                FIGHTER_MOVED -> runFighterMoved(it)

                MATCH_LOADING -> runMatchLoading(it)
                MATCH_ENDED -> runMatchEnded(it)
                ROUND_STARTED -> runRoundStarted(it)
                ROUND_ENDED -> runRoundEnded(it)

                BURST_ENABLED -> runBurstEnabled(it)
                STRIKE_STUNNED -> runStrikeStunned(it)
                DAMAGE_DEALT -> runDamageDealt(it)
            }
        }

        // PROCESS ViewerEvents
        botApi.generateViewerEvents(state).forEach { state.update(it)
            when (it.getType()) {
                NULL_EVENT -> false
                VIEWER_MESSAGE -> runViewerMessage(it)
                VIEWER_JOINED -> runViewerJoined(it)

                COMMAND_BET -> runCommandBet(it)
                COMMAND_HELP -> runCommandHelp(it)
                COMMAND_WALLET -> runCommandWallet(it)
            }
        }

    }

    private fun runViewerMessage(it: ViewerEvent) {
        log("${it.getName()} said “${it.getMessage()}”")
    }

    private fun runViewerJoined(it: ViewerEvent) {
        log("NEW Viewer ${it.getName()} added to viewers map")
    }

    private fun runCommandWallet(it: ViewerEvent) {
        log("!WALLET command from ${it.getName()} initiated")
        botApi.sendMessage("${it.getName()} initiated !WALLET")
    }

    private fun runCommandHelp(it: ViewerEvent) {
        log("!HELP command from ${it.getName()} initiated")
        botApi.sendMessage("${it.getName()} initiated !HELP")
    }

    private fun runCommandBet(it: ViewerEvent) {
        if (it.getBetAmount() > 0) {
            botApi.sendMessage("${addCommas(it.getBetAmount())} \uD835\uDE86\$ ${it.getBetBanner().first} ${it.getName()}")
            log("!BET command from ${it.getName()}, ${addCommas(it.getBetAmount())} $WD on ${it.getBetBanner().second}, initiated")
        } else log("!BET command from ${it.getName()} failed to initiate, invalid amount")
    }

    private fun runFighterJoined(it: FighterEvent) {
        log("NEW Fighter ${it.getName()} added to fighters map")
    }

    private fun runFighterMoved(it: FighterEvent) {
        log("Fighter ${it.getName()} moved ${
        if (it.get().getCabinet() > 3) "off cabinet" 
        else "to ${it.get().getSeatString()
        }, ${it.get().getCabinetString()}"}")
    }

    private fun runDamageDealt(it: FighterEvent) {
        log(it.getType().name)
    }

    private fun runStrikeStunned(it: FighterEvent) {
        log(it.getType().name)
    }

    private fun runBurstEnabled(it: FighterEvent) {
        log(it.getType().name)
    }

    private fun runRoundEnded(it: FighterEvent) {
        sessionMode = MODE_SLASH
        log("Round ENDED with ${
        if (it.getDelta(0) == 1) "P1 ${it.get(0).getName()}" 
        else "P2 ${it.get(1).getName()}"
        } as the winner.")
    }

    private fun runRoundStarted(it: FighterEvent) {
        sessionMode = MODE_MATCH
        log("Round STARTED with P1 ${it.get(0).getName()} and P2 ${it.get(1).getName()}")
    }

    private fun runMatchEnded(it: FighterEvent) {
        sessionMode = MODE_VICTORY
        log("Match ENDED with ${
        if (it.getDelta(0) == 1) "P1 ${it.get(0).getName()}" 
        else "P2 ${it.get(1).getName()}"
        } as the winner.")
    }

    private fun runMatchLoading(it: FighterEvent) {
        sessionMode = MODE_LOADING
        log("Match LOADING with P1 ${it.get(0).getName()} and P2 ${it.get(1).getName()}")
    }

}



//        // New match underway?
//        val lobbyMatchPlayers = Duo(FighterData(), FighterData())
//        val clientMatchPlayers = Duo(FighterData(), FighterData())
//
//        lobbyHandler.getFighters().filter { it.isLoading() }.forEach { p ->
//
//            // Lobby Match stuff --------
//            if (p.getPlaySide() == 0) lobbyMatchPlayers.f1 = p.getData()
//            else lobbyMatchPlayers.f1 = FighterData()
//            if (p.getPlaySide() == 1) lobbyMatchPlayers.f2 = p.getData()
//            else lobbyMatchPlayers.f2 = FighterData()
//
//            if (lobbyMatchPlayers.f1.steamId != -1L
//                && lobbyMatchPlayers.f2.steamId != -1L
//                && lobbyMatchPlayers.f1.cabinetId == lobbyMatchPlayers.f2.cabinetId) {
//                val newMatch = Match(matchHandler.archiveMatches.size.toLong(), lobbyMatchPlayers.f1.cabinetId, lobbyMatchPlayers)
//                matchHandler.lobbyMatches[newMatch.getCabinet().toInt()] = Pair(newMatch.matchId, newMatch)
//            }
//
//            // Client Match stuff --------
//            if (p.getCabinet() == getClient().getCabinet() && p.getPlaySide() == 0)
//                clientMatchPlayers.f1 = p.getData() else clientMatchPlayers.f1 = FighterData()
//            if (p.getCabinet() == getClient().getCabinet() && p.getPlaySide() == 1)
//                clientMatchPlayers.f2 = p.getData() else clientMatchPlayers.f2 = FighterData()
//            matchHandler.updateClientMatch(lobbyHandler.getMatchData(), this)
//
//            // Set sessionMode to MODE_MATCH
//            if (sessionMode == MODE_MATCH
//                && clientMatchPlayers.f1.steamId == -1L
//                && clientMatchPlayers.f2.steamId == -1L) {
//                players.values.forEach {
//                    if (it.getCabinet() == getClient().getCabinet()
//                        && it.getPlaySide().toInt() == 0)
//                        clientMatchPlayers.f1 = it.getData()
//                    if (it.getCabinet() == getClient().getCabinet()
//                        && it.getPlaySide().toInt() == 1)
//                        clientMatchPlayers.f2 = it.getData()
//                }
//            }
//            // Set sessionMode to MODE_LOADING
//            if (matchHandler.clientMatch.matchId == -1L
//                && clientMatchPlayers.f1.steamId > 0L
//                && clientMatchPlayers.f2.steamId > 0L) {
//                matchHandler.clientMatch =
//                    Match(matchHandler.archiveMatches.size.toLong(), getClient().getCabinet(), clientMatchPlayers)
//                utils.log("[SESS] Generated Match ${getIdString(matchHandler.archiveMatches.size.toLong())}")
//                somethingChanged = true
//                setMode(MODE_LOADING)
//            }
//            // Set sessionMode to LOBBY_MODE
//            if (sessionMode != LOBBY_MODE && sessionMode != MODE_LOADING
//                && matchHandler.clientMatch.getHealth(0) < 0
//                && matchHandler.clientMatch.getHealth(1) < 0
//                && matchHandler.clientMatch.getRisc(0) < 0
//                && matchHandler.clientMatch.getRisc(1) < 0
//                && matchHandler.clientMatch.getTension(0) < 0
//                && matchHandler.clientMatch.getTension(1) < 0
//            ) {
//                matchHandler.clientMatch = Match()
//                somethingChanged = true
//                setMode(LOBBY_MODE)
//            }
//
//        }
//
//        // Filter Twitch messages for valid commands to execute
//        bettingHandler.parseViewerData()
//
//        return somethingChanged
//    }
//
//    fun updateClientMatch(xrdApi: XrdApi): Boolean {
//        return matchHandler.updateClientMatch(xrdApi.getMatchData(), this)
//    }
//
//    fun getActivePlayerCount() = max(players.values.filter { !it.isIdle() }.size, 1)
//
//
//    var sessionMode: Int = 0
//
//    fun setMode(mode: Int) {
//        sessionMode = mode
//        when (mode) {
//            LOBBY_MODE -> utils.log("[SESS] sessionMode = LOBBY_MODE")
//            MODE_LOADING -> utils.log("[SESS] sessionMode = MODE_LOADING")
//            MODE_MATCH -> utils.log("[SESS] sessionMode = MODE_MATCH")
//            MODE_SLASH -> utils.log("[SESS] sessionMode = MODE_SLASH")
//            MODE_VICTORY -> utils.log("[SESS] sessionMode = MODE_VICTORY")
//        }
//    }
//
//    fun getPlayersList(): List<Fighter> = players.values.toList()
//        .sortedByDescending { item -> item.getRating() }
//        .sortedByDescending { item -> item.getBounty() }
//        .sortedByDescending { item -> if (!item.isIdle()) 1 else 0 }
//

//}



