package session

import SIMULATE_MODE
import database.DatabaseHandler
import database.SqlApi
import memscan.*
import utils.Duo
import utils.getIdString

class ApiHandler {

    private var clientId: Long = -1
    private val xrdApi: XrdApi = if (SIMULATE_MODE) MemRandomizer() else MemHandler()
    private val dataApi: SqlApi = DatabaseHandler()
    private val snapshot: Duo<LobbySnap> = Duo(LobbySnap(), LobbySnap())

    fun isXrdApiConnected() = xrdApi.isConnected()

    fun isDataApiConnected() = dataApi.isConnected()

    fun getClientId() = clientId
    fun defineClientId(session: Session) {
        val playerData = xrdApi.getPlayerData().filter { it.steamUserId != 0L }
        if (clientId == -1L && playerData.isNotEmpty()) {
            clientId = xrdApi.getClientSteamId()
            session.log("C: GearNet client defined ${getIdString(clientId)} ... (${session.getClient().getNameString()})")
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

}