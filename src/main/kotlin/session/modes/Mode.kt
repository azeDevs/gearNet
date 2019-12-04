package session.modes

import events.*
import tornadofx.Controller

abstract class Mode : Controller() {

    override fun toString(): String = "MODE_"

    // TODO: HAVE THESE INIT WITHIN Session, and then distributed into SessionMode to be passed into the relevant Mode
    init {
        subscribe<XrdConnectionEvent> { runXrdConnection(it) }
        subscribe<ViewerMessageEvent> { runViewerMessage(it) }
        subscribe<ViewerJoinedEvent> { runViewerJoined(it) }
        subscribe<ViewerBetEvent> { runCommandBet(it) }
        subscribe<FighterJoinedEvent> { runFighterJoined(it) }
        subscribe<FighterMovedEvent> { runFighterMoved(it) }
        subscribe<MatchLoadingEvent> { runMatchLoading(it) }
        subscribe<RoundStartedEvent> { runRoundStarted(it) }
        subscribe<RoundResolvedEvent> { runRoundResolved(it) }
        subscribe<RoundDrawEvent> { runRoundDraw(it) }
        subscribe<MatchResolvedEvent> { runMatchResolved(it) }
        subscribe<MatchConcludedEvent> { runMatchConcluded(it) }
    }

    abstract fun runMatchConcluded(event: MatchConcludedEvent)
    abstract fun runMatchResolved(event: MatchResolvedEvent)
    abstract fun runRoundDraw(event: RoundDrawEvent)
    abstract fun runRoundResolved(event: RoundResolvedEvent)
    abstract fun runRoundStarted(event: RoundStartedEvent)
    abstract fun runMatchLoading(event: MatchLoadingEvent)
    abstract fun runFighterMoved(event: FighterMovedEvent)
    abstract fun runFighterJoined(event: FighterJoinedEvent)
    abstract fun runCommandBet(event: ViewerBetEvent)
    abstract fun runViewerJoined(event: ViewerJoinedEvent)
    abstract fun runViewerMessage(event: ViewerMessageEvent)
    abstract fun runXrdConnection(event: XrdConnectionEvent)

}