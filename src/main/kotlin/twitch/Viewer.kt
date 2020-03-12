package twitch

import utils.addCommas

class Viewer(val data:ViewerData) {

    private var scoreTotal = 0
    private var scoreDelta = 0
    private var teamR = false
    private var teamB = false

    fun isValid() = data.isValid()

    fun setTeamR() { teamR = true }
    fun setTeamB() { teamB = true }
    fun isTeamR() = teamR
    fun isTeamB() = teamB

    fun getScore() = scoreTotal
    fun getScoreString() = if (getScore() > 0) "${addCommas(getScore().toString())} W$" else "FREE"
    fun changeScore(change:Int) {
        scoreDelta = change
        scoreTotal += change
        if (scoreTotal < 0) scoreTotal = 0
        teamR = false; teamB = false
    }

}