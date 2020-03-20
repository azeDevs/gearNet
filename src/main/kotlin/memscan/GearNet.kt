package memscan

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import memscan.GearNetFrameData.FrameData
import memscan.GearNetUpdates.Companion.IC_COMPLETE
import memscan.GearNetUpdates.Companion.IC_DATA_PLAYER
import memscan.GearNetUpdates.Companion.IC_MATCHUP
import memscan.GearNetUpdates.Companion.IC_SCAN
import memscan.GearNetUpdates.GNLog
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import utils.getIdString
import utils.timeMillis

class GearNet {

    private val xrdApi: XrdApi = MemHandler()
    private val frameData = GearNetFrameData()
    private val gnUpdates = GearNetUpdates()
    private val gearShift = GearNetShifter(gnUpdates, this)
    private var clientId: Long = -1


    /**
     *  Initiates memory scan on Guilty Gear Xrd to become [FrameData]
     */
    fun start() = GlobalScope.launch {
        val startTime = timeMillis()
        delay(12)
        if (xrdApi.isConnected()) generateFrameData(startTime)
        else gnUpdates.add(IC_SCAN, "Xrd Disconnected")
        refreshGearNetUpdates()
    }


    /**
     *  Public access
     */
    fun getShift() = gearShift.getShift()
    fun getFrame() = frameData.lastFrame()
    fun getUpdateString() = gnUpdates.getUpdatesAsString(gearShift.getShift())
    fun getClientMatchup(): MatchupData = getFrame().matchupData.firstOrNull { it.isOnCabinet(getClientCabinet()) } ?: MatchupData()
    fun getClientCabinet() = getClientPlayer().cabinetId.toInt()
    fun getClientPlayer(): PlayerData = getFrame().playerData.firstOrNull { it.steamId == xrdApi.getClientSteamId() } ?: PlayerData()


    /**
     *
     */
    private fun refreshGearNetUpdates() {
        gnUpdates.clearUpdatesToConsole()
        start()
    }


    /**
     *
     */
    private fun generateFrameData(startTime: Long) {
        defineClientId()
        gearShift.update()
        val updates = getGNLogUpdates()
        gnUpdates.add(frameData.getFrameUpdateLog(startTime, updates))
        updates.forEach { gnUpdates.add(it) }
    }


    /**
     *  Define which [steamId] should be associated with current [GearNet] session
     */
    private fun getClientId() = clientId
    private fun defineClientId() {
        if (getClientId() == -1L && xrdApi.getFighterData().any { it.steamId != 0L }) {
            clientId = xrdApi.getClientSteamId()
            gnUpdates.add(IC_COMPLETE, "Client ID defined: ${getIdString(clientId)}")
        }
    }




    /**
     *  Collects all [FighterData] and [MatchData] from [XrdApi]
     *  and integrates them into a usable [FrameData] object
     */
    private fun getGNLogUpdates(): List<GNLog> {
        val totalUpdates = mutableListOf<GNLog>()
        val matchData = xrdApi.getMatchData()
        val dataList: MutableList<PlayerData> = mutableListOf()

        xrdApi.getFighterData().filter { it.isValid() }.forEach { fighterData ->
            val playerFactory = PlayerDataFactory()
            playerFactory.setNewData(generatePlayerData(fighterData, matchData))
            val updatedData = generatePlayerData(fighterData, matchData)
            // Get the last FrameData to be compared to the incoming PlayerData
            frameData.lastFrame().playerData.forEach { legacyData ->
                // Does the legacy PlayerData have the same steamID as the new PlayerData?
                if (legacyData.steamId == updatedData.steamId) {
                    playerFactory.setOldData(legacyData)
                    // Does the new PlayerData differ from the legacy PlayerData?
                    // If not then use apply the legacy PlayerData instead
                    val playerUpdates = playerFactory.receivedUpdates()
                    if (playerUpdates.isNotEmpty()) {
                        dataList.add(updatedData)
                        totalUpdates.addAll(playerUpdates)
                    } else dataList.add(legacyData)
                }
            }
            // If the PlayerData contains a unique steamID, add new PlayerData
            if (playerFactory.isNewPlayer()) {
                dataList.add(updatedData)
                totalUpdates.add(GNLog(IC_DATA_PLAYER, "Player ${updatedData.userName} added"))
            }
        }

        val muList: List<MatchupData> = getMatchupData(dataList, matchData)
        if (muList.isNotEmpty()) {
            muList.filter { newMu -> frameData.lastFrame().matchupData.none { oldMu -> newMu.equals(oldMu) } }.forEach {
                totalUpdates.add(GNLog(IC_MATCHUP, "Matchup: ${it.player1.userName} vs ${it.player2.userName} [${it.timer}]"))
            }
        }
        if (totalUpdates.isNotEmpty()) frameData.addFrame(dataList, muList)
        return totalUpdates
    }


    /**
     *
     */
    private fun getMatchupData(dataList: MutableList<PlayerData>, matchData: MatchData): List<MatchupData> {
        val muList: MutableList<MatchupData> = mutableListOf()
        dataList.forEach { data1 ->
            dataList.forEach { data2 ->
                if (data1.steamId == data2.opponentId && data1.opponentId == data2.steamId) {
                    if (data1.isOnCabinet(getClientCabinet()) && data2.isOnCabinet(getClientCabinet())) {
                        if (data1.isSeatedAt(PLAYER_1)) {
                            if (muList.none { it.equals(MatchupData(data1, data2, matchData.timer)) }) muList.add(MatchupData(data1, data2, matchData.timer))
                        } else {
                            if (muList.none { it.equals(MatchupData(data2, data1, matchData.timer)) }) muList.add(MatchupData(data2, data1, matchData.timer))
                        }
                    } else if (data1.isSeatedAt(PLAYER_1)) {
                        if (muList.none { it.equals(MatchupData(data1, data2)) }) muList.add(MatchupData(data1, data2))
                    } else {
                        if (muList.none { it.equals(MatchupData(data2, data1)) }) muList.add(MatchupData(data2, data1))
                    }
                }
            }
        }
        return muList
    }


    /**
     *
     */
    private fun getOpponent(fighterData: FighterData): FighterData {
        return xrdApi.getFighterData().firstOrNull {
            return@firstOrNull if (it.isOnCabinet(fighterData.cabinetId.toInt())) {
                when {
                    fighterData.isSeatedAt(PLAYER_1) -> it.isSeatedAt(PLAYER_2)
                    fighterData.isSeatedAt(PLAYER_2) -> it.isSeatedAt(PLAYER_1)
                    else -> false
                }
            } else false
        } ?: FighterData()
    }


    /**
     *
     */
    private fun generatePlayerData(fighterData: FighterData, matchData: MatchData): PlayerData {
        when {
            fighterData.isSeatedAt(PLAYER_1) && fighterData.isOnCabinet(getClientCabinet()) -> {
                return PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent,
                    getOpponent(fighterData).steamId,
                    matchData.health.second,
                    matchData.rounds.second,
                    matchData.tension.second,
                    matchData.stunCurrent.second,
                    matchData.stunMaximum.second,
                    matchData.burst.second,
                    matchData.struck.second,
                    matchData.guardGauge.second
                )
            }
            fighterData.isSeatedAt(PLAYER_2) && fighterData.isOnCabinet(getClientCabinet()) -> {
                return PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent,
                    getOpponent(fighterData).steamId,
                    matchData.health.second,
                    matchData.rounds.second,
                    matchData.tension.second,
                    matchData.stunCurrent.second,
                    matchData.stunMaximum.second,
                    matchData.burst.second,
                    matchData.struck.second,
                    matchData.guardGauge.second
                )
            }
            fighterData.isSeatedAt(PLAYER_1) -> {
                return PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent,
                    getOpponent(fighterData).steamId
                )
            }
            fighterData.isSeatedAt(PLAYER_2) -> {
                return PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent,
                    getOpponent(fighterData).steamId
                )
            }
            else -> {
                return PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent
                )
            }
        }
    }


    /**
     *  [MatchupData] Class
     */
    @Suppress("CovariantEquals")
    data class MatchupData(
        val player1: PlayerData = PlayerData(),
        val player2: PlayerData = PlayerData(),
        val timer: Int = -1
    ) {
        fun isValid() = player1.isValid() && player2.isValid() && timer > -1
        fun getLoaders() = if(player1.loadPercent in 1..99 || player2.loadPercent in 1..99) (player1.loadPercent + player2.loadPercent)/2 else -1
        fun isOnCabinet(cabinetId: Int) = player1.isOnCabinet(cabinetId) && player2.isOnCabinet(cabinetId)
        fun equals(other: MatchupData) = timer == other.timer &&
                player1.steamId == other.player1.steamId &&
                player2.steamId == other.player2.steamId
    }


    /**
     *  [PlayerData] Class
     */
    @Suppress("CovariantEquals")
    data class PlayerData(
        val steamId: Long = -1L,
        val userName: String = "",
        val characterId: Byte = -0x1,
        val cabinetId: Byte = -0x1,
        val seatingId: Byte = -0x1,
        val matchesWon: Int = -1,
        val matchesSum: Int = -1,
        val loadPercent: Int = -1,
        val opponentId: Long = -1L,
        val health: Int = -1,
        val rounds: Int = -1,
        val tension: Int = -1,
        val stunCurrent: Int = -1,
        val stunMaximum: Int = -1,
        val burst: Boolean = false,
        val struck: Boolean = false,
        val guardGauge: Int = -1
    ) {
        fun isValid() = steamId > 0
        fun isOnCabinet(cabinetId: Int) = this.cabinetId.toInt() == cabinetId
        fun isSeatedAt(seatingId: Int) = this.seatingId.toInt() == seatingId
        fun equals(other: PlayerData) = steamId == other.steamId &&
                userName == other.userName &&
                characterId == other.characterId &&
                cabinetId == other.cabinetId &&
                seatingId == other.seatingId &&
                matchesWon == other.matchesWon &&
                matchesSum == other.matchesSum &&
                loadPercent == other.loadPercent &&
                health == other.health &&
                rounds == other.rounds &&
                tension == other.tension &&
                stunCurrent == other.stunCurrent &&
                stunMaximum == other.stunMaximum &&
                burst == other.burst &&
                struck == other.struck &&
                guardGauge == other.guardGauge
    }


}

