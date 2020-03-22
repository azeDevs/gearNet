package memscan

import memscan.GearNet.MatchupData
import memscan.GearNet.PlayerData
import memscan.GearNetUpdates.Companion.IC_COMPLETE
import memscan.GearNetUpdates.GNLog
import utils.plural
import utils.timeMillis

class GearNetFrameData {


    /**
     *  [FrameData] Class
     */
    private val frames: MutableList<FrameData> = mutableListOf()
    fun addFrame(updatedDataList: MutableList<PlayerData>, updatedMuList: List<MatchupData>) = frames.add(FrameData(updatedDataList, updatedMuList))
    fun lastFrame() = frames.lastOrNull() ?: FrameData()


    /**
     *  @return a [String] for use with gnUpdates
     */
    fun getFrameUpdateLog(startTime:Long, updates: List<GNLog>): GNLog {
        return if (updates.isNotEmpty()) {
            val updateText = "Frame generated, ${updates.size} ${plural("update", updates.size)} made"
            val matchupText = "with ${lastFrame().matchupData.size} ${plural("Matchup", lastFrame().matchupData.size)}"
            val delay = timeMillis() - startTime
            val fps = ((16.33/(delay+1))*60).toInt()
            val totalFrames = "${frames.size} total ${plural("frame", frames.size)}"
            GNLog(IC_COMPLETE, "$updateText $matchupText ($delay ms delay / $fps FPS / $totalFrames)")
        } else GNLog()
    }


    /**
     *  [FrameData] Class
     */
    data class FrameData(
        val playerData: List<PlayerData> = listOf(),
        val matchupData: List<MatchupData> = listOf(),
        val frameTime: Long = timeMillis()
    )

}