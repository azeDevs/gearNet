package session.modes

import application.LogText.Effect.*
import application.log
import session.L

class SessionMode(private var mode: Mode = ModeNull()) {

    // TODO: SET NULL MODE WHENEVER A MATCH IS INVALIDATED TO ALLOW FOR A CLEAN RESET
    // TODO: MAKE EACH Mode HAVE ITS OWN SERIES OF FUNCTIONS BEFORE MOVING TO A NEW Mode

    fun get() = mode
    fun isMode(vararg mode: Mode) = mode.any { it == this.mode }

    fun update(updatedMode: Mode): Boolean {
        var updated = false
        if (updatedMode != mode) {
            if (isMode(ModeNull())) updated = true
            when (updatedMode) {
                ModeLobby() -> if (isMode(ModeVictory(), ModeMatch())) updated = true
                ModeLoading() -> if (isMode(ModeLobby())) updated = true
                ModeMatch() -> if (isMode(ModeLoading(), ModeSlash())) updated = true
                ModeSlash() -> if (isMode(ModeMatch())) updated = true
                ModeVictory() -> if (isMode(ModeSlash(), ModeMatch())) updated = true
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