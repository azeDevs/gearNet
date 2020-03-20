package memscan

import tornadofx.observableListOf

class GearNetUpdates {


    companion object {
        const val IC_COMPLETE = "✔️" // ✔️
        const val IC_MATCHUP = "⚔️" // ⚔️
        const val IC_GEAR = "⚙️" // ⚙️
        const val IC_CHAT = "\uD83D\uDCAC   " // 💬
        const val IC_SCAN = "\uD83D\uDCE1   " // 📡
        const val IC_DATA_PLAYER = "\uD83D\uDCBE   " // 💾
        const val IC_DATA_CHANGE = "     \uD83D\uDDAB   " // 🖫
    }


    private val observableUpdates = observableListOf<GNLog>()
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
    fun getGNLogs() = observableUpdates


    /**
     *
     */
    fun clearUpdatesToConsole() {
        if (gnUpdates.isNotEmpty()) gnUpdates.forEach {
            println("${it.tag}${it.text}")
        }
        observableUpdates.setAll(gnUpdates)
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