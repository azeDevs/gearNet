package twitch

import models.PlayerData
import utils.keepInRange

class Viewer(oldData:ViewerData = ViewerData(), newData:ViewerData = oldData): PlayerData<ViewerData>(oldData, newData, newData.name, newData.id) {

    private var scoreTotal = 0
    private var scoreDelta = 0

    fun getScoreTotal() = scoreTotal
    fun getScoreDelta() = scoreDelta

    fun changeScore(value:Int): Int {
        scoreTotal = keepInRange(scoreTotal+value, 0)
        scoreDelta = value
        return scoreTotal
    }



    /*

         f1 - 7 chain  [ VS ]  chain 1 - f2
                   bet 64% payout  |  p2chain*4 percent payout reduction
                                      p1chain*8 percent payout bonus
        p1bet = 100             p2bet = 1000
    */

}