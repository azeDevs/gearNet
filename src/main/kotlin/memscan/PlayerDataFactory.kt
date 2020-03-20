package memscan
import memscan.GearNet.PlayerData
import memscan.GearNetUpdates.Companion.IC_DATA_CHANGE
import memscan.GearNetUpdates.Companion.IC_DATA_PLAYER
import memscan.GearNetUpdates.GNLog
import utils.plural

class PlayerDataFactory {

    private var oldData: PlayerData = PlayerData()
    private var newData: PlayerData = PlayerData()

    fun isNewPlayer() = !oldData.isValid()
    fun setOldData(playerData: PlayerData) { this.oldData = playerData }
    fun setNewData(playerData: PlayerData) { this.newData = playerData }

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
            oldData.health != newData.health -> updates.add(GNLog(IC_DATA_CHANGE, "health $ic ${oldData.health} → ${newData.health}"))
            oldData.rounds != newData.rounds -> updates.add(GNLog(IC_DATA_CHANGE, "rounds $ic ${oldData.rounds} → ${newData.rounds}"))
            oldData.tension != newData.tension -> updates.add(GNLog(IC_DATA_CHANGE, "tension $ic ${oldData.tension} → ${newData.tension}"))
            oldData.stunCurrent != newData.stunCurrent -> updates.add(GNLog(IC_DATA_CHANGE, "stunCurrent $ic ${oldData.stunCurrent} → ${newData.stunCurrent}"))
            oldData.stunMaximum != newData.stunMaximum -> updates.add(GNLog(IC_DATA_CHANGE, "stunMaximum $ic ${oldData.stunMaximum} → ${newData.stunMaximum}"))
            oldData.burst != newData.burst -> updates.add(GNLog(IC_DATA_CHANGE, "burst $ic ${oldData.burst} → ${newData.burst}"))
            oldData.struck != newData.struck -> updates.add(GNLog(IC_DATA_CHANGE, "struck $ic ${oldData.struck} → ${newData.struck}"))
            oldData.guardGauge != newData.guardGauge -> updates.add(GNLog(IC_DATA_CHANGE, "guardGauge $ic ${oldData.guardGauge} → ${newData.guardGauge}"))
        }
        return if (updates.isNotEmpty()) {
            val totalUpdates = mutableListOf(GNLog(IC_DATA_PLAYER, "Player ${newData.userName} got ${updates.size} ${plural("update", updates.size)}"))
            updates.forEach { totalUpdates.add(it) }
            totalUpdates
        } else emptyList()
    }

}