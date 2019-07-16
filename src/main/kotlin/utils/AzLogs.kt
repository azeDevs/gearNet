package utils

var consoleLog = arrayListOf("${Log.APP} GearNet // Bounty Bets 0.6.3")
var watchedLog: MutableMap<String, String> = mutableMapOf()

fun log(text: String) {
    if (consoleLog.size >= 20) consoleLog.removeAt(0)
    consoleLog.add(text)
    println(text)
}

fun log(tag:String, text:String) {
    watchedLog.put(tag, text)
}

object Log {
    const val APP = "[APP]"
    const val MEM = "[MEM]"
    const val EVE = "[EVE]"
}