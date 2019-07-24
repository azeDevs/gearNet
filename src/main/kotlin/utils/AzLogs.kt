package utils

import ARTIFACT_NAME
import BUILD_VERSION

var consoleLog = arrayListOf("Starting $ARTIFACT_NAME $BUILD_VERSION")
var watchedLog: MutableMap<String, String> = mutableMapOf()

fun log(text: String) {
    if (consoleLog.size >= 20) consoleLog.removeAt(0)
    consoleLog.add(text)
    println(text)
}

fun log(tag:String, text:String) {
    watchedLog.put(tag, text)
}