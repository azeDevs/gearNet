package events

import models.Fighter


class FighterEvent {
    private val eventType: EventType
    private val fighters: Pair<Fighter, Fighter>
    private val deltas: Pair<Int, Int>

    fun getType() = eventType
    fun getFighter(i:Int = 0) = if (i == 0) fighters.first else fighters.second
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
    // 1 2 1
    constructor(eventType: EventType = EventType.NULL_EVENT,
                fighters: Pair<Fighter, Fighter> = Pair(Fighter(), Fighter()),
                delta: Int = 0) {
        this.eventType = eventType
        this.fighters = Pair(fighters.first, fighters.second)
        this.deltas = Pair(delta, delta)
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
    ROUND_ENDED,
    MATCH_ENDED,

    // CLIENT EVENTS
    XRD_CONNECTED,
    XRD_DISCONNECT

}