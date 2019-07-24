package utils

var consoleLog: MutableList<String> = arrayListOf()
var watchedLog: MutableMap<String, String> = mutableMapOf()

fun log(text: String) {
    if (consoleLog.size >= 20) consoleLog.removeAt(0)
    consoleLog.add(text)
    println(text)
}

fun log(tag:String, text:String) {
    watchedLog.put(tag, text)
}