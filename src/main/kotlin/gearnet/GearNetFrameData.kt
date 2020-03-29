package gearnet

import gearnet.GearNet.MatchupData
import gearnet.GearNet.PlayerData
import gearnet.GearNetShifter.Shift
import gearnet.GearNetUpdates.Companion.IC_COMPLETE
import gearnet.GearNetUpdates.GNLog
import utils.plural
import utils.timeMillis

class GearNetFrameData {


    /**
     *  [FrameData] Class
     */
    private val frames: MutableList<FrameData> = mutableListOf()
    private val matchArchive: MutableList<MatchupData> = mutableListOf()
    fun addFrame(updatedDataList: MutableList<PlayerData>, updatedMuList: List<MatchupData>, gearShift: GearNetShifter.Shift) = frames.add(FrameData(updatedDataList, updatedMuList, gearShift))
    fun lastFrame() = frames.lastOrNull() ?: FrameData()
    fun oldFrame() = if(frames.size>1) frames[frames.size-2] else lastFrame()

    fun getMatchArchive() = matchArchive

    /**
     *  @return a [String] for use with gnUpdates
     */
    fun archiveMatchups() {
        lastFrame().matchupData.forEach {
            if (it.shift == Shift.GEAR_VICTORY) {
                if (matchArchive.none { mu -> mu.equals(it) }) matchArchive.add(it)
            }
        }
    }


    /**
     *  @return a [String] for use with gnUpdates
     */
    fun getFrameUpdateLog(startTime:Long, updates: List<GNLog>): GNLog {
        return if (updates.isNotEmpty()) {
            val updateText = "Frame generated, ${updates.size} ${plural("update", updates.size)}"
            val matchupText = "with ${lastFrame().matchupData.size} ${plural("matchup", lastFrame().matchupData.size)}[${getMatchArchive().size}]"
            val delay = timeMillis() - startTime
            val fps = ((16.33/(delay+1))*60).toInt()
            val totalFrames = "${frames.size} total ${plural("frame", frames.size)}"
            GNLog(IC_COMPLETE, "$updateText $matchupText ($fps FPS @ $delay ms / $totalFrames)")
        } else GNLog()
    }


    /**
     *  [FrameData] Class
     */
    data class FrameData( // TODO: ADD MATCH ARCHIVE TO FrameData OBJECT
        val playerData: List<PlayerData> = listOf(),
        val matchupData: List<MatchupData> = listOf(),
        val gearShift: GearNetShifter.Shift = GearNetShifter.Shift.GEAR_OFFLINE,
        val frameTime: Long = timeMillis()
    )

}