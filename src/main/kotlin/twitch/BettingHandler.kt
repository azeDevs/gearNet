package twitch

import session.log
import utils.stringToInt

class BettingHandler {

    val bot = TwitchBot()
    val gamblers: HashMap<Long, Gambler> = HashMap()

    fun refreshGamblers() {
        bot.getMessages().forEach {
            log("CHAT ${it.name}: ${it.text}")
            if (!it.text.isEmpty() && it.text[0].toString().equals("!")) {
                if (!gamblers.containsKey(it.id)) gamblers.put(it.id, Gambler(it.name, it.id))
                determineCommand(gamblers[it.id]!!, it)
            }
        }
        bot.clearMessages()
    }

    /*
        !bet !help !command !info
        !bet P1 1000
        !wallet

        GAME PHASES:
        - Lobby     ( BETTING ALLOWED )
        - Loading   ( BETTING ALLOWED )
        - Match     > BETTING BLOCKED <
        - Slash     > BETTING BLOCKED <
        - Victory   ( PAYOUT AND BETTING ALLOWED )

        USER SENDS BET COMMAND:
        - If it's an Info request ... display Info (Wallet/Help)
        - If it's a bet, and bets aren't being accepted ... display "error" and Info (Wallet/Help)
        - If it's a bet, and User lacks enough W$ ... display "error" and Info (Wallet/Help)
        - If it's a bet, and User has enough W$ ... display "success" and add Bet to User's placed bets


    */

    fun determineCommand(g:Gambler, m: Message) {
        log("COMMAND from ${m.name}: \"${m.id}\"")
        val cmd = m.text.substring(1).split("\\s".toRegex()).toList()
        if (cmd.size.equals(3)) {
            when (cmd[0].toUpperCase()) {
                "BET" -> {
                    val amount = stringToInt(cmd[1])
                    if (amount > 0 && cmd[2].equals("P1", true) ) {
                        g.p1Bet += amount
                    } else if (amount > 0 && cmd[2].equals("P2", true) ) {
                        g.p2Bet += amount
                    }
                }
                else -> {
//                    bot.sendMessage("")
                }
            }
            // CONVERT STRING 0 AND CHECK FOR MAJOR COMMAND
            // CONVERT STRING 1 INTO LONG FOR BET AMOUNT

            // VERIFY WALLET AND BET AMOUNT
            // PLACE BET AND SEND CONFIRMATION TWITCH MESSAGE
        }
    }

    fun initFightingPhase() {
        // CLOSE BETS & SEND MESSAGE SHOWING FIGHT/BET INFO
    }

    fun initBettingPhase() {
        // SHOW RESULTS & SEND MESSAGE ANNOUNCING NEXT FIGHT
    }

}