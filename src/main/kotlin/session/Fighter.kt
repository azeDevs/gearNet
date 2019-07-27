package session

import javafx.geometry.Rectangle2D
import memscan.FighterData
import utils.XrdChar.getCharacterName
import utils.addCommas
import utils.getIdString
import utils.log
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 *
 * XrdHandler                  updates and archives Lobby getData().
 *  ┗━ Duo<Lobby>               contains past and present Lobby data
 *      ┗━ List<Cabinet>        contains Match and Players seating data
 *          ┣━ Match            contains fighting Players and Match data
 *          ┗━ List<Fighter>     contains Fighter bounty and chains data
 *
 * [Fighter]
 * contains Fighter bounty and chains data
 *
 */
class Fighter(oldData: FighterData = FighterData(), newData: FighterData = oldData) : PlayerData<FighterData>(oldData, newData, newData.displayName, newData.steamId) {

    var present = true

    private var bounty = 0
    private var change = 0
    private var chain = 0
    private var idle = 1

    fun getCharacterId() = getData().characterId

    fun getCharacterName() = getCharacterName(getData().characterId)

    fun getIdle() = idle

    fun isIdle() = getIdle() <= 0

    fun incrementIdle(s: Session) {
        changeBounty(0)
        if (--idle <= 0) {
            if (changeChain(-1) <= 0) {
                present = false
                idle = 0
            } else {
//                idle = max(1,s.getActivePlayerCount())
                log("P: ${getIdString(getId())} is idle ... Standby reset to ${idle} and chain reduced by 1 (${getData().displayName})")
            }
        }
    }

    fun getBounty() = bounty

    fun getBountyFormatted(ramp:Float = 1f) = if (getBounty() > 0) "${addCommas(min(getBounty()-getChange()+(getChange()*ramp).toInt(), getBounty()).toString())} W$"
    else "${addCommas(max(getBounty()-getChange()+(getChange()*ramp).toInt(), getBounty()).toString())} W$"

    fun getBountyString(ramp:Float = 1f) = if (getBounty() > 0) getBountyFormatted(if (change!=0) ramp else 1f) else "FREE"

    fun getRecordString() = "W:${getMatchesWon()}  /  M:${getMatchesPlayed()}"

    fun changeBounty(amount:Int) {
        change = amount
        bounty += amount
        if (bounty < 10) bounty = 0
    }

    fun getChain(modify:Int = 0) = chain + modify

    fun getChainString():String = if (getChain()>=8) "★" else if (getChain()>0) getChain().toString() else ""

    fun changeChain(amount:Int): Int {
        chain += amount
        if (chain < 0) chain = 0
        if (chain > 8) chain = 8
        return chain
    }

    fun getChange() = change

    fun hasId(fighter:Fighter) = getId() == fighter.getId()

    fun getChangeString(ramp:Float = 1f, change:Int = this.change): String {
        if (change > 0) return "+${addCommas(min(change*ramp, change.toFloat()).toInt().toString())} W$"
        else if (change < 0) return "-${addCommas(abs(max(change*ramp, change.toFloat()).toInt()).toString())} W$"
        else return ""
    }

    fun getMatchesWon() = getData().matchesWon

    fun getMatchesPlayed() = getData().matchesSum

    fun getCabinet() = getData().cabinetId

    fun getCabinetString(cabId:Int = getCabinet()): String {
        when(cabId) {
            0 -> return "Cabinet A"
            1 -> return "Cabinet B"
            2 -> return "Cabinet C"
            3 -> return "Cabinet D"
            else -> return ""
        }
    }

    fun isSeated(seatId:Int) = getData().seatingId == seatId
    fun getSeat() = getData().seatingId

    fun getSeatString(cabId:Int = getCabinet(), sideId:Int = getSeat()): String {
        if (cabId > 3) return ""
        when(sideId) {
            0 -> return "Red seat"
            1 -> return "Blue seat"
            2 -> return "2nd seat"
            3 -> return "3rd seat"
            4 -> return "4th seat"
            5 -> return "5th seat"
            6 -> return "6th seat"
            7 -> return "Spectator seat"
            else -> return "[${getSeat()}]"
        }
    }

    fun getStatusString() = if (idle == 0) "Idle: ${idle}" else "Standby: ${idle} [${getLoadPercent()}%]"

    fun getLoadPercent() = getData().loadingPct

    fun isLoading() = getData().loadingPct > 0 && getData().loadingPct < 100

    fun justPlayed() = getData().matchesSum > oldData().matchesSum

    fun justLost() = getData().matchesWon == oldData().matchesWon && justPlayed()

    fun justWon() = getData().matchesWon > oldData().matchesWon && justPlayed()

    fun getRating():Float {
        if (getMatchesPlayed() > 0) return ((((getMatchesWon().toFloat() * 0.1) * getChain()) + getMatchesWon()) / (getMatchesPlayed().toFloat())).toFloat()
        else return 0F
    }

    fun getRatingLetter(): String {
        var grade = "-"
        if (getMatchesWon() >= 1 && getRating() > 0.0f) grade  = "D"
        if (getMatchesWon() >= 1 && getRating() >= 0.1f) grade  = "D+"
        if (getMatchesWon() >= 2 && getRating() >= 0.2f) grade  = "C"
        if (getMatchesWon() >= 3 && getRating() >= 0.3f) grade  = "C+"
        if (getMatchesWon() >= 5 && getRating() >= 0.4f) grade  = "B"
        if (getMatchesWon() >= 8 && getRating() >= 0.6f) grade  = "B+"
        if (getMatchesWon() >= 13 && getRating() >= 1.0f) grade  = "A"
        if (getMatchesWon() >= 21 && getRating() >= 1.2f) grade = "A+"
        if (getMatchesWon() >= 34 && getRating() >= 1.4f) grade = "S"
        if (getMatchesWon() >= 55 && getRating() >= 1.6f) grade = "S+"
        return grade
    }

    fun getRatingImage(matchesWon:Int = getMatchesWon(), rating:Float = getRating()): Rectangle2D {
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

    fun getChainImage(chain:Int = getChain()): Rectangle2D {
        var grade = Rectangle2D(256.0, 448.0, 64.0, 64.0)
        if (chain == 1) grade = Rectangle2D(128.0, 0.0, 64.0, 64.0)
        if (chain == 2) grade = Rectangle2D(128.0, 64.0, 64.0, 64.0)
        if (chain == 3) grade = Rectangle2D(128.0, 128.0, 64.0, 64.0)
        if (chain == 4) grade = Rectangle2D(128.0, 192.0, 64.0, 64.0)
        if (chain == 5) grade = Rectangle2D(128.0, 256.0, 64.0, 64.0)
        if (chain == 6) grade = Rectangle2D(128.0, 320.0, 64.0, 64.0)
        if (chain == 7) grade = Rectangle2D(128.0, 384.0, 64.0, 64.0)
        if (chain == 8) grade = Rectangle2D(128.0, 448.0, 64.0, 64.0)
        return grade
    }

}

