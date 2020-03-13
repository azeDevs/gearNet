package models

import javafx.geometry.Rectangle2D
import session.Character
import session.Session
import session.log
import utils.addCommas
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class Player(private val id:Long = -1, private val name:String = "", private var characterId:Byte = -0x1) {

    private var scoreTotal = 0
    private var scoreDelta = 0
    private var bystanding = 1
    private var atension = 0
    private var rating = 0
    private var matchesWon = 0
    private var matchesSum = 0

    fun getId() = this.id
    fun getIdString(id:Long) = if (id.toString().length > 8) "ID${id.toString().substring(id.toString().length-8, id.toString().length)}" else "ID${id}"
    fun getName() = this.name
    fun isValid() = id > 0

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
                log("P: ${getIdString(id)} is idle ... Standby reset to $bystanding and chain reduced by 1 ($name)")
            }
        }
    }

    fun getAtension() = this.atension
    fun changeAtension(amount:Int) { atension += amount }

    fun getRating() = this.rating
    fun getRatingString():String = if (getRating()>=8) "★" else if (getRating()>0) getRating().toString() else ""
    fun changeRating(amount:Int): Int {
        rating += amount
        if (rating < 0) rating = 0
        if (rating > 8) rating = 8
        return rating
    }

    fun getMatchesWon() = this.matchesWon
    fun getMatchesSum() = this.matchesSum
    fun setMatchesWon(value:Int) { matchesWon = value }
    fun setMatchesSum(value:Int) { matchesSum = value }
    fun addMatchLost() { matchesSum++ }
    fun addMatchWon() { matchesWon++; matchesSum++ }

    fun getCharacterId() = this.characterId
    fun getCharacterName() = Character.getCharacterName(this.characterId)

    fun getRecordString() = "W:$matchesWon  /  M:$matchesSum"
    fun getStatusFloat():Float {
        return if (getMatchesSum() > 0) ((((getMatchesWon().toFloat() * 0.1) * getRating()) + getMatchesWon()) / (getMatchesSum().toFloat())).toFloat()
        else 0F
    }

    fun getStatusImage(matchesWon:Int = getMatchesWon(), rating:Float = getStatusFloat()): Rectangle2D {
        var grade = Rectangle2D(0.0, 640.0, 128.0, 64.0)
        if (matchesWon >= 1 && rating > 0.0f) grade  = Rectangle2D(0.0, 0.0, 128.0, 64.0)  // D
        if (matchesWon >= 1 && rating >= 0.1f) grade = Rectangle2D(0.0, 64.0, 128.0, 64.0)  // D+
        if (matchesWon >= 2 && rating >= 0.2f) grade = Rectangle2D(0.0, 128.0, 128.0, 64.0)  // C
        if (matchesWon >= 3 && rating >= 0.3f) grade  = Rectangle2D(0.0, 192.0, 128.0, 64.0)  // C+
        if (matchesWon >= 5 && rating >= 0.4f) grade  = Rectangle2D(0.0, 256.0, 128.0, 64.0)  // B
        if (matchesWon >= 8 && rating >= 0.6f) grade  = Rectangle2D(0.0, 320.0, 128.0, 64.0)  // B+
        if (matchesWon >= 13 && rating >= 1.0f) grade = Rectangle2D(0.0, 384.0, 128.0, 64.0)  // A
        if (matchesWon >= 21 && rating >= 1.2f) grade = Rectangle2D(0.0, 448.0, 128.0, 64.0)  // A+
        if (matchesWon >= 34 && rating >= 1.4f) grade = Rectangle2D(0.0, 512.0, 128.0, 64.0)  // S
        if (matchesWon >= 55 && rating >= 1.6f) grade = Rectangle2D(0.0, 576.0, 128.0, 64.0)  // S+
        return grade
    }

    fun getRatingImage(rating:Int = getRating()): Rectangle2D {
        var grade = Rectangle2D(256.0, 448.0, 64.0, 64.0)
        if (rating == 1) grade = Rectangle2D(128.0, 0.0, 64.0, 64.0)
        if (rating == 2) grade = Rectangle2D(128.0, 64.0, 64.0, 64.0)
        if (rating == 3) grade = Rectangle2D(128.0, 128.0, 64.0, 64.0)
        if (rating == 4) grade = Rectangle2D(128.0, 192.0, 64.0, 64.0)
        if (rating == 5) grade = Rectangle2D(128.0, 256.0, 64.0, 64.0)
        if (rating == 6) grade = Rectangle2D(128.0, 320.0, 64.0, 64.0)
        if (rating == 7) grade = Rectangle2D(128.0, 384.0, 64.0, 64.0)
        if (rating == 8) grade = Rectangle2D(128.0, 448.0, 64.0, 64.0)
        return grade
    }

}