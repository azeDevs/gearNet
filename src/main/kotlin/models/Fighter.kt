package models

import memscan.FighterData


class Fighter(fighterData: FighterData = FighterData()) : Player(fighterData.steamUserId, fighterData.displayName) {

    private var data = Pair(fighterData, fighterData)

    private fun oldData() = data.first
    fun getData() = data.second

    fun updatePlayerData(updatedData: FighterData, playersActive: Int) {
        data = Pair(getData(), updatedData)
        if (isLoading()) setBystanding(playersActive)
        setMatchesWon(updatedData.matchesWon)
        setMatchesSum(updatedData.matchesSum)
        setCharacterId(updatedData.characterId)
    }

    fun isOnCabinet(cabId:Int = -1) = if(cabId in 0..3) getData().cabinetLoc.toInt() == cabId else cabId in 0..3
    fun getCabinet() = getData().cabinetLoc.toInt()
    fun getCabinetString(cabId:Int = getCabinet()): String = when(cabId) {
        0 -> "A"
        1 -> "B"
        2 -> "C"
        3 -> "D"
        else -> "F"
    }

    fun isOnPlaySide(sideId:Int = -1) = if(getPlaySide() in 0..1) getData().playerSide.toInt() == sideId else getPlaySide() in 0..1
    fun getPlaySide() = getData().playerSide.toInt()
    fun getPlaySideString(cabId:Int = getCabinet(), sideId:Int = getPlaySide()): String = if (cabId > 3) "Wandering"
        else when(sideId) {
            0 -> "Red"
            1 -> "Blue"
            2 -> "Prospect"
            3 -> "3rd"
            4 -> "4th"
            5 -> "5th"
            6 -> "6th"
            7 -> "Spectating"
            else -> "[${getPlaySide()}]"
        }

    fun getLoadPercent() = getData().loadingPct
    private fun isLoading() = getData().loadingPct in 1..99

    fun hasPlayed() = getData().matchesSum > oldData().matchesSum
    fun isLoser() = getData().matchesWon == oldData().matchesWon && hasPlayed()
    fun isWinner() = getData().matchesWon > oldData().matchesWon && hasPlayed()

    fun getDebugDataString(mask: Int = -1) = when {
        !isValid() -> "-"
        mask == 0 -> getUserName()
        mask == 1 -> "${getCabinetString()+getPlaySide()}[${getLoadPercent()}] ${getUserName()}"
        mask == 2 -> "${getCabinetString()} ${getPlaySideString()}: [${getLoadPercent()}] ${getUserName()}"
        else -> "-"
    }


}

