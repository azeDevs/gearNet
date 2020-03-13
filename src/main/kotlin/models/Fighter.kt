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
    }

    fun getCabinet() = getData().cabinetLoc.toInt()
    fun getCabinetString(cabId:Int = getCabinet()): String {
        when(cabId) {
            0 -> return "Cabinet A"
            1 -> return "Cabinet B"
            2 -> return "Cabinet C"
            3 -> return "Cabinet D"
            else -> return "Lobby"
        }
    }

    fun getPlaySide() = getData().playerSide
    fun getPlaySideString(cabId:Int = getCabinet(), sideId:Int = getPlaySide().toInt()): String = if (cabId > 3) "Wandering"
        else when(sideId) {
            0 -> "Red Seat"
            1 -> "Blue Seat"
            2 -> "Prospect"
            3 -> "3rd"
            4 -> "4th"
            5 -> "5th"
            6 -> "6th"
            7 -> "Spectating"
            else -> "[${getPlaySide().toInt()}]"
        }


    fun getLoadPercent() = getData().loadingPct
    private fun isLoading() = getData().loadingPct in 1..99

    fun hasPlayed() = getData().matchesSum > oldData().matchesSum
    fun isLoser() = getData().matchesWon == oldData().matchesWon && hasPlayed()
    fun isWinner() = getData().matchesWon > oldData().matchesWon && hasPlayed()



}

