package events

import twitch.TwitchBot
import twitch.ViewerData
import utils.log
import utils.stringToInt

class BotHandler {

    private val botApi = TwitchBot()

    fun parseViewerData() {
        botApi.getViewerData().forEach {
            determineCommand(it)
        }
        botApi.clearMessages()
    }

    fun determineCommand(v: ViewerData) {
        val cmd = v.text.substring(1).split("\\s".toRegex()).toList()
        log("T COMMAND: ${v.name}: \"${cmd[0]}\"")
        when (cmd[0].toUpperCase()) {
            "BET" -> {
                if (cmd.size.equals(3)) {
                    val amount = stringToInt(cmd[1])
                    if (amount > 0 && cmd[2].equals("P1", true) ) {
                        botApi.sendMessage("${v.name} bet ${amount} that Fighter 1 will win!")
                    } else if (amount > 0 && cmd[2].equals("P2", true) ) {
                        botApi.sendMessage("${v.name} bet ${amount} that Fighter 2 will win!")
                    }
                }
            }
            "USERS" -> {
                botApi.getViewers()
            }
            else -> {
                botApi.sendMessage("Uh, what?")
            }
        }
        // CONVERT STRING 0 AND CHECK FOR MAJOR COMMAND
        // CONVERT STRING 1 INTO LONG FOR BET AMOUNT

        // VERIFY WALLET AND BET AMOUNT
        // PLACE BET AND SEND CONFIRMATION TWITCH MESSAGE

    }

    fun initFightingPhase() {
        // CLOSE BETS & SEND MESSAGE SHOWING FIGHT/BET INFO
    }

    fun initBettingPhase() {
        // SHOW RESULTS & SEND MESSAGE ANNOUNCING NEXT FIGHT
    }


    /*

        VALID COMMANDS:
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

}