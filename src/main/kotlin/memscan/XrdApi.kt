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
    val steamId: Long = -1L,
    val userName: String = "",
    val characterId: Byte = -0x1,
    val cabinetId: Byte = -0x1,
    val seatingId: Byte = -0x1,
    val matchesWon: Int = -1,
    val matchesSum: Int = -1,
    val loadPercent: Int = -1
) { fun isValid() = steamId > 0
    fun isOnCabinet(cabinetId: Int) = this.cabinetId.toInt() == cabinetId
    fun isSeatedAt(seatingId: Int) = this.seatingId.toInt() == seatingId
    fun equals(other: FighterData) = other.userName == userName &&
                other.characterId == characterId &&
                other.cabinetId == cabinetId &&
                other.seatingId == seatingId &&
                other.matchesWon == matchesWon &&
                other.matchesSum == matchesSum &&
                other.loadPercent == loadPercent
}

@Suppress("CovariantEquals")
data class MatchData(
    val timer: Int = -1,
    val health: Pair<Int, Int> = Pair(-1,-1),
    val rounds: Pair<Int, Int> = Pair(-1,-1),
    val tension: Pair<Int, Int> = Pair(-1,-1),
    val stunCurrent: Pair<Int, Int> = Pair(-1,-1),
    val stunMaximum: Pair<Int, Int> = Pair(-1,-1),
    val burst: Pair<Boolean, Boolean> = Pair(first = false, second = false),
    val struck: Pair<Boolean, Boolean> = Pair(first = false, second = false),
    val guardGauge: Pair<Int, Int> = Pair(-1,-1)
) { fun isValid() = timer > -1
    fun equals(other: MatchData) = timer == other.timer &&
            health.first == other.health.first &&
            stunCurrent.first == other.stunCurrent.first &&
            stunMaximum.first == other.stunMaximum.first &&
            rounds.first == other.rounds.first &&
            tension.first == other.tension.first &&
            burst.first == other.burst.first &&
            struck.first == other.struck.first &&
            guardGauge.first == other.guardGauge.first &&
            health.second == other.health.second &&
            rounds.second == other.rounds.second &&
            tension.second == other.tension.second &&
            burst.second == other.burst.second &&
            struck.second == other.struck.second &&
            guardGauge.second == other.guardGauge.second
}



