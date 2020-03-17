package models

import javafx.geometry.Rectangle2D
import memscan.FighterData
import session.Character
import session.Session
import twitch.WatcherData
import utils.addCommas
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class Player(
    private val playerId:Long = -1,
    private val userName:String = "",
    private var teamSeatId:Int = -1,
    private var characterId:Byte = -0x1,
    private var data:Pair<FighterData, FighterData> = Pair(FighterData(), FighterData())
) {
    constructor(fighterData: FighterData) : this(fighterData.steamUserId, fighterData.displayName, fighterData.playerSide.toInt(), fighterData.characterId, Pair(fighterData, fighterData))
    constructor(watcherData: WatcherData) : this(watcherData.twitchId, watcherData.displayName)

    fun oldData() = data.first
    fun getData() = data.second
    fun updatePlayerData(updatedData: FighterData, playersActive: Int) {
        data = Pair(getData(), updatedData)
        if (isLoading()) setBystanding(playersActive)
        setMatchesWon(updatedData.matchesWon)
        setMatchesSum(updatedData.matchesSum)
        setCharacterId(updatedData.characterId)
    }

    companion object {
        const val PLAYER_1 = 0
        const val PLAYER_2 = 1
        const val PROSPECT = 2

        const val MAX_MUNITY = 16
        const val MAX_RESPECT = 160
        const val MAX_ATENSION = 1600
    }

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Identification (Name/ID)
     */
    fun isValid() = playerId > 0
    fun isWatcher() = getCharacterId() == Character.NULL
    fun getPlayerId() = this.playerId
    fun getIdString(id:Long) = if (id.toString().length > 8) "ID${id.toString().substring(id.toString().length-8, id.toString().length)}" else "ID${id}"
    fun getUserName() = this.userName
    fun getCharacterId() = this.characterId
    fun setCharacterId(id:Byte) { characterId = id }
    fun getCharacterString() = Character.getCharacterInitials(getCharacterId())

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Location (Cab/Seat/Team)
     */
    fun resetTeam() { teamSeatId = -1 }
    fun setTeamR() { teamSeatId = 0 }
    fun setTeamB() { teamSeatId = 1 }
    fun isTeamR() = teamSeatId == 0
    fun isTeamB() = teamSeatId == 1
    fun isOnPlaySide(sideId:Int = -1) = if(getPlaySide() in 0..1) getData().playerSide.toInt() == sideId else getPlaySide() in 0..1
    fun getPlaySide() = getData().playerSide.toInt()
    fun getPlaySideString(cabId:Int = getCabinet(), sideId:Int = getPlaySide()): String = if (cabId > 3) "Wandering"
    else when(sideId) {
        0 -> "Red"
        1 -> "Blue"
        2 -> "Prospect"
        3 -> "3rd"
        4 -> "4th"
        5 -> "5th"
        6 -> "6th"
        7 -> "Spectating"
        else -> "[${getPlaySide()}]"
    }
    fun isOnCabinet(cabId:Int = -1) = if(cabId in 0..3) getData().cabinetLoc.toInt() == cabId else cabId in 0..3
    fun getCabinet() = getData().cabinetLoc.toInt()
    fun getCabinetString(cabId:Int = getCabinet()): String = when(cabId) {
        0 -> "A"
        1 -> "B"
        2 -> "C"
        3 -> "D"
        else -> "F"
    }

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Bystanding (Standby/Absent)
     */
    private var bystanding = 1

    fun isAbsent() = bystanding <= 0
    fun getBystanding() = this.bystanding
    fun setBystanding(playersActive: Int) { bystanding = max(1,playersActive) }
    fun getBystandingString() = if (bystanding == 0) "Absent" else "Bystanding: $bystanding"
    fun incrementBystanding(s: Session) {
        changeScore(0)
        if (--bystanding <= 0) {
            if (changeRating(-1) <= 0) {
                bystanding = 0
            } else {
                bystanding = max(1,s.getActivePlayerCount())
                println("P: ${getIdString(playerId)} is idle ... Standby reset to $bystanding and chain reduced by 1 ($userName)")
            }
        }
    }


    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Match Record (Wins/Loses)
     */
    private var matchesWon = 0
    private var matchesSum = 0

    fun getMatchesWon() = this.matchesWon
    fun getMatchesSum() = this.matchesSum
    fun setMatchesWon(value:Int) { matchesWon = value }
    fun setMatchesSum(value:Int) { matchesSum = value }
    fun addMatchLost() { matchesSum++ }
    fun addMatchWon() { matchesWon++; matchesSum++ }
    fun getRecordString() = "W:$matchesWon  /  M:$matchesSum"


    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Rating (Risk/Fury)
     */
    private var riskRating = 0 //Random.nextInt(0, 8)

    fun getRating() = this.riskRating
    fun getRatingString():String = if (getRating()>=8) "★" else if (getRating()>0) getRating().toString() else ""
    fun changeRating(amount:Int): Int {
        riskRating += amount
        if (riskRating < 0) riskRating = 0
        if (riskRating > 8) riskRating = 8
        return riskRating
    }
    fun getRatingImage(side:Int = -1): Rectangle2D {
        var rating = if (side == PLAYER_1) Rectangle2D(832.0, 704.0, 256.0, 64.0) else Rectangle2D(1088.0, 704.0, 256.0, 64.0)
        when(getRating()) {
            8 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 0.0, 256.0, 64.0) else Rectangle2D(384.0, 0.0, 256.0, 64.0)
            7 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 64.0, 256.0, 64.0) else Rectangle2D(384.0, 64.0, 256.0, 64.0)
            6 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 128.0, 256.0, 64.0) else Rectangle2D(384.0, 128.0, 256.0, 64.0)
            5 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 196.0, 256.0, 64.0) else Rectangle2D(384.0, 196.0, 256.0, 64.0)
            4 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 256.0, 256.0, 64.0) else Rectangle2D(384.0, 256.0, 256.0, 64.0)
            3 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 320.0, 256.0, 64.0) else Rectangle2D(384.0, 320.0, 256.0, 64.0)
            2 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 384.0, 256.0, 64.0) else Rectangle2D(384.0, 384.0, 256.0, 64.0)
            1 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 448.0, 256.0, 64.0) else Rectangle2D(384.0, 448.0, 256.0, 64.0)
            0 -> rating = if (side == PLAYER_1) Rectangle2D(832.0, 704.0, 256.0, 64.0) else Rectangle2D(1088.0, 704.0, 256.0, 64.0)
            -1 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 512.0, 256.0, 64.0) else Rectangle2D(384.0, 512.0, 256.0, 64.0)
            -2 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 576.0, 256.0, 64.0) else Rectangle2D(384.0, 576.0, 256.0, 64.0)
            -3 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 640.0, 256.0, 64.0) else Rectangle2D(384.0, 640.0, 256.0, 64.0)
            -4 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 704.0, 256.0, 64.0) else Rectangle2D(384.0, 740.0, 256.0, 64.0)
            -5 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 768.0, 256.0, 64.0) else Rectangle2D(384.0, 768.0, 256.0, 64.0)
            -6 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 832.0, 256.0, 64.0) else Rectangle2D(384.0, 832.0, 256.0, 64.0)
            -7 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 896.0, 256.0, 64.0) else Rectangle2D(384.0, 896.0, 256.0, 64.0)
            -8 -> rating = if (side == PLAYER_1) Rectangle2D(128.0, 960.0, 256.0, 64.0) else Rectangle2D(384.0, 960.0, 256.0, 64.0)
        }
        return rating
    }


    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Score (W$)
     */
    private var signsTotal = 0
    private var scoreTotal = 0 //Random.nextInt(0, 9999999)
    private var scoreDelta = 0 //Random.nextInt(-99999, 99999)

    fun getSigns() = this.signsTotal
    fun setSigns(value:Int) { signsTotal = value }
    fun addSigns(amount:Int) { signsTotal += amount }
    fun getScoreTotal() = this.scoreTotal
    fun getScoreDelta() = this.scoreDelta
    fun changeScore(amount:Int) {
        scoreDelta = amount
        scoreTotal += amount
        if (scoreTotal < 10) scoreTotal = 0
    }
    fun getScoreTotalString() = if (scoreTotal > 0) "${addCommas("$scoreTotal")} W$" else "FREE"
    fun getScoreDeltaString(ramp:Float = 1f, change:Int = scoreDelta) = when {
        change > 0 -> "+${addCommas(min(change*ramp, change.toFloat()).toInt().toString())} W$"
        change < 0 -> "-${addCommas(abs(max(change*ramp, change.toFloat()).toInt()).toString())} W$"
        isAbsent() -> "Absent"
        else -> when(bystanding) {
            1 -> "α"
            2 -> "β"
            3 -> "γ"
            4 -> "δ"
            5 -> "ε"
            6 -> "ζ"
            7 -> "η"
            8 -> "θ"
            else -> "?"
        }
    }


    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Atension Stats
     */

    private var munity = 0
    private var respect = 0
    private var atension = 0

    fun getMunity() = this.munity
    fun setMunity(value:Int) { munity = value }
    fun addMunity(amount:Int) { munity += amount }

    fun getRespect() = this.respect
    fun setRespect(value:Int) { respect = value }
    fun addRespect(amount:Int) { respect += amount }

    fun getAtension() = this.atension
    fun setAtension(value:Int) { atension = value }
    fun addAtension(amount:Int) { atension += amount }


    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Boss Status
     */
    fun getStatusFloat(): Float = if (getMatchesSum() > 0) ((((getMatchesWon().toFloat() * 0.1)
            * getRating()) + getMatchesWon()) / (getMatchesSum().toFloat())).toFloat() else 0F

    fun getStatusImage(matchesWon:Int = getMatchesWon(), rating:Float = getStatusFloat()): Rectangle2D {
        var grade = Rectangle2D(0.0, 0.0, 128.0, 64.0)                                        // STEEL  DEFAULT
        if (matchesWon >= 0 && rating > 0.0f) grade = Rectangle2D(0.0, 0.0, 128.0, 64.0)      // STEEL  D
        if (matchesWon >= 1 && rating >= 0.1f) grade = Rectangle2D(0.0, 64.0, 128.0, 64.0)    // STEEL  D+
        if (matchesWon >= 1 && rating >= 0.2f) grade = Rectangle2D(0.0, 128.0, 128.0, 64.0)   // BRONZE C
        if (matchesWon >= 2 && rating >= 0.3f) grade = Rectangle2D(0.0, 192.0, 128.0, 64.0)   // BRONZE C+
        if (matchesWon >= 3 && rating >= 0.4f) grade = Rectangle2D(0.0, 256.0, 128.0, 64.0)   // SILVER B
        if (matchesWon >= 5 && rating >= 0.6f) grade = Rectangle2D(0.0, 320.0, 128.0, 64.0)   // SILVER B+
        if (matchesWon >= 8 && rating >= 0.9f) grade = Rectangle2D(0.0, 384.0, 128.0, 64.0)   // GOLD   A
        if (matchesWon >= 13 && rating >= 1.2f) grade = Rectangle2D(0.0, 448.0, 128.0, 64.0)  // GOLD   A+
        if (matchesWon >= 21 && rating >= 1.4f) grade = Rectangle2D(0.0, 512.0, 128.0, 64.0)  // RED    S
        if (matchesWon >= 34 && rating >= 1.5f) grade = Rectangle2D(0.0, 576.0, 128.0, 64.0)  // RED    S+
        return grade
    }

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Match Status
     */
    fun getLoadPercent() = getData().loadingPct
    private fun isLoading() = getData().loadingPct in 1..99

    fun hasPlayed() = getData().matchesSum > oldData().matchesSum
    fun isLoser() = getData().matchesWon == oldData().matchesWon && hasPlayed()
    fun isWinner() = getData().matchesWon > oldData().matchesWon && hasPlayed()

    fun getDebugDataString(mask: Int = -1) = when {
        !isValid() -> "-"
        mask == 0 -> getUserName()
        mask == 1 -> "${getCabinetString()+getPlaySide()}[${getLoadPercent()}] ${getUserName()}"
        mask == 2 -> "${getCabinetString()+getPlaySide()}[${getLoadPercent()}] ${getUserName()} (${getCharacterString()})"
        else -> "="
    }

}