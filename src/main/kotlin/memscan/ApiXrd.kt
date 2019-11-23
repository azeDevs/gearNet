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


/* data class LobbyData(
    Lobby name? String = ""
    Search ID? String = ""
    Connection Restriction? Int = -1
    Casual Match? Boolean = False
    Rotation? Winner, Loser, Both, Retry
    Rounds to win? Int = -1
    Open Cabinets? Int = -1
    Cabinet HP? Pair<Int, Int>
    Cabinet Rounds? Pair<Int, Int>
    About to close? Boolean = False
    New Chat Messages? List<FighterMessageEvent(Fighter, String)>
 //) {}
*/


data class FighterData(
    private val steamId: Long = -1L,
    private val displayName: String = "",
    private val characterId: Int = -1,
    private val matchesWon: Int = -1,
    private val matchesSum: Int = -1,
    private val loadingPct: Int = -1,
    private val cabinetId: Int = -1,
    private val seatingId: Int = -1
    // Color Pallete ID? Int = -1
    // Selected Stage ID? Int = -1
    // Lobby Avatar Head? Int = -1
    // Lobby Avatar Color? Int = -1
) {
    fun isValid() = steamId > 0
    fun steamId() = steamId
    fun displayName() = displayName
    fun characterId() = characterId
    fun matchesWon() = matchesWon
    fun matchesSum() = matchesSum
    fun loadingPct() = loadingPct
    fun cabinetId() = cabinetId
    fun seatingId() = seatingId
}


data class MatchSnap(
    private val timer: Int = -1,
    private val health: Pair<Int, Int> = Pair(-1, -1),
    private val rounds: Pair<Int, Int> = Pair(-1, -1),
    private val tension: Pair<Int, Int> = Pair(-1, -1),
    private val canBurst: Pair<Boolean, Boolean> = Pair(first = false, second = false),
    private val strikeStun: Pair<Boolean, Boolean> = Pair(first = false, second = false),
    private val guardGauge: Pair<Int, Int> = Pair(-1, -1),
    private val stunMaximum: Pair<Int, Int> = Pair(-1,-1),
    private val stunProgress: Pair<Int, Int> = Pair(-1,-1)
    // Tension Pulse? Pair<Int, Int>
    // Untech Time Remaining? Pair<Int, Int>
    // Burst Gauge Value? Pair<Int, Int>
    // Is being Thrown? Pair<Boolean, Boolean>
    // Is being IKed? Pair<Boolean, Boolean>
    // Is in RC/Super flash? Pair<Boolean, Boolean>  (if False when 25 meter is spent, a Blitz occured)
    // Beat Counter? Pair<Int, Int>
) {
    fun isSameAs(other: MatchSnap) = timer == other.timer &&
            health.first == other.health.first &&
            rounds.first == other.rounds.first &&
            tension.first == other.tension.first &&
            canBurst.first == other.canBurst.first &&
            strikeStun.first == other.strikeStun.first &&
            guardGauge.first == other.guardGauge.first &&
            stunMaximum.first == other.stunMaximum.first &&
            stunProgress.first == other.stunProgress.first &&
            health.second == other.health.second &&
            rounds.second == other.rounds.second &&
            tension.second == other.tension.second &&
            canBurst.second == other.canBurst.second &&
            strikeStun.second == other.strikeStun.second &&
            guardGauge.second == other.guardGauge.second &&
            stunMaximum.second == other.stunMaximum.second &&
            stunProgress.second == other.stunProgress.second

    fun isValid() = timer > 0
    fun timer() = timer
    fun health(seat: Int) = if (seat == 0) health.first else if (seat == 1) health.second else MatchSnap().health.second
    fun rounds(seat: Int) = if (seat == 0) rounds.first else if (seat == 1) rounds.second else MatchSnap().rounds.second
    fun tension(seat: Int) = if (seat == 0) tension.first else if (seat == 1) tension.second else MatchSnap().tension.second
    fun canBurst(seat: Int) = if (seat == 0) canBurst.first else if (seat == 1) canBurst.second else MatchSnap().canBurst.second
    fun strikeStun(seat: Int) = if (seat == 0) strikeStun.first else if (seat == 1) strikeStun.second else MatchSnap().strikeStun.second
    fun guardGauge(seat: Int) = if (seat == 0) guardGauge.first else if (seat == 1) guardGauge.second else MatchSnap().guardGauge.second
    fun stunMaximum(seat: Int) = if (seat == 0) stunMaximum.first else if (seat == 1) stunMaximum.second else MatchSnap().guardGauge.second
    fun stunProgress(seat: Int) = if (seat == 0) stunProgress.first else if (seat == 1) stunProgress.second else MatchSnap().guardGauge.second
}