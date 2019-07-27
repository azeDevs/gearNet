package utils

import session.Session.Mode
import session.Session.Mode.*

class SessionMode(private var mode: Mode = NULL) {

    fun get() = mode
    fun isMode(vararg mode:Mode) = mode.filter { it == this.mode }.isNotEmpty()

    fun update(updatedMode:Mode): Boolean {
        var updated = false
        if (updatedMode != mode) {
            if (isMode(NULL)) updated = true
            when (updatedMode) {
                LOBBY -> if (isMode(VICTORY, MATCH)) updated = true
                LOADING -> if (isMode(LOBBY)) updated = true
                MATCH -> if (isMode(LOADING, SLASH)) updated = true
                SLASH -> if (isMode(MATCH)) updated = true
                VICTORY -> if (isMode(SLASH, MATCH)) updated = true
                else -> updated = false
            }
            if (updated) {
                log("Session changed to [${updatedMode.name}] (formerly ${mode.name.toLowerCase()})")
                mode = updatedMode
            }
        }
        return updated
    }

}