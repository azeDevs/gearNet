package session

import memscan.MatchData
import utils.Duo

/**
 *
 * XrdListener                  updates and archives Lobby data.
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Player>     contains Player bounty and chains data
 *
 * [Match]
 * contains fighting Players and Match data
 *
 */
class Match (
    val fighters: Duo<Player> = Duo(Player(), Player()),
    val cabinetId: Int = -1,
    val matchData: MatchData = MatchData()
)
//{
//
//    private val P1 = 0
//    private val P2 = 1
//
//    private var winner = -1
//    private var roundOngoing = false
//    private val snapshots = arrayListOf(matchData)
//
//    // Gotten from MatchData, else gotten from LobbyData (LOBBY QUALITY DATA)
//    private var character = Duo(players.p1.characterId.toInt(), players.p2.characterId.toInt())
//    private var handle = Duo(players.p1.displayName, players.p2.displayName)
//    private var rounds = Duo(matchData.rounds.first, matchData.rounds.second)
//    private var health = Duo(matchData.health.first, matchData.health.second)
//
//    // Gotten from MatchData, else considered useless (MATCH QUALITY DATA)
//    private var matchTimer = matchData.timer
//    private var tension = Duo(matchData.tension.first, matchData.tension.second)
//    private var canBurst = Duo(matchData.canBurst.first, matchData.canBurst.second)
//    private var strikeStun = Duo(matchData.strikeStun.first, matchData.strikeStun.second)
//    private var guardGauge = Duo(matchData.guardGauge.first, matchData.guardGauge.second)
//
//    fun getData() = snapshots.last()
//
//    /*
//
//        if (strikeStunned)
//
//    */
//
//    fun updateMatch(updatedData: MatchData, session:Session): Boolean {
//        if (!getData().equals(updatedData)) {
//
//            snapshots.add(updatedData)
//            matchTimer = updatedData.timer
//
//            health.p1 = keepInRange(getData().health.first)//, 0, 420)
//            tension.p1 = keepInRange(getData().tension.first)//, 0, 10000)
//            guardGauge.p1 = keepInRange(getData().guardGauge.first)//, 0, 12800)
//            rounds.p1 = updatedData.rounds.first
//            canBurst.p1 = getData().canBurst.first
//            strikeStun.p1 = getData().strikeStun.first
//
//            health.p2 = keepInRange(getData().health.second)//, 0, 420)
//            tension.p2 = keepInRange(getData().tension.second)//, 0, 10000)
//            guardGauge.p2 = keepInRange(getData().guardGauge.second)//, 0, 12800)
//            rounds.p2 = updatedData.rounds.second
//            canBurst.p2 = getData().canBurst.second
//            strikeStun.p2 = getData().strikeStun.second
//
//            // Has the round started?
//            if (!roundOngoing && getHealth(P1) == 420 && getHealth(P2) == 420 && getWinner() == -1) {
//                roundOngoing = true
//                session.setMode(MATCH_MODE)
//                utils.log("[MATC] ID$matchId Duel ${getRounds(P1) + getRounds(P2) + 1} ... LET'S ROCK!")
//            }
//
//            // Has the round ended, and did player 1 win?
//            if (roundOngoing && getWinner() == -1 && getHealth(P2) == 0 && getHealth(P1) != getHealth(P2) ) {
//                roundOngoing = false
//                session.setMode(SLASH_MODE)
//                utils.log("[MATC] ID$matchId P1 wins Duel ${getRounds(P1) + getRounds(P2) + 1}")
//            }
//
//            // Has the round ended, and did player 2 win?
//            if (roundOngoing && getWinner() == -1 && getHealth(P1) == 0 && getHealth(P2) != getHealth(P1)) {
//                roundOngoing = false
//                session.setMode(SLASH_MODE)
//                utils.log("[MATC] ID$matchId P2 wins Duel ${getRounds(P1) + getRounds(P2) + 1}")
//            }
//
//            // Did player 1 win the match?
//            if (getRounds(P1) == lobbyData.roundWins && winner == -1) {
//                winner = 0
//                session.setMode(VICTORY_MODE)
//                utils.log("[MATC] ID$matchId CONCLUDED, P1 \"${getHandleString(P1)}\" wins Match")
//            }
//
//            // Did player 2 win the match?
//            if (getRounds(P2) == lobbyData.roundWins && winner == -1) {
//                winner = 1
//                utils.log("[MATC] ID$matchId CONCLUDED, P2 \"${getHandleString(P2)}\" wins Match")
//                session.setMode(VICTORY_MODE)
//            }
//
//            return true
//
//        } else return false
//    }
//
//    fun getWinner():Int = winner
//    fun getTimer():Int = matchTimer
//    fun getRounds(side:Int):Int = rounds.p(side)
//    fun getHealth(side:Int):Int = health.p(side)
//    fun getCharacter(side:Int):Int = character.p(side)
//    fun getTension(side:Int):Int = tension.p(side)
//    fun getRisc(side:Int):Int = guardGauge.p(side)
//    fun getBurst(side:Int):Boolean = canBurst.p(side)
//    fun getHitStun(side:Int):Boolean = strikeStun.p(side)
//
//    fun getHandleString(side:Int):String = handle.p(side)
//    fun getHealthString(side:Int):String = "HP: ${getHealth(side)} / 420"
//    fun getRoundsString(side:Int):String = "Rounds: ${getRounds(side)} / ${lobbyData.roundWins}"
//    fun getTensionString(side:Int):String = "Tension: ${getTension(side)} / 10000"
//    fun getRiscString(side:Int):String = "   RISC: ${getRisc(side)} / 12800"
//    fun getBurstString(side:Int):String = "  Burst: ${getBurst(side)}"
//    fun getHitStunString(side:Int):String = "  IsHit: ${getHitStun(side)}"
//
//    fun getCabinetId():Byte = cabinetId
//    fun getCabinetString(cabId:Int = getCabinetId().toInt()): String {
//        return when(cabId) {
//            0 -> "CABINET A"
//            1 -> "CABINET B"
//            2 -> "CABINET C"
//            3 -> "CABINET D"
//            else -> "$cabId"
//        }
//    }
//
//}