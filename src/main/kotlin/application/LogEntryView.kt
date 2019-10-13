package application

import application.LogText.Effect.*
import javafx.scene.text.TextFlow
import tornadofx.c
import tornadofx.text
import utils.keepInRange
import utils.prnt
import utils.strToInt

/* LOG LINE UTILITY FUNCTION */

private fun generateLogLines(vararg logText: LogText): List<LogLine> {
    val truncatedLogTexts: MutableList<LogText> = arrayListOf()
    val assembledLines: MutableList<LogLine> = arrayListOf()
    val cutLogTexts: MutableList<LogText> = arrayListOf()
    var colCount = 0

    logText.forEach {
        colCount += it.get().length
        val cutText = it.get().substring(0, keepInRange(colCount - 100, 0, it.get().length))
        if (colCount > 100 && cutText.isNotEmpty()) {
            val newText = it.get().substring(0, it.get().length - (colCount - 100))
            truncatedLogTexts.add(LogText(newText, it.getEffect()))
            cutLogTexts.add(LogText(cutText, it.getEffect()))
        } else truncatedLogTexts.add(it)
    }
    truncatedLogTexts.add(LogText("\n"))
    assembledLines.add(LogLine(truncatedLogTexts))
    if (cutLogTexts.isNotEmpty()) assembledLines.addAll(generateLogLines(*cutLogTexts.toTypedArray()))
    return assembledLines
}


/**
 *log
 *
 * @return ???
 */
val logs: MutableList<LogLine> = arrayListOf()

fun log(text: String) = log(LogText(text))
fun log(tag: String, value: Int) = log(tag, value.toString())
fun log(tag: String, text: String) = log(LogText(tag, YLW), LogText(text))
fun log(vararg logTexts: LogText) {
    generateLogLines(*logTexts).forEach {
        prnt(it.getText())
        logs.add(it)
        if (logs.size > 32) logs.removeAt(0)
    }
}

fun updateLogs(console: TextFlow) {
    logs.forEach { log -> log.logTexts.forEach { it.appendTo(console) } }
    logs.clear()
}

class LogLine(val logTexts: List<LogText>) {
    fun getText(): String {
        val sb = StringBuilder()
        logTexts.forEach { sb.append(it.get()) }
        return sb.toString()
    }
}

class LogText(private val text: String = "", private val effect: Effect = NONE) {
    enum class Effect { NONE, LOW, MED, PUR, RED, ORN, YLW, GRN, BLU, SCALE, BREAK }

    fun get() = text
    fun getEffect() = effect
    fun appendTo(flow: TextFlow) {
        when (effect) {
            NONE -> { flow.apply { text(get()) { AppStyle.fontFiraRegular?.let { font = it; fill = c("#dcddde") } } } }
            LOW -> { flow.apply { text(get()) { AppStyle.fontFiraLight?.let { font = it; fill = c("#666666") } } } }
            MED -> { flow.apply { text(get()) { AppStyle.fontFiraRegular?.let { font = it; fill = c("#888888") } } } }
            PUR -> { flow.apply { text(get()) { AppStyle.fontFiraRegular?.let { font = it; fill = c("#09F9F9") } } } }
            RED -> { flow.apply { text(get()) { AppStyle.fontFiraBold?.let { font = it; fill = c("#f04747") } } } }
            ORN -> { flow.apply { text(get()) { AppStyle.fontFiraBold?.let { font = it; fill = c("#ff5500") } } } }
            YLW -> { flow.apply { text(get()) { AppStyle.fontFiraBold?.let { font = it; fill = c("#faa61a") } } } }
            GRN -> { flow.apply { text(get()) { AppStyle.fontFiraBold?.let { font = it; fill = c("#40b581") } } } }
            BLU -> { flow.apply { text(get()) { AppStyle.fontFiraBold?.let { font = it; fill = c("#33aaee") } } } }
            SCALE -> {
                flow.apply {
                    if (strToInt(text) == 0) text(get()) {
                        AppStyle.fontFiraBold?.let {
                            font = it; fill = c("#faa61a")
                        }
                    }
                    if (strToInt(text) > 0) text(get()) {
                        AppStyle.fontFiraBold?.let {
                            font = it; fill = c("#40b581")
                        }
                    }
                    if (strToInt(text) < 0) text(get()) {
                        AppStyle.fontFiraBold?.let {
                            font = it; fill = c("#f04747")
                        }
                    }
                }
            }
            BREAK -> { flow.apply { text("\n") { AppStyle.fontFiraLight?.let { font = it; fill = c("#faa61a") } } } }
        }
    }
}

data class LogEntry(val tag: String = "", val logText: LogText = LogText()) {
    fun getText() = logText.get()
}