package models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Rectangle2D
import memscan.FighterData
import memscan.MatchData
import session.Character
import twitch.WatcherData
import utils.addCommas
import kotlin.math.max

class Player(
    private val playerId:Long = -1,
    private val userName:String = "",
    private var fighterData:Pair<FighterData, FighterData> = Pair(FighterData(), FighterData())
) {
    constructor(fighterData: FighterData) : this(fighterData.steamUserId, fighterData.displayName, Pair(fighterData, fighterData))
    constructor(watcherData: WatcherData) : this(watcherData.twitchId, watcherData.displayName)

    val playerObjectProperty = SimpleObjectProperty(this)
    val isValidProperty = SimpleBooleanProperty(this, "isPlayerValid", isValid())
    val nameProperty = SimpleStringProperty(this, "name", getUserName())
    val scoreTotalProperty = SimpleStringProperty(this, "scoreTotal", getScoreTotalString())
//    val scoreDeltaProperty = SimpleStringProperty(this, "scoreDelta", getScoreDeltaString())
//    val characterIdProperty = SimpleIntegerProperty(this, "characterId", getCharacterId().toInt())
//    val playSideIdProperty = SimpleIntegerProperty(this, "playSideId", getPlaySide())
//    val isWatcherProperty = SimpleBooleanProperty(this, "isWatcher", isWatcher())
//    val isBeingDamagedProperty = SimpleBooleanProperty(this, "isBeingDamaged", isBeingDamaged())


    fun oldFighterData() = fighterData.first
    fun getFighterData() = fighterData.second
    fun updateFighterData(updatedData: FighterData, playersActive: Int) {
        fighterData = Pair(getFighterData(), updatedData)
        if (isLoading()) setBystanding(playersActive)
        setMatchesWon(updatedData.matchesWon)
        setMatchesSum(updatedData.matchesSum)
        if (isBeingDamaged()) when(getPlaySide()) {
            PLAYER_1 -> println("PLAYER_1 DAMAGED!")
            PLAYER_2 -> println("PLAYER_2 DAMAGED!")
        }
    }

    private var matchData:Pair<MatchData, MatchData> = Pair(MatchData(), MatchData())
    fun isStaged() = isOnPlaySide(PLAYER_1) || isOnPlaySide(PLAYER_2)
    fun oldMatchData() = matchData.first
    fun getMatchData() = matchData.second
    fun updateMatchData(updatedData: MatchData) { matchData = Pair(getMatchData(), updatedData) }

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
    fun getCharacterId() = getFighterData().characterId
    fun getCharacterString() = Character.getCharacterInitials(getCharacterId())

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Location (Cab/Seat/Team)
     */
    private var teamR = false
    private var teamB = false
    fun setTeam(sideId:Int = -1) = when (sideId) {
        PLAYER_1 -> { teamR = true }
        PLAYER_2 -> { teamB = true }
        else -> { teamR = false; teamB = false }
    }
    fun isTeam(sideId:Int) = (sideId == PLAYER_1 && teamR) || (sideId == PLAYER_2 && teamB)
    fun getTeamString() = if(teamR && teamB) "X"
        else if(teamR && !teamB) "R"
        else if(!teamR && teamB) "B"
        else "-"

    fun isOnPlaySide(sideId:Int = -1) = if(getPlaySide() in 0..1) getFighterData().playerSide.toInt() == sideId else getPlaySide() in 0..1
    fun getPlaySide() = getFighterData().playerSide.toInt()
    fun getPlaySideString(): String = if (getCabinet() > 3) "-"
    else when(getPlaySide()) {
        0 -> "R"
        1 -> "B"
        2 -> "P"
        3 -> "3"
        4 -> "4"
        5 -> "5"
        6 -> "6"
        7 -> "S"
        else -> getPlaySide().toString()
    }
    fun isOnCabinet(cabId:Int = -1) = if(isWatcher()) true else if(cabId in 0..3) getCabinet() == cabId else getCabinet() in 0..3
    fun getCabinet() = getFighterData().cabinetLoc.toInt()
    fun getCabinetString(cabId:Int = getCabinet()): String = when(cabId) {
        0 -> "A"
        1 -> "B"
        2 -> "C"
        3 -> "D"
        else -> "-"
    }

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Bystanding (Standby/Absent)
     */
    private var bystanding = 1

    fun isAbsent() = bystanding <= 0
    fun getBystanding() = this.bystanding
    fun setBystanding(playersActive: Int) { bystanding = max(1,playersActive) }
    fun getBystandingString() = when(bystanding) {
        1 -> "α"
        2 -> "β"
        3 -> "γ"
        4 -> "δ"
        5 -> "ε"
        6 -> "ζ"
        7 -> "η"
        8 -> "θ"
        else -> "Absent"
    }
    fun incrementBystanding(activePlayerCount:Int) {
        changeScore(0)
        if (--bystanding <= 0) {
            if (changeRating(-1) <= 0) {
                bystanding = 0
            } else {
                bystanding = max(1,activePlayerCount)
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
    fun getRecordString() = "W:$matchesWon / M:$matchesSum"


    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Rating (Risk/Fury)
     */
    private var riskRating = 0 //Random.nextInt(0, 8)

    fun getRating() = this.riskRating
    fun changeRating(amount:Int): Int {
        riskRating += amount
        if (riskRating < 0) riskRating = 0
        if (riskRating > 8) riskRating = 8
        return riskRating
    }
    fun getRatingString():String = when(getRating()) {
         8 -> "Risk S+"
         7 -> "Risk S"
         6 -> "Risk A+"
         5 -> "Risk A"
         4 -> "Risk B+"
         3 -> "Risk B"
         2 -> "Risk C+"
         1 -> "Risk C"
         0 -> "DareDevil"
        -1 -> "Fury C"
        -2 -> "Fury C+"
        -3 -> "Fury B"
        -4 -> "Fury B+"
        -5 -> "Fury A"
        -6 -> "Fury A+"
        -7 -> "Fury S"
        -8 -> "Fury S+"
        else -> "-"
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
    fun addSigns(amount:Int) { signsTotal += amount }
    fun getScoreTotal() = this.scoreTotal
    fun getScoreDelta() = this.scoreDelta
    fun changeScore(amount:Int) {
        scoreDelta = if (scoreTotal + amount < 0) -scoreTotal else amount
        scoreTotal += amount
        if (scoreTotal < 10) scoreTotal = 0
    }
    fun getScoreTotalString() = if (scoreTotal > 0) "${addCommas("$scoreTotal")} W$" else "FREE"
    fun getScoreDeltaString(change:Int = scoreDelta) = when {
        change > 0 -> "+${addCommas(change.toString())}"
        change < 0 -> "-${addCommas(change.toString())}"
        else -> ""
    }


    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Atension Stats
     */

    private var munity = 0
    private var respect = 0
    private var atension = 0

    fun getMunity() = this.munity
    fun setMunity(value:Int) { munity = value }

    fun getRespect() = this.respect
    fun setRespect(value:Int) { respect = value }
    fun addRespect(amount:Int) { respect += amount }

    fun getAtension() = this.atension
    fun setAtension(value:Int) { atension = value }
    fun addAtension(amount:Int) { atension += amount }

    fun getAtensionString() = if(getAtension()!=0) "A:${getAtension()}/" else "" +
            if(getRespect()!=0) "R:${getRespect()}/" else "" +
            if(getMunity()!=0) "M:${getMunity()}/" else "" +
            if(getScoreTotal()!=0) getScoreTotalString() else "" + if(getScoreDelta()!=0)" (${getScoreDeltaString()})" else ""

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Player Boss Status
     */
    fun getStatusFloat(): Float = if (getMatchesSum() > 0) ((((getMatchesWon().toFloat() * 0.1) * getRating()) + getMatchesWon()) / (getMatchesSum().toFloat())).toFloat() else 0F

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
    val timer: Int = -1,
    val health: Pair<Int, Int> = Pair(-1,-1),
    val rounds: Pair<Int, Int> = Pair(-1,-1),
    val tension: Pair<Int, Int> = Pair(-1,-1),
    val stunProgress: Pair<Int, Int> = Pair(-1,-1),
    val stunMaximum: Pair<Int, Int> = Pair(-1,-1),
    val canBurst: Pair<Boolean, Boolean> = Pair(first = false, second = false),
    val strikeStun: Pair<Boolean, Boolean> = Pair(first = false, second = false),
    val guardGauge: Pair<Int, Int> = Pair(-1,-1)
     */

    fun isInMatch():Boolean = getMatchData().isValid()
    fun isBeingDamaged():Boolean = if(getPlaySide() == PLAYER_1) oldMatchData().health.first != getMatchData().health.first else oldMatchData().health.second != getMatchData().health.second

    fun getRounds():Int = if(getPlaySide() == PLAYER_1) getMatchData().rounds.first else getMatchData().rounds.second
    fun getHealth():Int = if(getPlaySide() == PLAYER_1) getMatchData().health.first else getMatchData().health.second
    fun getStunProgress():Int = if(getPlaySide() == PLAYER_1) getMatchData().stunProgress.first else getMatchData().stunProgress.second
    fun getMaxStun():Int = if(getPlaySide() == PLAYER_1) getMatchData().stunMaximum.first else getMatchData().stunMaximum.second
    fun getTension():Int = if(getPlaySide() == PLAYER_1) getMatchData().tension.first else getMatchData().tension.second
    fun getRisc():Int = if(getPlaySide() == PLAYER_1) getMatchData().guardGauge.first else getMatchData().guardGauge.second
    fun getBurst():Boolean = if(getPlaySide() == PLAYER_1) getMatchData().canBurst.first else getMatchData().canBurst.second
    fun getStrikeStun():Boolean = if(getPlaySide() == PLAYER_1) getMatchData().strikeStun.first else getMatchData().strikeStun.second

    fun getRoundsString():String = "Rounds: ${getRounds()} / 2"
    fun getHealthString():String = "Health: ${getHealth()} / 420 ${if(isBeingDamaged()) "!" else ""}"
    fun getStunString():String = "Stun: ${getStunProgress()} / ${getMaxStun()}"
    fun getTensionString():String = "Tension: ${getTension()} / 10000"
    fun getRiscString():String = "RISC: ${getRisc()} / 12800"
    fun getBurstString():String = "Burst: ${if(getBurst()) "O" else "X"}"
    fun getStrikeStunString():String = "Struck: ${if(getStrikeStun()) "O" else "X"}"

    private fun getLoadPercentString() = if(isLoading()) "${getLoadPercent()}%" else if(isInMatch()) "►" else ""
    private fun getLoadPercent() = getFighterData().loadingPct
    private fun isLoading() = getLoadPercent() in 1..99

    fun hasPlayed() = getFighterData().matchesSum > oldFighterData().matchesSum
    fun isLoser() = getFighterData().matchesWon == oldFighterData().matchesWon && hasPlayed()
    fun isWinner() = getFighterData().matchesWon > oldFighterData().matchesWon && hasPlayed()

    /** ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ **
     *  Debugging Mode
     */
    fun getDebugDataString(mask: Int = -1) = when {
        !isValid() -> "-"
        mask == 0 -> getUserName()
        mask == 1 -> "[${getPlaySideString()}] ${getUserName()} ${getLoadPercentString()}"
        mask == 2 -> "[${getPlaySideString()}] ${getUserName()} (${getCharacterString()}) ${getLoadPercentString()}"
        mask == 3 -> "[${getCabinetString()+getPlaySideString()}] ${getUserName()} ${getLoadPercentString()}"
        mask == 4 -> "[${getCabinetString()+getPlaySideString()}] ${getUserName()} (${getCharacterString()}) ${getLoadPercentString()}"
        else -> "="
    }

}