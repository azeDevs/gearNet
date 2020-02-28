package session.modes

import views.logging.LogText.Effect.CYA
import views.logging.LogText.Effect.LOW
import views.logging.log
import session.L
import session.Session

class SessionMode(val s: Session, private var mode: Mode = ModeNull(s)) {

    // TODO: SET NULL MODE WHENEVER A MATCH IS INVALIDATED TO ALLOW FOR A CLEAN RESET

    fun get() = mode
    fun isMode(vararg mode: Mode) = mode.any { it.toString().equals(this.mode.toString(), true) }

    fun update(updatedMode: Mode): Boolean {
        var updated = false
        if (!isMode(updatedMode)) {
            if (isMode(ModeNull(s))) updated = true
            when (updatedMode.toString()) {
                ModeLobby(s).toString() -> if (isMode(ModeVictory(s), ModeMatch(s))) updated = true
                ModeLoading(s).toString() -> if (isMode(ModeLobby(s))) updated = true
                ModeMatch(s).toString() -> if (isMode(ModeLoading(s), ModeSlash(s))) updated = true
                ModeSlash(s).toString() -> if (isMode(ModeMatch(s))) updated = true
                ModeVictory(s).toString() -> if (isMode(ModeSlash(s), ModeMatch(s))) updated = true
                else -> updated = false
            }
            if (updated) {
                log(
                    L(mode.toString(), CYA),
                    L(" -> ", LOW),
                    L("$updatedMode", CYA)
                )
                mode = updatedMode }
        }
        return updated
    }


    override fun toString(): String = mode.toString()


}