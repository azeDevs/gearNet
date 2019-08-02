package memscan

class MemSimulator : XrdApi {
    override fun isConnected(): Boolean = false
    override fun getClientSteamId(): Long = -1
    override fun getFighterData(): List<FighterData> = listOf()
    override fun getMatchData(): MatchData = MatchData()
}