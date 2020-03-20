package twitch

import MyApp.Companion.TWITCH_CHAT_BOT
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import memscan.GearNetUpdates.Companion.IC_CHAT
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2
import session.Session
import utils.getTokenFromFile

typealias CME = ChannelMessageEvent
typealias OA2C = OAuth2Credential
typealias TCB = TwitchClientBuilder

class TwitchHandler(private val s: Session) : BotApi {

    private val watcherData: MutableList<WatcherData> = mutableListOf()
    private val twitchClient: TwitchClient = TCB.builder()
        .withChatAccount(OA2C("twitch", getTokenFromFile("keys", "twitch_bot")))
        .withEnableChat(true)
        .withEnableHelix(true)
        .withEnableKraken(true)
        .withEnableTMI(true)
        .build()

    init {
        twitchClient.chat.eventManager.onEvent(CME::class.java).subscribe {
            watcherData.add(WatcherData(it.user.id, it.user.name, it.message))
        }
        twitchClient.chat.joinChannel("azeDevs")
        sendMessage("Hello World!")
    }

    override fun sendMessage(message: String) {
        logChat("roboaze", message)
        if (TWITCH_CHAT_BOT) twitchClient.chat.sendMessage("azeDevs", message)
    }
    override fun isConnected(): Boolean = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution
    override fun getViewerData(): List<WatcherData> {
        val outList: MutableList<WatcherData> = arrayListOf()
        watcherData.forEach { outList.add(it) }
        watcherData.clear()
        return outList
    }

    fun addWatcherData(watcherData:WatcherData) { this.watcherData.add(watcherData) }

    //fun getViewers() = twitchClient.messagingInterface.getChatters("azeDevs").execute().allViewers

    fun generateWatcherEvents() {
        getViewerData().forEach {
            if (it.message.isNotEmpty()) {
                logChat(it.displayName, it.message)
                // ADD VIEWER IF THEY ARE NEW
//                if (!s.getPlayersMap().containsKey(it.twitchId)) {
//                    s.getPlayersMap().put(it.twitchId, Player(it))
//                    println("${it.displayName} added to Viewers Map")
//                }
                // RUN COMMAND IF THERE IS ONE
                if (it.message.contains("azpngRC") && !s.getPlayersMap()[it.twitchId]!!.isTeam(PLAYER_1)) {
                    s.getPlayersMap()[it.twitchId]!!.setTeam(PLAYER_1)
                    sendMessage("${it.displayName} joins red")
                }
                if (it.message.contains("azpngBC") && !s.getPlayersMap()[it.twitchId]!!.isTeam(PLAYER_2)) {
                    s.getPlayersMap()[it.twitchId]!!.setTeam(PLAYER_2)
                    sendMessage("${it.displayName} joins blue")
                }
            }
        }
    }

    fun logChat(displayName:String, message:String) = println("${IC_CHAT}$displayName: $message")

}