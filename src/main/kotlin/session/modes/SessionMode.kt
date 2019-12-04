package session.modes

import application.LogText.Effect.*
import application.log
import session.L
import session.Session

class SessionMode(val s: Session, private var mode: Mode = ModeNull(s)) {

    // TODO: SET NULL MODE WHENEVER A MATCH IS INVALIDATED TO ALLOW FOR A CLEAN RESET
    // TODO: MAKE EACH Mode HAVE ITS OWN SERIES OF FUNCTIONS BEFORE MOVING TO A NEW Mode

    fun get() = mode
    fun isMode(vararg mode: Mode) = mode.any { it == this.mode }

    fun update(updatedMode: Mode): Boolean {
        var updated = false
        if (updatedMode != mode) {
            if (isMode(ModeNull(s))) updated = true
            when (updatedMode) {
                ModeLobby(s) -> if (isMode(ModeVictory(s), ModeMatch(s))) updated = true
                ModeLoading(s) -> if (isMode(ModeLobby(s))) updated = true
                ModeMatch(s) -> if (isMode(ModeLoading(s), ModeSlash(s))) updated = true
                ModeSlash(s) -> if (isMode(ModeMatch(s))) updated = true
                ModeVictory(s) -> if (isMode(ModeSlash(s), ModeMatch(s))) updated = true
                else -> updated = false
            }
            if (updated) {
                log(
                    L("Session Mode changed to "),
                    L(updatedMode.toString(), ORN_MODE),
                    L(" (formerly ", LOW),
                    L(mode.toString(), MED),
                    L(")", LOW)
                )
                mode = updatedMode
            }
        }
        return updated
    }


    override fun toString(): String = mode.toString()


}