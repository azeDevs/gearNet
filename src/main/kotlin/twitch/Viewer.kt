package twitch

import models.PlayerData
import utils.keepInRange

class Viewer(oldData:ViewerData = ViewerData(), newData:ViewerData = oldData): PlayerData<ViewerData>(oldData, newData, newData.name, newData.twitchId) {

    private var scoreTotal = 1000
    private var scoreDelta = 0

    fun getScoreTotal() = scoreTotal
    fun getScoreDelta() = scoreDelta

    fun changeScore(value:Int): Int {
        scoreTotal = keepInRange(scoreTotal+value, 0)
        scoreDelta = value
        return scoreTotal
    }

}