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

class ViewerData(
    val id:Long = -1,
    val name:String = "",
    val text:String = "",
    val fighterId:Long = -1,
    val betAmount:Int = -1,
    val betBanner:Pair<String, String> = Pair("null","❌")
)
