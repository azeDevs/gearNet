package twitch

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import session.log
import utils.getTokenFromFile

class TwitchBot(accessToken: String = getTokenFromFile("keys", "twitch_bot")) : BotApi {
    private val messageCache: MutableList<Message> = mutableListOf()
    private val twitchClient: TwitchClient

    init {
        val credentials = OAuth2Credential("twitch", accessToken)
        twitchClient = TwitchClientBuilder.builder().withChatAccount(credentials)
                .withEnableChat(true)
                .withEnableHelix(true)
                .withEnableTMI(true)
                .build()
        twitchClient.chat.eventManager.onEvent(ChannelMessageEvent::class.java).subscribe {
            messageCache.add(Message(it.user.id, it.user.name, it.message))
        }
//        sendMessage("Hello World!")
    }

    override fun sendMessage(message: String) = twitchClient.chat.sendMessage("azeDevs", message)

    override fun getMessages(): List<Message> = messageCache
    fun clearMessages() { messageCache.clear() }

    override fun isConnected(): Boolean {
        val flag = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution
        log("Bot.isConnected() == ${flag}")
        return flag
    }

    fun <T> eval(callback: (client: TwitchClient) -> T): T = callback.invoke(twitchClient)
}
