package utils

import application.LogText
import application.LogText.Effect.LOW
import application.LogText.Effect.YLW
import application.log
import session.Session.Mode
import session.Session.Mode.*

typealias L = LogText

class SessionMode(private var mode: Mode = NULL) {

    fun get() = mode
    fun isMode(vararg mode:Mode) = mode.any { it == this.mode }

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
                log(L("Session changed to "), L(updatedMode.name,YLW), L(" (formerly ${mode.name})",LOW))
                mode = updatedMode
            }
        }
        return updated
    }

}