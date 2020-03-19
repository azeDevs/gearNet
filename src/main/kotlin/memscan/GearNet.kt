package memscan

import memscan.GearNetFrameData.FrameData
import memscan.IconLegend.IC_GEAR
import memscan.IconLegend.IC_OKAY
import memscan.IconLegend.IC_PLAYER
import memscan.IconLegend.IC_PLUG
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import utils.getIdString
import utils.timeMillis

class GearNet {

    companion object {
        const val GEAR_OFFLINE = 0
        const val GEAR_LOBBY = 1
        const val GEAR_MATCH = 2
        const val GEAR_SLASH = 3
        const val GEAR_VICTORY = 4
        const val GEAR_LOADING = 5
        const val GEAR_TRAINER = 6


    }

    private val xrdApi: XrdApi = MemHandler()
    private val frameData = GearNetFrameData()
    private var clientId: Long = -1
    private var gearMode: Int = -1

    // TODO: GEAR SHIFTER
    // FIXME: PLAYER IDS DON'T ASSOCIATE PRE-MATCH, PLS FIX, (check this#getOpponent)
    // FIXME: MATCHDATA DOESN'T ASSOCIATE, PLS FIX

    /**
     *  Define which steamId should be associated with this [GearNet] session
     */
    private fun getClientId() = clientId
    private fun defineClientId() {
        if (getClientId() == -1L) {
            setGearMode(GEAR_OFFLINE)
            if (xrdApi.getFighterData().any { it.steamId != 0L }) {
                clientId = xrdApi.getClientSteamId()
                println("${IC_OKAY}clientId defined ${getIdString(clientId)}")
                setGearMode(GEAR_LOBBY)
            }
        }
    }

    /**
     *  [GearNet] Gear Shifter™
     */
    private fun setGearMode(mode: Int) {
        if (gearMode != mode) {
            gearMode = mode
            when (mode) {
                GEAR_OFFLINE -> println("$IC_GEAR→ OFFLINE")
                GEAR_LOBBY -> println("$IC_GEAR→ LOBBY")
                GEAR_MATCH -> println("$IC_GEAR→ MATCH")
                GEAR_SLASH -> println("$IC_GEAR→ SLASH")
                GEAR_VICTORY -> println("$IC_GEAR→ VICTORY")
                GEAR_LOADING -> println("$IC_GEAR→ LOADING")
                GEAR_TRAINER -> println("$IC_GEAR→ TRAINER")
            }
        }
    }

    /**
     *  Iniates and update from Guilty Gear Xrd to become [FrameData]
     */
    fun nextFrame() = when (xrdApi.isConnected()) {
        true -> {
            defineClientId()
            val startTime = timeMillis()
            frameData.logFrame(startTime, integratePlayerData())
        }
        else -> println("$IC_PLUG   ${frameData.frameCount()}: Frames stored, Xrd Disconnected")
    }

    /**
     *  Collects all [FighterData] and [MatchData] from [XrdApi]
     *  and integrates them into a usable [FrameData] object
     */
    private fun integratePlayerData(): Int {
        var totalUpdates = 0
        val matchData = xrdApi.getMatchData()
        val dataList: MutableList<PlayerData> = mutableListOf()

        xrdApi.getFighterData().filter { it.isValid() }.forEach { fighterData ->
            val playerFactory = PlayerDataFactory()
            playerFactory.setNewData(getPlayerData(fighterData, matchData))
            val updatedData = getPlayerData(fighterData, matchData)
            // Get the last FrameData to be compared to the incoming PlayerData
            frameData.lastFrame().playerDataList.forEach { legacyData ->
                // Does the legacy PlayerData have the same steamID as the new PlayerData?
                if (legacyData.steamId == updatedData.steamId) {
                    playerFactory.setOldData(legacyData)
                    // Does the new PlayerData differ from the legacy PlayerData?
                    // If not then use apply the legacy PlayerData instead
                    val playerUpdates = playerFactory.receivedUpdates()
                    if (playerUpdates > 0) {
                        dataList.add(updatedData)
                        totalUpdates += playerUpdates
                    } else dataList.add(legacyData)
                }
            }
            // If the PlayerData contains a unique steamID, add new PlayerData
            if (playerFactory.isNewPlayer()) {
                totalUpdates++
                dataList.add(updatedData)
                println("$IC_PLAYER   PlayerData ${updatedData.userName} added")
            }
        }

        if (totalUpdates > 0) {
            frameData.addFrame(dataList)
        }
        return totalUpdates
    }

    private fun isOnClientCabinet(fighterData: FighterData) = fighterData.isOnCabinet(getClientFighter().cabinetId.toInt())
    private fun getClientFighter() = xrdApi.getFighterData().firstOrNull { it.steamId == clientId } ?: FighterData()
    private fun getOpponent(fighterData: FighterData, seatingId: Int): FighterData = xrdApi.getFighterData().firstOrNull { it.cabinetId == fighterData.cabinetId && it.seatingId.toInt() == seatingId } ?: FighterData()
    private fun getPlayer(steamId: Long): PlayerData = frameData.lastFrame().playerDataList.firstOrNull { it.steamId == steamId } ?: PlayerData()

    private fun getPlayerData(fighterData: FighterData, matchData: MatchData): PlayerData {
        when {
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
                    getOpponent(fighterData, PLAYER_2).steamId
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
                    getOpponent(fighterData, PLAYER_1).steamId
                )
            }
            fighterData.isSeatedAt(PLAYER_1) && isOnClientCabinet(fighterData) -> {
                return PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent,
                    getOpponent(fighterData, PLAYER_2).steamId,
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
            fighterData.isSeatedAt(PLAYER_2) && isOnClientCabinet(fighterData) -> {
                return PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent,
                    getOpponent(fighterData, PLAYER_1).steamId,
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
        val guardGauge: Int = -1,
        val frameTime: Long = timeMillis()
    ) {
        fun isValid() = steamId > 0
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

