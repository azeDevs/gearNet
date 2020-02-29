package session

import memscan.FighterData
import views.logging.LogText.Effect.*

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

    fun getCabinet() = getData().cabinetId()

    fun isSeated(seatId:Int) = getData().seatingId() == seatId

    fun getSeat() = getData().seatingId()

    fun isLoading() = getData().loadingPct() in 1..99

    fun justExitedStage() = ((oldData().seatingId() == 0 || oldData().seatingId() == 1)
            && (getData().seatingId() != 0 || getData().seatingId() != 1))
            || (oldData().seatingId() == 0 && getData().seatingId() == 1)
            || (oldData().seatingId() == 1 && getData().seatingId() == 0)

    fun getLog() = when (getSeat()) {
        0 -> L(getName(), RED)
        1 -> L(getName(), BLU)
        else -> L(getName(), YLW_FIGHT)
    }

}

