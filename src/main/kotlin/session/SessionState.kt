package session

import memscan.FighterData
import memscan.MatchSnap
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

    fun isMode(vararg mode:Mode) = this.sessionMode.isMode(*mode)
    fun update(mode:Mode) = this.sessionMode.update(mode)

    fun getFighters(): List<Fighter> = fighters.values.filter { it.isValid() }
    fun getFighter(fighterData:FighterData) = fighters.getOrDefault(fighterData.steamId, Fighter(fighterData))
    fun update(fd:FighterData):Boolean {
        val fighter = getFighter(fd)
        val flag = contains(fighter)
        if (flag) fighter.update(fd)
        else fighters[fighter.getId()] = fighter
        return flag
    }

    fun getViewer(id:Long) = viewers.getOrDefault(id, Viewer())
    fun putViewer(v:Viewer) = if (v.isValid()) { viewers[v.getId()] = v; true } else false
    fun update(vd:ViewerData):Boolean {
        val viewer = viewers.values.filter { it.isValid() }.firstOrNull { it.getId() == vd.twitchId } ?: Viewer()
        val flag = viewer.isValid()
        viewer.update(vd)
        viewers[viewer.getId()] = viewer
        return flag
    }

    fun getStage() = matchStage
    fun getMatch() = matchStage.getMatch()
    fun update(md: MatchSnap):Boolean = matchStage.addSnap(md)
    fun addBet(vb: ViewerBet):Boolean = matchStage.addBet(vb)

    fun contains(fighter: Fighter) = fighters.containsKey(fighter.getId())
    fun contains(viewer:Viewer) = viewers.containsKey(viewer.getId())
    fun contains(fighter:FighterData) = fighters.containsKey(fighter.steamId)
    fun contains(viewer:ViewerData) = viewers.containsKey(viewer.twitchId)

}

