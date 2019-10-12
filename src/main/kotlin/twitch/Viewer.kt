package twitch

import session.PlayerData
import utils.keepInRange

/*

    // TODO: Have Match handle MatchData snaps instead of SessionState handling Match snaps.
    - remember how MatchData snaps propogate

    // TODO: Give every Match a unique ID, and store alongside Fighter and Viewer maps.
    - find out wtf Fighter names don't display in console


    // Store all ViewerBets in the relevant Match.
    - remember htf matches work

    // Should the Match invalidate, so do the ViewerBets.
    - ezpz

*/

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