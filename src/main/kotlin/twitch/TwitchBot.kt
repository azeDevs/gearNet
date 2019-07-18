package twitch

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import utils.getTokenFromFile
import utils.log

class TwitchBot : BotApi {
    private val messageCache: MutableList<Message> = mutableListOf()
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
            messageCache.add(Message(it.user.id, it.user.name, it.message))
        }

        twitchClient.getChat().joinChannel("azeDevs")
//        sendMessage("Hi WOrld!")
    }

    fun getViewers() {
        val chatters = twitchClient.messagingInterface.getChatters("azeDevs").execute()
        log("VIPs: " + chatters.vips)
        log("Mods: " + chatters.moderators)
        log("Admins: " + chatters.admins)
        log("Staff: " + chatters.staff)
        log("Viewers: " + chatters.viewers)
        log("All Viewers (sum of the above): " + chatters.allViewers)
    }

    override fun sendMessage(message: String) = twitchClient.chat.sendMessage("azeDevs", message)

    override fun getMessages(): List<Message> = messageCache
    fun clearMessages() { messageCache.clear() }

    override fun isConnected(): Boolean = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution

    fun <T> eval(callback: (client: TwitchClient) -> T): T = callback.invoke(twitchClient)
}
