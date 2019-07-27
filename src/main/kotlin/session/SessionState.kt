package session

import events.FighterEvent
import events.ViewerEvent
import memscan.FighterData
import memscan.MatchData
import session.Session.Mode
import twitch.Viewer
import twitch.ViewerBet
import twitch.ViewerData
import utils.SessionMode


class SessionState {

    private val sessionMode: SessionMode = SessionMode()
    private val matchStage: MatchStage = MatchStage()
    private val fighters: HashMap<Long, Fighter> = HashMap()
    private val viewers: HashMap<Long, Viewer> = HashMap()

    fun getMode() = sessionMode.get()
    fun isMode(vararg mode:Mode) = this.sessionMode.isMode(*mode)
    fun update(mode:Mode) = this.sessionMode.update(mode)

    fun getFighters(): List<Fighter> = fighters.values.filter { it.isValid() }
    fun getFighter(fighterData:FighterData) = fighters.getOrDefault(fighterData.steamId, Fighter(fighterData))
    fun update(fd:FighterData):Boolean {
        val fighter = getFighter(fd)
        var flag = contains(fighter)
        if (flag) fighter.update(fd)
        else fighters.put(fighter.getId(), fighter)
        return flag
    }

    fun getViewers(): List<Viewer> = viewers.values.filter { it.isValid() }
    fun getViewer(id:Long) = viewers.getOrDefault(id, Viewer())
    fun putViewer(v:Viewer) = if (v.isValid()) { viewers.put(v.getId(), v); true } else false
    fun update(vd:ViewerData):Boolean {
        val viewer = getViewers().firstOrNull { it.getId() == vd.twitchId } ?: Viewer()
        var flag = viewer.isValid()
        viewer.update(vd)
        viewers.put(viewer.getId(), viewer)
        return flag
    }

    fun getStage() = matchStage
    fun getMatch() = matchStage.getMatch()
    fun update(md: MatchData):Boolean = matchStage.update(md)
    fun addBet(vb: ViewerBet):Boolean = matchStage.addBet(vb)

    fun contains(fighter: Fighter) = fighters.containsKey(fighter.getId())
    fun contains(viewer:Viewer) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterEvent) = fighters.containsKey(fighter.getId())
    fun contains(viewer:ViewerEvent) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterData) = fighters.containsKey(fighter.steamId)
    fun contains(viewer:ViewerData) = viewers.containsKey(viewer.twitchId)

}

