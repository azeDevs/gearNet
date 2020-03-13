package models

import twitch.ViewerData

class Viewer(viewerData: ViewerData) : Player(viewerData.twitchId, viewerData.displayName) {

    private var teamR = false
    private var teamB = false

    fun setTeamR() { teamR = true }
    fun setTeamB() { teamB = true }
    fun isTeamR() = teamR
    fun isTeamB() = teamB
    fun resetTeam() {
        teamR = false
        teamB = false
    }

}