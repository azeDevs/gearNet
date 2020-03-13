package twitch

import MyApp.Companion.SILENT_TWITCH
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import models.Viewer
import session.Session
import session.log
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
            true -> log("CHAT roboaze: $message")
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

    //fun getViewers() = twitchClient.messagingInterface.getChatters("azeDevs").execute().allViewers

    fun generateViewerEvents() {
        getViewerData().forEach {
            if (!it.message.isEmpty()) {
                log("CHAT ${it.displayName}: ${it.message}")
                // ADD VIEWER IF THEY ARE NEW
                if (!s.viewers.containsKey(it.twitchId)) {
                    s.viewers.put(it.twitchId, Viewer(it))
                    log("${it.displayName} added to Viewers Map")
                }
                // RUN COMMAND IF THERE IS ONE
                if (it.message.contains("azpngRC") && !s.viewers[it.twitchId]!!.isTeamR()) {
                    s.viewers[it.twitchId]!!.setTeamR()
                    sendMessage("${it.displayName} joins azpngRC")
                }
                if (it.message.contains("azpngBC") && !s.viewers[it.twitchId]!!.isTeamB()) {
                    s.viewers[it.twitchId]!!.setTeamB()
                    sendMessage("${it.displayName} joins azpngBC")
                }
            }
        }
    }

}