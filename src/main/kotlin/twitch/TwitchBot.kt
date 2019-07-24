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

        twitchClient.chat.eventManager.onEvent(ChannelMessageEvent::class.java).subscribe { parseMessage(it) }
        twitchClient.getChat().joinChannel("azeDevs")
    }

    private fun parseMessage(it: ChannelMessageEvent) {
        val v = ViewerData(it.user.id, it.user.name, it.message)
        log("Viewer ${v.name} said \"${v.text}\"")
        if (!it.message.isEmpty() && it.message.substring(0,1).equals("!")) {
            val cmd = it.message.toUpperCase().substring(1).split("\\s".toRegex()).toList()
            when (cmd[0]) {
                "R" -> messageCache.add(runBetCommand(cmd, v))
                "B" -> messageCache.add(runBetCommand(cmd, v))
                "RED" -> messageCache.add(runBetCommand(cmd, v))
                "BLU" -> messageCache.add(runBetCommand(cmd, v))
                "BLUE" -> messageCache.add(runBetCommand(cmd, v))
                "USERS" -> runUsersCommand(cmd, v)
                else -> messageCache.add(ViewerData(it.user.id, it.user.name, it.message))
            }

        }
    }

    private fun runBetCommand(cmd: List<String>, v: ViewerData): ViewerData {
        var fighterId = -1L
        var betAmount = 5
        var betBanner = Pair("null","❌")
        if(cmd[0].equals("R", true)) betBanner = Pair("\uD83D\uDD34","Red")
        if(cmd[0].equals("B", true)) betBanner = Pair("\uD83D\uDD35","Blue")
        if(cmd.size.equals(2)) betAmount = stringToInt(cmd[1])
        return ViewerData(v.id, v.name, v.text, fighterId, betAmount, betBanner)
    }

    fun runUsersCommand(cmd: List<String>, v: ViewerData) {
        // FIXME: THIS DOESN'T GET USERS DUE TO THE "BOT" BEING A USER AND NOT AN APP
        log("Viewer ${v.name} initiated \"${cmd[0]}\" ...")
        val chatters = twitchClient.messagingInterface.getChatters("azeDevs").execute()
        log("  VIPs: " + chatters.vips)
        log("  Mods: " + chatters.moderators)
        log("  Admins: " + chatters.admins)
        log("  Staff: " + chatters.staff)
        log("  Viewers: " + chatters.viewers)
        log("  All Viewers (sum of the above): " + chatters.allViewers)
    }

    override fun sendMessage(message: String) = twitchClient.chat.sendMessage("azeDevs", "［ $message ］")
    override fun isConnected(): Boolean = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution
    override fun getViewerData(): List<ViewerData> {
        val outList: MutableList<ViewerData> = arrayListOf()
        messageCache.forEach { outList.add(it) }
        messageCache.clear()
        return outList
    }

}
