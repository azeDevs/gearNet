package twitch

/**
 * twitch.BotApi
 */
interface BotApi {

    /**
     * @return if botApi is running and responsive
     */
    fun isConnected(): Boolean

    /**
     * Send a botApi message to Twitch chat
     */
    fun sendMessage(message:String)

    /**
     * @return a List of all Messages
     */
    fun getViewerData(): List<ViewerData>

}

data class ViewerData(
    val twitchId:Long = -1,
    val displayName:String = "",
    val message:String = ""
) {
    fun isValid() = twitchId > 0
}