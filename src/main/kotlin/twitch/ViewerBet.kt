package twitch

import kotlin.math.roundToInt

const val RED_CHIP = "\uD83D\uDD34"
const val BLU_CHIP = "\uD83D\uDD35"

class ViewerBet {

    private val viewer: Viewer
    private var redChips: Int = 0
    private var bluChips: Int = 0
    private var redWager: Int = 0
    private var bluWager: Int = 0

    constructor(viewer: Viewer = Viewer()) {
        this.viewer = viewer
        val text = viewer.getData().text
        if (text.isNotEmpty()) {
            for (i in 0..text.length-1) {
                if (text[i].equals(RED_CHIP)) redChips++
                if (text[i].equals(BLU_CHIP)) bluChips++
            }
            this.redWager = getBetAmount(redChips)
            this.bluWager = getBetAmount(bluChips)
        }
    }

    private fun getBetAmount(chips:Int): Int {
        if (viewer.getScoreTotal() > 10) {
            return (viewer.getScoreTotal() * chips * 0.1).roundToInt()
        } else return 10
    }

    fun isValid() = viewer.isValid() && (redWager > 0 || bluWager > 0)
    fun getChips() = Pair(redChips, bluChips)
    fun getWager() = Pair(redWager, bluWager)
    fun getChips(seatId:Int) = if (seatId == 0) redChips else bluChips
    fun getWager(seatId:Int) = if (seatId == 0) redWager else bluWager

}