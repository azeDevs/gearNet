package memscan

import memscan.GearNet.MatchupData
import memscan.GearNet.PlayerData
import memscan.GearNetShifter.Shift.*
import memscan.GearNetUpdates.Companion.IC_GEAR
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2

class GearNetShifter(private val gnUpdates: GearNetUpdates) {

    enum class Shift {
        GEAR_OFFLINE,
        GEAR_LOADING,
        GEAR_LOBBY,
        GEAR_INTRO,
        GEAR_MATCH,
        GEAR_SLASH,
        GEAR_DRAWN,
        GEAR_VICTORY,
        GEAR_TRAINER
    }

    private var gearShift: Shift = GEAR_OFFLINE
    fun getShift() = gearShift

    fun update(
        dataList: MutableList<PlayerData>,
        muList: List<MatchupData>,
        clientCabinet: Int
    ): Shift {
        val oldShift = gearShift
        val clientMatch = muList.firstOrNull { it.isOnCabinet(clientCabinet) } ?: MatchupData()
        val player1 = dataList.firstOrNull { it.isOnCabinet(clientCabinet) && it.isSeatedAt(PLAYER_1) } ?: PlayerData()
        val player2 = dataList.firstOrNull { it.isOnCabinet(clientCabinet) && it.isSeatedAt(PLAYER_2) } ?: PlayerData()

        // Shift GEAR_OFFLINE
        if (dataList.isEmpty()) shift(GEAR_OFFLINE)
        else {


            // Shift GEAR_LOBBY
            if (!clientMatch.isTimeValid() &&
                oldShift != GEAR_LOADING
            ) shift(GEAR_LOBBY)


            // Shift GEAR_LOADING
            if (!clientMatch.isTimeValid() &&
                player1.isLoading() && player2.isLoading()
            ) shift(GEAR_LOADING)


            // Shift GEAR_INTRO
            if (clientMatch.isTimeValid() &&
                (clientMatch.player1.health > 0 || clientMatch.player2.health > 0 && clientMatch.timer == 99)
                && clientMatch.winner == -1
            ) shift(GEAR_INTRO)


            // Shift GEAR_MATCH
            if (clientMatch.isTimeValid() &&
                (clientMatch.player1.health > 0 || clientMatch.player2.health > 0 && clientMatch.timer in 0..98)
                && clientMatch.winner == -1
            ) shift(GEAR_MATCH)


            // Shift GEAR_DRAWN
            if (clientMatch.isTimeValid() &&
                ((clientMatch.player1.health == 0 && clientMatch.player2.health == 0) ||
                (clientMatch.player1.health == clientMatch.player2.health && clientMatch.timer == 0))
                && clientMatch.winner == -1
            ) shift(GEAR_DRAWN)


            // Shift GEAR_SLASH
            if (clientMatch.isTimeValid() &&
                (clientMatch.player1.health != clientMatch.player2.health && clientMatch.timer == 0) ||
                ((clientMatch.player1.health == 0 || clientMatch.player2.health == 0)
                        && (clientMatch.player1.health != clientMatch.player2.health))
                && clientMatch.winner == -1
            ) shift(GEAR_SLASH)


            // Shift GEAR_VICTORY
            if (clientMatch.isTimeValid() &&
                clientMatch.winner != -1
                && clientMatch.timer != -1
            ) shift(GEAR_VICTORY)


            // Shift GEAR_TRAINER
            // TODO: check seatingIDs and for health values existing
//            if (
//                clientMatchOnGoing.player1.health == 0 && clientMatchOnGoing.player2.health == 0
//            ) shift(GEAR_TRAINER)


        }

        // Log GearNetShifter Update
        if (gearShift != oldShift) when (gearShift) {
            GEAR_OFFLINE -> gnUpdates.add(IC_GEAR, "→ OFFLINE")
            GEAR_LOADING -> gnUpdates.add(IC_GEAR, "→ LOADING")
            GEAR_LOBBY -> gnUpdates.add(IC_GEAR, "→ LOBBY")
            GEAR_INTRO -> gnUpdates.add(IC_GEAR, "→ INTRO")
            GEAR_MATCH -> gnUpdates.add(IC_GEAR, "→ MATCH")
            GEAR_SLASH -> gnUpdates.add(IC_GEAR, "→ SLASH")
            GEAR_DRAWN -> gnUpdates.add(IC_GEAR, "→ DRAWN")
            GEAR_VICTORY -> gnUpdates.add(IC_GEAR, "→ VICTORY")
            GEAR_TRAINER -> gnUpdates.add(IC_GEAR, "→ TRAINER")
        }

        return gearShift
    }


    /**
     *  Set [GearNet] Gear Shift™
     */
    private fun shift(gear: Shift) { if (gearShift != gear) gearShift = gear }


}