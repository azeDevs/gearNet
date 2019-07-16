package utils

var consoleLog = arrayListOf("[CLIE] GearNet // Bounty Bets 0.6.3")
var watchedLog: MutableMap<String, String> = mutableMapOf()

fun log(text: String) {
    if (consoleLog.size > 15) consoleLog.removeAt(0)
    consoleLog.add(text)
    println(text)
}

fun log(tag:String, text:String) {
    watchedLog.put(tag, text)
}