package session

import utils.Duo

class Event {
    private var eventType: EventType = EventType.NULL_EVENT
    private var players: Duo<Player> = Duo(Player(), Player())
    private var deltas: Duo<Int> = Duo(0, 0)

    fun getType() = eventType
    fun getPlayers() = players
    fun getDeltas() = deltas
    fun getPlayer(i:Int = 0) = if (i == 0) players.p1 else players.p2
    fun getDelta(i:Int = 0) = if (i == 0) deltas.p1 else deltas.p2

    constructor(eventType: EventType = EventType.NULL_EVENT) {
        this.eventType = eventType
        this.players = Duo(Player(), Player())
        this.deltas = Duo(0, 0)
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

    // MATCH EVENTS
    MATCH_LOADING,
    BURST_ENABLED,
    STRIKE_STUNNED,
    DAMAGE_DEALT,
    ROUND_ENDED,
    MATCH_ENDED,

    // CLIENT EVENTS
    XRD_CONNECTED,
    XRD_DISCONNECT,
    LOBBY_DISPLAYED,
    MATCH_DISPLAYED

}