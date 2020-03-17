package session

import MyApp.Companion.SIMULATE_MODE
import database.DatabaseHandler
import database.SqlApi
import memscan.MemHandler
import memscan.MemRandomizer
import memscan.XrdApi
import session.Session.Companion.LOBBY_MODE
import twitch.BotEventHandler
import twitch.ViewerData
import utils.getIdString
import utils.getRandomName
import kotlin.random.Random

class ApiHandler(val session: Session) {

    private var clientId: Long = -1
    private val xrdApi: XrdApi = if (SIMULATE_MODE) MemRandomizer() else MemHandler()
    private val twitchHandler = BotEventHandler(session)
    private val databaseHandler: SqlApi = DatabaseHandler()

    fun isXrdApiConnected() = xrdApi.isConnected()

    fun getClientId() = clientId
    fun defineClientId() {
        val playerData = xrdApi.getFighterData().filter { it.steamUserId != 0L }
        if (clientId == -1L && playerData.isNotEmpty()) {
            clientId = xrdApi.getClientSteamId()
            println("GearNet client defined ${getIdString(clientId)}")
            session.setMode(LOBBY_MODE)
        }
    }

    fun getFightersInLobby() = xrdApi.getFighterData().filter { it.steamUserId != 0L }
    fun getFightersLoading() = xrdApi.getFighterData().filter { it.loadingPct in 1..99 }
    fun getMatchData() = xrdApi.getMatchData()


    fun updateViewers() {
        twitchHandler.generateViewerEvents()
        if (SIMULATE_MODE) when (Random.nextInt(333)) {
            0 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngRC"))
            1 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngBC"))
            2 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), getRandomName()))
        }
    }

}