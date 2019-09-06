package application

import application.LogText.Effect.*
import javafx.scene.text.TextFlow
import tornadofx.c
import tornadofx.text
import utils.keepInRange
import utils.prnt
import utils.strToInt

/* LOG LINE UTILITY FUNCTION */

private fun generateLogLines(vararg logText:LogText): List<LogLine> {
    val truncatedLogTexts: MutableList<LogText> = arrayListOf()
    val assembledLines: MutableList<LogLine> = arrayListOf()
    val cutLogTexts: MutableList<LogText> = arrayListOf()
    var colCount = 0

    logText.forEach {
        colCount += it.getText().length
        val cutText = it.getText().substring(0, keepInRange(colCount-100, 0, it.getText().length))
        if (colCount > 100 && cutText.isNotEmpty()) {
            val newText = it.getText().substring(0, it.getText().length-(colCount-100))
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
fun log(text:String) = log(LogText(text))
fun log(tag:String, value:Int) = log(tag, value.toString())
fun log(tag:String, text:String) = log(LogText(tag, YLW), LogText(text))
fun log(vararg logTexts:LogText) {
    generateLogLines(*logTexts).forEach { prnt(it.getText())
        logs.add(it)
        if (logs.size > 32) logs.removeAt(0)
    }
}

class LogLine(val logTexts:List<LogText>) {
    fun getText():String { val sb = StringBuilder()
        logTexts.forEach { sb.append(it.getText()) }
        return sb.toString() }
}

class LogText(private val text:String = "", private val effect:Effect = NONE) {
    enum class Effect { NONE, LOW, YLW, RED, GRN, BLU, SCALE, BREAK }
    fun getText() = text
    fun getEffect() = effect
    fun appendTo(flow: TextFlow) {
        when (effect) {
            NONE -> { flow.apply { text(getText()) { AppStyle.fontFiraCodeRegular?.let { font = it; fill = c("#dcddde") } } } }
            LOW -> { flow.apply { text(getText()) { AppStyle.fontFiraCodeLight?.let { font = it; fill = c("#666666") } } } }
            YLW -> { flow.apply { text(getText()) { AppStyle.fontFiraCodeBold?.let { font = it; fill = c("#faa61a") } } } } //c("#33aaee") } } } }
            RED -> { flow.apply { text(getText()) { AppStyle.fontFiraCodeBold?.let { font = it; fill = c("#f04747") } } } } //c("#33aaee") } } } }
            GRN -> { flow.apply { text(getText()) { AppStyle.fontFiraCodeBold?.let { font = it; fill = c("#40b581") } } } } //c("#33aaee") } } } }
            BLU -> { flow.apply { text(getText()) { AppStyle.fontFiraCodeBold?.let { font = it; fill = c("#33aaee") } } } }
            SCALE -> { flow.apply {
                if (strToInt(text) == 0) text(getText()) { AppStyle.fontFiraCodeBold?.let { font = it; fill = c("#faa61a") } }
                if (strToInt(text) > 0) text(getText()) { AppStyle.fontFiraCodeBold?.let { font = it; fill = c("#40b581") } }
                if (strToInt(text) < 0) text(getText()) { AppStyle.fontFiraCodeBold?.let { font = it; fill = c("#f04747") } }
            } }
            BREAK -> { flow.apply { text("\n") { AppStyle.fontFiraCodeLight?.let { font = it; fill = c("#faa61a") } } } }
        }
    }
}

data class LogEntry (val tag: String = "", val logText: LogText = LogText()) { fun getText() = logText.getText() }