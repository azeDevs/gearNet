package memscan

import javafx.collections.ObservableList
import memscan.GearNet.PlayerData
import memscan.IconLegend.IC_DATA
import tornadofx.asObservable
import tornadofx.observableListOf
import utils.plural
import utils.timeMillis

class GearNetFrameData {

    private val frames: MutableList<FrameData> = mutableListOf()
    fun addFrame(updatedDataList: MutableList<PlayerData>) = frames.add(FrameData(updatedDataList.asObservable()))
    fun lastFrame() = frames.lastOrNull() ?: FrameData()
    fun frameCount() = frames.size

    fun logFrame(startTime:Long, updates:Int) {
        val delay = timeMillis() - startTime
        val fps = ((16.33/(delay+1))*60).toInt()
        val updateText = if (updates>0) "$updates ${plural("update", updates)} made" else ""
        val totalFrames = "${frameCount()} total ${plural("frame", frameCount())}"
        if (updates > 0) println("$IC_DATA   $updateText ($delay ms delay / $fps FPS) $totalFrames")
    }

    /**
     *  [FrameData] Class
     */
    data class FrameData(
        val playerDataList: ObservableList<PlayerData> = observableListOf(),
        val frameTime: Long = timeMillis()
    )

}