package twitch

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import events.EventType.VIEWER_JOINED
import events.EventType.VIEWER_MESSAGE
import events.ViewerEvent
import session.SessionState
import utils.getTokenFromFile
import utils.keepInRange
import utils.strToInt

typealias CME = ChannelMessageEvent
typealias OA2C = OAuth2Credential
typealias TCB = TwitchClientBuilder

class BotHandler : BotApi {

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

    }

    override fun sendMessage(message: String) = twitchClient.chat.sendMessage("azeDevs", message)
    override fun isConnected(): Boolean = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution
    override fun getViewerData(): List<ViewerData> {
        val outList: MutableList<ViewerData> = arrayListOf()
        viewerDatas.forEach { outList.add(it) }
        viewerDatas.clear()
        return outList
    }

    //sendMessage("\uD83D\uDC4B Hello World! \uD83E\uDD16")
    //fun getViewers() = twitchClient.messagingInterface.getChatters("azeDevs").execute().allViewers

    fun generateViewerEvents(state: SessionState): List<ViewerEvent> {
        val events: MutableList<ViewerEvent> = arrayListOf()
        getViewerData().forEach {
            var eventType = VIEWER_MESSAGE
            var viewer = Viewer(it)
            if (!state.contains(it)) events.add(ViewerEvent(VIEWER_JOINED, viewer, it.text))
            else viewer = Viewer(state.getViewer(it.twitchId).getData(), it)
            // TODO: CHECK IF THE VIEWER PLACED A BET, AND ADD IT TO THE MATCH PRE-EVENT
            events.add(ViewerEvent(eventType, viewer, it.text))
        }
        return events
    }

    private fun getBetAmount(cmd: List<String>, viewer: Viewer): Int = if (cmd.size == 2) keepInRange(strToInt(cmd[1]), 10, viewer.getScoreTotal()) else 10

}
