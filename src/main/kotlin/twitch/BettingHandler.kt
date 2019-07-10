package twitch

class BettingHandler {

    fun scanMessage(message:Message) {
        val id = message.twitchUserId
        val text = message.messageText
        if (!text.isEmpty() && text[0].toString().equals("!")) {
            println("COMMAND from ${id}: \"${text}\"")
        }
    }

}