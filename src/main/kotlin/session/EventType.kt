package session

import utils.Duo

class Event {
    private var eventType: EventType = EventType.NULL_EVENT
    private var players: Duo<Player> = Duo(Player(), Player())
    private var deltas: Duo<Int> = Duo(0, 0)

    fun getType() = eventType
    fun getPlayers() = players
    fun getDeltas() = deltas
    fun getPlayer() = players.p1
    fun getDelta() = deltas.p1

    constructor(eventType: EventType = EventType.NULL_EVENT) {
        this.eventType = eventType
    }
    constructor(eventType: EventType = EventType.NULL_EVENT,
                player: Player = Player(),
                delta: Int = 0) {
        this.eventType = eventType
        this.players = Duo(player, player)
        this.deltas = Duo(delta, delta)
    }
    constructor(eventType: EventType = EventType.NULL_EVENT,
                players: Duo<Player> = Duo(Player(), Player()),
                deltas: Duo<Int> = Duo(0, 0)) {
        this.eventType = eventType
        this.players = Duo(players.p1, players.p2)
        this.deltas = Duo(deltas.p1, deltas.p2)
    }
}

enum class EventType { NULL_EVENT,

    // LOBBY EVENTS
    PLAYER_JOINED,
    PLAYER_MOVED,
    MATCH_ENDED,

    // MATCH EVENTS
    MATCH_LOADING,
    BURST_ENABLED,
    STRIKE_STUNNED,
    DAMAGE_DEALT,
    ROUND_ENDED,

    // CLIENT EVENTS
    LOBBY_DISPLAYED,
    MATCH_DISPLAYED

}