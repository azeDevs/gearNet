package gearnet

import MyApp.Companion.SIMULATION_MODE
import gearnet.GearNetFrameData.FrameData
import gearnet.GearNetShifter.Shift
import gearnet.GearNetUpdates.Companion.IC_COMPLETE
import gearnet.GearNetUpdates.Companion.IC_DATA_PLAYER
import gearnet.GearNetUpdates.Companion.IC_MATCHUP
import gearnet.GearNetUpdates.Companion.IC_SCAN
import gearnet.GearNetUpdates.GNLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import utils.NameGen
import utils.prLn
import utils.prnt
import utils.timeMillis

class GearNet {

    private val xrdApi: XrdApi = if (SIMULATION_MODE) MemRandomizer() else MemHandler()
    private val gnUpdates = GearNetUpdates()
    private val frameData = GearNetFrameData()
    private val gearShift = GearNetShifter(gnUpdates)
    private var xrdConnected = false


    /**
     *  Initiates memory scan on Guilty Gear Xrd to become [FrameData]
     */
    fun start() = GlobalScope.launch {
        val startTime = timeMillis()
        delay(24)
        if (xrdApi.isConnected()) {
            generateFrameData(startTime)
            // Update console once when Xrd Connected
            if (!xrdConnected) {
                gnUpdates.add(IC_SCAN, "Xrd Connected")
                xrdConnected = true
            }
        } else {
            // Update console once when Xrd Disconnected
            if (xrdConnected) {
                gnUpdates.add(IC_SCAN, "Xrd Disconnected")
                xrdConnected = false
            }
        }
        refreshGearNetUpdates()
    }


    /**
     *  Public access
     */
    fun getShift() = gearShift.getShift()
    fun getFrame() = frameData.lastFrame()
    fun getUpdateString() = gnUpdates.getUpdatesAsString(gearShift.getShift())



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
        val updates = update()
        gnUpdates.add(frameData.getFrameUpdateLog(startTime, updates))
        frameData.archiveMatchups()
        updates.forEach { gnUpdates.add(it) }
    }


    /**
     *  Collects all [FighterData] and [MatchData] from [XrdApi]
     *  and integrates them into a usable [FrameData] object
     */
    private fun update(): List<GNLog> {
        val totalUpdates = mutableListOf<GNLog>()
        val matchData = xrdApi.getMatchData()
        logMatchData(matchData)
        val dataList: MutableList<PlayerData> = mutableListOf()

        xrdApi.getFighterData().filter { it.isValid() }.forEach { fighterData ->
            val playerFactory = PlayerDataFactory()
            playerFactory.generateNewData(fighterData, matchData, frameData, getOpponent(fighterData), getClientCabinet())

            // Add GNLogs to totalUpdates
            val playerUpdates = playerFactory.receivedUpdates()
            if (playerUpdates.isNotEmpty()) {
                dataList.add(playerFactory.getNewData())
                totalUpdates.addAll(playerUpdates)
            } else dataList.add(playerFactory.getOldData())

            // If the PlayerData contains a unique steamID, add new PlayerData
            if (playerFactory.isNewPlayer()) {
                dataList.add(playerFactory.getNewData())
                totalUpdates.add(GNLog(IC_DATA_PLAYER, "Player ${playerFactory.getNewData().userName} added"))
            }
        }

        // Resolve and store MatchupData
        val matchupFactory = MatchupDataFactory()
        val muList: List<MatchupData> = matchupFactory.getMatchupData(dataList, matchData, frameData, getClientCabinet())
        if (muList.isNotEmpty()) {
            muList.filter { newMu -> frameData.lastFrame().matchupData.none { oldMu -> newMu.equals(oldMu) } }.forEach {
                totalUpdates.add(GNLog(IC_MATCHUP, "Matchup: ${it.player1.userName} vs ${it.player2.userName} [${it.timer}]"))
            }
        }

        if (totalUpdates.isNotEmpty()) frameData.addFrame(dataList, muList, gearShift.update(dataList, muList, getClientCabinet()))
        return totalUpdates
    }

    private fun logMatchData(matchData: MatchData) {
        val timer = matchData.timer
        val p1hp = matchData.health.first
        val p2hp = matchData.health.second
        val p1rnds = matchData.rounds.first
        val p2rnds = matchData.rounds.second
        val p1tens = matchData.tension.first
        val p2tens = matchData.tension.second
        val p1stun = matchData.stunCurrent.first
        val p2stun = matchData.stunCurrent.second
        val p1stmx = matchData.stunMaximum.first
        val p2stmx = matchData.stunMaximum.second
        val p1brst = matchData.burst.first
        val p2brst = matchData.burst.second
        val p1hit = matchData.struck.first
        val p2hit = matchData.struck.second
        val p1risc = matchData.guardGauge.first
        val p2risc = matchData.guardGauge.second
        prLn("$timer | hp:$p1hp $p2hp | rnds:$p1rnds $p2rnds | tens:$p1tens $p2tens | stun:$p1stun $p2stun | stmx:$p1stmx $p2stmx | brst:$p1brst $p2brst | hit:$p1hit $p2hit | risc:$p1risc $p2risc")
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
     *  Define which [steamId] should be associated with current [GearNet] session
     */
    private var clientId: Long = -1
    private fun getClientId() = clientId
    private fun defineClientId() {
        if (getClientId() == -1L && xrdApi.getFighterData().any { it.steamId != 0L }) {
            clientId = xrdApi.getClientSteamId()
            gnUpdates.add(IC_COMPLETE, "Client ID defined: ${NameGen.getIdString(getClientId())}")
        }
    }


    fun getRedPlayer(): PlayerData = getClientMatchup().player1
    fun getBluePlayer(): PlayerData = getClientMatchup().player2
    fun getClientMatchup(): MatchupData = if(getClientPlayer().cabinetId.toInt()>3) MatchupData() else frameData.lastFrame().matchupData.firstOrNull { it.isOnCabinet(getClientCabinet()) } ?: MatchupData()
    fun getClientCabinet(): Int = if(getClientPlayer().cabinetId.toInt()>3) -1 else getClientPlayer().cabinetId.toInt()
    fun getClientPlayer(): PlayerData = frameData.lastFrame().playerData.firstOrNull { it.steamId == xrdApi.getClientSteamId() } ?: PlayerData()


    /**
     *  [MatchupData] Class
     */
    @Suppress("CovariantEquals")
    data class MatchupData(
        val player1: PlayerData = PlayerData(),
        val player2: PlayerData = PlayerData(),
        val shift: Shift = Shift.GEAR_LOBBY,
        val winner: Int = -1,
        val timer: Int = -1
    ) {
        fun isTimeValid() = player1.isValid() && player2.isValid() && timer > -1
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
        val stunLocked: Boolean = false,
        val guardGauge: Int = -1,
        val healthDelta: Int = 0,
        val tensionDelta: Int = 0,
        val stunCurrentDelta: Int = 0,
        val stunMaximumDelta: Int = 0,
        val guardGaugeDelta: Int = 0
    ) {
        fun isValid() = steamId > 0
        fun isOnCabinet(cabinetId: Int = this.cabinetId.toInt()) = if(this.cabinetId.toInt() in 0..3) this.cabinetId.toInt() == cabinetId else false
        fun isSeated(seatingId: Int = this.seatingId.toInt()) = if(this.cabinetId.toInt() in 0..3) this.seatingId.toInt() == seatingId else false
        fun isLoading() = loadPercent in 1..99
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
                stunLocked == other.stunLocked &&
                guardGauge == other.guardGauge
    }


}

