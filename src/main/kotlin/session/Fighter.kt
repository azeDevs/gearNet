package session

import memscan.FighterData

/**
 *
 * XrdHandler                  updates and archives Lobby getData().
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [Fighter]
 * contains Fighter bounty and chains data
 *
 */
class Fighter(oldData: FighterData = FighterData(), newData: FighterData = oldData) : PlayerData<FighterData>(oldData, newData, newData.displayName, newData.steamId) {

    private var bounty = 0
    private var delta = 0
    private var chain = 0
    private var idle = 1

    fun getDelta() = delta

    private fun getMatchesWon() = getData().matchesWon

    private fun getMatchesPlayed() = getData().matchesSum

    fun getCabinet() = getData().cabinetId

    fun getCabinetString(cabId:Int = getCabinet()): String {
        return when(cabId) {
            0 -> "Cabinet A"
            1 -> "Cabinet B"
            2 -> "Cabinet C"
            3 -> "Cabinet D"
            else -> ""
        }
    }

    fun isSeated(seatId:Int) = getData().seatingId == seatId
    fun getSeat() = getData().seatingId

    fun getSeatString(cabId:Int = getCabinet(), sideId:Int = getSeat()): String {
        if (cabId > 3) return ""
        return when(sideId) {
            0 -> "Red seat"
            1 -> "Blue seat"
            2 -> "2nd seat"
            3 -> "3rd seat"
            4 -> "4th seat"
            5 -> "5th seat"
            6 -> "6th seat"
            7 -> "Spectator seat"
            else -> "[${getSeat()}]"
        }
    }

    fun isLoading() = getData().loadingPct in 1..99

    fun justPlayed() = getMatchesPlayed() > oldData().matchesSum

    fun justLost() = getMatchesWon() == oldData().matchesWon && justPlayed()

    fun justWon() = getMatchesWon() > oldData().matchesWon && justPlayed()

}

