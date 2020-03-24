package memscan
import memscan.GearNet.PlayerData
import memscan.GearNetUpdates.Companion.IC_DATA_CHANGE
import memscan.GearNetUpdates.Companion.IC_DATA_PLAYER
import memscan.GearNetUpdates.GNLog
import models.Player
import utils.plural

class PlayerDataFactory() {


    private var oldData: PlayerData = PlayerData()
    private var newData: PlayerData = PlayerData()


    /**
     *
     */
    fun getNewData() = newData
    fun getOldData() = newData
    fun isNewPlayer() = !oldData.isValid()


    /**
     *
     */
    fun generateNewData(
        fighterData: FighterData,
        matchData: MatchData,
        frameData: GearNetFrameData,
        opponentData: FighterData,
        clientCabinet: Int
    ) {
        frameData.lastFrame().playerData.forEach { legacyData -> if (legacyData.steamId == fighterData.steamId) this.oldData = legacyData }
        val seatId = fighterData.seatingId.toInt()
        val playerData = when {
            isStagedOnClientCabinet(fighterData, clientCabinet) -> {
                PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent,
                    opponentData.steamId,
                    // MatchData values
                    matchData.health.toList()[seatId],
                    matchData.rounds.toList()[seatId],
                    matchData.tension.toList()[seatId],
                    matchData.stunCurrent.toList()[seatId],
                    matchData.stunMaximum.toList()[seatId],
                    matchData.burst.toList()[seatId],
                    matchData.struck.toList()[seatId],
                    matchData.guardGauge.toList()[seatId],
                    // MatchData deltas
//                    oldData.health-oldPlayerData.health, // healthDelta
//                    oldData.tension-oldPlayerData.tension, // tensionDelta
//                    oldData.stunCurrent-oldPlayerData.stunCurrent, // stunCurrentDelta
//                    oldData.stunMaximum-oldPlayerData.stunMaximum, // stunMaximumDelta
//                    oldData.guardGauge-oldPlayerData.guardGauge  // guardGaugeDelta

                    matchData.health.toList()[seatId]-oldData.health, // healthDelta
                    matchData.tension.toList()[seatId]-oldData.tension, // tensionDelta
                    matchData.stunCurrent.toList()[seatId]-oldData.stunCurrent, // stunCurrentDelta
                    matchData.stunMaximum.toList()[seatId]-oldData.stunMaximum, // stunMaximumDelta
                    matchData.guardGauge.toList()[seatId]-oldData.guardGauge  // guardGaugeDelta
                )
            }
            isStagedAnywhere(fighterData) -> {
                PlayerData(
                    fighterData.steamId,
                    fighterData.userName,
                    fighterData.characterId,
                    fighterData.cabinetId,
                    fighterData.seatingId,
                    fighterData.matchesWon,
                    fighterData.matchesSum,
                    fighterData.loadPercent,
                    opponentData.steamId
                )
            }
            else -> {
                PlayerData(
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

        this.newData = playerData
    }

    private fun isStagedOnClientCabinet(fighterData: FighterData, clientCabinet: Int) =
        isStagedAnywhere(fighterData) && fighterData.isOnCabinet(
            clientCabinet
        )

    private fun isStagedAnywhere(fighterData: FighterData) =
        fighterData.isSeatedAt(Player.PLAYER_1) || fighterData.isSeatedAt(Player.PLAYER_2)


    /**
     *
     */
    fun receivedUpdates(): List<GNLog> {
        val ic = "Δ"
        val updates = mutableListOf<GNLog>()
        if (oldData.steamId == newData.steamId) when {
            oldData.userName != newData.userName -> updates.add(GNLog(IC_DATA_CHANGE, "userName $ic ${oldData.userName} → ${newData.userName}"))
            oldData.characterId != newData.characterId -> updates.add(GNLog(IC_DATA_CHANGE, "characterId $ic ${oldData.characterId} → ${newData.characterId}"))
            oldData.cabinetId != newData.cabinetId -> updates.add(GNLog(IC_DATA_CHANGE, "cabinetId $ic ${oldData.cabinetId} → ${newData.cabinetId}"))
            oldData.seatingId != newData.seatingId -> updates.add(GNLog(IC_DATA_CHANGE, "seatingId $ic ${oldData.seatingId} → ${newData.seatingId}"))
            oldData.matchesWon != newData.matchesWon -> updates.add(GNLog(IC_DATA_CHANGE, "matchesWon $ic ${oldData.matchesWon} → ${newData.matchesWon}"))
            oldData.matchesSum != newData.matchesSum -> updates.add(GNLog(IC_DATA_CHANGE, "matchesSum $ic ${oldData.matchesSum} → ${newData.matchesSum}"))
            oldData.loadPercent != newData.loadPercent -> updates.add(GNLog(IC_DATA_CHANGE, "loadPercent $ic ${oldData.loadPercent} → ${newData.loadPercent}"))
            oldData.opponentId != newData.opponentId -> updates.add(GNLog(IC_DATA_CHANGE, "opponentId $ic ${oldData.opponentId} → ${newData.opponentId}"))
            // MatchData values
            oldData.health != newData.health -> updates.add(GNLog(IC_DATA_CHANGE, "health $ic ${oldData.health} → ${newData.health}"))
            oldData.rounds != newData.rounds -> updates.add(GNLog(IC_DATA_CHANGE, "rounds $ic ${oldData.rounds} → ${newData.rounds}"))
            oldData.tension != newData.tension -> updates.add(GNLog(IC_DATA_CHANGE, "tension $ic ${oldData.tension} → ${newData.tension}"))
            oldData.stunCurrent != newData.stunCurrent -> updates.add(GNLog(IC_DATA_CHANGE, "stunCurrent $ic ${oldData.stunCurrent} → ${newData.stunCurrent}"))
            oldData.stunMaximum != newData.stunMaximum -> updates.add(GNLog(IC_DATA_CHANGE, "stunMaximum $ic ${oldData.stunMaximum} → ${newData.stunMaximum}"))
            oldData.burst != newData.burst -> updates.add(GNLog(IC_DATA_CHANGE, "burst $ic ${oldData.burst} → ${newData.burst}"))
            oldData.stunLocked != newData.stunLocked -> updates.add(GNLog(IC_DATA_CHANGE, "stunLocked $ic ${oldData.stunLocked} → ${newData.stunLocked}"))
            oldData.guardGauge != newData.guardGauge -> updates.add(GNLog(IC_DATA_CHANGE, "guardGauge $ic ${oldData.guardGauge} → ${newData.guardGauge}"))
            // MatchData deltas
            oldData.healthDelta != newData.healthDelta -> updates.add(GNLog(IC_DATA_CHANGE, "healthDelta $ic ${oldData.healthDelta} → ${newData.healthDelta}"))
            oldData.tensionDelta != newData.tensionDelta -> updates.add(GNLog(IC_DATA_CHANGE, "tensionDelta $ic ${oldData.tensionDelta} → ${newData.tensionDelta}"))
            oldData.stunCurrentDelta != newData.stunCurrentDelta -> updates.add(GNLog(IC_DATA_CHANGE, "stunCurrentDelta $ic ${oldData.stunCurrentDelta} → ${newData.stunCurrentDelta}"))
            oldData.stunMaximumDelta != newData.stunMaximumDelta -> updates.add(GNLog(IC_DATA_CHANGE, "stunMaximumDelta $ic ${oldData.stunMaximumDelta} → ${newData.stunMaximumDelta}"))
            oldData.guardGaugeDelta != newData.guardGaugeDelta -> updates.add(GNLog(IC_DATA_CHANGE, "guardGaugeDelta $ic ${oldData.guardGaugeDelta} → ${newData.guardGaugeDelta}"))
        }
        return if (updates.isNotEmpty()) {
            val totalUpdates = mutableListOf(GNLog(IC_DATA_PLAYER, "Player ${newData.userName} got ${updates.size} ${plural("update", updates.size)}"))
            updates.forEach { totalUpdates.add(it) }
            totalUpdates
        } else emptyList()
    }


}