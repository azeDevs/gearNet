package twitch

import models.PlayerData

class Viewer(oldData:ViewerData = ViewerData(), newData:ViewerData = ViewerData()): PlayerData<ViewerData>(oldData, newData, newData.name, newData.id) {

    fun getBetFighterId() = getData().fighterId
    fun getBetAmount() = getData().betAmount

    /*

         f1 - 7 chain  [ VS ]  chain 1 - f2
                   bet 64% payout  |  p2chain*4 percent payout reduction
                                      p1chain*8 percent payout bonus
        p1bet = 100             p2bet = 1000
    */

}