package memscan

import memscan.GearNet.MatchupData
import memscan.GearNetShifter.Shift.*
import memscan.GearNetUpdates.Companion.IC_GEAR

class GearNetShifter(private val gnUpdates: GearNetUpdates, private val gn: GearNet) {

    enum class Shift {
        GEAR_OFFLINE,
        GEAR_LOADING,
        GEAR_LOBBY,
        GEAR_MATCH,
        GEAR_SLASH,
        GEAR_DRAWN,
        GEAR_VICTORY,
        GEAR_TRAINER
    }

    private var gearShift: Shift = GEAR_OFFLINE
    fun getShift() = gearShift

    fun update() {
        val oldShift = gearShift

        // Shift GEAR_OFFLINE
        if (!gn.getClientFighter().isValid()) shift(GEAR_OFFLINE)
        else {
            val clientMatchLoading = gn.getFrame().matchupData.firstOrNull { !it.isValid()
                    && it.isOnCabinet(gn.getClientCabinet())
                    && it.getLoaders() in 1..99

            } ?: MatchupData()
            val clientMatchOnGoing = gn.getFrame().matchupData.firstOrNull { it.isValid()
                    && it.isOnCabinet(gn.getClientCabinet()) } ?: MatchupData()


            // Shift GEAR_LOADING
            // FIXME: THIS STILL DOESN'T WORK, PLS FIX
            if (!clientMatchOnGoing.isValid() &&
                clientMatchLoading.isValid()
            ) shift(GEAR_LOADING)


            // Shift GEAR_LOBBY
            if (!clientMatchOnGoing.isValid() &&
                !clientMatchLoading.isValid()
            ) shift(GEAR_LOBBY)


            // Shift GEAR_MATCH
            if (clientMatchOnGoing.isValid() &&
                (clientMatchOnGoing.player1.health > 0 || clientMatchOnGoing.player2.health > 0 && clientMatchOnGoing.timer > 0)
            ) shift(GEAR_MATCH)


            // Shift GEAR_SLASH
            if (clientMatchOnGoing.isValid() &&
                (clientMatchOnGoing.player1.health != clientMatchOnGoing.player2.health && clientMatchOnGoing.timer == 0) ||
                (clientMatchOnGoing.player1.health == 0 || clientMatchOnGoing.player2.health == 0)
                && (clientMatchOnGoing.player1.health != clientMatchOnGoing.player2.health)
            ) shift(GEAR_SLASH)


            // Shift GEAR_DRAWN
            // TODO: check that they aren't actually in Training mode instead
            if (
                (clientMatchOnGoing.player1.health == 0 && clientMatchOnGoing.player2.health == 0) ||
                (clientMatchOnGoing.player1.health == clientMatchOnGoing.player2.health && clientMatchOnGoing.timer == 0)
            ) shift(GEAR_DRAWN)


            // Shift GEAR_VICTORY
            // TODO: check if on client cab and that one of their match records has incremented
//            if (
//                clientMatchOnGoing.player1.health == 0 && clientMatchOnGoing.player2.health == 0
//            ) shift(GEAR_VICTORY)


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
            GEAR_MATCH -> gnUpdates.add(IC_GEAR, "→ MATCH")
            GEAR_SLASH -> gnUpdates.add(IC_GEAR, "→ SLASH")
            GEAR_DRAWN -> gnUpdates.add(IC_GEAR, "→ DRAWN")
            GEAR_VICTORY -> gnUpdates.add(IC_GEAR, "→ VICTORY")
            GEAR_TRAINER -> gnUpdates.add(IC_GEAR, "→ TRAINER")
        }

    }


    /**
     *  Set [GearNet] Gear Shift™
     */
    private fun shift(gear: Shift) { if (gearShift != gear) gearShift = gear }


}