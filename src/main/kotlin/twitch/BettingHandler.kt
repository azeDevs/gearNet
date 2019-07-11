package twitch

import session.Player

class BettingHandler {
    val gamblers: HashMap<Long, Player> = HashMap()

    fun scanMessage(message:Message) {
        if (!message.text.isEmpty() && message.text[0].toString().equals("!")) {

            determineCommand(message)
        }
    }

    fun determineCommand(message:Message) {
        println("COMMAND from ${message.name}: \"${message.id}\"")
        val cmdText = message.text.substring(1).split("\\s".toRegex()).toList()
        if (cmdText.size.equals(3)) {

        }
    }

    /*
        !bet !help !command !info
        !bet P1 1000
        !wallet
    */

}