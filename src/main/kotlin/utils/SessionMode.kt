package utils

import application.LogText
import application.LogText.Effect.*
import application.log

typealias L = LogText

class SessionMode(private var mode: Mode = Mode.NULL) {

    fun get() = mode
    fun isMode(vararg mode:Mode) = mode.any { it == this.mode }

    fun update(updatedMode:Mode): Boolean {
        var updated = false
        if (updatedMode != mode) {
            if (isMode(Mode.NULL)) updated = true
            when (updatedMode) {
                Mode.LOBBY -> if (isMode(Mode.VICTORY, Mode.MATCH)) updated = true
                Mode.LOADING -> if (isMode(Mode.LOBBY)) updated = true
                Mode.MATCH -> if (isMode(Mode.LOADING, Mode.SLASH)) updated = true
                Mode.SLASH -> if (isMode(Mode.MATCH)) updated = true
                Mode.VICTORY -> if (isMode(Mode.SLASH, Mode.MATCH)) updated = true
                else -> updated = false
            }
            if (updated) {
                log(L("Session Mode changed to "), L(updatedMode.name, ORN_MODE),
                    L(" (formerly ", LOW), L(mode.name, MED), L(")", LOW))
                mode = updatedMode
            }
        }
        return updated
    }


    override fun toString(): String = mode.name

    enum class Mode {
        NULL,
        LOBBY,
        LOADING,
        MATCH,
        SLASH,
        VICTORY
    }

}