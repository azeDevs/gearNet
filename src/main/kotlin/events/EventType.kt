package events

import memscan.MatchSnap
import session.Fighter
import session.Match
import tornadofx.EventBus.RunOn.BackgroundThread
import tornadofx.FXEvent
import twitch.Viewer
import utils.The

interface GearNetEvent {
    fun getEventAsString() : String
}

class XrdConnectionEvent(val connected: Boolean) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString() : String = "!XC${The(connected).toInt()}" }
class XrdMatchUpdateEvent(val matchSnap: MatchSnap) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString() : String = "!MU" }

class FighterJoinedEvent(val fighter: Fighter) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!FJ" }
class FighterMovedEvent(val fighter: Fighter) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!FM" }

class MatchLoadingEvent(val match: Match) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!ML" }
class MatchResolvedEvent(val match: Match) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!MR" }
class MatchConcludedEvent(val match: Match) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!MC" }

class RoundStartedEvent(val match: Match) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!RS" }
class RoundResolvedEvent(val match: Match) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!RR" }
class RoundDrawEvent(val match: Match) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!RD" }

class ViewerJoinedEvent(val viewer:Viewer) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!VJ" }
class ViewerMessageEvent(val viewer:Viewer, val text:String) : FXEvent(BackgroundThread),
    GearNetEvent { override fun getEventAsString(): String = "!VM" }
