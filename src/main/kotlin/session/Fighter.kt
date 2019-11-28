package session

import javafx.beans.property.SimpleStringProperty
import memscan.FighterData
import tornadofx.getValue
import tornadofx.setValue

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
class Fighter(oldData: FighterData = FighterData(), newData: FighterData = oldData) : PlayerData<FighterData>(oldData, newData, newData.displayName(), newData.steamId()) {

    private val nameProperty = SimpleStringProperty(this, "name", newData.displayName())
    var fighterName by nameProperty

    private var bounty = 0
    private var delta = 0
    private var chain = 0
    private var idle = 1

    fun getBounty() = bounty
    fun getDelta() = delta

    private fun getMatchesWon() = getData().matchesWon()

    private fun getMatchesPlayed() = getData().matchesSum()

    fun getCabinet() = getData().cabinetId()

    fun isSeated(seatId:Int) = getData().seatingId() == seatId

    fun getSeat() = getData().seatingId()

    fun isLoading() = getData().loadingPct() in 1..99

    fun justPlayed() = getMatchesPlayed() > oldData().matchesSum()

    fun justLost() = getMatchesWon() == oldData().matchesWon() && justPlayed()

    fun justWon() = getMatchesWon() > oldData().matchesWon() && justPlayed()

    fun justExitedStage() = ((oldData().seatingId() == 0 || oldData().seatingId() == 1)
            && (getData().seatingId() != 0 || getData().seatingId() != 1))
            || (oldData().seatingId() == 0 && getData().seatingId() == 1)
            || (oldData().seatingId() == 1 && getData().seatingId() == 0)

}

