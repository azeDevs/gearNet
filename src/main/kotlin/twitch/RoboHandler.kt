package twitch

import MyApp.Companion.SIMULATION_MODE
import MyApp.Companion.TWITCH_CHAT_BOT
import application.arcade.Arcadia
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import memscan.GearNetUpdates
import models.Player
import utils.getRandomName
import utils.getTokenFromFile
import kotlin.random.Random

class RoboHandler(private val a: Arcadia) : BotApi {


    private val watcherData: MutableList<WatcherData> = mutableListOf()
    private val twitchClient: TwitchClient = TwitchClientBuilder.builder()
        .withChatAccount(OAuth2Credential("twitch", getTokenFromFile("keys", "twitch_bot")))
        .withEnableChat(true)
        .withEnableHelix(true)
        .withEnableKraken(true)
        .withEnableTMI(true)
        .build()

    init {
        twitchClient.chat.eventManager.onEvent(ChannelMessageEvent::class.java).subscribe { watcherData.add(WatcherData(it.user.id, it.user.name, it.message)) }
        twitchClient.chat.joinChannel("azeDevs")
        sendMessage("Hello World!")
    }


    /**
     *
     */
    override fun sendMessage(message: String) { if (TWITCH_CHAT_BOT) twitchClient.chat.sendMessage("azeDevs", logChat("roboaze", "\uD83E\uDD16 $message")) else logChat("roboaze", "\uD83D\uDCBB $message") }
    override fun isConnected(): Boolean = twitchClient.messagingInterface.getChatters("azeDevs").isFailedExecution
    override fun getWatcherData(): List<WatcherData> {
        val outList: MutableList<WatcherData> = arrayListOf()
        watcherData.forEach { outList.add(it) }
        watcherData.clear()
        return outList
    }


    /**
     *
     */
    fun generateWatcherEvents() {
        if (SIMULATION_MODE) when (Random.nextInt(2560)) {
            0 -> addWatcherData(WatcherData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngRC"))
            1 -> addWatcherData(WatcherData(Random.nextLong(1000000000, 9999999999), getRandomName(), "azpngBC"))
            2 -> addWatcherData(WatcherData(Random.nextLong(1000000000, 9999999999), getRandomName(), getRandomName()))
        }
        getWatcherData().forEach {
            if (it.message.isNotEmpty()) {
                logChat(it.displayName, it.message)

                // ADD VIEWER IF THEY ARE NEW
                if (!a.getPlayersMap().containsKey(it.twitchId)) {
                    a.getPlayersMap()[it.twitchId] = Player(it)
                    println("${it.displayName} added to Viewers Map")
                }
                // RUN COMMAND IF THERE IS ONE
                if (it.message.contains("azpngRC") && !a.getPlayersMap()[it.twitchId]!!.isTeam(Player.PLAYER_1)) {
                    a.getPlayersMap()[it.twitchId]!!.setTeam(Player.PLAYER_1)
                    sendMessage("${it.displayName} joins red")
                }
                if (it.message.contains("azpngBC") && !a.getPlayersMap()[it.twitchId]!!.isTeam(Player.PLAYER_2)) {
                    a.getPlayersMap()[it.twitchId]!!.setTeam(Player.PLAYER_2)
                    sendMessage("${it.displayName} joins blue")
                }
            }
        }
    }


    /**
     *
     */
    private fun addWatcherData(watcherData:WatcherData) { this.watcherData.add(watcherData) }
    private fun logChat(displayName:String, message:String): String {
        println("${GearNetUpdates.IC_CHAT}$displayName: $message")
        return message
    }


}