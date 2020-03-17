package session

import MyApp.Companion.SIMULATE_MODE
import database.DatabaseHandler
import database.SqlApi
import memscan.*
import twitch.BotEventHandler
import twitch.ViewerData
import utils.Duo
import utils.getIdString
import utils.getRandomName
import kotlin.random.Random

class ApiHandler(session: Session) {

    private var clientId: Long = -1
    private val xrdApi: XrdApi = if (SIMULATE_MODE) MemRandomizer() else MemHandler()
    private val twitchHandler = BotEventHandler(session)
    private val dataApi: SqlApi = DatabaseHandler()
    private val snapshot: Duo<LobbySnap> = Duo(LobbySnap(), LobbySnap())

    fun isXrdApiConnected() = xrdApi.isConnected()

    fun getClientId() = clientId
    fun defineClientId(session: Session) {
        val playerData = xrdApi.getPlayerData().filter { it.steamUserId != 0L }
        if (clientId == -1L && playerData.isNotEmpty()) {
            clientId = xrdApi.getClientSteamId()
            log("C: GearNet client defined ${getIdString(clientId)} ... (${session.getClient().getUserName()})")
        }
    }

    fun getSnap(): LobbySnap {
        val lobbyData = xrdApi.getLobbyData()
        val playerData = xrdApi.getPlayerData().filter { it.steamUserId != 0L }
        if (clientId == -1L && playerData.isNotEmpty()) clientId = xrdApi.getClientSteamId()
        val updatedSnap = LobbySnap(playerData, lobbyData)
        snapshot.p1 = snapshot.p2
        snapshot.p2 = updatedSnap
        return updatedSnap
    }

    fun getMatchData(): MatchData {
        return xrdApi.getMatchData()
    }

    fun updateViewers() {
        twitchHandler.generateViewerEvents()
        if (SIMULATE_MODE) when (Random.nextInt(333)) {
            0 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngRC"))
            1 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngBC"))
            2 -> twitchHandler.addViewerData(ViewerData(Random.nextLong(1000000000, 9999999999), getRandomName(), getRandomName()))
        }
    }

}