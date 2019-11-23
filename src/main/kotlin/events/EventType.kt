package events

import session.Fighter
import session.Match
import tornadofx.EventBus.RunOn.BackgroundThread
import tornadofx.FXEvent
import twitch.Viewer
import twitch.ViewerBet

class XrdConnectionEvent(val connected: Boolean) : FXEvent(BackgroundThread)
class FighterJoinedEvent(val fighter: Fighter) : FXEvent(BackgroundThread)
class FighterMovedEvent(val fighter: Fighter) : FXEvent(BackgroundThread)
//class FighterMessageEvent(val fighter:Fighter, val text:String) : FXEvent(BackgroundThread)

class MatchLoadingEvent(val match: Match) : FXEvent(BackgroundThread)
class RoundStartedEvent(val match: Match) : FXEvent(BackgroundThread)
class RoundResolvedEvent(val match: Match) : FXEvent(BackgroundThread)
class MatchResolvedEvent(val match: Match) : FXEvent(BackgroundThread)
class MatchConcludedEvent(val match: Match) : FXEvent(BackgroundThread)

class ViewerJoinedEvent(val viewer:Viewer) : FXEvent(BackgroundThread)
class ViewerMessageEvent(val viewer:Viewer, val text:String) : FXEvent(BackgroundThread)
class CommandBetEvent(val viewer:Viewer, val bet:ViewerBet) : FXEvent(BackgroundThread)
