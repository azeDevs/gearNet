package session

import memscan.LobbyData
import memscan.MatchData
import memscan.PlayerData

class Snap(val lobby: LobbyData, val players:List<PlayerData>, val match:List<MatchData>, val clientId:Long) {
    
    fun getMatchSnapshots():List<Match> {
        val snaps = arrayListOf<Match>()
        
        return snaps
    }
    
}