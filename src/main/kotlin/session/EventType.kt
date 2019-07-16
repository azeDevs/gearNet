package session

import utils.Duo

class Event(
    val type: EventType = EventType.NULL_EVENT,
    val deltas: Duo<Int> = Duo(0, 0),
    val players: Duo<Player> = Duo(Player(), Player())
)

enum class EventType { NULL_EVENT,

    // LOBBY EVENTS
    LOBBY_PLAYER_JOINED,
    LOBBY_PLAYER_MOVED,
    LOBBY_MATCH_ENDED,

    // MATCH EVENTS
    MATCH_LOADING,
    MATCH_BURST_ENABLED,
    MATCH_STRIKE_STUNNED,
    MATCH_DAMAGE_DEALT,
    MATCH_ROUND_ENDED,

    // CLIENT EVENTS
    CLIENT_DISPLAYED_LOBBY,
    CLIENT_DISPLAYED_MATCH

}