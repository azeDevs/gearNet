package twitch

import BLU_BANNER
import RED_BANNER
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import events.EventType.*
import events.ViewerEvent
import models.Fighter
import session.SessionState
import utils.getTokenFromFile
import utils.keepInRange
import utils.stringToInt

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
//        sendMessage("\uD83D\uDC4B Hello World! \uD83E\uDD16")
    }

    override fun sendMessage(message: String) = twitchClient.chat.sendMessage("azeDevs", message) //"［$message］"
    override fun isConnected(): Boolean = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution
    override fun getViewerData(): List<ViewerData> {
        val outList: MutableList<ViewerData> = arrayListOf()
        viewerDatas.forEach { outList.add(it) }
        viewerDatas.clear()
        return outList
    }

    //fun getViewers() = twitchClient.messagingInterface.getChatters("azeDevs").execute().allViewers

    fun generateViewerEvents(state: SessionState): List<ViewerEvent> {
        val events: MutableList<ViewerEvent> = arrayListOf()
        getViewerData().forEach {
            var viewer = Viewer(it)
            if (!state.contains(it)) events.add(ViewerEvent(VIEWER_JOINED, viewer, it.text))
            else viewer = Viewer(state.getViewer(it.twitchId).getData(), it)

            var eventType = VIEWER_MESSAGE
            // TODO: DEFINE fighter USING REFERENCED MATCH DATA AND VIEWER ENTRY
            var fighter = Fighter()
            var betBanner = Pair("","")
            var betAmount = -1
            if (it.text.isNotEmpty() && it.text.substring(0,1).equals("!")) {
                eventType = COMMAND_HELP
                val cmd = it.text.toUpperCase().substring(1).split("\\s".toRegex()).toList()
                if(cmd[0].equals("R", true) || cmd[0].equals("B", true)) {
                    eventType = COMMAND_BET
                    when (cmd[0]) {
                        "R" -> betBanner = RED_BANNER
                        "B" -> betBanner = BLU_BANNER
                    }
                    if(cmd.size == 2) betAmount = keepInRange(stringToInt(cmd[1]), 5, viewer.getScoreTotal())
                    else betAmount = 5
                } else if (cmd[0].equals("WALLET", true)) {
                    eventType = COMMAND_WALLET
                }
            }
            events.add(ViewerEvent(eventType, viewer, it.text, fighter, betBanner, betAmount))
        }
        return events
    }

}
