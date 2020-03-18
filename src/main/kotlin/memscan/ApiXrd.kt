package memscan

/**
 * memscan.XrdApi
 * provides [FighterData]
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
    fun getFighterData(): List<FighterData>

    /**
     * @return data from current match
     */
    fun getMatchData(): MatchData

}

@Suppress("CovariantEquals")
data class FighterData(
    val steamUserId: Long = -1L,
    val displayName: String = "",
    val characterId: Byte = -0x1,
    val cabinetLoc: Byte = -0x1,
    val playerSide: Byte = -0x1,
    val matchesWon: Int = -1,
    val matchesSum: Int = -1,
    val loadingPct: Int = -1
) { fun isValid() = steamUserId > -1
    fun equals(other: FighterData) = other.displayName == displayName &&
                other.characterId == characterId &&
                other.cabinetLoc == cabinetLoc &&
                other.playerSide == playerSide &&
                other.matchesWon == matchesWon &&
                other.matchesSum == matchesSum &&
                other.loadingPct == loadingPct
}

@Suppress("CovariantEquals")
data class MatchData(
    val timer: Int = -1,
    val health: Pair<Int, Int> = Pair(-1,-1),
    val rounds: Pair<Int, Int> = Pair(-1,-1),
    val tension: Pair<Int, Int> = Pair(-1,-1),
    val stunProgress: Pair<Int, Int> = Pair(-1,-1),
    val stunMaximum: Pair<Int, Int> = Pair(-1,-1),
    val canBurst: Pair<Boolean, Boolean> = Pair(first = false, second = false),
    val strikeStun: Pair<Boolean, Boolean> = Pair(first = false, second = false),
    val guardGauge: Pair<Int, Int> = Pair(-1,-1)
) { fun isValid() = timer > -1
    fun equals(other: MatchData) = timer == other.timer &&
            health.first == other.health.first &&
            stunProgress.first == other.stunProgress.first &&
            stunMaximum.first == other.stunMaximum.first &&
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



