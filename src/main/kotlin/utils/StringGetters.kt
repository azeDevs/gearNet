package utils

import application.LogText
import application.LogText.Effect.*
import session.L


fun getIdStr(id: Long): String {
    return if (id < 0) "INVALID"
    else if (id.toString().length > 8) "ID${id.toString().substring(id.toString().length-8, id.toString().length)}"
    else "ID${id}"
}

fun getSeatStr(sideId:Int = -1): String {
    return when(sideId) {
        0 -> "Red seat"
        1 -> "Blue seat"
        2 -> "2nd seat"
        3 -> "3rd seat"
        4 -> "4th seat"
        5 -> "5th seat"
        6 -> "6th seat"
        7 -> "Spectator seat"
        else -> "null"
    }
}

fun getSeatLog(sideId:Int = -1): LogText {
    return when(sideId) {
        0 -> L("Red seat", RED)
        1 -> L("Blue seat", BLU)
        2 -> L("2nd seat", YLW)
        3 -> L("3rd seat")
        4 -> L("4th seat")
        5 -> L("5th seat")
        6 -> L("6th seat")
        7 -> L("Spectator seat", CYA)
        else -> L("null", CYA)
    }
}

fun getCabinetStr(cabId:Int = -1): String {
    return when(cabId) {
        0 -> "Cabinet A"
        1 -> "Cabinet B"
        2 -> "Cabinet C"
        3 -> "Cabinet D"
        else -> "null"
    }
}