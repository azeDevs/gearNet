package gearnet

import MyApp.Companion.VERSION
import gearnet.GearNetShifter.Shift
import gearnet.GearNetShifter.Shift.*

class GearNetUpdates {


    companion object {
        const val IC_COMPLETE = "✔  " // ✔️
        const val IC_MATCHUP = "⚔  " // ⚔️
        const val IC_GEAR = "⚙  " // ⚙️
        const val IC_CHAT = "\uD83D\uDCAC  " // 💬
        const val IC_SCAN = "\uD83D\uDCE1  " // 📡
        const val IC_DATA_PLAYER = "    \uD83D\uDCBE  " // 💾
        const val IC_DATA_CHANGE = "      \uD83D\uDDAB  " // 🖫
    }


    private val observableUpdates = mutableListOf<GNLog>()
    private val gnUpdates = mutableListOf<GNLog>()


    /**
     *
     */
    fun add(update: GNLog) { if (update.isValid()) gnUpdates.add(update) }
    fun add(text: String, level: Int = -1) { if (text.isNotBlank()) gnUpdates.add(GNLog(IC_DATA_CHANGE, text, level)) }
    fun add(tag: String, text: String, level: Int = -1) { if (text.isNotBlank()) gnUpdates.add(GNLog(tag, text, level)) }


    /**
     *
     */
    fun getUpdatesAsString(gearShift: Shift): String {
        val sb = StringBuilder("ＧｅａｒＮｅｔ   //   $VERSION   ")
        sb.append(when(gearShift) {
            GEAR_LOADING -> "$IC_GEAR LOADING"
            GEAR_LOBBY -> "$IC_GEAR LOBBY"
            GEAR_MATCH -> "$IC_GEAR MATCH"
            GEAR_SLASH -> "$IC_GEAR SLASH"
            GEAR_DRAWN -> "$IC_GEAR DRAWN"
            GEAR_VICTORY -> "$IC_GEAR VICTORY"
            GEAR_TRAINER -> "$IC_GEAR TRAINER"
            else -> "$IC_GEAR OFFLINE"
        })
        sb.append("\n")//⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯\n")
        observableUpdates.forEach {
            when(it.tag) {
                IC_GEAR -> sb.append("\n${it.tag} ${it.text}\n")
                IC_MATCHUP -> sb.append("\n${it.tag} ${it.text}\n")
                IC_COMPLETE ->  sb.append("\n${it.tag} ${it.text}\n")
                IC_DATA_PLAYER ->  sb.append("\n${it.tag} ${it.text}\n")
                else -> sb.append("${it.tag} ${it.text}\n")
            }
        }
        return sb.toString()
    }


    /**
     *
     */
    fun clearUpdatesToConsole() {
        if (gnUpdates.isNotEmpty()) {
            observableUpdates.clear()
            gnUpdates.forEach {
                println("${it.tag}${it.text}")
                observableUpdates.add(it)
            }
        }
        gnUpdates.clear()
    }


    /**
     *  [MatchupData] Class
     */
    data class GNLog(
        val tag: String = "",
        val text: String = "",
        val level: Int = -1
    ) { fun isValid() = text.isNotBlank() }


}