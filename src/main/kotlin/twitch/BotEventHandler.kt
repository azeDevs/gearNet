package twitch

import MyApp.Companion.SILENT_TWITCH
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import models.Watcher
import session.Session
import utils.getTokenFromFile

typealias CME = ChannelMessageEvent
typealias OA2C = OAuth2Credential
typealias TCB = TwitchClientBuilder

class BotEventHandler(private val s: Session) : BotApi {

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
        when (SILENT_TWITCH) {
            true -> println("CHAT roboaze: $message")
            false -> twitchClient.chat.sendMessage("azeDevs", message)
        }
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
            if (!it.message.isEmpty()) {
                println("CHAT ${it.displayName}: ${it.message}")
                // ADD VIEWER IF THEY ARE NEW
                if (!s.api.getWatchersMap().containsKey(it.twitchId)) {
                    s.api.getWatchersMap().put(it.twitchId, Watcher(it))
                    println("${it.displayName} added to Viewers Map")
                }
                // RUN COMMAND IF THERE IS ONE
                if (it.message.contains("azpngRC") && !s.api.getWatchersMap()[it.twitchId]!!.isTeamR()) {
                    s.api.getWatchersMap()[it.twitchId]!!.setTeamR()
                    sendMessage("${it.displayName} joins red")
                }
                if (it.message.contains("azpngBC") && !s.api.getWatchersMap()[it.twitchId]!!.isTeamB()) {
                    s.api.getWatchersMap()[it.twitchId]!!.setTeamB()
                    sendMessage("${it.displayName} joins blue")
                }
            }
        }
    }

}