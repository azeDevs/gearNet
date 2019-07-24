package session

import events.FighterEvent
import events.ViewerEvent
import events.XrdEventListener
import memscan.FighterData
import models.Fighter
import twitch.TwitchBot
import twitch.Viewer
import twitch.ViewerData

class SessionState {

    private val fighters: HashMap<Long, Fighter> = HashMap()
    private val viewers: HashMap<Long, Viewer> = HashMap()
    private var sessionMode = SessionMode.MODE_NULL
    private val xrdListener = XrdEventListener()
    private val botApi = TwitchBot()

    fun getFighter(id:Long) = fighters.getOrDefault(id, Fighter())
    fun getViewer(id:Long) = viewers.getOrDefault(id, Viewer())

    fun update(f:FighterEvent) = fighters.put(f.get().getId(), Fighter(fighters.getOrDefault(f.getId(), Fighter()).getData(), f.get().getData()))
    fun update(v:ViewerEvent) = viewers.put(v.get().getId(), Viewer(viewers.getOrDefault(v.getId(), Viewer()).getData(), v.get().getData()))

    fun contains(fighter:Fighter) = fighters.containsKey(fighter.getId())
    fun contains(viewer:Viewer) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterEvent) = fighters.containsKey(fighter.getId())
    fun contains(viewer:ViewerEvent) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterData) = fighters.containsKey(fighter.steamId)
    fun contains(viewer:ViewerData) = viewers.containsKey(viewer.twitchId)

    fun containsFighter(id:Long) = fighters.containsKey(id)
    fun containsViewer(id:Long) = viewers.containsKey(id)

}