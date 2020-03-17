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

    private val viewerDatas: MutableList<ViewerData> = mutableListOf()
    private val twitchClient: TwitchClient = TCB.builder()
        .withChatAccount(OA2C("twitch", getTokenFromFile("keys", "twitch_bot")))
        .withEnableChat(true)
        .withEnableHelix(true)
        .withEnableKraken(true)
        .withEnableTMI(true)
        .build()

    init {
        twitchClient.chat.eventManager.onEvent(CME::class.java).subscribe {
            viewerDatas.add(ViewerData(it.user.id, it.user.name, it.message))
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
    override fun getViewerData(): List<ViewerData> {
        val outList: MutableList<ViewerData> = arrayListOf()
        viewerDatas.forEach { outList.add(it) }
        viewerDatas.clear()
        return outList
    }

    fun addViewerData(viewerData:ViewerData) { viewerDatas.add(viewerData) }

    //fun getViewers() = twitchClient.messagingInterface.getChatters("azeDevs").execute().allViewers

    fun generateViewerEvents() {
        getViewerData().forEach {
            if (!it.message.isEmpty()) {
                println("CHAT ${it.displayName}: ${it.message}")
                // ADD VIEWER IF THEY ARE NEW
                if (!s.watchers.containsKey(it.twitchId)) {
                    s.watchers.put(it.twitchId, Watcher(it))
                    println("${it.displayName} added to Viewers Map")
                }
                // RUN COMMAND IF THERE IS ONE
                if (it.message.contains("azpngRC") && !s.watchers[it.twitchId]!!.isTeamR()) {
                    s.watchers[it.twitchId]!!.setTeamR()
                    sendMessage("${it.displayName} joins red")
                }
                if (it.message.contains("azpngBC") && !s.watchers[it.twitchId]!!.isTeamB()) {
                    s.watchers[it.twitchId]!!.setTeamB()
                    sendMessage("${it.displayName} joins blue")
                }
            }
        }
    }

}