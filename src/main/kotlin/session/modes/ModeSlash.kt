package session.modes

import events.*

class ModeSlash : Mode() {

    override fun toString(): String = "${super.toString()}SLASH"

    override fun runMatchConcluded(event: MatchConcludedEvent) {
        TODO("not implemented")
    }

    override fun runMatchResolved(event: MatchResolvedEvent) {
        TODO("not implemented")
    }

    override fun runRoundDraw(event: RoundDrawEvent) {
        TODO("not implemented")
    }

    override fun runRoundResolved(event: RoundResolvedEvent) {
        TODO("not implemented")
    }

    override fun runRoundStarted(event: RoundStartedEvent) {
        TODO("not implemented")
    }

    override fun runMatchLoading(event: MatchLoadingEvent) {
        TODO("not implemented")
    }

    override fun runFighterMoved(event: FighterMovedEvent) {
        TODO("not implemented")
    }

    override fun runFighterJoined(event: FighterJoinedEvent) {
        TODO("not implemented")
    }

    override fun runCommandBet(event: ViewerBetEvent) {
        TODO("not implemented")
    }

    override fun runViewerJoined(event: ViewerJoinedEvent) {
        TODO("not implemented")
    }

    override fun runViewerMessage(event: ViewerMessageEvent) {
        TODO("not implemented")
    }

    override fun runXrdConnection(event: XrdConnectionEvent) {
        TODO("not implemented")
    }
}