package memscan
import memscan.GearNet.PlayerData
import memscan.GearNetIcons.IC_FRAME
import memscan.GearNetIcons.IC_PLAYER
import utils.plural

class PlayerDataFactory {

    private var oldData: PlayerData = PlayerData()
    private var newData: PlayerData = PlayerData()

    fun isNewPlayer() = !oldData.isValid()
    fun setOldData(playerData: PlayerData) { this.oldData = playerData }
    fun setNewData(playerData: PlayerData) { this.newData = playerData }

    fun receivedUpdates(): List<String> {
        val ic = "Δ"
        val updates = mutableListOf<String>()
        if (oldData.steamId == newData.steamId) when {
            oldData.userName != newData.userName -> updates.add("userName $ic ${oldData.userName} → ${newData.userName}")
            oldData.characterId != newData.characterId -> updates.add("characterId $ic ${oldData.characterId} → ${newData.characterId}")
            oldData.cabinetId != newData.cabinetId -> updates.add("cabinetId $ic ${oldData.cabinetId} → ${newData.cabinetId}")
            oldData.seatingId != newData.seatingId -> updates.add("seatingId $ic ${oldData.seatingId} → ${newData.seatingId}")
            oldData.matchesWon != newData.matchesWon -> updates.add("matchesWon $ic ${oldData.matchesWon} → ${newData.matchesWon}")
            oldData.matchesSum != newData.matchesSum -> updates.add("matchesSum $ic ${oldData.matchesSum} → ${newData.matchesSum}")
            oldData.loadPercent != newData.loadPercent -> updates.add("loadPercent $ic ${oldData.loadPercent} → ${newData.loadPercent}")
            oldData.opponentId != newData.opponentId -> updates.add("opponentId $ic ${oldData.opponentId} → ${newData.opponentId}")
            oldData.health != newData.health -> updates.add("health $ic ${oldData.health} → ${newData.health}")
            oldData.rounds != newData.rounds -> updates.add("rounds $ic ${oldData.rounds} → ${newData.rounds}")
            oldData.tension != newData.tension -> updates.add("tension $ic ${oldData.tension} → ${newData.tension}")
            oldData.stunCurrent != newData.stunCurrent -> updates.add("stunCurrent $ic ${oldData.stunCurrent} → ${newData.stunCurrent}")
            oldData.stunMaximum != newData.stunMaximum -> updates.add("stunMaximum $ic ${oldData.stunMaximum} → ${newData.stunMaximum}")
            oldData.burst != newData.burst -> updates.add("burst $ic ${oldData.burst} → ${newData.burst}")
            oldData.struck != newData.struck -> updates.add("struck $ic ${oldData.struck} → ${newData.struck}")
            oldData.guardGauge != newData.guardGauge -> updates.add("guardGauge $ic ${oldData.guardGauge} → ${newData.guardGauge}")
        }
        return if (updates.isNotEmpty()) {
            val totalUpdates = mutableListOf("$IC_PLAYER   Player ${newData.userName} got ${updates.size} ${plural("update", updates.size)}")
            updates.forEach { totalUpdates.add("     $IC_FRAME $it") }
            totalUpdates
        } else emptyList()
    }

}