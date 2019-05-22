package memscan

/**
 * memscan.XrdApi
 * provides [PlayerData]
 * provides [MatchData]
 */
interface XrdApi {

    /**
     * @return Is Xrd running and ready to serve data
     */
    fun isConnected(): Boolean

    /**
     * @return the Steam ID of the local player client
     */
    fun getClientSteamId(): Long

    /**
     * @return a List of the Xrd lobby's active players and their data
     */
    fun getPlayerData(): List<PlayerData>

    /**
     * @return data from current match
     */
    fun getMatchData(): MatchData

    /**
     * @return data from current lobby
     */
    fun getLobbyData(): LobbyData

}

data class PlayerData(
    //val miniHealth: Pair<Int, Int>,
    //val miniRounds: Pair<Int, Int>,
    //val readiedUp: Pair<Boolean, Boolean>,
    val steamUserId: Long = -1L,
    val displayName: String = "",
    val characterId: Byte = -0x1,
    val cabinetLoc: Byte = -0x1,
    val playerSide: Byte = -0x1,
    val matchesWon: Int = -1,
    val matchesSum: Int = -1,
    val loadingPct: Int = -1
) { fun equals(other: PlayerData) = other.displayName.equals(displayName) &&
                other.characterId == characterId &&
                other.cabinetLoc == cabinetLoc &&
                other.playerSide == playerSide &&
                other.matchesWon == matchesWon &&
                other.matchesSum == matchesSum &&
                other.loadingPct == loadingPct
}

data class MatchData(
    //val frameDelay: Int = -1,
    //val comboBeats: Pair<Int, Int>,
    //val comboDamage: Pair<Int, Int>,
    //val tensionPulse: Pair<Float, Float>,
    //val stunProgress: Pair<Int, Int>,
    //val inputMotions: Pair<?, ?>,
    //val inputButtons: Pair<?, ?>,
    val timer: Int = -1,
    val health: Pair<Int, Int> = Pair(-1,-1),
    val rounds: Pair<Int, Int> = Pair(-1,-1),
    val tension: Pair<Int, Int> = Pair(-1,-1),
    val canBurst: Pair<Boolean, Boolean> = Pair(false,false),
    val strikeStun: Pair<Boolean, Boolean> = Pair(false,false),
    val guardGauge: Pair<Int, Int> = Pair(-1,-1)
) { fun equals(other: MatchData) = timer == other.timer &&
            health.first == other.health.first &&
            rounds.first == other.rounds.first &&
            tension.first == other.tension.first &&
            canBurst.first == other.canBurst.first &&
            strikeStun.first == other.strikeStun.first &&
            guardGauge.first == other.guardGauge.first &&
            health.second == other.health.second &&
            rounds.second == other.rounds.second &&
            tension.second == other.tension.second &&
            canBurst.second == other.canBurst.second &&
            strikeStun.second == other.strikeStun.second &&
            guardGauge.second == other.guardGauge.second

}

//class LobbyMessage(
//    val userId: Long = -1L,
//    val text: String = ""
//)

data class LobbyData(
    //val matchTime: Int = -1
    //val restriction: Int = -1     // Connection restriction
    //val matchType: Int = -1       // Serious, Casual, Training
    //val matchRule: Int = -1       // Winner stays, Loser, etc
    //val passworded: Boolean = false
    //val chatText: List<LobbyMessage> = arrayListOf()
    val cabinets: List<CabinetData> = emptyList(),
    val lobbyName: String = "",
    val roundWins: Int = 2
)

data class CabinetData(
    val readiedUp: Pair<Boolean, Boolean> = Pair(false,false),
    val miniHealth: Pair<Int, Int> = Pair(-1,-1),
    val miniRounds: Pair<Int, Int> = Pair(-1,-1)
)



