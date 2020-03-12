package twitch

class Viewer(val data:ViewerData) {

    private var scoreTotal = 100L
    private var scoreDelta = 0L
    private var wager = -1

    fun getScore() = scoreTotal
    fun isValid() = data.isValid()

    /*

         p1 - 7 chain  [ VS ]  chain 1 - p2
                   bet 64% payout  |  p2chain*4 percent payout reduction
                                      p1chain*8 percent payout bonus
        p1bet = 100             p2bet = 1000
    */

}