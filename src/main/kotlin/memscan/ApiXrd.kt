package memscan


/**
 * memscan.XrdApi
 * provides [FighterData]
 * provides [MatchSnap]
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
    fun getMatchSnap(): MatchSnap

}

data class FighterData(
    val steamId: Long = -1L,
    val displayName: String = "",
    val characterId: Int = -1,
    val cabinetId: Int = -1,
    val seatingId: Int = -1,
    val matchesWon: Int = -1,
    val matchesSum: Int = -1,
    val loadingPct: Int = -1
) {
    fun isValid() = steamId > 0
}

data class MatchSnap(
    val timer: Int = -1,
    val health: Pair<Int, Int> = Pair(-1, -1),
    val rounds: Pair<Int, Int> = Pair(-1, -1),
    val tension: Pair<Int, Int> = Pair(-1, -1),
    val canBurst: Pair<Boolean, Boolean> = Pair(false, false),
    val strikeStun: Pair<Boolean, Boolean> = Pair(false, false),
    val guardGauge: Pair<Int, Int> = Pair(-1, -1)
) {
    fun isSameAs(other: MatchSnap) = timer == other.timer &&
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
    fun isValid() = timer > 0
    fun health(seat: Int) = if (seat == 0) health.first else if (seat == 1) health.second else MatchSnap().health.second
    fun rounds(seat: Int) = if (seat == 0) rounds.first else if (seat == 1) rounds.second else MatchSnap().rounds.second
    fun tension(seat: Int) = if (seat == 0) tension.first else if (seat == 1) tension.second else MatchSnap().tension.second
    fun canBurst(seat: Int) = if (seat == 0) canBurst.first else if (seat == 1) canBurst.second else MatchSnap().canBurst.second
    fun strikeStun(seat: Int) = if (seat == 0) strikeStun.first else if (seat == 1) strikeStun.second else MatchSnap().strikeStun.second
    fun guardGauge(seat: Int) = if (seat == 0) guardGauge.first else if (seat == 1) guardGauge.second else MatchSnap().guardGauge.second
}