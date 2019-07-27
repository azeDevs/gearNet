package session

import events.FighterEvent
import events.ViewerEvent
import memscan.FighterData
import memscan.MatchData
import models.Fighter
import models.Match
import session.SessionMode.*
import twitch.Viewer
import twitch.ViewerData
import utils.log



class SessionState {

    private var matchSnaps: MutableList<Match> = arrayListOf()

    private val fighters: HashMap<Long, Fighter> = HashMap()
    private val viewers: HashMap<Long, Viewer> = HashMap()
    private var mode: SessionMode = MODE_NULL

    fun getMode() = mode
    fun isMode(vararg mode:SessionMode) = mode.filter { it == this.mode }.isNotEmpty()

    fun getMatch() = if (matchSnaps.isNotEmpty()) matchSnaps[matchSnaps.lastIndex] else Match()
    fun oldMatch() = if (matchSnaps.size > 1) matchSnaps[matchSnaps.lastIndex] else getMatch()
    fun getFighters() = fighters.values.filter { it.isValid() }
    fun getFighter(fighterData:FighterData) = fighters.getOrDefault(fighterData.steamId, Fighter(fighterData))

    fun getViewers() = viewers.values.filter { it.isValid() }
    fun getViewer(id:Long) = viewers.getOrDefault(id, Viewer())

    fun update(fd:FighterData):Boolean {
        val fighter = getFighter(fd)
        var flag = contains(fighter)
        if (flag) fighter.update(fd)
        else fighters.put(fighter.getId(), fighter)
        return flag
    }
    fun update(vd:ViewerData):Boolean {
        val viewer = getViewers().firstOrNull { it.getId() == vd.twitchId } ?: Viewer()
        var flag = viewer.isValid()
        viewer.update(vd)
        viewers.put(viewer.getId(), viewer)
        return flag
    }
    fun update(md:MatchData):Boolean {
        val fighterRed = getFighters().firstOrNull { it.getSeat() == 0 } ?: Fighter()
        val fighterBlue = getFighters().firstOrNull { it.getSeat() == 1 } ?: Fighter()
        val matchOut = Match(Pair(fighterRed, fighterBlue), md)
        if (!matchOut.equals(getMatch())) {
            matchSnaps.add(matchOut)
            log("matchSnaps", matchSnaps.size)
        }
        return oldMatch().getFighter(0).getId() != fighterRed.getId() || oldMatch().getFighter(1).getId() != fighterBlue.getId()
    }

    fun update(updatedMode:SessionMode): Boolean {
        var updated = false
        if (updatedMode != mode) {
            if (isMode(MODE_NULL)) updated = true
            when (updatedMode) {
                MODE_LOBBY -> if (isMode(MODE_VICTORY, MODE_MATCH)) updated = true
                MODE_LOADING -> if (isMode(MODE_LOBBY)) updated = true
                MODE_MATCH -> if (isMode(MODE_LOADING, MODE_SLASH)) updated = true
                MODE_SLASH -> if (isMode(MODE_MATCH)) updated = true
                MODE_VICTORY -> if (isMode(MODE_SLASH, MODE_MATCH)) updated = true
                else -> updated = false
            }
            if (updated) {
                log("Session changed to [${updatedMode.name}] (formerly ${mode.name.toLowerCase()})")
                mode = updatedMode
            }
        }
        return updated
    }

    fun contains(fighter:Fighter) = fighters.containsKey(fighter.getId())
    fun contains(viewer:Viewer) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterEvent) = fighters.containsKey(fighter.getId())
    fun contains(viewer:ViewerEvent) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterData) = fighters.containsKey(fighter.steamId)
    fun contains(viewer:ViewerData) = viewers.containsKey(viewer.twitchId)

}