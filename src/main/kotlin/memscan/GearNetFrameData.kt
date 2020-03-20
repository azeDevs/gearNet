package memscan

import javafx.collections.ObservableList
import memscan.GearNet.MatchupData
import memscan.GearNet.PlayerData
import memscan.GearNetUpdates.Companion.IC_COMPLETE
import memscan.GearNetUpdates.GNLog
import tornadofx.asObservable
import tornadofx.observableListOf
import utils.plural
import utils.timeMillis

class GearNetFrameData {


    private val frames: MutableList<FrameData> = mutableListOf()
    fun addFrame(updatedDataList: MutableList<PlayerData>, updatedMuList: List<MatchupData>) = frames.add(FrameData(updatedDataList.asObservable(), updatedMuList.asObservable()))
    fun lastFrame() = frames.lastOrNull() ?: FrameData()


    /**
     *  @return a [String] for use with gnUpdates
     */
    fun getFrameUpdateLog(startTime:Long, updates: List<GNLog>): GNLog {
        return if (updates.isNotEmpty()) {
            val updateText = "Frame generated, ${updates.size} ${plural("update", updates.size)} made"
            val matchupText = "with ${lastFrame().muDataList.size} ${plural("Matchup", lastFrame().muDataList.size)}"
            val delay = timeMillis() - startTime
            val fps = ((16.33/(delay+1))*60).toInt()
            val totalFrames = "${frames.size} total ${plural("frame", frames.size)}"
            GNLog(IC_COMPLETE, "$updateText $matchupText ($delay ms delay / $fps FPS / $totalFrames / ${lastFrame().frameTime})")
        } else GNLog()
    }


    /**
     *  [FrameData] Class
     */
    data class FrameData(
        val playerDataList: ObservableList<PlayerData> = observableListOf(),
        val muDataList: ObservableList<MatchupData> = observableListOf(),
        val frameTime: Long = timeMillis()
    )


}