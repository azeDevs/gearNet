package twitch

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import utils.getTokenFromFile
import utils.log
import utils.stringToInt

class TwitchBot : BotApi {
    private val messageCache: MutableList<ViewerData> = mutableListOf()
    private val twitchClient: TwitchClient

    init {
        twitchClient = TwitchClientBuilder.builder()
            .withChatAccount(OAuth2Credential("twitch", getTokenFromFile("keys", "twitch_bot")))
//            .withClientId(getTokenFromFile("keys", "twitch_bot_client"))
//            .withClientSecret(getTokenFromFile("keys", "twitch_bot_secret"))
            .withEnableChat(true)
            .withEnableHelix(true)
            .withEnableKraken(true)
            .withEnableTMI(true)
            .build()

        twitchClient.chat.eventManager.onEvent(ChannelMessageEvent::class.java).subscribe {
            parseMessage(it)
        }

        twitchClient.getChat().joinChannel("azeDevs")
//        sendMessage("Hi WOrld!")
    }

    private fun parseMessage(it: ChannelMessageEvent) {
        val v = ViewerData(it.user.id, it.user.name, it.message)
        log("Viewer ${v.name} said \"${v.text}\"")
        if (!it.message.isEmpty() && it.message.substring(0,1).equals("!")) {
            val cmd = it.message.toUpperCase().substring(1).split("\\s".toRegex()).toList()

            // Check first word for valid command
            when (cmd[0]) {
                "BET" -> runBetCommand(cmd, v)
                "USERS" -> runUsersCommand(cmd, v)
            }

            messageCache.add(ViewerData(it.user.id, it.user.name, it.message))
        }
    }

    private fun runBetCommand(cmd: List<String>, v: ViewerData) {

        if (cmd.size.equals(3)) {
            log("Viewer ${v.name} initiated \"${cmd[0]}\"")
            // CONVERT STRING 1 INTO LONG FOR BET AMOUNT
            val amount = stringToInt(cmd[1])
            if (amount > 0) {
                if (cmd[2].equals("P1", true)) {
                    sendMessage("${v.name} bet ${amount} that Fighter 1 will win!")
                } else if (cmd[2].equals("P2", true)) {
                    sendMessage("${v.name} bet ${amount} that Fighter 2 will win!")
                } else log("Viewer ${v.name} failed to initiate \"${cmd[0]}\", invalid Fighter")
            } else log("Viewer ${v.name} failed to initiate \"${cmd[0]}\", invalid amount")

            // TODO: VERIFY WALLET AND BET AMOUNT
            // TODO: PLACE BET AND SEND CONFIRMATION TWITCH MESSAGE
        } else {
            log("Viewer ${v.name} failed to initiate command \"${cmd[0]}\", insufficient parameters")
        }
    }

    fun runUsersCommand(cmd: List<String>, v: ViewerData) {
        log("Viewer ${v.name} initiated \"${cmd[0]}\" ...")
        val chatters = twitchClient.messagingInterface.getChatters("azeDevs").execute()
        log("  VIPs: " + chatters.vips)
        log("  Mods: " + chatters.moderators)
        log("  Admins: " + chatters.admins)
        log("  Staff: " + chatters.staff)
        log("  Viewers: " + chatters.viewers)
        log("  All Viewers (sum of the above): " + chatters.allViewers)
    }

    override fun sendMessage(message: String) = twitchClient.chat.sendMessage("azeDevs", "🤖[$message]")

    override fun getViewerData(): List<ViewerData> = messageCache
    fun clearMessages() { messageCache.clear() }

    override fun isConnected(): Boolean = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution

}
