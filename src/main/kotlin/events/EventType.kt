package events

import session.Fighter
import twitch.Viewer


class FighterEvent {

    private val eventType: EventType
    private val fighters: Pair<Fighter, Fighter>
    private val deltas: Pair<Int, Int>

    fun get(i:Int = 0) = if (i == 0) fighters.first else fighters.second
    fun getId(i:Int = 0) = if (i == 0) fighters.first.getId() else fighters.second.getId()
    fun getName(i:Int = 0) = if (i == 0) fighters.first.getName() else fighters.second.getName()

    fun getType() = eventType
    fun getDelta(i:Int = 0) = if (i == 0) deltas.first else deltas.second

    // 1 0 0
    constructor(eventType: EventType = EventType.NULL_EVENT) {
        this.eventType = eventType
        this.fighters = Pair(Fighter(), Fighter())
        this.deltas = Pair(0, 0)
    }
    // 1 1 0
    constructor(eventType: EventType = EventType.NULL_EVENT,
                fighter: Fighter = Fighter()
    ) {
        this.eventType = eventType
        this.fighters = Pair(fighter, fighter)
        this.deltas = Pair(0, 0)
    }
    // 1 1 1
    constructor(eventType: EventType = EventType.NULL_EVENT,
                fighter: Fighter = Fighter(),
                delta: Int = 0) {
        this.eventType = eventType
        this.fighters = Pair(fighter, fighter)
        this.deltas = Pair(delta, delta)
    }
    // 1 2 0
    constructor(eventType: EventType = EventType.NULL_EVENT,
                fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter())) {
        this.eventType = eventType
        this.fighters = Pair(fighters.first, fighters.second)
        this.deltas = Pair(0, 0)
    }
    // 1 2 2
    constructor(eventType: EventType = EventType.NULL_EVENT,
                fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter()),
                deltas: Pair<Int, Int> = Pair(0, 0)) {
        this.eventType = eventType
        this.fighters = Pair(fighters.first, fighters.second)
        this.deltas = Pair(deltas.first, deltas.second)
    }
}

class ViewerEvent(
    private val eventType: EventType,
    private val viewer: Viewer,
    private val message: String
) {

    fun get() = viewer
    fun getId() = viewer.getId()
    fun getName() = viewer.getName()
    fun getData() = viewer.getData()

    fun getType() = eventType
    fun getMessage() = message

}

enum class EventType { NULL_EVENT,

    // LOBBY EVENTS
    FIGHTER_JOINED,
    FIGHTER_MOVED,

    // MATCH EVENTS
    MATCH_LOADING,
    BURST_ENABLED,
    STRIKE_STUNNED,
    DAMAGE_DEALT,
    ROUND_STARTED,
    ROUND_RESOLVED,
    MATCH_RESOLVED,
    MATCH_CONCLUDED,

    // CLIENT EVENTS
    XRD_CONNECTED,
    XRD_DISCONNECT,

    // VIEWER EVENTS
    VIEWER_MESSAGE,
    VIEWER_JOINED,
    COMMAND_BET,
    COMMAND_HELP,
    COMMAND_WALLET

}