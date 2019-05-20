package session

import memscan.LobbyData
import memscan.MatchData
import memscan.PlayerData
import utils.Duo
import utils.keepInRange


class Match(val matchId: Long, private val cabinetId: Byte, val players: Duo<PlayerData, PlayerData> = Duo(PlayerData(), PlayerData()), matchData: MatchData = MatchData(), val lobbyData: LobbyData = LobbyData()) {

    private val P1 = 0
    private val P2 = 1

    private var winner = -1
    private var roundStarted = false
    private var allData = arrayListOf(matchData)

    // Gotten from MatchData, else gotten from LobbyData (LOBBY QUALITY DATA)
    private var character = Duo(players.p1.characterId.toInt(), players.p2.characterId.toInt())
    private var handle = Duo(players.p1.displayName, players.p2.displayName)
    private var rounds = Duo(matchData.rounds.first, matchData.rounds.second)
    private var health = Duo(matchData.health.first, matchData.health.second)

    // Gotten from MatchData, else considered useless (MATCH QUALITY DATA)
    private var matchTimer = matchData.timer
    private var tension = Duo(matchData.tension.first, matchData.tension.second)
    private var burst = Duo(matchData.burst.first, matchData.burst.second)
    private var isHit = Duo(matchData.isHit.first, matchData.isHit.second)
    private var risc = Duo(matchData.risc.first, matchData.risc.second)

    fun getData() = allData.last()
    fun allData() = allData

    fun updateMatchData(updatedData: MatchData, session:Session): Boolean {
        if (!getData().equals(updatedData)) {

            allData.add(updatedData)
            matchTimer = updatedData.timer

            health.p1 = keepInRange(getData().health.first)//, 0, 420)
            tension.p1 = keepInRange(getData().tension.first)//, 0, 10000)
            risc.p1 = keepInRange(getData().risc.first)//, 0, 12800)
            rounds.p1 = updatedData.rounds.first
            burst.p1 = getData().burst.first
            isHit.p1 = getData().isHit.first

            health.p2 = keepInRange(getData().health.second)//, 0, 420)
            tension.p2 = keepInRange(getData().tension.second)//, 0, 10000)
            risc.p2 = keepInRange(getData().risc.second)//, 0, 12800)
            rounds.p2 = updatedData.rounds.second
            burst.p2 = getData().burst.second
            isHit.p2 = getData().isHit.second

            // Has the round started?
            if (roundStarted == false && getHealth(P1) == 420 && getHealth(P2) == 420 && getWinner() == -1) {
                roundStarted = true
                session.log("M[$matchId]: Round Start - DUEL ${getRounds(P1) + getRounds(P2) + 1}, LET'S ROCK! ... ${lobbyData.roundWins} rounds to win")
                session.setMode(session.MATCH_MODE)
            }

            // Has the round ended, and did player 1 win?
            if (roundStarted && winner==-1 && health.p2 == 0 && health.p1 > 0) {
                roundStarted = false
                session.log("M[$matchId]: Round Completed - Player 1 wins the round ... (${players.p1.displayName}) needs ${getRounds(P2)}/${lobbyData.roundWins} rounds to win")
                session.setMode(session.SLASH_MODE)
            }

            // Has the round ended, and did player 2 win?
            if (roundStarted && winner==-1 && getHealth(P1) == 0 && getHealth(P2) > 0) {
                roundStarted = false
                session.log("M[$matchId]: Round Completed - Player 2 wins the round ... (${players.p2.displayName}) needs ${getRounds(P2)}/${lobbyData.roundWins} rounds to win")
                session.setMode(session.SLASH_MODE)
            }

            // Did player 1 win the match?
            if (getRounds(P1) == lobbyData.roundWins && winner == -1) {
                winner = 0
                session.log("M[$matchId]: Match CONCLUSION - Player 1 has taken the match ... (${getHandleString(P1)})")
                session.setMode(session.VICTORY_MODE)
            }

            // Did player 2 win the match?
            if (getRounds(P2) == lobbyData.roundWins && winner == -1) {
                winner = 1
                session.log("M[$matchId]: Match CONCLUSION - Player 2 has taken the match ... (${getHandleString(P2)})")
                session.setMode(session.VICTORY_MODE)
            }

            return true

        } else return false
    }

    fun getWinner():Int = winner
    fun getTimer():Int = matchTimer
    fun getRounds(side:Int):Int = rounds.p(side) as Int
    fun getHealth(side:Int):Int = health.p(side) as Int
    fun getCharacter(side:Int):Int = character.p(side) as Int
    fun getTension(side:Int):Int = tension.p(side) as Int
    fun getRisc(side:Int):Int = risc.p(side) as Int
    fun getBurst(side:Int):Boolean = burst.p(side) as Boolean
    fun getHitStun(side:Int):Boolean = isHit.p(side) as Boolean

    fun getHandleString(side:Int):String = handle.p(side) as String
    fun getHealthString(side:Int):String = "HP: ${getHealth(side)} / 420"
    fun getRoundsString(side:Int):String = "Rounds: ${getRounds(side)} / ${lobbyData.roundWins}"
    fun getTensionString(side:Int):String = "Tension: ${getTension(side)} / 10000"
    fun getRiscString(side:Int):String = "   RISC: ${getRisc(side)} / 12800"
    fun getBurstString(side:Int):String = "  Burst: ${getBurst(side)}"
    fun getHitStunString(side:Int):String = "  IsHit: ${getHitStun(side)}"

    fun getCabinet():Byte = cabinetId
    fun getCabinetString(cabId:Int = getCabinet().toInt()): String {
        when(cabId) {
            0 -> return "CABINET A (Snaps ${allData.size})"
            1 -> return "CABINET B (Snaps ${allData.size})"
            2 -> return "CABINET C (Snaps ${allData.size})"
            3 -> return "CABINET D (Snaps ${allData.size})"
            else -> return "CABINET $cabId (Snaps ${allData.size})"
        }
    }
}