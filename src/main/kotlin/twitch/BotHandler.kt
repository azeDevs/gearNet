package twitch

import MyApp.Companion.SILENCE_BOT
import application.log
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import events.CommandBetEvent
import events.ViewerJoinedEvent
import events.ViewerMessageEvent
import session.Session
import utils.getTokenFromFile

typealias CME = ChannelMessageEvent
typealias OA2C = OAuth2Credential
typealias TCB = TwitchClientBuilder

class BotHandler(private val s: Session) : BotApi {

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
        sendMessage("\uD83D\uDC4B Hello World! \uD83E\uDD16")
    }

    override fun sendMessage(message: String) {
        if (!SILENCE_BOT) twitchClient.chat.sendMessage("azeDevs", message)
        else log("“roboaze” said “${message}”")
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
            var viewer = Viewer(it)
            if (!s.state().contains(viewer)) s.fire(ViewerJoinedEvent(viewer))
            else viewer = Viewer(s.state().getViewer(it.twitchId).getData(), it)
            s.fire(ViewerMessageEvent(viewer, it.text))
            if (ViewerBet(viewer).isValid()) s.fire(CommandBetEvent(viewer, ViewerBet(viewer)))
        }
    }

}
