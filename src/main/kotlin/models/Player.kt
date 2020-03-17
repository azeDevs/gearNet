package models

import javafx.geometry.Rectangle2D
import session.Character
import session.Session
import session.log
import utils.addCommas
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class Player(private val playerId:Long = -1, private val userName:String = "", private var characterId:Byte = -0x1) {

    companion object {
        const val PLAYER1 = 0
        const val PLAYER2 = 1
    }

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Identification (Name/ID)
     */
    fun isValid() = playerId > 0
    fun getPlayerId() = this.playerId
    fun getIdString(id:Long) = if (id.toString().length > 8) "ID${id.toString().substring(id.toString().length-8, id.toString().length)}" else "ID${id}"
    fun getUserName() = this.userName
    fun getCharacterId() = this.characterId
    fun setCharacterId(id:Byte) { characterId = id }
    fun getCharacterName() = Character.getCharacterName(this.characterId)


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
                log("P: ${getIdString(playerId)} is idle ... Standby reset to $bystanding and chain reduced by 1 ($userName)")
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
        var rating = if (side == PLAYER1) Rectangle2D(832.0, 704.0, 256.0, 64.0) else Rectangle2D(1088.0, 704.0, 256.0, 64.0)
        when(getRating()) {
            8 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 0.0, 256.0, 64.0) else Rectangle2D(384.0, 0.0, 256.0, 64.0)
            7 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 64.0, 256.0, 64.0) else Rectangle2D(384.0, 64.0, 256.0, 64.0)
            6 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 128.0, 256.0, 64.0) else Rectangle2D(384.0, 128.0, 256.0, 64.0)
            5 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 196.0, 256.0, 64.0) else Rectangle2D(384.0, 196.0, 256.0, 64.0)
            4 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 256.0, 256.0, 64.0) else Rectangle2D(384.0, 256.0, 256.0, 64.0)
            3 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 320.0, 256.0, 64.0) else Rectangle2D(384.0, 320.0, 256.0, 64.0)
            2 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 384.0, 256.0, 64.0) else Rectangle2D(384.0, 384.0, 256.0, 64.0)
            1 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 448.0, 256.0, 64.0) else Rectangle2D(384.0, 448.0, 256.0, 64.0)
            0 -> rating = if (side == PLAYER1) Rectangle2D(832.0, 704.0, 256.0, 64.0) else Rectangle2D(1088.0, 704.0, 256.0, 64.0)
            -1 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 512.0, 256.0, 64.0) else Rectangle2D(384.0, 512.0, 256.0, 64.0)
            -2 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 576.0, 256.0, 64.0) else Rectangle2D(384.0, 576.0, 256.0, 64.0)
            -3 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 640.0, 256.0, 64.0) else Rectangle2D(384.0, 640.0, 256.0, 64.0)
            -4 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 704.0, 256.0, 64.0) else Rectangle2D(384.0, 740.0, 256.0, 64.0)
            -5 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 768.0, 256.0, 64.0) else Rectangle2D(384.0, 768.0, 256.0, 64.0)
            -6 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 832.0, 256.0, 64.0) else Rectangle2D(384.0, 832.0, 256.0, 64.0)
            -7 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 896.0, 256.0, 64.0) else Rectangle2D(384.0, 896.0, 256.0, 64.0)
            -8 -> rating = if (side == PLAYER1) Rectangle2D(128.0, 960.0, 256.0, 64.0) else Rectangle2D(384.0, 960.0, 256.0, 64.0)
        }
        return rating
    }


    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Score (W$)
     */
    private var scoreTotal = 0 //Random.nextInt(0, 9999999)
    private var scoreDelta = 0 //Random.nextInt(-99999, 99999)

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
    private var signs = 0
    private var munity = 0
    private var respect = 0
    private var atension = 0

    fun getSigns() = this.signs
    fun setSigns(value:Int) { signs = value }
    fun addSigns(amount:Int) { signs += amount }

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

}