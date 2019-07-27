package twitch

import session.PlayerData
import utils.keepInRange

// FIXME: REMOVE SessionState AS A DEPENDENCY!!!
/*
    Store all ViewerBets in the relevant Match. Should the Match invalidate, so do the ViewerBets.
    Have Match handle MatchData snaps instead of SessionState handling Match snaps.
    Give every Match a unique ID, and store alongside Fighter and Viewer maps.

*/
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