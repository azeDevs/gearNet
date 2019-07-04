package session

import utils.Duo
import utils.keepInRange

class XrdLobby {
    private val roamingPlayers = emptyList<Player>()
    private val cabinets = listOf(Cabinet(),Cabinet(),Cabinet(),Cabinet())

    fun getXrdEvents(): List<XrdEvent> {
        val events:MutableList<XrdEvent> = emptyList<XrdEvent>().toMutableList()

        getPlayers().filter { it.hasWon() }.forEach {

            // NOTE: you were trying to figure out how to add polymorphic events...
            //events.add(XrdEvent(Event.PLAYER_WINS_MATCH, ))
        }

        return events
    }

    private fun getPlayers():List<Player> {
        val allPlayers = roamingPlayers.toMutableSet()
        for (i in 0..3) allPlayers.addAll(cabinets[i].getPlayers())
        return allPlayers.toList()
    }

    fun getCabinet(index:Int = 0) = cabinets[keepInRange(index, 0, 3)]

}


class XrdEvent(val event: Event = Event.NULL_EVENT,
               val deltas: Duo<Int> = Duo(0,0),
               val players: Duo<Player> = Duo(Player(),Player()))

enum class Event {
    NULL_EVENT,

    // CLIENT EVENT
    CLIENT_STARTED,
    CLIENT_STOPPED,
    CLIENT_DISPLAYED_LOBBY,
    CLIENT_DISPLAYED_MATCH,

    PLAYER_LOADING_P1,
    PLAYER_LOADING_P2,
    PLAYER_WINS_ROUND,
    PLAYER_LOST_ROUND,
    PLAYER_WINS_MATCH,
    PLAYER_LOST_MATCH,
    PLAYER_BURST_ENABLED,
    PLAYER_BURST_INVALID,
    PLAYER_ENTERS_HITSTUN,
    PLAYER_LEAVES_HITSTUN,
    PLAYER_HEALTH_CHANGED,
    PLAYER_LOCATE_CHANGED,

    // APP EVENTS
    INIT_RESULTS_INTRO,
    STOP_RESULTS_INTRO,
    INIT_RESULTS_OUTRO,
    STOP_RESULTS_OUTRO,

    INIT_SCOREBOARD_INTRO,
    STOP_SCOREBOARD_INTRO,
    INIT_SCOREBOARD_OUTRO,
    STOP_SCOREBOARD_OUTRO,

    INIT_BOUNTY_PAYOUT,
    STOP_BOUNTY_PAYOUT,

    INIT_CHAINS_PAYOUT,
    STOP_CHAINS_PAYOUT,

    INIT_CHAINS_CHANGE,
    STOP_CHAINS_CHANGE,

    INIT_PLAYER_RANK_UP,
    STOP_PLAYER_RANK_UP,

    INIT_PLAYER_RANK_DN,
    STOP_PLAYER_RANK_DN

}