package models

import twitch.WatcherData

class Watcher(watcherData: WatcherData) : Player(watcherData.twitchId, watcherData.displayName) {

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