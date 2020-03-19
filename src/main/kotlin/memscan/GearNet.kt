package memscan

import javafx.collections.ObservableList
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import tornadofx.asObservable
import tornadofx.observableListOf
import utils.plural
import utils.timeMillis

@Suppress("CovariantEquals")
class GearNet {

    private val xrdApi: XrdApi = MemHandler()
    private val frames: MutableList<FrameData> = mutableListOf(FrameData())

    fun nextFrame() = when (xrdApi.isConnected()) {
        true -> {
            var updates = 0
            val updatedDataList: MutableList<PlayerData> = mutableListOf()
            val matchData = xrdApi.getMatchData()
            xrdApi.getFighterData().forEach { fighterData ->
                val updatedPlayerData = getPlayerData(fighterData, matchData)
                frames.last().playerDataList.forEach { legacy ->
                    if (legacy.steamId == updatedPlayerData.steamId && !legacy.equals(updatedPlayerData)) {
                        updatedDataList.add(updatedPlayerData)
                        println("${++updates}: ${updatedPlayerData.userName} updated")
                    } else if (legacy.equals(updatedPlayerData)) updatedDataList.add(legacy)
                    else {
                        updatedDataList.add(updatedPlayerData)
                        println("${++updates}: ${updatedPlayerData.userName} added")
                    }
                }

            }

            if (updates>0) frames.add(FrameData(updatedDataList.asObservable()))
            println("${frames.size}: Frames Completed, $updates ${plural("update", updates)}")
        } else -> {
            println("${frames.size}: Xrd Disconnected")

        }
    }

    private fun getPlayerData(fighterData: FighterData, matchData: MatchData) =
        if (fighterData.cabinetLoc.toInt() == 0 && fighterData.playerSide.toInt() == PLAYER_1) {
            PlayerData(
                fighterData.steamUserId,
                fighterData.displayName,
                fighterData.characterId.toInt(),
                fighterData.cabinetLoc.toInt(),
                fighterData.playerSide.toInt(),
                fighterData.matchesWon,
                fighterData.matchesSum,
                fighterData.loadingPct,
                matchData.health.first,
                matchData.rounds.first,
                matchData.tension.first,
                matchData.stunProgress.first,
                matchData.stunMaximum.first,
                matchData.canBurst.first,
                matchData.strikeStun.first,
                matchData.guardGauge.first
            )
        } else if (fighterData.cabinetLoc.toInt() == 0 && fighterData.playerSide.toInt() == PLAYER_2) {
            PlayerData(
                fighterData.steamUserId,
                fighterData.displayName,
                fighterData.characterId.toInt(),
                fighterData.cabinetLoc.toInt(),
                fighterData.playerSide.toInt(),
                fighterData.matchesWon,
                fighterData.matchesSum,
                fighterData.loadingPct,
                matchData.health.second,
                matchData.rounds.second,
                matchData.tension.second,
                matchData.stunProgress.second,
                matchData.stunMaximum.second,
                matchData.canBurst.second,
                matchData.strikeStun.second,
                matchData.guardGauge.second
            )
        } else {
            PlayerData(
                fighterData.steamUserId,
                fighterData.displayName,
                fighterData.characterId.toInt(),
                fighterData.cabinetLoc.toInt(),
                fighterData.playerSide.toInt(),
                fighterData.matchesWon,
                fighterData.matchesSum,
                fighterData.loadingPct
            )
        }


    /**
     *  Data Classes
     */
    data class FrameData(
        val playerDataList: ObservableList<PlayerData> = observableListOf(),
        val frameTime: Long = timeMillis()
    )
    data class PlayerData(
        val steamId: Long = -1L,
        val userName: String = "",
        val character: Int = -1,
        val cabinet: Int = -1,
        val seating: Int = -1,
        val matchesWon: Int = -1,
        val matchesSum: Int = -1,
        val loadPercent: Int = -1,
        val health: Int = -1,
        val rounds: Int = -1,
        val tension: Int = -1,
        val stunCurrent: Int = -1,
        val stunMaximum: Int = -1,
        val burst: Boolean = false,
        val struck: Boolean = false,
        val guardGauge: Int = -1,
        val frameTime: Long = timeMillis()
    ) { fun isValid() = steamId > -1
        fun equals(other: PlayerData) = steamId == other.steamId &&
                userName == other.userName &&
                character == other.character &&
                cabinet == other.cabinet &&
                seating == other.seating &&
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

