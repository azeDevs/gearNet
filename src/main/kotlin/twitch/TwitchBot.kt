package twitch

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

class TwitchBot : BotApi {

    private val viewerDatas: MutableList<ViewerData> = mutableListOf()
    private val twitchClient: TwitchClient

    init {
        twitchClient = TCB.builder()
            .withChatAccount(OA2C("twitch", getTokenFromFile("keys", "twitch_bot")))
//            .withClientId(getTokenFromFile("keys", "twitch_bot_client"))
//            .withClientSecret(getTokenFromFile("keys", "twitch_bot_secret"))
            .withEnableChat(true)
            .withEnableHelix(true)
            .withEnableKraken(true)
            .withEnableTMI(true)
            .build()

        twitchClient.chat.eventManager.onEvent(CME::class.java).subscribe {
            viewerDatas.add(ViewerData(it.user.id, it.user.name, it.message))
        }
        twitchClient.getChat().joinChannel("azeDevs")
    }

    override fun sendMessage(message: String) = twitchClient.chat.sendMessage("azeDevs", "$message") //"［$message］"
    override fun isConnected(): Boolean = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution
    override fun getViewerData(): List<ViewerData> {
        val outList: MutableList<ViewerData> = arrayListOf()
        viewerDatas.forEach { outList.add(it) }
        viewerDatas.clear()
        return outList
    }

    fun getViewers() = twitchClient.messagingInterface.getChatters("azeDevs").execute().allViewers

    fun generateViewerEvents(state: SessionState): List<ViewerEvent> {
        val events: MutableList<ViewerEvent> = arrayListOf()
        getViewerData().forEach {
            if (!state.contains(it)) events.add(ViewerEvent(VIEWER_JOINED, Viewer(it), it.text))
            var eventType = VIEWER_MESSAGE
            var viewer = Viewer(it) // TODO: FIND EXISTING USER, PASS IN oldData, else PASS IN newData only
            var fighter = Fighter()
            var betBanner = Pair("","")
            var betAmount = -1
            if (!it.text.isEmpty() && it.text.substring(0,1).equals("!")) {
                eventType = COMMAND_HELP
                val cmd = it.text.toUpperCase().substring(1).split("\\s".toRegex()).toList()
                if(cmd[0].equals("R", true) || cmd[0].equals("B", true)) {
                    eventType = COMMAND_BET
                    when (cmd[0]) {
                        "R" -> betBanner = Pair("\uD83D\uDD34","Red")
                        "B" -> betBanner = Pair("\uD83D\uDD35","Blue")
                    }
                    if(cmd.size == 2) betAmount = keepInRange(stringToInt(cmd[1]), 5, viewer.getScoreTotal())
                    else betAmount = 5
                }
            }
            events.add(ViewerEvent(eventType, viewer, it.text, fighter, betBanner, betAmount))
        }
        return events
    }

}
