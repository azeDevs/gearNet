package session

data class Snap(
    val health: Pair<Int, Int>,
    val rounds: Pair<Int, Int>,
    val timer: Int,
    val tension: Int = -1,
    val canBurst: Boolean = false,
    val strikeStun: Boolean = false,
    val guardGauge: Pair<Int, Int>
)