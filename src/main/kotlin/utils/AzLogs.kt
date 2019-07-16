package utils

var consoleLog = arrayListOf("Starting GearNet // Bounty Bets 0.6.3")
var watchedLog: MutableMap<String, String> = mutableMapOf()

fun log(text: String) {
    if (consoleLog.size >= 20) consoleLog.removeAt(0)
    consoleLog.add(text)
    println(text)
}

fun log(tag:String, text:String) {
    watchedLog.put(tag, text)
}