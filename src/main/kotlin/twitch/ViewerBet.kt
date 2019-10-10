package twitch

import kotlin.math.roundToInt

const val RED_CHIP = "\uD83D\uDD34"
const val BLU_CHIP = "\uD83D\uDD35"

class ViewerBet(private val viewer: Viewer = Viewer()) {

    private var redChips: Int = 0
    private var bluChips: Int = 0
    private var redWager: Int = 0
    private var bluWager: Int = 0

    init {
        val text = viewer.getData().text
        if (text.isNotEmpty()) {
            for (i in 1 until text.length) {
                if (text.substring(i-1,i+1) == RED_CHIP) redChips++
                if (text.substring(i-1,i+1) == BLU_CHIP) bluChips++
            }
            this.redWager = getBetAmount(redChips)
            this.bluWager = getBetAmount(bluChips)
        }
    }

    private fun getBetAmount(chips:Int): Int {
        return if (viewer.getScoreTotal() > 10) (viewer.getScoreTotal() * chips * 0.1).roundToInt()
        else 10
    }

    fun getViewer() = viewer
    fun isValid() = viewer.isValid() && hasChips() && hasWager()
    private fun hasChips() = redChips > 0 || bluChips > 0
    private fun hasWager() = redWager > 0 || bluWager > 0
    fun getChips(seatId:Int) = if (seatId == 0) redChips else if (seatId == 1) bluChips else 0
    fun getWager(seatId:Int) = if (seatId == 0) redWager else if (seatId == 1) bluWager else 0

}