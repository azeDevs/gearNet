package twitch

class Gambler(val name:String, val id:Long) {
    var ranked = -1
    var wallet = 100L
    var p1Bet = 0L
    var p2Bet = 0L

    /*

         p1 - 7 chain  [ VS ]  chain 1 - p2
                   bet 64% payout  |  p2chain*4 percent payout reduction
                                      p1chain*8 percent payout bonus
        p1bet = 100             p2bet = 1000
    */

}