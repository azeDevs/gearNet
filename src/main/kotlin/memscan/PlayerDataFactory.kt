package memscan
import memscan.GearNet.PlayerData
import memscan.IconLegend.IC_PLAYER
import utils.plural

class PlayerDataFactory {

    private var oldData: PlayerData = PlayerData()
    private var newData: PlayerData = PlayerData()

    fun isNewPlayer() = !oldData.isValid()
    fun setOldData(playerData: PlayerData) { this.oldData = playerData }
    fun setNewData(playerData: PlayerData) { this.newData = playerData }

    fun receivedUpdates(): Int {
        val updateLogs = mutableListOf<String>()
        var updates = 0
        if (oldData.steamId == newData.steamId) when {
            oldData.userName != newData.userName -> { updates++ }
            oldData.characterId != newData.characterId -> { updates++ }
            oldData.cabinetId != newData.cabinetId -> { updates++ }
            oldData.seatingId != newData.seatingId -> { updates++ }
            oldData.matchesWon != newData.matchesWon -> { updates++ }
            oldData.matchesSum != newData.matchesSum -> { updates++ }
            oldData.loadPercent != newData.loadPercent -> { updates++ }
            oldData.opponentId != newData.opponentId -> { updates++
                if (newData.opponentId > 0) updateLogs.add("New Match: ${newData.userName} vs ${newData.opponentId}")
            }
            oldData.health != newData.health -> { updates++ }
            oldData.rounds != newData.rounds -> { updates++ }
            oldData.tension != newData.tension -> { updates++ }
            oldData.stunCurrent != newData.stunCurrent -> { updates++ }
            oldData.stunMaximum != newData.stunMaximum -> { updates++ }
            oldData.burst != newData.burst -> { updates++ }
            oldData.struck != newData.struck -> { updates++ }
            oldData.guardGauge != newData.guardGauge -> { updates++ }
        }
        if (updates > 0) println("$IC_PLAYER   Player ${newData.userName} got $updates ${plural("update", updates)}")
        return updates
    }

}