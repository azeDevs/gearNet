package session

import events.FighterEvent
import events.ViewerEvent
import memscan.FighterData
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
    private var mode: SessionMode = SessionMode.MODE_NULL

    fun getMode() = mode
    fun isMode(vararg mode:SessionMode) = mode.filter { it == this.mode }.isNotEmpty()

    fun updateMode(updatedMode:SessionMode): Boolean {
        var updated = false
        if (updatedMode != mode) {
            if (isMode(MODE_NULL)) updated = true
            when (updatedMode) {
                MODE_LOBBY -> if (isMode(MODE_VICTORY)) updated = true
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

    fun getMatch() = if (matchSnaps.isNotEmpty()) matchSnaps[matchSnaps.lastIndex] else Match()
    fun getFighters() = fighters.values.filter { it.isValid() }
    fun getFighter(id:Long) = fighters.getOrDefault(id, Fighter())
    fun getViewers() = viewers.values.filter { it.isValid() }
    fun getViewer(id:Long) = viewers.getOrDefault(id, Viewer())

    fun update(f:FighterEvent) = fighters.put(f.get().getId(), Fighter(fighters.getOrDefault(f.getId(), Fighter()).getData(), f.get().getData()))
    fun update(v:ViewerEvent) = viewers.put(v.get().getId(), Viewer(viewers.getOrDefault(v.getId(), Viewer()).getData(), v.get().getData()))
    fun update(m:Match) = matchSnaps.add(m)

    fun contains(fighter:Fighter) = fighters.containsKey(fighter.getId())
    fun contains(viewer:Viewer) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterEvent) = fighters.containsKey(fighter.getId())
    fun contains(viewer:ViewerEvent) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterData) = fighters.containsKey(fighter.steamId)
    fun contains(viewer:ViewerData) = viewers.containsKey(viewer.twitchId)

    fun containsFighter(id:Long) = fighters.containsKey(id)
    fun containsViewer(id:Long) = viewers.containsKey(id)

}