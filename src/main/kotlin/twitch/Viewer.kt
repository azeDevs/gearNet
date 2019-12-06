package twitch

import session.PlayerData
import utils.keepInRange

class Viewer(oldData:ViewerData = ViewerData(), newData:ViewerData = oldData): PlayerData<ViewerData>(oldData, newData, newData.name, newData.twitchId) {

    private var scoreTotal = 1000
    private var scoreDelta = 0

    fun getScoreTotal() = scoreTotal
    fun getScoreDelta() = scoreDelta

    fun changeScore(value:Int, subtraction:Int = 0): Int {
        scoreDelta = value-subtraction
        scoreTotal = keepInRange(scoreTotal+scoreDelta, 0)
        return scoreTotal
    }

}