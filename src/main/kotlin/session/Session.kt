package session

import events.EventType.*
import events.FighterEvent
import events.XrdEventListener
import models.Fighter
import tornadofx.Controller
import twitch.TwitchBot
import twitch.Viewer
import utils.addCommas
import utils.log


class Session : Controller() {

    private var sessionMode = SessionMode.LOADING_MODE
    private val fighters: HashMap<Long, Fighter> = HashMap()
    private val viewers: HashMap<Long, Viewer> = HashMap()

    private val xrdListener = XrdEventListener()
    private val botApi = TwitchBot()

    fun refreshSession() {
        // PROCESS Xrd EVENTS
        xrdListener.generateEvents().forEach {
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

        // PROCESS Viewers
        botApi.getViewerData().forEach {
            if (it.betAmount > 0) {
                botApi.sendMessage("${addCommas(it.betAmount)} \uD835\uDE86\$ ${it.betBanner.first} ${it.name}")
                log("Bet from ${it.name}, ${addCommas(it.betAmount)} W\$ on ${it.betBanner.second}")
            } else log("Bet failed from Viewer ${it.name}, invalid amount")
        }

    }

    private fun runFighterJoined(it: FighterEvent) {
        log("Fighter \"${it.getFighter().getName()}\" joined")
    }

    private fun runFighterMoved(it: FighterEvent) {
        log("Fighter \"${it.getFighter().getName()}\" moved ${
        if (it.getFighter().getCabinet() > 3) "off cabinet" 
        else "to ${it.getFighter().getSeatString()
        }, ${it.getFighter().getCabinetString()}"}")
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
        log("Round ENDED with ${
        if (it.getDelta(0) == 1) "P1 \"${it.getFighter(0).getName()}\"" 
        else "P2 \"${it.getFighter(1).getName()}\""
        } as the winner.")
    }

    private fun runRoundStarted(it: FighterEvent) {
        log("Round STARTED with P1 \"${it.getFighter(0).getName()}\" and P2 \"${it.getFighter(1).getName()}\"")
    }

    private fun runMatchEnded(it: FighterEvent) {
        log("Match ENDED with ${
        if (it.getDelta(0) == 1) "P1 \"${it.getFighter(0).getName()}\"" 
        else "P2 \"${it.getFighter(1).getName()}\""
        } as the winner.")
    }

    private fun runMatchLoading(it: FighterEvent) {
        log("Match LOADING with P1 \"${it.getFighter(0).getName()}\" and P2 \"${it.getFighter(1).getName()}\"")
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
//            // Set sessionMode to MATCH_MODE
//            if (sessionMode == MATCH_MODE
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
//            // Set sessionMode to LOADING_MODE
//            if (matchHandler.clientMatch.matchId == -1L
//                && clientMatchPlayers.f1.steamId > 0L
//                && clientMatchPlayers.f2.steamId > 0L) {
//                matchHandler.clientMatch =
//                    Match(matchHandler.archiveMatches.size.toLong(), getClient().getCabinet(), clientMatchPlayers)
//                utils.log("[SESS] Generated Match ${getIdString(matchHandler.archiveMatches.size.toLong())}")
//                somethingChanged = true
//                setMode(LOADING_MODE)
//            }
//            // Set sessionMode to LOBBY_MODE
//            if (sessionMode != LOBBY_MODE && sessionMode != LOADING_MODE
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
//            LOADING_MODE -> utils.log("[SESS] sessionMode = LOADING_MODE")
//            MATCH_MODE -> utils.log("[SESS] sessionMode = MATCH_MODE")
//            SLASH_MODE -> utils.log("[SESS] sessionMode = SLASH_MODE")
//            VICTORY_MODE -> utils.log("[SESS] sessionMode = VICTORY_MODE")
//        }
//    }
//
//    fun getPlayersList(): List<Fighter> = players.values.toList()
//        .sortedByDescending { item -> item.getRating() }
//        .sortedByDescending { item -> item.getBounty() }
//        .sortedByDescending { item -> if (!item.isIdle()) 1 else 0 }
//

//}



